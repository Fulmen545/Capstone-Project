package com.riso.android.mealtracker;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.riso.android.mealtracker.data.DatabaseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailMealFragment extends Fragment {
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
    @BindView(R.id.detailDate)
    TextView detailDate;
    @BindView(R.id.detailTIme)
    TextView detailTIme;
    @BindView(R.id.detailLocation)
    TextView detailLocation;
    @BindView(R.id.detailDescription)
    TextView detailDescription;
    @BindView(R.id.detailCustomfields)
    TextView detailCustomfields;
    @BindView(R.id.detailSpinner)
    Spinner detailSpinner;

    JSONObject custJson;
    String[] jsonKeys;
    ArrayAdapter<String> custFieldsAdapter;
    Bundle bundle;

    Boolean custFields = true;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_meal, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(getActivity());
        bundle = this.getArguments();
        getActivity().setTitle(bundle.getString(TYPE));
        detailDate = view.findViewById(R.id.detailDate);
        detailDate.setText(bundle.getString(DATE));
        detailTIme = view.findViewById(R.id.detailTIme);
        detailTIme.setText(bundle.getString(TIME));
        detailLocation = view.findViewById(R.id.detailLocation);
        detailLocation.setText(bundle.getString(LOCATION));
        detailDescription = view.findViewById(R.id.detailDescription);
        detailDescription.setText(bundle.getString(DESCRIPTION));
        detailCustomfields = view.findViewById(R.id.detailCustomfields);
        if (!bundle.getString(CUST_FIELDS).equals("{}")) {
            try {
                custJson = new JSONObject(bundle.getString(CUST_FIELDS));
            } catch (JSONException e) {
                Log.e("DETAIL_FRGMT", "Could not parse malformed JSON: \"" + bundle.getString(CUST_FIELDS) + "\"");

            }

            Iterator<String> keys = custJson.keys();
            int i = 0;
            jsonKeys = new String[custJson.length()];
            while (keys.hasNext()) {
                String key = keys.next();
                jsonKeys[i] = key;
                i++;
            }
        } else {
            jsonKeys = new String[]{"No cust. field"};
            custFields = false;
        }

        DatabaseQuery databaseQuery = new DatabaseQuery(getContext());
//        custFields = databaseQuery.getCustomFields(bundle.getString(USER));
        detailSpinner = view.findViewById(R.id.detailSpinner);
        custFieldsAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, jsonKeys);
        detailSpinner.setAdapter(custFieldsAdapter);
        if (custFields) {
            detailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String editCustomValue = "";
                    try {
                        editCustomValue = custJson.get(jsonKeys[position]).toString();
                    } catch (JSONException ignored) {
                    } finally {
                        detailCustomfields.setText(editCustomValue);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            try {
                detailCustomfields.setText(custJson.get(jsonKeys[0]).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            detailCustomfields.setText("");
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.GET_ACCOUNTS)) {
                int i = 3;
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                EasyPermissions.requestPermissions(
                        this,
                        "This app needs to access your Google account (via Contacts).",
                        REQUEST_PERMISSION_GET_ACCOUNTS,
                        Manifest.permission.GET_ACCOUNTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }


        return view;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_GET_ACCOUNTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.editIcon:
//                Toast.makeText(getContext(),"Settings", Toast.LENGTH_SHORT).show();
                AddMealFragment addMealFragment = new AddMealFragment();
                addMealFragment.setArguments(bundle);
                changeTo(addMealFragment, android.R.id.content, "tag1");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeTo(Fragment fragment, int containerViewId, String tag) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().replace(containerViewId, fragment, tag == null ? fragment.getClass().getName() : tag).commit();

    }

}
