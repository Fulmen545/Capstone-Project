package com.riso.android.mealtracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by richard.janitor on 23-Sep-18.
 */

public class DatabaseQuery {
    Context context;
    String[] color;
    String[] fields;
    String[] custFields;
    private String user;
    MealItem[] mealsStored;


    public DatabaseQuery(Context context){
        this.context=context;
    }

    public void removeMeal(String mealId){
        context.getContentResolver().delete(DbColumns.MealsEntry.CONTENT_URI_MEALS,
                DbColumns.MealsEntry._ID + " =? ", new String[]{mealId});

    }

    public void getColor() {
        Cursor c = context.getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_FIELDS,
                new String[]{DbColumns.MealsEntry.NAME_FLD,
                        DbColumns.MealsEntry.COLOR},
                DbColumns.MealsEntry.FIELDS_USR + " in ('default', '" + user + "') AND " +
                        DbColumns.MealsEntry.TYPE_FLD + " ='type'",
                null,
                null);
        if (c.getCount() != 0) {
            color = new String[c.getCount()];
            fields = new String[c.getCount()];
            int i = 0;
            if (c.moveToFirst()) {
                do {
                    fields[i]=c.getString(c.getColumnIndex(DbColumns.MealsEntry.NAME_FLD));
                    color[i]=c.getString(c.getColumnIndex(DbColumns.MealsEntry.COLOR));
                    i++;
                } while (c.moveToNext());
            }
        }
    }

    public String selectUser() {
        Cursor c = context.getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_USERS,
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

    public MealItem[] getStoredMeals(String date) {
        user = selectUser();
        getColor();
        Cursor c = context.getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_MEALS,
                new String[]{DbColumns.MealsEntry._ID,
                        DbColumns.MealsEntry.TYPE_ML,
                        DbColumns.MealsEntry.DESCRIPTION,
                        DbColumns.MealsEntry.DATE,
                        DbColumns.MealsEntry.TIME,
                        DbColumns.MealsEntry.LOCATION,
                        DbColumns.MealsEntry.CUST_FIELDS,
                        DbColumns.MealsEntry.GCALENDAR},
                DbColumns.MealsEntry.MEALS_USR + "=? AND " + DbColumns.MealsEntry.DATE + " =?",
                new String[]{user, date},
                DbColumns.MealsEntry.DATE + " DESC");
        if (c.getCount() != 0) {
            mealsStored = new MealItem[c.getCount()];
            int i = 0;
            if (c.moveToFirst()) {
                do {
                    mealsStored[i] = new MealItem(c.getString(c.getColumnIndex(DbColumns.MealsEntry._ID)),
                            c.getString(c.getColumnIndex(DbColumns.MealsEntry.TYPE_ML)),
                            c.getString(c.getColumnIndex(DbColumns.MealsEntry.DESCRIPTION)),
                            c.getString(c.getColumnIndex(DbColumns.MealsEntry.DATE)),
                            c.getString(c.getColumnIndex(DbColumns.MealsEntry.TIME)),
                            c.getString(c.getColumnIndex(DbColumns.MealsEntry.LOCATION)),
                            c.getString(c.getColumnIndex(DbColumns.MealsEntry.CUST_FIELDS)),
                            c.getString(c.getColumnIndex(DbColumns.MealsEntry.GCALENDAR)),
                            setColor(c.getString(c.getColumnIndex(DbColumns.MealsEntry.TYPE_ML))));
                    i++;
                } while (c.moveToNext());
            }
        }
        return mealsStored;
    }

    public String[] getLastMeal() {
        user = selectUser();
        String[] lastMeal = new String[5];
        Cursor c = context.getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_MEALS,
                new String[]{DbColumns.MealsEntry.TYPE_ML,
                        DbColumns.MealsEntry.DESCRIPTION,
                        DbColumns.MealsEntry.DATE,
                        DbColumns.MealsEntry.TIME,
                        DbColumns.MealsEntry.LOCATION},
                DbColumns.MealsEntry.MEALS_USR + "=?",
                new String[]{user},
                DbColumns.MealsEntry._ID + " DESC limit 1");
        if (c.getCount() != 0) {
            if (c.moveToFirst()) {
                lastMeal[0]=c.getString(c.getColumnIndex(DbColumns.MealsEntry.TYPE_ML));
                lastMeal[1]=c.getString(c.getColumnIndex(DbColumns.MealsEntry.DATE));
                lastMeal[2]=c.getString(c.getColumnIndex(DbColumns.MealsEntry.TIME));
                lastMeal[3]=c.getString(c.getColumnIndex(DbColumns.MealsEntry.LOCATION));
                lastMeal[4]=c.getString(c.getColumnIndex(DbColumns.MealsEntry.DESCRIPTION));
            }
        }
        return lastMeal;
    }


    public String setColor(String field){
        for (int i=0; i<fields.length; i++){
            if (fields[i].equals(field)){
                return color[i];
            }
        }
        return "grey";
    }

    public void updateColor(String color, String field){
        user = selectUser();
        ContentValues cv = new ContentValues();
        cv.put(DbColumns.MealsEntry.COLOR, color);
        context.getContentResolver().update(DbColumns.MealsEntry.CONTENT_URI_FIELDS,
                cv,DbColumns.MealsEntry.NAME_FLD + "=? AND " + DbColumns.MealsEntry.FIELDS_USR + "=?",new String[]{field, user});
    }

    public void updateMeal (String id, String mealType, String desc, String date, String time, String location, String custFields, boolean gCalendar, String email){
        ContentValues cv = new ContentValues();
        cv.put(DbColumns.MealsEntry.TYPE_ML, mealType);
        cv.put(DbColumns.MealsEntry.DESCRIPTION, desc);
        cv.put(DbColumns.MealsEntry.DATE, date);
        cv.put(DbColumns.MealsEntry.TIME, time);
        cv.put(DbColumns.MealsEntry.LOCATION, location);
        cv.put(DbColumns.MealsEntry.CUST_FIELDS, custFields);
        cv.put(DbColumns.MealsEntry.GCALENDAR, Boolean.toString(gCalendar));
        cv.put(DbColumns.MealsEntry.MEALS_USR, email);
        context.getContentResolver().update(DbColumns.MealsEntry.CONTENT_URI_MEALS, cv, DbColumns.MealsEntry._ID + "=?",new String[]{id});
    }

    public String[] getCustomFields(String user){
        try {
            Cursor c = context.getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_FIELDS,
                    new String[]{"DISTINCT " + DbColumns.MealsEntry.NAME_FLD},
                    DbColumns.MealsEntry.TYPE_FLD + "= 'custom'",
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
        return custFields;
    }

    public String getTypeColor(String type, String user){
        this.user = user;
        getColor();
        return setColor(type);
    }
}
