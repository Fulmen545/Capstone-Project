package com.riso.android.mealtracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.riso.android.mealtracker.data.DbColumns;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginFragment extends Fragment {
    private static final int RC_SIGN_IN = 1;
    private static final String TOKEN = "token";
    private static final String LOGIN = "login";
    private static final String SIGNIN = "SignIn";


    @BindView(R.id.signIn)
    Button signInBtn;

    GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        signInBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {
        if (isDeviceOnline()) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.no_internet);
            builder.setMessage(R.string.sign_msg);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String token = account.getId();
            String first = account.getGivenName();
            String last = account.getEmail();
            // Signed in successfully, show authenticated UI.
//            updateUI(account);
            try {
                insertUser(account.getGivenName(), account.getEmail(), account.getId());
            } catch (Exception ex){
                Log.e(LOGIN, ex.toString());
            }
            Toast.makeText(getContext(),getString(R.string.welcome) + account.getGivenName(), Toast.LENGTH_LONG).show();
            Bundle bundle = new Bundle();
            bundle.putString(TOKEN, token);
            MainFragment mf = new MainFragment();
            mf.setArguments(bundle);
            changeTo(mf, android.R.id.content, "tag1");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(SIGNIN,  e.toString());
//            updateUI(null);
        }
    }

    public void changeTo(Fragment fragment, int containerViewId, String tag) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().replace(containerViewId, fragment, tag == null ? fragment.getClass().getName() : tag).commit();

    }

    public void insertUser(String firstName, String email, String token) {
        ContentValues cv = new ContentValues();
        cv.put(DbColumns.MealsEntry.FIRST, firstName);
        cv.put(DbColumns.MealsEntry.EMAIL, email);
        cv.put(DbColumns.MealsEntry.TOKEN, token);
        cv.put(DbColumns.MealsEntry.USER, "true");
        getContext().getContentResolver().insert(DbColumns.MealsEntry.CONTENT_URI_USERS, cv);
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
