package com.riso.android.mealtracker.data;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by richard.janitor on 23-Sep-18.
 */

public class DatabaseQuery {
    Context context;
    String[] color;
    String[] fields;
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
                DbColumns.MealsEntry.DATE);
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


    public String setColor(String field){
        for (int i=0; i<fields.length; i++){
            if (fields[i].equals(field)){
                return color[i];
            }
        }
        return "grey";
    }
}
