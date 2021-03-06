package com.riso.android.mealtracker;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
//    @BindView(R.id.passwordEditText)
    EditText passField;
//    @BindView(R.id.emailEditText)
    EditText emailField;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        LoginFragment lf = new LoginFragment();
        MainFragment mf = new MainFragment();
        if (savedInstanceState == null) {
            if (isSignedIn()) {
                // signed in. Show the "sign out" button and explanation.
                ft.add(android.R.id.content, mf).commit();
            } else {
                // not signed in. Show the "sign in" button and explanation.
                ft.add(android.R.id.content, lf).commit();
            }
        }

//        setContentView(R.layout.activity_main);
//        passField= (EditText)findViewById(R.id.passwordEditText);
//        emailField= findViewById(R.id.emailEditText);
//        passField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        emailField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
//        ButterKnife.bind(this, parent);
//        passField= (EditText)findViewById(R.id.passwordEditText);
//        emailField= findViewById(R.id.passwordEditText);
//        passField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        emailField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }
}
