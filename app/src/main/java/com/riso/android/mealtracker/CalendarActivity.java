package com.riso.android.mealtracker;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.riso.android.mealtracker.data.DatabaseQuery;
import com.riso.android.mealtracker.data.MealItem;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class CalendarActivity extends AppCompatActivity {
    com.google.api.services.calendar.Calendar mService;
    ProgressDialog mProgress;
    GoogleAccountCredential mCredential;
    String user;
    MealItem[] mealItems;
    String calendarDate;

    @BindView(R.id.simpleCalendarView)
    CalendarView calendarView;
    @BindView(R.id.getEventsBtn)
    Button getEventsBtn;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);
        ((AppCompatActivity) this).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) this).getSupportActionBar().setDisplayShowHomeEnabled(true);
        DatabaseQuery databaseQuery = new DatabaseQuery(this);
        user = databaseQuery.selectUser();
        calendarView = findViewById(R.id.simpleCalendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendarDate= String.format("%02d/%02d/%04d", dayOfMonth, month+1, year);
            }
        });
        getEventsBtn = findViewById(R.id.getEventsBtn);
        getEventsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetEventsTask().execute(calendarDate);
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Calendar API ...");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
//                    mOutputText.setText("This app requires Google Play Services. Please install " +
//                            "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_PERMISSION_GET_ACCOUNTS:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    private class GetEventsTask extends AsyncTask<String, Void, Void> {

        private Exception mLastError = null;

        GetEventsTask() {
            mCredential = GoogleAccountCredential.usingOAuth2(
                    CalendarActivity.this, Arrays.asList(SCOPES))
                    .setSelectedAccountName(user)
                    .setBackOff(new ExponentialBackOff());

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, mCredential)
                    .setApplicationName("MealTracker")
                    .build();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date dt = formatter.parse(params[0]);
                formatter.applyPattern("yyyy-MM-dd");
                String newFormat = formatter.format(dt);
                DateTime startDate = new DateTime(newFormat + "T00:00:00+02:00");
                DateTime endDate = new DateTime(newFormat + "T23:59:00+02:00");

                Events eventsGet = mService.events().list("primary")
                        .setMaxResults(10)
                        .setTimeMin(startDate)
                        .setTimeMax(endDate)
                        .setSingleEvents(true)
                        .execute();
                List<Event> items = eventsGet.getItems();
                int i = 0;
                for (Event event : items) {
                    if (event.getSummary().startsWith("MT: ")) {
                        i++;
                    }
                }
                mealItems = new MealItem[i];
                i = 0;
                for (Event event : items) {
                    if (event.getSummary().startsWith("MT: ")) {
                        String[] typeAndDesc = splitSummary(event.getSummary());
                        String[] dateTime = splitDateTime(event.getStart().getDateTime().toString());
                        mealItems[i] = new MealItem("x", typeAndDesc[0], typeAndDesc[1], dateTime[0], dateTime[1], event.getLocation(),event.getDescription(),"true","green");
                        i++;
                    }
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Void output) {
            super.onPostExecute(output);
            mProgress.hide();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleTestActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(CalendarActivity.this, "The following error occurred:\n"
                            + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CalendarActivity.this, "Request cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    public void getEvents(DateTime mealItem) throws ParseException, IOException {
//
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//        Date dt = formatter.parse(mealItem.dateItem);
//        formatter.applyPattern("yyyy-MM-dd");
//        String newFormat = formatter.format(dt);
//        DateTime startDate = new DateTime(newFormat + "T" + mealItem.timeItem + ":00+02:00");
//        DateTime endDate = new DateTime(newFormat + "T" + mealItem.timeItem + ":00+01:00");
//        Event event1 = new Event();
//        event1.setSummary("MT: " + mealItem.typeItem + " - " + mealItem.descItem);
//        event1.setStart(new EventDateTime().setDateTime(startDate));
//        event1.setEnd(new EventDateTime().setDateTime(endDate));
//        event1.setDescription(mealItem.customItem);
//        event1.setColorId(setColorId(mealItem.colorItem));
//        event1.setLocation(mealItem.locationItem);
//        mService.events().insert("primary", event1).execute();
////        Log.i(getClass().getSimpleName(), event1.getId());
//    }

    private String setColorId(String color) {
        switch (color) {
            case "red":
                return "11";
            case "blue":
                return "9";
            case "green":
                return "10";
            case "Cyan":
                return "7";
            case "Orange":
                return "6";
            case "Yellow":
                return "5";
            case "Pink":
                return "4";
            case "Purple":
                return "3";
            case "Brown":
                return "2";
            case "Deep Purple":
                return "1";
            default:
                return "8";
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    public String[] splitSummary(String summary) {
        String data = summary.replaceFirst("MT: ", "");
        return data.split(" - ");
    }

    public String[] splitDateTime(String datetime) throws ParseException {
        String[] date = datetime.split("T");
        String time = date[1].substring(0, 5);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = formatter.parse(date[0]);
        formatter.applyPattern("dd/MM/yyyy");
        String newFormat = formatter.format(dt);
        date[0]=newFormat;
        date[1]=time;
        return date;
    }

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        } else {
            new GetEventsTask().execute(calendarDate);
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
//            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            DatabaseQuery databaseQuery = new DatabaseQuery(getApplication());
            String accountName = databaseQuery.selectUser();
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

}
