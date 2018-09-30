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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.riso.android.mealtracker.data.DatabaseQuery;
import com.riso.android.mealtracker.data.DbColumns;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {
    String[] typeFoods;
    String[] custFields;
    @BindView(R.id.typeFoodSettingsSpinner)
    Spinner typeFoodSpinner;
    @BindView(R.id.colorNewFoodSettingsSpinner)
    Spinner colorNewFoodSettingsSpinner;
    @BindView(R.id.colorFoodSettingsSpinner)
    Spinner colorFoodSettingsSpinner;
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
    @BindView(R.id.removeColor)
    Button removeColorBtn;;
    @BindView(R.id.addCustFld)
    Button addCustFldBtn;
    @BindView(R.id.addCustSettingsEdt)
    EditText addCustEdt;
    @BindView(R.id.removeCust)
    Button removeCustBtn;


    private String user;
    private ArrayAdapter<String> foodTypesAdapter;
    private ArrayAdapter<String> custFieldsAdapter;
    private ArrayAdapter<String> colorAdapter;
    private String[] colors = new String[]{"Purple","Deep Purple", "Orange", "Brown", "Cyan", "Yellow", "Pink"};

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
        final DatabaseQuery databaseQuery = new DatabaseQuery(this);
        colorAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, colors);
        colorFoodSettingsSpinner = findViewById(R.id.colorFoodSettingsSpinner);
        colorFoodSettingsSpinner.setAdapter(colorAdapter);
        colorNewFoodSettingsSpinner = findViewById(R.id.colorNewFoodSettingsSpinner);
        colorNewFoodSettingsSpinner.setAdapter(colorAdapter);
        typeFoodSpinner = findViewById(R.id.typeFoodSettingsSpinner);

        foodTypesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, typeFoods);
        typeFoodSpinner.setAdapter(foodTypesAdapter);
        custFieldSpinner = findViewById(R.id.custSettingsSpinner);
        custFieldsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, custFields);
        custFieldSpinner.setAdapter(custFieldsAdapter);
        removeFoodSpinner = findViewById(R.id.removeTypeSettingsSpinner);
//        ArrayAdapter<String> removeFoodAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, typeFoods);
        removeFoodSpinner.setAdapter(foodTypesAdapter);
        confirmColorBtn = findViewById(R.id.confirmColor);
        confirmColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseQuery.updateColor(colorFoodSettingsSpinner.getSelectedItem().toString(), typeFoodSpinner.getSelectedItem().toString());
                Toast.makeText(SettingsActivity.this, "Color was updated", Toast.LENGTH_SHORT).show();
            }
        });

        editFoodEdt = findViewById(R.id.typeFoodSettingsEdt);
        addColorBtn = findViewById(R.id.addColor);
        addColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertColorCusotm(editFoodEdt.getText().toString(),"type",colorNewFoodSettingsSpinner.getSelectedItem().toString(),user);
                getFoodTypes();
                foodTypesAdapter = new ArrayAdapter<String>(SettingsActivity.this, android.R.layout.simple_spinner_item, typeFoods);
                typeFoodSpinner.setAdapter(foodTypesAdapter);
                removeFoodSpinner.setAdapter(foodTypesAdapter);
                editFoodEdt.setText("");
                Toast.makeText(getApplicationContext(),"Food type was added", Toast.LENGTH_SHORT).show();
            }
        });
        removeColorBtn = findViewById(R.id.removeColor);
//        removeColorBtn.setEnabled(!isRemoveTypeDefault(removeFoodSpinner));
//        removeFoodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                removeColorBtn.setEnabled(!isRemoveTypeDefault(removeFoodSpinner));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        if (removeColorBtn.isEnabled()) {
            removeColorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (custFieldSpinner.getSelectedItem().toString().equals("Add field")){
                    }else {
                        removeFoodType(removeFoodSpinner.getSelectedItem().toString(), "type");
                        getFoodTypes();
                        if (typeFoods.length == 0) {
                            typeFoods = new String[1];
                            typeFoods[0] = "Add field";
                        }
                        foodTypesAdapter = new ArrayAdapter<String>(SettingsActivity.this, android.R.layout.simple_spinner_item, typeFoods);
                        typeFoodSpinner.setAdapter(foodTypesAdapter);
                        removeFoodSpinner.setAdapter(foodTypesAdapter);
                        Toast.makeText(getApplicationContext(), "Food type was removed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
//        } else {
//            removeColorBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(), "You can't remove default value", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }

        addCustEdt = findViewById(R.id.addCustSettingsEdt);
        addCustFldBtn = findViewById(R.id.addCustFld);
        addCustFldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertColorCusotm(addCustEdt.getText().toString(),"custom","", user);
                getCustomFields();
                custFieldsAdapter = new ArrayAdapter<String>(SettingsActivity.this,
                        android.R.layout.simple_spinner_item, custFields);
                custFieldSpinner.setAdapter(custFieldsAdapter);
                addCustEdt.setText("");
                Toast.makeText(getApplicationContext(),"Custom field was added", Toast.LENGTH_SHORT).show();
            }
        });
        removeCustBtn = findViewById(R.id.removeCust);
//        removeCustBtn.setEnabled(!isRemoveCusotmDefault(custFieldSpinner));
//        custFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                removeCustBtn.setEnabled(!isRemoveCusotmDefault(custFieldSpinner));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        if (removeCustBtn.isEnabled()) {
            removeCustBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (custFieldSpinner.getSelectedItem().toString().equals("Add field")){
                    }else{
                        removeFoodType(custFieldSpinner.getSelectedItem().toString(), "custom");
                        getCustomFields();
                        if (custFields.length == 0) {
                            custFields = new String[1];
                            custFields[0] = "Add field";
                        }
                        custFieldsAdapter = new ArrayAdapter<String>(SettingsActivity.this,
                                android.R.layout.simple_spinner_item, custFields);
                        custFieldSpinner.setAdapter(custFieldsAdapter);
                        Toast.makeText(getApplicationContext(), "Custom field was removed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
//        } else {
//            removeCustBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(), "You can't remove default value", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }

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
                    DbColumns.MealsEntry.TYPE_FLD + "= 'type' AND " + DbColumns.MealsEntry.FIELDS_USR + " in ('" + user + "')" ,
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
            } else {
                typeFoods = new String[1];
                typeFoods[0] = "Add field";
            }
        } catch (Exception ex) {
            Log.e("ADDMEAL", "RISO EX: " + ex);
        }

    }

    private void getCustomFields(){
        try {
            Cursor c = this.getContentResolver().query(DbColumns.MealsEntry.CONTENT_URI_FIELDS,
                    new String[]{"DISTINCT " + DbColumns.MealsEntry.NAME_FLD},
                    DbColumns.MealsEntry.TYPE_FLD + "= 'custom' AND " + DbColumns.MealsEntry.FIELDS_USR + " in ('" + user + "')",
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
            } else {
                custFields = new String[1];
                custFields[0] = "Add field";
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

    private void removeFoodType(String color, String type){
        getContentResolver().delete(DbColumns.MealsEntry.CONTENT_URI_FIELDS, DbColumns.MealsEntry.NAME_FLD + "= ? AND " +
                DbColumns.MealsEntry.TYPE_FLD + "= ? ", new String[]{color, type});
    }

}
