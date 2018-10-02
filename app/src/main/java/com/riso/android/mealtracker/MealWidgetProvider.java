package com.riso.android.mealtracker;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.riso.android.mealtracker.data.DatabaseQuery;
import com.riso.android.mealtracker.data.DbColumns;
import com.riso.android.mealtracker.data.MealItem;

/**
 * Implementation of App Widget functionality.
 */
public class MealWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        DatabaseQuery databaseQuery = new DatabaseQuery(context);
        String[] lastMeal = databaseQuery.getLastMeal();
        String widgetTitle = lastMeal[0];
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.meal_widget_provider);
        views.setTextViewText(R.id.titleWidget, widgetTitle);
        views.setTextViewText(R.id.dateWidget, lastMeal[1]);
        views.setTextViewText(R.id.timeWidget, lastMeal[2]);
        views.setTextViewText(R.id.locationWidget, lastMeal[3]);
        views.setTextViewText(R.id.descriptionWidget, lastMeal[4]);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

