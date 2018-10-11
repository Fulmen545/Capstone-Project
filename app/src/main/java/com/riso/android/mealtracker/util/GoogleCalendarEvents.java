package com.riso.android.mealtracker.util;

import android.content.Context;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
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
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by richard.janitor on 03-Oct-18.
 */

public class GoogleCalendarEvents {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    GoogleAccountCredential credential;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };
    Context context;



    public GoogleCalendarEvents(Context context) throws GeneralSecurityException, IOException {
        this.context = context;
//        GoogleCredential credential = new GoogleCredential.Builder()
//                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
//                .setJsonFactory(JSON_FACTORY)
//                .setServiceAccountId(emailAddress)
//                .setServiceAccountPrivateKeyFromP12File(new File("MyProject.p12"))
//                .setServiceAccountScopes(Collections.singleton(SQLAdminScopes.SQLSERVICE_ADMIN))
//                .setServiceAccountUser("user@example.com")
//                .build();

//        credential = GoogleAccountCredential.usingOAuth2(
//                context, Arrays.asList(SCOPES))
//                .setBackOff(new ExponentialBackOff())
//                .setSelectedAccountName(user);
//
//
//
//        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
//                .setApplicationName("MealTracker")
//                .build();;
////        service =
////                new com.google.api.services.tasks.Tasks.Builder(httpTransport, jsonFactory, credential)
////                        .setApplicationName("Google-TasksAndroidSample/1.0").build();
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//        Date dt = formatter.parse(date + " " + time);
//        DateTime startDate = new DateTime(dt);
//        Date et = new Date(dt.getTime() + 3600000);
//        DateTime endDate = new DateTime(et);
//        Event event = new Event();
//        event.setSummary(title);
//        event.setStart(new EventDateTime().setDateTime(startDate));
//        event.setEnd(new EventDateTime().setDateTime(endDate));
//        event.setDescription(desc);
//        event.setColorId(setColorId(color));
//        event.setLocation(location);
//        event = service.events().insert("Ym9vbi5tYXh0ZXN0QGdtYWlsLmNvbQ", event).execute();
////        event = service.events().insert("#contacts@group.v.calendar.google.com", event).execute();
//        Log.i(getClass().getSimpleName(), event.getId());


    }

    public void sentEvent(String title, String date, String time, String location, String desc, String color, String user) throws ParseException, IOException {
        credential = GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(user);



//        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
//                .setApplicationName("MealTracker")
//                .build();


        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, jsonFactory, credential)
                .setApplicationName("MealTracker")
                .build();

//        service =
//                new com.google.api.services.tasks.Tasks.Builder(httpTransport, jsonFactory, credential)
//                        .setApplicationName("Google-TasksAndroidSample/1.0").build();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date dt = formatter.parse(date + " " + time);
//        DateTime startDate = new DateTime(dt);
        DateTime startDate = new DateTime("2018-10-28T00:00:00-07:00");
        Date et = new Date(dt.getTime() + 3600000);
        DateTime endDate = new DateTime(et);
        Event event = new Event();
        event.setSummary(title);
        event.setStart(new EventDateTime().setDateTime(startDate));
        event.setEnd(new EventDateTime().setDateTime(endDate));
        event.setDescription(desc);
        event.setColorId(setColorId(color));
        event.setLocation(location);
        event = service.events().insert("primary", event).execute();
//        event = service.events().insert("#contacts@group.v.calendar.google.com", event).execute();
        Log.i(getClass().getSimpleName(), event.getId());
    }

    private String setColorId(String color){
        switch (color){
            case "red": return "11";
            case "blue": return "9";
            case "green": return "10";
            case "cyan": return "7";
            case "deepOrange": return "6";
            case "yellow": return "5";
            case "pink": return "4";
            case "purple": return "3";
            case "brown": return "2";
            case "deepPurple": return "1";
            default: return "8";
        }
    }
}
