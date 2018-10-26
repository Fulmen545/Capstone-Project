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
    String id;

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
        ButterKnife.bind(this, view);
        bundle = this.getArguments();
        getActivity().setTitle(bundle.getString(TYPE));
        detailDate.setText(bundle.getString(DATE));
        detailTIme.setText(bundle.getString(TIME));
        detailLocation.setText(bundle.getString(LOCATION));
        detailDescription.setText(bundle.getString(DESCRIPTION));
        id = bundle.getString(ID);

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

//        DatabaseQuery databaseQuery = new DatabaseQuery(getContext());
//        custFields = databaseQuery.getCustomFields(bundle.getString(USER));
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

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!id.equals("x")){
            inflater.inflate(R.menu.menu_detail, menu);
        }
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
