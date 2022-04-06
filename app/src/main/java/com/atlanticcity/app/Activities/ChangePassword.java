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
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

import org.json.JSONObject;

public class ChangePassword extends AppCompatActivity {

    EditText et_current_password,et_new_password;
    Button btn_change_password;
    Toolbar toolbar;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    String strCurrentPassword,strNewPassword;
    public String TAG = "ChangePassword";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        viewInitializer();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void viewInitializer(){
        et_current_password = findViewById(R.id.et_current_password);
        et_new_password = findViewById(R.id.et_new_password);
        btn_change_password = findViewById(R.id.btn_change_password);
        toolbar = findViewById(R.id.toolbar);
        helper = new ConnectionHelper(ChangePassword.this);
        isInternet = helper.isConnectingToInternet();
        setupToolbar();

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            strCurrentPassword = et_current_password.getText().toString().trim();
            strNewPassword = et_new_password.getText().toString().trim();

            if(password_validator(strNewPassword)){
                displayMessage(getString(R.string.password_validation));
            }else if(password_validator(strCurrentPassword)){
                displayMessage(getString(R.string.password_validation));
            }else if(isInternet){
                ChangePassword();
            }else {
                Toast.makeText(ChangePassword.this, getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
            }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.change_password));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void dismissDialog(){
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();
    }

    void showDialog(){
        customDialog = new CustomDialog(ChangePassword.this);
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

    boolean password_validator(String password){
        if(password.isEmpty() || password.length()<6){
            return true;
        }else {
            return false;
        }
    }


    public void displayMessage(  String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(ChangePassword.this,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }


    private void ChangePassword() {
        showDialog();
        AndroidNetworking.post(URLHelper.CHANGE_PASSWORD)

                .addBodyParameter("current_password", strCurrentPassword)
                .addBodyParameter("new_password", strNewPassword)
                .addHeaders("user-id", SharedHelper.getKey(ChangePassword.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(ChangePassword.this,"auth_id"))
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
                                    Toast.makeText(ChangePassword.this, responseObj.optString("message"), Toast.LENGTH_LONG).show();
                                    SharedHelper.putKey(ChangePassword.this,"logged_in","false");
                                    SharedHelper.putKey(ChangePassword.this,"user_id","");
                                    SharedHelper.putKey(ChangePassword.this,"auth_id","");
                                    SharedHelper.putKey(ChangePassword.this,"zipcode","");
                                    SharedHelper.putKey(ChangePassword.this,"date_of_birth","");
                                    Intent goToLogin = new Intent(ChangePassword.this, WelcomeActivity.class);
                                    goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(goToLogin);
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
}
