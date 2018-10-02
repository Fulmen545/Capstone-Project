package com.riso.android.mealtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import butterknife.ButterKnife;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);
        ((AppCompatActivity) this).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) this).getSupportActionBar().setDisplayShowHomeEnabled(true);
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
}
