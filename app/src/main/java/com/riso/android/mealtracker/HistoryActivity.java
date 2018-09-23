package com.riso.android.mealtracker;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.riso.android.mealtracker.data.DbColumns;
import com.riso.android.mealtracker.data.MealItem;
import com.riso.android.mealtracker.util.MealAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity {
    private MealItem[] mealsStored;
    private String[] color;
    private String[] fields;
    private String user;
    private MealAdapter mMeakAdapter;
    @BindView(R.id.rv_meals)
    RecyclerView mRecipeNamesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        ((AppCompatActivity) this).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) this).getSupportActionBar().setDisplayShowHomeEnabled(true);
        user = selectUser();
        getColor();
        getStoredMeals();
        mMeakAdapter = new MealAdapter(mealsStored);
        mRecipeNamesList = findViewById(R.id.rv_meals);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecipeNamesList.setLayoutManager(layoutManager);
        mRecipeNamesList.setAdapter(mMeakAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getColor() {
        Cursor c = getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_FIELDS,
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

    private void getStoredMeals() {
        Cursor c = getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_MEALS,
                new String[]{DbColumns.MealsEntry._ID,
                        DbColumns.MealsEntry.TYPE_ML,
                        DbColumns.MealsEntry.DESCRIPTION,
                        DbColumns.MealsEntry.DATE,
                        DbColumns.MealsEntry.TIME,
                        DbColumns.MealsEntry.LOCATION,
                        DbColumns.MealsEntry.CUST_FIELDS,
                        DbColumns.MealsEntry.GCALENDAR},
                DbColumns.MealsEntry.MEALS_USR + "=?",
                new String[]{user},
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
    }

    public String selectUser() {
        Cursor c = getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_USERS,
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

    public String setColor(String field){
        for (int i=0; i<fields.length; i++){
            if (fields[i].equals(field)){
                return color[i];
            }
        }
        return "grey";
    }


}
