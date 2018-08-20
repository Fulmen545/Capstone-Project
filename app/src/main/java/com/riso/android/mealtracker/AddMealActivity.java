package com.riso.android.mealtracker;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AddMealActivity extends AppCompatActivity {
    private static final String TOKEN = "token";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putString(TOKEN, intent.getStringExtra(TOKEN));
        AddMealFragment amf = new AddMealFragment();
        amf.setArguments(bundle);
        ft.add(android.R.id.content, amf).commit();
    }
}
