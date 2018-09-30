package com.riso.android.mealtracker;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.riso.android.mealtracker.data.DatabaseQuery;
import com.riso.android.mealtracker.data.DbColumns;
import com.riso.android.mealtracker.data.MealItem;
import com.riso.android.mealtracker.util.MealAdapter;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity implements MealAdapter.ListItemClickListener{
    private final String TYPE = "MEAL_TYPE";
    private final String DESCRIPTION = "DESCRIPTION";
    private final String DATE = "DATE";
    private final String TIME = "TIME";
    private final String LOCATION = "LOCATION";
    private final String CUST_FIELDS = "CUST_FIELDS";
    private final String COLOR = "COLOR";
    private final String GCALENDAR = "GCALENDAR";
    private final String ID = "ID";
    private final String USER = "USER";
    public MealItem[] mealsStored;
    private String[] color;
    private String[] fields;
    private String user;
    private MealAdapter mMeakAdapter;
    @BindView(R.id.rv_meals)
    RecyclerView mRecipeNamesList;
    @BindView(R.id.no_meal_tv)
    TextView no_meal_tv;

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
        no_meal_tv = findViewById(R.id.no_meal_tv);
        mRecipeNamesList = findViewById(R.id.rv_meals);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecipeNamesList.setLayoutManager(layoutManager);
        if (mealsStored == null){
            mRecipeNamesList.setVisibility(View.GONE);
            no_meal_tv.setVisibility(View.VISIBLE);
            no_meal_tv.setText(R.string.no_meals);
        } else {
            mRecipeNamesList.setVisibility(View.VISIBLE);
            no_meal_tv.setVisibility(View.GONE);
            mMeakAdapter = new MealAdapter(HistoryActivity.this, mealsStored);
            mRecipeNamesList.setAdapter(mMeakAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.historyIcon:
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                DatabaseQuery dbquery = new DatabaseQuery(HistoryActivity.this);
                                mealsStored=dbquery.getStoredMeals(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                if (mealsStored == null){
                                    mRecipeNamesList.setVisibility(View.GONE);
                                    no_meal_tv.setVisibility(View.VISIBLE);
                                    no_meal_tv.setText("No meals are recorded for day \n"+ dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                } else {
                                    mRecipeNamesList.setVisibility(View.VISIBLE);
                                    no_meal_tv.setVisibility(View.GONE);
                                    mMeakAdapter = new MealAdapter(HistoryActivity.this, mealsStored);
                                    mRecipeNamesList.setAdapter(mMeakAdapter);
                                }

//                                editDate.setHint("");
//                                editDate.setText(dayOfMonth + "/"
//                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_history, menu);
        return true;
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


    @Override
    public void onListItemClick(int listItem) {
        getStoredMeals();
        Bundle bundle = new Bundle();
        bundle.putString(ID, mealsStored[listItem].id);
        bundle.putString(TYPE, mealsStored[listItem].typeItem);
        bundle.putString(DESCRIPTION, mealsStored[listItem].descItem);
        bundle.putString(DATE, mealsStored[listItem].dateItem);
        bundle.putString(TIME, mealsStored[listItem].timeItem);
        bundle.putString(LOCATION, mealsStored[listItem].locationItem);
        bundle.putString(CUST_FIELDS, mealsStored[listItem].customItem);
        bundle.putString(GCALENDAR, mealsStored[listItem].gCalendarItem);
        bundle.putString(COLOR, mealsStored[listItem].colorItem);
        bundle.putString(USER, user);
        Intent intent = new Intent(this, MealDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
