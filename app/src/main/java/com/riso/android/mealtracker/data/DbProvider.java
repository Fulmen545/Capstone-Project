package com.riso.android.mealtracker.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by richard.janitor on 17-Aug-18.
 */

public class DbProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    private static final int MEAL = 100;
    private static final int MEAL_WITH_ID = 200;
    private static final int FIELD = 101;
    private static final int FIELD_WITH_ID = 201;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DbColumns.CONTENT_AUTHORITY;

        matcher.addURI(authority, DbColumns.MealsEntry.TABLE_NAME_MEALS, MEAL);
        matcher.addURI(authority, DbColumns.MealsEntry.TABLE_NAME_MEALS + "/#", MEAL_WITH_ID);
        matcher.addURI(authority, DbColumns.MealsEntry.TABLE_NAME_FIELDS, FIELD);
        matcher.addURI(authority, DbColumns.MealsEntry.TABLE_NAME_FIELDS + "/#", FIELD_WITH_ID);

        return matcher;
    }
    
    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case FIELD: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DbColumns.MealsEntry.TABLE_NAME_FIELDS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return retCursor;
            }
            case FIELD_WITH_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DbColumns.MealsEntry.TABLE_NAME_FIELDS,
                        projection,
                        DbColumns.MealsEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            case MEAL: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DbColumns.MealsEntry.TABLE_NAME_MEALS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return retCursor;
            }
            case MEAL_WITH_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DbColumns.MealsEntry.TABLE_NAME_MEALS,
                        projection,
                        DbColumns.MealsEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default:{
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case FIELD:{
                return DbColumns.MealsEntry.CONTENT_DIR_TYPE_FIELDS;
            }
            case FIELD_WITH_ID:{
                return DbColumns.MealsEntry.CONTENT_DIR_TYPE_FIELDS;
            }
            case MEAL:{
                return DbColumns.MealsEntry.CONTENT_DIR_TYPE_MEALS;
            }
            case MEAL_WITH_ID:{
                return DbColumns.MealsEntry.CONTENT_DIR_TYPE_MEALS;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MEAL:{
                long _id = db.insert(DbColumns.MealsEntry.TABLE_NAME_MEALS, null, values);
                if (_id > 0) {
                    returnUri = DbColumns.MealsEntry.buildStepsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case FIELD:{
                long _id = db.insert(DbColumns.MealsEntry.TABLE_NAME_FIELDS, null, values);
                if (_id > 0) {
                    returnUri = DbColumns.MealsEntry.buildIngredientsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (sUriMatcher.match(uri)) {
            case MEAL:{
                rowsDeleted = db.delete(DbColumns.MealsEntry.TABLE_NAME_MEALS,  selection, selectionArgs);
                break;
            }
            case FIELD:{
                rowsDeleted = db.delete(DbColumns.MealsEntry.TABLE_NAME_FIELDS,  selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (sUriMatcher.match(uri)) {
            case MEAL:{
                rowsUpdated = db.update(DbColumns.MealsEntry.TABLE_NAME_MEALS,  values, selection, selectionArgs);
                break;
            }
            case FIELD:{
                rowsUpdated = db.update(DbColumns.MealsEntry.TABLE_NAME_FIELDS,  values, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
