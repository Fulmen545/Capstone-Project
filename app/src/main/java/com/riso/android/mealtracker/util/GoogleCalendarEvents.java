package com.riso.android.mealtracker.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.ColorDefinition;
import com.google.api.services.calendar.model.Colors;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.model.Events;
import com.riso.android.mealtracker.GoogleTestActivity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by richard.janitor on 03-Oct-18.
 */

public class GoogleCalendarEvents {
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    GoogleAccountCredential credential;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };
    Context context;
    Calendar mService = null;
    ProgressDialog mProgress;
    GoogleAccountCredential mCredential;
    String user;





    public GoogleCalendarEvents(Context context, String user)  {
        this.context = context;
        this.user = user;

        mProgress = new ProgressDialog(context);
        mProgress.setMessage("Calling Google Calendar API ...");

        mCredential = GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList(SCOPES))
                .setSelectedAccountName(user)
                .setBackOff(new ExponentialBackOff());

    }

    public void sentEvent(String title, String date, String time, String location, String desc, String color) throws ParseException, IOException {

        mService = new Calendar.Builder(
                HTTP_TRANSPORT, jsonFactory, mCredential)
                .setApplicationName("MealTracker")
                .build();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date dt = formatter.parse(date);
        formatter.applyPattern("yyyy-MM-dd");
        String newFormat = formatter.format(dt);
//        DateTime startDate = new DateTime(dt);
        DateTime startDate = new DateTime(newFormat+"T"+time+":00+02:00");
        DateTime endDate = new DateTime(newFormat+"T"+time+":00+01:00");
        Event event1 = new Event();
        event1.setSummary(title);
        event1.setStart(new EventDateTime().setDateTime(startDate));
        event1.setEnd(new EventDateTime().setDateTime(endDate));
        event1.setDescription(desc);
        event1.setColorId(setColorId(color));
        event1.setLocation(location);
        new SendGoogleEvent().execute(event1);
//        event1 = mService.events().insert("primary", event1).execute();
//        event = service.events().insert("#contacts@group.v.calendar.google.com", event).execute();
//        Log.i(getClass().getSimpleName(), event1.getId());
    }

    private String setColorId(String color){
        switch (color){
            case "red": return "11";
            case "blue": return "9";
            case "green": return "10";
            case "Cyan": return "7";
            case "Orange": return "6";
            case "Yellow": return "5";
            case "Pink": return "4";
            case "Purple": return "3";
            case "Brown": return "2";
            case "Deep Purple": return "1";
            default: return "8";
        }
    }

    private class SendGoogleEvent extends AsyncTask<Event, Void, Void>{

        @Override
        protected Void doInBackground(Event... events) {
            try {
                events[0] = mService.events().insert("primary", events[0]).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Log.i(getClass().getSimpleName(), events[0].getId());
            return null;
        }
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {

        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("MealTracker")
                    .build();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            super.onPostExecute(output);
            mProgress.hide();
            if (output == null || output.size() == 0) {
//                mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the Google Calendar API:");
//                mOutputText.setText(TextUtils.join("\n", output));
            }
        }

//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//            mProgress.hide();
//            if (mLastError != null) {
//                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
//                    showGooglePlayServicesAvailabilityErrorDialog(
//                            ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                    .getConnectionStatusCode());
//                } else if (mLastError instanceof UserRecoverableAuthIOException) {
//                    startActivityForResult(
//                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
//                            GoogleTestActivity.REQUEST_AUTHORIZATION);
//                } else {
//                    mOutputText.setText("The following error occurred:\n"
//                            + mLastError.getMessage());
//                }
//            } else {
//                mOutputText.setText("Request cancelled.");
//            }
//        }
    }

    private List<String> getDataFromApi() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        Events events = mService.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        Colors colors = mService.colors().get().execute();
        for (Map.Entry<String, ColorDefinition> color : colors.getEvent().entrySet()) {
            System.out.println("ColorId : " + color.getKey());
            System.out.println("  Background: " + color.getValue().getBackground());
            System.out.println("  Foreground: " + color.getValue().getForeground());
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//        DateTime startDate = new DateTime(dt);
        DateTime startDate = new DateTime("2018-10-28T19:30:00+01:00");
        DateTime endDate = new DateTime("2018-10-28T19:30:00-00:00");
        Event event1 = new Event();
        event1.setSummary("Testujem iny event2");
        event1.setStart(new EventDateTime().setDateTime(startDate));
        event1.setEnd(new EventDateTime().setDateTime(endDate));
        event1.setDescription("Tu bude description");
        event1.setColorId("11");
        event1.setLocation("Doma u manky");
        event1 = mService.events().insert("primary", event1).execute();

        Log.i(getClass().getSimpleName(), event1.getId());

        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                // All-day events don't have start times, so just use
                // the start date.
                start = event.getStart().getDate();
            }
            eventStrings.add(
                    String.format("%s (%s)", event.getSummary(), start));
        }
        return eventStrings;
    }

//    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        Dialog dialog = apiAvailability.getErrorDialog(
//                context.getApplicationContext(),
//                connectionStatusCode,
//                REQUEST_GOOGLE_PLAY_SERVICES);
//        dialog.show();
//    }
}
