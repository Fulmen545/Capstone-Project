package com.riso.android.mealtracker.data;

import android.content.Context;

/**
 * Created by richard.janitor on 23-Sep-18.
 */

public class DatabaseQuery {
    Context context;

    public DatabaseQuery(Context context){
        this.context=context;
    }

    public void removeMeal(String mealId){
        context.getContentResolver().delete(DbColumns.MealsEntry.CONTENT_URI_MEALS,
                DbColumns.MealsEntry._ID + " =? ", new String[]{mealId});

    }
}
