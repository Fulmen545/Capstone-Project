package com.riso.android.mealtracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.riso.android.mealtracker.data.DbColumns;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {
    String[] typeFoods;
    String[] custFields;
    @BindView(R.id.typeFoodSettingsSpinner)
    Spinner typeFoodSpinner;
    @BindView(R.id.confirmColor)
    Button confirmColorBtn;
    @BindView(R.id.addColor)
    Button addColorBtn;
    @BindView(R.id.custSettingsSpinner)
    Spinner custFieldSpinner;
    @BindView(R.id.removeTypeSettingsSpinner)
    Spinner removeFoodSpinner;
    @BindView(R.id.typeFoodSettingsEdt)
    EditText editFoodEdt;


    private String user;
    private ArrayAdapter<String> foodTypesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        ((AppCompatActivity) this).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) this).getSupportActionBar().setDisplayShowHomeEnabled(true);

        user = selectUser();
        getFoodTypes();
        getCustomFields();
        typeFoodSpinner = findViewById(R.id.typeFoodSettingsSpinner);
        foodTypesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, typeFoods);
        typeFoodSpinner.setAdapter(foodTypesAdapter);
        custFieldSpinner = findViewById(R.id.custSettingsSpinner);
        ArrayAdapter<String> custFieldsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, custFields);
        custFieldSpinner.setAdapter(custFieldsAdapter);
        removeFoodSpinner = findViewById(R.id.removeTypeSettingsSpinner);
//        ArrayAdapter<String> removeFoodAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, typeFoods);
        removeFoodSpinner.setAdapter(foodTypesAdapter);
        editFoodEdt = findViewById(R.id.typeFoodSettingsEdt);
        addColorBtn = findViewById(R.id.addColor);
        addColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertColorCusotm(editFoodEdt.getText().toString(),"type","blue",user);
                getFoodTypes();
                foodTypesAdapter = new ArrayAdapter<String>(SettingsActivity.this, android.R.layout.simple_spinner_item, typeFoods);
                typeFoodSpinner.setAdapter(foodTypesAdapter);
                removeFoodSpinner.setAdapter(foodTypesAdapter);
//                foodTypesAdapter.notifyDataSetChanged();
                editFoodEdt.setText("");
                Toast.makeText(getApplicationContext(),"Food type was added", Toast.LENGTH_SHORT).show();
            }
        });


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

    public String selectUser() {
        Cursor c = this.getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_USERS,
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

    private void getFoodTypes(){
        try {
            Cursor c = this.getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_FIELDS,
                    new String[]{"DISTINCT " + DbColumns.MealsEntry.NAME_FLD, DbColumns.MealsEntry.COLOR},
                    DbColumns.MealsEntry.TYPE_FLD + "= 'type' AND " + DbColumns.MealsEntry.FIELDS_USR + " in ('default', '" + user + "')" ,
                    null,
                    null);
            if (c.getCount() != 0) {
                typeFoods = new String[c.getCount()];
                int i = 0;
                if (c.moveToFirst()) {
                    do {
                        typeFoods[i] = c.getString(c.getColumnIndex(DbColumns.MealsEntry.NAME_FLD));
                        i++;
                    } while (c.moveToNext());
                }
            }
        } catch (Exception ex) {
            Log.e("ADDMEAL", "RISO EX: " + ex);
        }

    }

    private void getCustomFields(){
        try {
            Cursor c = this.getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_FIELDS,
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

    }

    private void insertColorCusotm (String name, String type, String color, String email){
        ContentValues cv = new ContentValues();
        cv.put(DbColumns.MealsEntry.NAME_FLD, name);
        cv.put(DbColumns.MealsEntry.TYPE_FLD, type);
        cv.put(DbColumns.MealsEntry.COLOR, color);
        cv.put(DbColumns.MealsEntry.FIELDS_USR, email);
        this.getContentResolver().insert(DbColumns.MealsEntry.CONTENT_URI_FIELDS, cv);
    }
}
