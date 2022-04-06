package com.atlanticcity.app.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.atlanticcity.app.Adapters.AutoCompleteAdapter;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.PlacePredictions;
import com.atlanticcity.app.Models.UserModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ClaimDealActivity extends AppCompatActivity  /*implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,*//* LocationListener*/ {

    Toolbar toolbar;
    EditText et_first_name, et_last_name, et_phone, et_email, et_address;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    List<UserModel> userModels = new ArrayList<>();
    Button btn_claim_deal;
    RelativeLayout search_parent_view;
    TextView noresult, tv_skip;
    private ListView mAutoCompleteList;
    RequestQueue mRequestQueue;
    private Handler handler;
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private PlacePredictions predictions = new PlacePredictions();
    int kilometer = 10;
    double latitude;
    int value;
    double longitude;
    PlacesClient placesClient;
    String Latitude, Longitude;
    LocationManager manager;
    LinearLayout main_layout;
    LocationRequest mLocationRequest;
     // GoogleApiClient mGoogleApiClient;
    String country_code;
    public static final int REQUEST_LOCATION = 1450;
    RelativeLayout current_address_layout;
    TextView adress_curr, adress_curr_details;
    public String TAG = "ClaimDealActivity";
    List<Address> address;
    boolean is_clicked = false;
    TextView tvHide;
    String isDiningGuru = "false";
    int REQUEST_CHECK_SETTINGS = 987;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_deal);
        toolbar = findViewById(R.id.toolbar);
        et_first_name = findViewById(R.id.et_first_name);
        //   et_first_name.setAutofillHints(View.AUTOFILL_HINT_NAME);
        et_last_name = findViewById(R.id.et_last_name);
        et_phone = findViewById(R.id.et_phone);
        et_email = findViewById(R.id.et_email);
        et_address = findViewById(R.id.et_address);
        //    et_address.setAutofillHints(View.AUTOFILL_HINT_POSTAL_ADDRESS);
        btn_claim_deal = findViewById(R.id.btn_claim_deal);
        tv_skip = findViewById(R.id.tv_skip);
        search_parent_view = findViewById(R.id.search_parent_view);
        main_layout = findViewById(R.id.main_layout);
        adress_curr = findViewById(R.id.adress_curr);
        adress_curr_details = findViewById(R.id.adress_curr_details);
        tvHide = findViewById(R.id.tvHide);
        //  search_parent_view.setVisibility(View.GONE);
        noresult = findViewById(R.id.noresult);
        mAutoCompleteList = findViewById(R.id.searchResultLV);
        current_address_layout = findViewById(R.id.current_address_layout);
        mRequestQueue = Volley.newRequestQueue(this);

        try {
            Intent i = getIntent();
            isDiningGuru = i.getStringExtra("is_dining");
        } catch (Exception ex) {
            isDiningGuru = "false";
            ex.printStackTrace();
        }

        autoCompleteGoogleSearch(et_address);

        Places.initialize(ClaimDealActivity.this, getString(R.string.google_maps_key));
        placesClient = Places.createClient(ClaimDealActivity.this);
        mAutoCompleteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et_address.clearFocus();
                search_parent_view.setVisibility(View.GONE);
                is_clicked = true;
                setGoogleAddress(position);
                hideKeyboard(ClaimDealActivity.this);
            }
        });
        helper = new ConnectionHelper(ClaimDealActivity.this);
        isInternet = helper.isConnectingToInternet();

        setupToolbar();

        tvHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_parent_view.setVisibility(View.GONE);
            }
        });

        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ClaimDealActivity.this, DealsFullScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        current_address_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    is_clicked = true;
                    et_address.setText(address.get(0).getAddressLine(0));
                    search_parent_view.setVisibility(View.GONE);
                    current_address_layout.setVisibility(View.GONE);
                    et_address.clearFocus();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        btn_claim_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String first_name = et_first_name.getText().toString().trim();
                String last_name = et_last_name.getText().toString().trim();
                String phone_no = et_phone.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                String address = et_address.getText().toString().trim();

                if (isEmpty(first_name)) {
                    displayMessage("Please enter your first name");
                } else if (isEmpty(last_name)) {
                    displayMessage("Please enter your last name");
                } else if (isEmpty(phone_no)) {
                    displayMessage(getResources().getString(R.string.phone_validation));
                } else if (isEmpty(address)) {
                    displayMessage("Please enter your address");
                } else {
                    if (isDiningGuru == null) {
                        // Toast.makeText(ClaimDealActivity.this, "Diningguru", Toast.LENGTH_SHORT).show();
                        UpDateUser();

                    } else if (isDiningGuru.equalsIgnoreCase("true")) {
                        ClaimDiningGuru();
                    }

                }

            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ClaimDealActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }else {
            statusCheck();
            MapsInitializer.initialize(ClaimDealActivity.this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.get_prize));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent i = new Intent(ClaimDealActivity.this, DealsFullScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void dismissDialog() {
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();
    }

    void showDialog() {
        if (customDialog != null) {
            customDialog.show();
        } else {
            customDialog = new CustomDialog(ClaimDealActivity.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
        }
    }

    private void getProfile() {
        showDialog();
        AndroidNetworking.post(URLHelper.GER_PROFILE)

                .addBodyParameter("user-id", SharedHelper.getKey(ClaimDealActivity.this, "user_id"))
                .addHeaders("user-id", SharedHelper.getKey(ClaimDealActivity.this, "user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(ClaimDealActivity.this, "auth_id"))
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            JSONObject errorObj = response.getJSONObject("error");
                            if (errorObj.optString("status").equals("1")) {
                                displayMessage(errorObj.optString("message"));
                            } else {
                                JSONObject responseObj = response.getJSONObject("response");

                                if (responseObj.optString("status").equals("1")) {

                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<UserModel>() {
                                    }.getType();
                                    UserModel userModel = gson.fromJson(responseObj.getString("detail"), listType);

                                    et_first_name.setText(userModel.getFirst_name());
                                    et_last_name.setText(userModel.getLast_name());
                                    et_phone.setText(userModel.getPhoneno());
                                    et_email.setText(userModel.getEmail());

                                    if (userModel.getFirst_name() == null || userModel.getFirst_name().equalsIgnoreCase("")) {
                                        et_first_name.setEnabled(true);
                                    } else {
                                        et_first_name.setEnabled(false);
                                    }

                                    if (userModel.getLast_name() == null || userModel.getLast_name().equalsIgnoreCase("")) {
                                        et_last_name.setEnabled(true);
                                    } else {
                                        et_last_name.setEnabled(false);
                                    }

                                    if (userModel.getPhoneno() == null || userModel.getPhoneno().equalsIgnoreCase("")) {
                                        et_phone.setEnabled(true);
                                    } else {
                                        et_phone.setEnabled(false);
                                    }

                                    if (userModel.getEmail() == null || userModel.getEmail().equalsIgnoreCase("")) {
                                        et_email.setEnabled(true);
                                    } else {
                                        et_email.setEnabled(false);
                                    }

                                    main_layout.setVisibility(View.VISIBLE);
                                    et_address.requestFocus();

                                }
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        dismissDialog();

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        dismissDialog();

                        if (error.getErrorCode() != 0) {
                            // received error from server
                            // error.getErrorCode() - the error code from server
                            // error.getErrorBody() - the error body from server
                            // error.getErrorDetail() - just an error detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                            // get parsed error object (If ApiError is your class)
                            String errorObj = error.getErrorBody();
                            try {
                                JSONObject errorJson = new JSONObject(errorObj);
                                if (errorJson.optJSONObject("error").optString("status").equals("1")) {
                                    displayMessage(errorJson.optJSONObject("error").optString("message"));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });

    }

    public void displayMessage(String toastString) {
        try {
            Toast.makeText(ClaimDealActivity.this, "" + toastString, Toast.LENGTH_SHORT).show();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void UpDateUser() {

        showDialog();
        AndroidNetworking.post(URLHelper.UPDATE_PROFILE)
                .addHeaders("user-id", SharedHelper.getKey(ClaimDealActivity.this, "user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(ClaimDealActivity.this, "auth_id"))
                .addBodyParameter("email", et_email.getText().toString().trim())
                .addBodyParameter("address", et_address.getText().toString().trim())
                .addBodyParameter("phoneno", et_phone.getText().toString().trim())
                .addBodyParameter("first_name", et_first_name.getText().toString().trim())
                .addBodyParameter("last_name", et_last_name.getText().toString().trim())
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {


                            JSONObject errorObj = response.getJSONObject("error");
                            if (errorObj.optString("status").equals("1")) {
                                displayMessage(errorObj.optString("message"));
                            } else {
                                JSONObject responseObj = response.getJSONObject("response");
                                if (responseObj.optString("status").equals("1")) {
                                    //   Toast.makeText(ClaimDealActivity.this, responseObj.optString("message"), Toast.LENGTH_LONG).show();
                                    // Toast.makeText(ClaimDealActivity.this, "Deal claimed successfully, We will contact you soon. Thank you!", Toast.LENGTH_LONG).show();
                                    JSONObject detail = responseObj.getJSONObject("detail");
                                    SharedHelper.putKey(ClaimDealActivity.this, "first_name", detail.optString("first_name"));
                                    SharedHelper.putKey(ClaimDealActivity.this, "last_name", detail.optString("last_name"));
                                    dismissDialog();
                                    showSuccessDialog("Gift claimed successfully", "We will contact you soon, thank you!");

                                }
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        dismissDialog();
                        if (error.getErrorCode() != 0) {
                            // received error from server
                            // error.getErrorCode() - the error code from server
                            // error.getErrorBody() - the error body from server
                            // error.getErrorDetail() - just an error detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                            // get parsed error object (If ApiError is your class)
                            String errorObj = error.getErrorBody();
                            try {
                                JSONObject errorJson = new JSONObject(errorObj);
                                if (errorJson.optJSONObject("error").optString("status").equals("1")) {
                                    displayMessage(errorJson.optJSONObject("error").optString("message"));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });

    }

    private void ClaimDiningGuru() {

        showDialog();
        AndroidNetworking.post(URLHelper.CLAIM_DINING_GURU)
                .addHeaders("user-id", SharedHelper.getKey(ClaimDealActivity.this, "user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(ClaimDealActivity.this, "auth_id"))
                .addBodyParameter("email", et_email.getText().toString().trim())
                .addBodyParameter("address", et_address.getText().toString().trim())
                .addBodyParameter("phoneno", et_phone.getText().toString().trim())
                .addBodyParameter("first_name", et_first_name.getText().toString().trim())
                .addBodyParameter("last_name", et_last_name.getText().toString().trim())
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            JSONObject errorObj = response.getJSONObject("error");
                            if (errorObj.optString("status").equals("1")) {
                                displayMessage(errorObj.optString("message"));
                            } else {
                                JSONObject responseObj = response.getJSONObject("response");
                                if (responseObj.optString("status").equals("1")) {
                                    //   Toast.makeText(ClaimDealActivity.this, responseObj.optString("message"), Toast.LENGTH_LONG).show();
                                    // Toast.makeText(ClaimDealActivity.this, "Deal claimed successfully, We will contact you soon. Thank you!", Toast.LENGTH_LONG).show();
                                    JSONObject detail = responseObj.getJSONObject("detail");
                                    SharedHelper.putKey(ClaimDealActivity.this, "first_name", detail.optString("first_name"));
                                    SharedHelper.putKey(ClaimDealActivity.this, "last_name", detail.optString("last_name"));

                                    dismissDialog();
                                    showSuccessDialog("Prize claimed successfully", "We will contact you soon, thank you!");

                                }
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error

                        dismissDialog();


                        if (error.getErrorCode() != 0) {
                            // received error from server
                            // error.getErrorCode() - the error code from server
                            // error.getErrorBody() - the error body from server
                            // error.getErrorDetail() - just an error detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                            // get parsed error object (If ApiError is your class)
                            String errorObj = error.getErrorBody();
                            try {
                                JSONObject errorJson = new JSONObject(errorObj);
                                if (errorJson.optJSONObject("error").optString("status").equals("1")) {
                                    displayMessage(errorJson.optJSONObject("error").optString("message"));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });

    }

    boolean isEmpty(String str) {
        if (str.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private void autoCompleteGoogleSearch(final EditText google_seach_txt) {

        google_seach_txt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        google_seach_txt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.

                        return true; // consume.
                    }
                }
                return false; // pass on to other listeners.
            }
        });

        google_seach_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!google_seach_txt.hasFocus() && is_clicked) {
                    return;
                }

                if (i2 > 0 && is_clicked) {
                    return;
                }

                // optimised way is to start searching for laction after user has typed minimum 3 chars
                if (google_seach_txt.getText().length() > 0) {
                    is_clicked = false;
                    //  google_seach_txt.setText(charSequence);
                    noresult.setVisibility(View.INVISIBLE);
                    Runnable run = new Runnable() {

                        @Override
                        public void run() {

                            JSONObject object = new JSONObject();
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(google_seach_txt.getText().toString()),
                                    object, new com.android.volley.Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    if (google_seach_txt.getText().length() <= 0) {
                                        search_parent_view.setVisibility(View.GONE);
                                        return;
                                    }
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Log.v("PayNowRequestResponse", response.toString());

                                    if (mAutoCompleteAdapter == null)

                                        search_parent_view.setVisibility(View.VISIBLE);
                                    mAutoCompleteList.setVisibility(View.VISIBLE);

                                    Gson gson = new Gson();
                                    predictions = gson.fromJson(response.toString(), PlacePredictions.class);

                                    if (predictions.getPlaces().size() <= 0) {
                                        noresult.setVisibility(View.VISIBLE);
                                    } else {
                                        noresult.setVisibility(View.INVISIBLE);
                                    }

                                    if (mAutoCompleteAdapter == null) {
                                        mAutoCompleteAdapter = new AutoCompleteAdapter(ClaimDealActivity.this, predictions.getPlaces(), ClaimDealActivity.this);
                                        mAutoCompleteList.setAdapter(mAutoCompleteAdapter);
                                    } else {
                                        search_parent_view.setVisibility(View.VISIBLE);
                                        mAutoCompleteList.setVisibility(View.VISIBLE);
                                        mAutoCompleteAdapter.clear();
                                        mAutoCompleteAdapter.addAll(predictions.getPlaces());
                                        mAutoCompleteAdapter.notifyDataSetChanged();
                                        mAutoCompleteList.invalidate();
                                    }
                                }
                            }, new com.android.volley.Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.v("PayNowRequestResponse", error.toString());
                                }
                            });
                            mRequestQueue.add(jsonObjectRequest);

                        }

                    };
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                        if (predictions.getPlaces() != null && predictions.getPlaces().size() == 0) {
                            search_parent_view.setVisibility(View.GONE);
                        }

                    } else {
                        handler = new Handler();
                    }
                    handler.postDelayed(run, 1000);

                } else {

                    search_parent_view.setVisibility(View.GONE);
                    mAutoCompleteList.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private String getPlaceAutoCompleteUrl(String input) {
        int meters = kilometer * 100;
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/autocomplete/json");
        urlString.append("?input=");
        try {
            urlString.append(URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&location=");
        urlString.append(latitude + "," + longitude); // append lat long of current location to show nearby results.
        if (country_code.equalsIgnoreCase("") || country_code.isEmpty() || country_code == null) {
            urlString.append("&radius=" + meters + "&&components=country:us&language=en");
        } else {
            urlString.append("&radius=" + meters + "&&components=country:" + country_code + "&language=en");
        }

        //urlString.append("&language=en");
        urlString.append("&key=" + getResources().getString(R.string.google_maps_key));

        Log.d("FINAL URL:::   ", urlString.toString());
        return urlString.toString();
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setGoogleAddress(int position) {

        Log.v("PLACEID", "Place ID == >" + predictions.getPlaces().get(position).getPlaceID());
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS);
        FetchPlaceRequest request = FetchPlaceRequest.builder(predictions.getPlaces().get(position).getPlaceID(), placeFields)
                .build();
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse response) {

                try {
                    Place myPlace = response.getPlace();
                    LatLng queriedLocation = myPlace.getLatLng();
                    Latitude = String.valueOf(queriedLocation.latitude);
                    Longitude = String.valueOf(queriedLocation.longitude);

                    et_address.setText(myPlace.getAddress());
                    hideKeyboard(ClaimDealActivity.this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mAutoCompleteList.setVisibility(View.GONE);

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();

                        if (ContextCompat.checkSelfPermission(ClaimDealActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            //   buildGoogleApiClient();
                            statusCheck();


                            MapsInitializer.initialize(ClaimDealActivity.this);

                        }
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                }
                break;


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void statusCheck() {
        manager = (LocationManager) ClaimDealActivity.this.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //  enableLoc();
            createLocationRequest();
        }else {
            requestLocationGetData();
        }
    }


    protected void createLocationRequest() {

        showDialog();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                dismissDialog();
                Log.v("response_location", locationSettingsResponse.toString());
              //  Toast.makeText(ClaimDealActivity.this, "location enabled", Toast.LENGTH_SHORT).show();

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    dismissDialog();
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(ClaimDealActivity.this,
                                REQUEST_CHECK_SETTINGS);

                    } catch (IntentSender.SendIntentException sendEx) {

                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_CHECK_SETTINGS)) {
            if (resultCode == Activity.RESULT_OK) {
                showDialog();
                requestLocationGetData();

               // Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    /*private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(ClaimDealActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                        Log.v("Location error","Location error " + connectionResult.getErrorCode());
                    }
                }).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(ClaimDealActivity.this, REQUEST_LOCATION);

                        }catch (NullPointerException | IntentSender.SendIntentException e){
                            e.printStackTrace();
                            try{
                                status.startResolutionForResult(ClaimDealActivity.this, REQUEST_LOCATION);
                            }catch (Exception ee){
                                ee.printStackTrace();
                            }
                        }
                        break;
                }
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        showDialog();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(ClaimDealActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/

/*    @Override
    public void onLocationChanged(Location location) {
        Log.v("Location",location.toString());
        if (value == 0) {
            Log.v("Location",location.toString());

            Geocoder geoCoder = new Geocoder(this, Locale.getDefault()); //it is Geocoder

            latitude = location.getLatitude();
            longitude = location.getLongitude();
        *//* latitude = 26.414990;
         longitude = -80.120610;*//*

            try {

              address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
         //   address  = geoCoder.getFromLocation(26.414990, -80.120610, 1);
                country_code = address.get(0).getCountryCode();
                Log.v(country_code,country_code);

                String[] parts = address.get(0).getAddressLine(0).split(",");
                if(parts.length>0){
                    if(parts.length==1){
                        adress_curr.setText(parts[0]);
                    }else if(parts.length==2){
                        adress_curr.setText(parts[0]+","+parts[1]);
                    }else {
                        adress_curr.setText(parts[0]+","+parts[1]);
                    }

                }
                adress_curr_details.setText(address.get(0).getAdminArea()  + ", "+address.get(0).getCountryName());


                //  adress_curr_details.setText(address.get(0).getFeatureName() +  ", " + address.get(0).getSubAdminArea() +", "+address.get(0).getAdminArea() + ", "+address.get(0).getCountryName());

             //   String finalAddress = address.get(0).getAddressLine(0); //This is the complete address.
              //  adress_curr.setText(address.get(0).getAddressLine(0).);
              //  et_address.setText(finalAddress);
            } catch (IOException e) {
                    current_address_layout.setVisibility(View.GONE);
            }
                catch (NullPointerException e) {

                }
            if(isInternet){
                getProfile();
            }else {
                Toast.makeText(ClaimDealActivity.this, ClaimDealActivity.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
            }
            value++;
        }
    }*/

/*    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(ClaimDealActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }*/

    private void showSuccessDialog(String title, String message) {
        if (!isFinishing()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater)ClaimDealActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
              builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                Intent i = new Intent(ClaimDealActivity.this,DealsFullScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                    //   ShowClaimDealForm();
                }
            });

            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ClaimDealActivity.this, R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }


    void requestLocationGetData(){

        showDialog();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                List<Location> locationList = locationResult.getLocations();
                if(value == 0){
                    if(locationList.size()>0){
                        Geocoder geoCoder = new Geocoder(ClaimDealActivity.this, Locale.getDefault()); //it is Geocoder

                        latitude = locationList.get(0).getLatitude();
                        longitude = locationList.get(0).getLongitude();
        /* latitude = 26.414990;
         longitude = -80.120610;*/

                        try {

                            address = geoCoder.getFromLocation(latitude, longitude, 1);
                            //   address  = geoCoder.getFromLocation(26.414990, -80.120610, 1);
                            country_code = address.get(0).getCountryCode();
                            Log.v(country_code,country_code);

                            String[] parts = address.get(0).getAddressLine(0).split(",");
                            if(parts.length>0){
                                if(parts.length==1){
                                    adress_curr.setText(parts[0]);
                                }else if(parts.length==2){
                                    adress_curr.setText(parts[0]+","+parts[1]);
                                }else {
                                    adress_curr.setText(parts[0]+","+parts[1]);
                                }

                            }
                            adress_curr_details.setText(address.get(0).getAdminArea()  + ", "+address.get(0).getCountryName());

                            dismissDialog();

                            //  adress_curr_details.setText(address.get(0).getFeatureName() +  ", " + address.get(0).getSubAdminArea() +", "+address.get(0).getAdminArea() + ", "+address.get(0).getCountryName());

                            //   String finalAddress = address.get(0).getAddressLine(0); //This is the complete address.
                            //  adress_curr.setText(address.get(0).getAddressLine(0).);
                            //  et_address.setText(finalAddress);
                        } catch (IOException e) {
                            current_address_layout.setVisibility(View.GONE);
                        }
                        catch (NullPointerException e) {

                        }
                        if(isInternet){
                            getProfile();
                        }else {
                            Toast.makeText(ClaimDealActivity.this, ClaimDealActivity.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
                        }
                        value++;
                    }
                }




                //  onNewLocation(locationResult.getLastLocation());
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, Looper.myLooper());
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mFusedLocationClient!=null && mLocationCallback!=null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }

    }
}
