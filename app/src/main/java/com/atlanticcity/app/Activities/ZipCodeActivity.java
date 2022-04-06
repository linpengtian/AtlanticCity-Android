package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;


import org.json.JSONObject;

import java.util.Map;

public class ZipCodeActivity extends AppCompatActivity {

    EditText zip_code;
    Toolbar toolbar;
    TextView skip;
    Button btn_save;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    String ZIP_CODE;
    public String TAG = "ZipCodeActivity";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip_code);
        viewInitializer();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void viewInitializer(){
        zip_code = findViewById(R.id.zip_code);
        toolbar = findViewById(R.id.toolbar);
        skip = findViewById(R.id.skip);
        btn_save = findViewById(R.id.btn_save);
        helper = new ConnectionHelper(ZipCodeActivity.this);
        isInternet = helper.isConnectingToInternet();
        setupToolbar();

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String date_of_birth = SharedHelper.getKey(ZipCodeActivity.this,"date_of_birth");
            if(date_of_birth.equalsIgnoreCase("null")){
                Intent i = new Intent(ZipCodeActivity.this,DateOfBirth.class);
                startActivity(i);
                finish();
            }else {
                if(date_of_birth.equalsIgnoreCase("added") || !date_of_birth.isEmpty()){
                    Intent i = new Intent(ZipCodeActivity.this,DealsFullScreen.class);
                    startActivity(i);
                    finish();
                }else {
                    Intent i = new Intent(ZipCodeActivity.this,DateOfBirth.class);
                    startActivity(i);
                    finish();
                }
            }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            hideKeyboard(ZipCodeActivity.this);
            ZIP_CODE = zip_code.getText().toString().trim();
            if(ZIP_CODE.isEmpty()){
                Toast.makeText(ZipCodeActivity.this, getString(R.string.please_enter_zip_code), Toast.LENGTH_SHORT).show();
            }else if(isInternet){
                AddZipCode();
            }else {
                Toast.makeText(ZipCodeActivity.this, getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
            }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.earn_points_now));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent i = new Intent(ZipCodeActivity.this,DealsFullScreen.class);
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ZipCodeActivity.this,DealsFullScreen.class);
        startActivity(i);
        finish();
    }

    private void AddZipCode() {
        showDialog();
        AndroidNetworking.post(URLHelper.ADD_ZIPCODE)

                .addBodyParameter("zipcode", ZIP_CODE)
                .addHeaders("user-id", SharedHelper.getKey(ZipCodeActivity.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(ZipCodeActivity.this,"auth_id"))
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        dismissDialog();
                        try {

                            JSONObject errorObj = response.getJSONObject("error");
                            if(errorObj.optString("status").equals("1")){
                                displayMessage(errorObj.optString("message"));
                            }else{
                                JSONObject responseObj = response.getJSONObject("response");
                                if(responseObj.optString("status").equals("1")){
                                    Toast.makeText(ZipCodeActivity.this, responseObj.optString("message"), Toast.LENGTH_LONG).show();
                                    SharedHelper.putKey(ZipCodeActivity.this,"zipcode","added");
                                    //   displayMessage(responseObj.optString("message"));

                                    Intent i = new Intent(ZipCodeActivity.this,PointsActivity.class);
                                    i.putExtra("status","zipcode");
                                    i.putExtra("points",responseObj.optString("detail"));
                                    startActivity(i);
                                    finish();
                                }
                            }

                        }catch (Exception ex){
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
                                if(errorJson.optJSONObject("error").optString("status").equals("1")){
                                    displayMessage(errorJson.optJSONObject("error").optString("message"));
                                }
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });

    }

    public void displayMessage(  String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(ZipCodeActivity.this,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    void dismissDialog(){
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();
    }

    void showDialog(){
        customDialog = new CustomDialog(ZipCodeActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }

    public static void hideKeyboard(Activity activity) {
        try{
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
