package com.riso.android.mealtracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by richard.janitor on 15-Aug-18.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tracker.db";

    private static final int DATABASE_VERSION = 1;
    
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MEALS_TABLE = "CREATE TABLE " + DbColumns.MealsEntry.TABLE_NAME_MEALS + " (" +
                DbColumns.MealsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbColumns.MealsEntry.TYPE_ML + " TEXT NOT NULL, " +
                DbColumns.MealsEntry.DESCRIPTION + " TEXT, " +
                DbColumns.MealsEntry.DATE + " TEXT NOT NULL, " +
                DbColumns.MealsEntry.TIME + " TEXT NOT NULL, " +
                DbColumns.MealsEntry.LOCATION + " TEXT NOT NULL, " +
                DbColumns.MealsEntry.CUST_FIELDS + " TEXT, " +
                DbColumns.MealsEntry.GCALENDAR + " INTEGER NOT NULL, " +
                DbColumns.MealsEntry.MEALS_USR + " TEXT NOT NULL" +
                "); ";


        final String SQL_CREATE_FIELDS_TABLE = "CREATE TABLE " + DbColumns.MealsEntry.TABLE_NAME_FIELDS + " (" +
                DbColumns.MealsEntry.NAME_FLD + " TEXT NOT NULL, " +
                DbColumns.MealsEntry.TYPE_FLD + " TEXT NOT NULL, " +
                DbColumns.MealsEntry.COLOR + " TEXT, " +
                DbColumns.MealsEntry.FIELDS_USR + " TEXT NOT NULL" +
                "); ";

        final String SQL_CREATE_USERS_TABLE = "CREATE TABLE " + DbColumns.MealsEntry.TABLE_NAME_USERS + " (" +
                DbColumns.MealsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbColumns.MealsEntry.FIRST + " TEXT NOT NULL, " +
                DbColumns.MealsEntry.EMAIL + " TEXT NOT NULL, " +
                DbColumns.MealsEntry.TOKEN + " TEXT NOT NULL, " +
                DbColumns.MealsEntry.USER + " TEXT" +
                "); ";

        db.execSQL(SQL_CREATE_MEALS_TABLE);
        db.execSQL(SQL_CREATE_FIELDS_TABLE);
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL("INSERT INTO fields VALUES ('Breakfast', 'type', 'green', 'default')");
        db.execSQL("INSERT INTO fields VALUES ('Lunch', 'type', 'blue', 'default')");
        db.execSQL("INSERT INTO fields VALUES ('Dinner', 'type', 'red', 'default')");
        db.execSQL("INSERT INTO fields VALUES ('Calories', 'custom', '', 'default')");
        db.execSQL("INSERT INTO fields VALUES ('Vegan', 'custom', '', 'default')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbColumns.MealsEntry.TABLE_NAME_MEALS);
        db.execSQL("DROP TABLE IF EXISTS " + DbColumns.MealsEntry.TABLE_NAME_FIELDS);
        db.execSQL("DROP TABLE IF EXISTS " + DbColumns.MealsEntry.TABLE_NAME_USERS);
        onCreate(db);
    }
}
