package com.riso.android.mealtracker.util;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by richard.janitor on 03-Oct-18.
 */

public class GoogleCalendarEvents {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();


    public GoogleCalendarEvents(String title, String date, String time, String location, String desc, String color) throws ParseException, GeneralSecurityException, IOException {

//        Calendar service = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,google credentials)
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date dt = formatter.parse(date + " " + time);
        DateTime startDate = new DateTime(dt);
        Date et = new Date(dt.getTime() + 3600000);
        DateTime endDate = new DateTime(et);
        Event event = new Event();
        event.setSummary(title);
        event.setStart(new EventDateTime().setDateTime(startDate));
        event.setEnd(new EventDateTime().setDateTime(endDate));
        event.setDescription(desc);
        event.setColorId(setColorId(color));
        event.setLocation(location);

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
