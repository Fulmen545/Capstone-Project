package com.riso.android.mealtracker;


import android.Manifest;
import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import com.riso.android.mealtracker.data.DatabaseQuery;
import com.riso.android.mealtracker.data.DbColumns;
import com.riso.android.mealtracker.data.MealItem;
import com.riso.android.mealtracker.util.GoogleCalendarEvents;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddMealFragment extends Fragment {
    private final String TYPE = "MEAL_TYPE";
    private final String DESCRIPTION = "DESCRIPTION";
    private final String DATE = "DATE";
    private final String TIME = "TIME";
    private final String LOCATION = "LOCATION";
    private final String CUST_FIELDS = "CUST_FIELDS";
    private final String COLOR = "COLOR";
    private final String GCALENDAR = "GCALENDAR";
    private final String ID = "ID";
    private final String USER = "USER";

    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    static final int REQUEST_AUTHORIZATION = 1001;


    Spinner typeFoodSpinner;
    Spinner custFieldSpinner;
    String[] arraySpinner = new String[]{"Breakfast", "Lunch", "Dinner"};
    String[] typeFoods;
    String[] custFields;
    EditText editDate;
    EditText editTime;
    EditText editDesc;
    DatePickerDialog datePickerDialog;
    GoogleApiClient mGoogleApiClient;
    EditText editLocation;
    Button addBtn;
    JSONObject custJson = new JSONObject();
    EditText editCustom;
    Button save;
    CheckBox calendar;

    ArrayAdapter<String> foodTypesAdapter;
    ArrayAdapter<String> custFieldsAdapter;
    int typeFoodSpinnerPosition;
    boolean edit = false;
    boolean saved = false;
    Bundle bundle;
    String user;
    com.google.api.services.calendar.Calendar mService;
    ProgressDialog mProgress;


    private ArrayList permissions = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_meal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        user = selectUser();
        getFoodTypes();
        getCustomFields();
        typeFoodSpinner = view.findViewById(R.id.typeFoodSpinner);
        foodTypesAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, typeFoods);
//        adapter.setDropDownViewResource(android.R.layout.activity_list_item);
        typeFoodSpinner.setAdapter(foodTypesAdapter);
        custFieldSpinner = view.findViewById(R.id.customSpinner);
        custFieldsAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, custFields);
        custFieldSpinner.setAdapter(custFieldsAdapter);
        custFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String editCustomValue = "";
                try {
                    editCustomValue = custJson.get(custFields[position]).toString();
                } catch (JSONException ignored) {
                } finally {
                    editCustom.setText(editCustomValue);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editDate = view.findViewById(R.id.editDate);
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                editDate.setHint("");
                                editDate.setText(String.format("%02d/%02d/%04d", dayOfMonth, (monthOfYear + 1), year));

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
        editTime = view.findViewById(R.id.editTime);
        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                        editTime.setText(selectedHour + ":" + selectedMinute);
                        editTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        editCustom = view.findViewById(R.id.editCusotom);
        addBtn = view.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String custValue : custFields) {
                    if (custValue == custFieldSpinner.getSelectedItem().toString()) {
                        try {
                            custJson.put(custValue, editCustom.getText());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), "Field was added", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
//                insertMeal(typeFoodSpinner.getSelectedItem().toString(),
//                        editDesc.getText().toString(),
//                        editDate.getText().toString(),
//                        editTime.getText().toString(),
//                        editLocation.getText().toString(),
//                        custJson.toString(),
//                        calendar.isChecked(),
//                        selectUser());
            }
        });

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnectionSuspended(int cause) {
                        }

                        @Override
                        public void onConnected(Bundle connectionHint) {

                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                        }
                    }).build();
            mGoogleApiClient.connect();
        } else {
            mGoogleApiClient.connect();
        }
//        permissions.add(ACCESS_FINE_LOCATION);
//        permissions.add(ACCESS_COARSE_LOCATION);
        editLocation = view.findViewById(R.id.editLocation);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation != null) {
                editLocation.setText("Lat.:" + mLocation.getLatitude() + " Long.:" + mLocation.getLongitude());

            } else {
                Toast.makeText(getContext(), "Couldn't access location", Toast.LENGTH_SHORT).show();
                editLocation.setText("");
            }
        } else {
            editLocation.setText("");
        }
        editDesc = view.findViewById(R.id.editDescription);
        calendar = view.findViewById(R.id.calChckbx);
        save = view.findViewById(R.id.saveBtn);

        bundle = this.getArguments();

        mCredential = GoogleAccountCredential.usingOAuth2(
                getContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        if (!bundle.containsKey(ID)) {
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (calendar.isChecked()) {
//                        if (! isGooglePlayServicesAvailable()) {
//                            acquireGooglePlayServices();
//                        } else if (mCredential.getSelectedAccountName() == null) {
//                            chooseAccount();
//                        } else if (! isDeviceOnline()) {
//                            Toast.makeText(getContext(),"No network connection available.", Toast.LENGTH_SHORT).show();
//                        } else {
//                            GoogleCalendarEvents googleCalendarEvents = new GoogleCalendarEvents(getContext(), user);
//                            DatabaseQuery databaseQuery = new DatabaseQuery(getContext());
//                            try {
//                                googleCalendarEvents.sentEvent("MT: "+ typeFoodSpinner.getSelectedItem().toString()+ " - " +editDesc.getText().toString(),
//                                        editDate.getText().toString(),
//                                        editTime.getText().toString(),
//                                        editLocation.getText().toString(),
//                                        custJson.toString(),
//                                        databaseQuery.getTypeColor(typeFoodSpinner.getSelectedItem().toString(), user));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
                        getResultsFromApi();

                    }
                    insertMeal(typeFoodSpinner.getSelectedItem().toString(),
                            editDesc.getText().toString(),
                            editDate.getText().toString(),
                            editTime.getText().toString(),
                            editLocation.getText().toString(),
                            custJson.toString(),
                            calendar.isChecked(),
                            selectUser());
                    Toast.makeText(getContext(), "Meal was added", Toast.LENGTH_SHORT).show();

//                    Intent intent = new Intent(getContext(), MealWidgetProvider.class);
//                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//                    int[] ids = AppWidgetManager.getInstance(getActivity()).getAppWidgetIds(new ComponentName(getActivity(), MealWidgetProvider.class));
//                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
//                    getContext().sendBroadcast(intent);
//                    getActivity().onBackPressed();
                }
            });

        } else {
            getActivity().setTitle(R.string.edit_meal);
            edit = true;
            typeFoodSpinnerPosition = foodTypesAdapter.getPosition(bundle.getString(TYPE));
            typeFoodSpinner.setSelection(typeFoodSpinnerPosition, true);
            editDesc.setText(bundle.getString(DESCRIPTION));
            editDate.setText(bundle.getString(DATE));
            editTime.setText(bundle.getString(TIME));
            editLocation.setText(bundle.getString(LOCATION));
            try {
                custJson = new JSONObject(bundle.getString(CUST_FIELDS));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (bundle.getString(GCALENDAR).equals("true")) {
                calendar.setChecked(true);
            }
            final DatabaseQuery databaseQuery = new DatabaseQuery(getContext());
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saved = true;
                    typeFoodSpinner.setSelection(typeFoodSpinnerPosition);
                    databaseQuery.updateMeal(bundle.getString(ID),
                            typeFoodSpinner.getSelectedItem().toString(),
                            editDesc.getText().toString(),
                            editDate.getText().toString(),
                            editTime.getText().toString(),
                            editLocation.getText().toString(),
                            custJson.toString(),
                            calendar.isChecked(),
                            selectUser());
                    Toast.makeText(getContext(), "Meal was edited", Toast.LENGTH_SHORT).show();
//                    getActivity().onBackPressed();
                }
            });
        }

        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Calling Google Calendar API ...");

    }

    public void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(getContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
        } else {
            GoogleCalendarEvents googleCalendarEvents = new GoogleCalendarEvents(getContext(), user);
            DatabaseQuery databaseQuery = new DatabaseQuery(getContext());
            String color = databaseQuery.getTypeColor(typeFoodSpinner.getSelectedItem().toString(), user);
            MealItem mealItem = new MealItem("X", typeFoodSpinner.getSelectedItem().toString(),
                    editDesc.getText().toString(), editDate.getText().toString(),
                    editTime.getText().toString(), editLocation.getText().toString(),
                    custJson.toString(), "true", color);
            new SendEventTask().execute(mealItem);

//            try {
//                googleCalendarEvents.sentEvent("MT: "+ typeFoodSpinner.getSelectedItem().toString()+ " - " +editDesc.getText().toString(),
//                        editDate.getText().toString(),
//                        editTime.getText().toString(),
//                        editLocation.getText().toString(),
//                        custJson.toString(),
//                        databaseQuery.getTypeColor(typeFoodSpinner.getSelectedItem().toString(), user));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getFoodTypes();
        getCustomFields();
        foodTypesAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, typeFoods);
        typeFoodSpinner.setAdapter(foodTypesAdapter);
        typeFoodSpinner.setSelection(typeFoodSpinnerPosition, true);
        custFieldsAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, custFields);
        custFieldSpinner.setAdapter(custFieldsAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_meal, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (edit) {
                    Bundle newbundle;
                    if (saved) {
                        newbundle = new Bundle();
                        newbundle.putString(ID, bundle.getString(ID));
                        newbundle.putString(TYPE, typeFoodSpinner.getSelectedItem().toString());
                        newbundle.putString(DESCRIPTION, editDesc.getText().toString());
                        newbundle.putString(DATE, editDate.getText().toString());
                        newbundle.putString(TIME, editTime.getText().toString());
                        newbundle.putString(LOCATION, editLocation.getText().toString());
                        newbundle.putString(CUST_FIELDS, custJson.toString());
                        newbundle.putString(GCALENDAR, Boolean.toString(calendar.isChecked()));
                        newbundle.putString(COLOR, bundle.getString(COLOR));
                        newbundle.putString(USER, bundle.getString(USER));
                    } else {
                        newbundle = bundle;
                    }
                    DetailMealFragment detailMealFragment = new DetailMealFragment();
                    detailMealFragment.setArguments(newbundle);
                    changeTo(detailMealFragment, android.R.id.content, "tag1");
                } else {
                    getActivity().onBackPressed();
                }
                return true;
            case R.id.settings:
//                Toast.makeText(getContext(),"Settings", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String selectUser() {
        Cursor c = getActivity().getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_USERS,
                new String[]{DbColumns.MealsEntry.EMAIL},
                null,
                null,
                null);
        if (c.moveToNext()) {
            return c.getString(c.getColumnIndex("email"));
        } else {
            return "";
        }
    }

    private void insertMeal(String mealType, String desc, String date, String time, String location, String custFields, boolean gCalendar, String email) {
        ContentValues cv = new ContentValues();
        cv.put(DbColumns.MealsEntry.TYPE_ML, mealType);
        cv.put(DbColumns.MealsEntry.DESCRIPTION, desc);
        cv.put(DbColumns.MealsEntry.DATE, date);
        cv.put(DbColumns.MealsEntry.TIME, time);
        cv.put(DbColumns.MealsEntry.LOCATION, location);
        cv.put(DbColumns.MealsEntry.CUST_FIELDS, custFields);
        cv.put(DbColumns.MealsEntry.GCALENDAR, Boolean.toString(gCalendar));
        cv.put(DbColumns.MealsEntry.MEALS_USR, email);
        getContext().getContentResolver().insert(DbColumns.MealsEntry.CONTENT_URI_MEALS, cv);
    }

    private void getFoodTypes() {
        try {
            Cursor c = getActivity().getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_FIELDS,
                    new String[]{"DISTINCT " + DbColumns.MealsEntry.NAME_FLD, DbColumns.MealsEntry.COLOR},
                    DbColumns.MealsEntry.TYPE_FLD + "= 'type' AND " + DbColumns.MealsEntry.FIELDS_USR + " in ('default', '" + user + "')",
                    null,
                    null);
            if (c.getCount() != 0) {
                typeFoods = new String[c.getCount()];
                int i = 0;
                if (c.moveToFirst()) {
                    do {
                        typeFoods[i] = c.getString(c.getColumnIndex(DbColumns.MealsEntry.NAME_FLD));
                        i++;
                    } while (c.moveToNext());
                }
            }
        } catch (Exception ex) {
            Log.e("ADDMEAL", "RISO EX: " + ex);
        }

    }

    private void getCustomFields() {
        try {
            Cursor c = getActivity().getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_FIELDS,
                    new String[]{"DISTINCT " + DbColumns.MealsEntry.NAME_FLD},
                    DbColumns.MealsEntry.TYPE_FLD + "= 'custom' AND " + DbColumns.MealsEntry.FIELDS_USR + " in ('default', '" + user + "')",
                    null,
                    null);
            if (c.getCount() != 0) {
                custFields = new String[c.getCount()];
                int i = 0;
                if (c.moveToFirst()) {
                    do {
                        custFields[i] = c.getString(c.getColumnIndex(DbColumns.MealsEntry.NAME_FLD));
                        i++;
                    } while (c.moveToNext());
                }
            }
        } catch (Exception ex) {
            Log.e("ADDMEAL", "RISO EX: " + ex);
        }

    }

    public void changeTo(Fragment fragment, int containerViewId, String tag) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().replace(containerViewId, fragment, tag == null ? fragment.getClass().getName() : tag).commit();

    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getContext());
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getContext());
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                getActivity(),
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                getContext(), Manifest.permission.GET_ACCOUNTS)) {
//            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            DatabaseQuery databaseQuery = new DatabaseQuery(getContext());
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

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
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
                                getActivity().getPreferences(Context.MODE_PRIVATE);
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

    private class SendEventTask extends AsyncTask<MealItem, Void, Void> {

        private Exception mLastError = null;

        SendEventTask() {
            mCredential = GoogleAccountCredential.usingOAuth2(
                    getActivity(), Arrays.asList(SCOPES))
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
        protected Void doInBackground(MealItem... params) {
            try {
                sentEvent(params[0]);
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
                    Toast.makeText(getActivity(), "The following error occurred:\n"
                            + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Request cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sentEvent(MealItem mealItem) throws ParseException, IOException {

//        mService = new com.google.api.services.calendar.Calendar.Builder(
//                HTTP_TRANSPORT, jsonFactory, mCredential)
//                .setApplicationName("MealTracker")
//                .build();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date dt = formatter.parse(mealItem.dateItem);
        formatter.applyPattern("yyyy-MM-dd");
        String newFormat = formatter.format(dt);
//        DateTime startDate = new DateTime(dt);
        DateTime startDate = new DateTime(newFormat + "T" + mealItem.timeItem + ":00+02:00");
        DateTime endDate = new DateTime(newFormat + "T" + mealItem.timeItem + ":00+01:00");
        Event event1 = new Event();
        event1.setSummary("MT: " + mealItem.typeItem + " - " + mealItem.descItem);
        event1.setStart(new EventDateTime().setDateTime(startDate));
        event1.setEnd(new EventDateTime().setDateTime(endDate));
        event1.setDescription(mealItem.customItem);
        event1.setColorId(setColorId(mealItem.colorItem));
        event1.setLocation(mealItem.locationItem);
//        new GoogleCalendarEvents.SendGoogleEvent().execute(event1);
        event1 = mService.events().insert("primary", event1).execute();
//        event = service.events().insert("#contacts@group.v.calendar.google.com", event).execute();
//        Log.i(getClass().getSimpleName(), event1.getId());
    }

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

}
