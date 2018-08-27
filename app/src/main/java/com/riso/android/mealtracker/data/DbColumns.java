package com.riso.android.mealtracker.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by richard.janitor on 15-Aug-18.
 */

public class DbColumns {

    public static final String CONTENT_AUTHORITY = "com.riso.android.mealtracker";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MealsEntry implements BaseColumns {
        public static final String TABLE_NAME_MEALS = "meals";
        public static final String TABLE_NAME_FIELDS = "fields";
        public static final String MEAL_ID = "mealid";
        public static final String TYPE_ML = "typeml";
        public static final String DESCRIPTION = "description";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String LOCATION = "location";
        public static final String CUST_FIELDS = "custfields";
        public static final String GCALENDAR = "gcalendar";
        public static final String MEALS_USR = "mealusr";
        public static final String FIELDS_USR = "fieldusr";
        public static final String NAME_FLD = "namefld";
        public static final String TYPE_FLD = "typefld";
        public static final String COLOR = "color";

        //user table
        public static final String TABLE_NAME_USERS = "users";
        public static final String FIRST = "first";
        public static final String EMAIL = "email";
        public static final String TOKEN = "typefld";
        public static final String USER = "color";

        public static final Uri CONTENT_URI_MEALS = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME_MEALS).build();

        public static final Uri CONTENT_URI_FIELDS = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME_FIELDS).build();

        public static final Uri CONTENT_URI_USERS = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME_USERS).build();

        public static final String CONTENT_DIR_TYPE_MEALS =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME_MEALS;

        public static final String CONTENT_ITEM_TYPE_MEALS =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME_MEALS;

        public static final String CONTENT_DIR_TYPE_FIELDS =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME_FIELDS;

        public static final String CONTENT_ITEM_TYPE_FIELDS =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME_FIELDS;

        public static final String CONTENT_DIR_TYPE_USERS =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME_USERS;

        public static Uri buildIngredientsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_MEALS, id);
        }

        public static Uri buildStepsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_FIELDS, id);
        }

        public static Uri buildUsersUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_USERS, id);
        }
    }
}
