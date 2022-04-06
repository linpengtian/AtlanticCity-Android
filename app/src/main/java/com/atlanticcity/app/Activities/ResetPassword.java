package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import com.atlanticcity.app.Helper.SharedHelper;
import com.chaos.view.PinView;
import com.google.android.material.snackbar.Snackbar;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

import org.json.JSONObject;

public class ResetPassword extends AppCompatActivity {

    Toolbar toolbar;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    Button btn_change_password;
    EditText et_email_address,et_new_password;
    String strEmail,strCode,strNewPassword;

    public String TAG = "ResetPassword";
    PinView pinView;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        viewInitializer();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void viewInitializer(){
        toolbar = findViewById(R.id.toolbar);
        btn_change_password = findViewById(R.id.btn_change_password);
        et_email_address = findViewById(R.id.et_email_address);
        pinView = findViewById(R.id.digits_layout);
        pinView.requestFocus();
        et_new_password = findViewById(R.id.et_new_password);

        try {
            Intent i = getIntent();
            strEmail = i.getExtras().getString("email");
            et_email_address.setText(strEmail);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        helper = new ConnectionHelper(ResetPassword.this);
        isInternet = helper.isConnectingToInternet();
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            hideKeyboard(ResetPassword.this);
            strEmail = et_email_address.getText().toString().trim();
            strCode = pinView.getText().toString();
          //  strCode = code_1.getText().toString().trim()+code_2.getText().toString().trim()+code_3.getText().toString().trim()+code_4.getText().toString().trim();
            strNewPassword = et_new_password.getText().toString();

            if(isEmpty_(strEmail)){
                Toast.makeText(ResetPassword.this, getString(R.string.email_validation), Toast.LENGTH_SHORT).show();
            }else if(isEmpty_(strCode)){
                displayMessage(getString(R.string.please_enter_code));
            }else if(password_validator(strNewPassword)){
                displayMessage(getString(R.string.password_validation));
            }else if(isInternet){
                ResetPassword();
            }else {
                Toast.makeText(ResetPassword.this, getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
            }

            }
        });

        setupToolbar();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.reset_password));
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
        customDialog = new CustomDialog(ResetPassword.this);
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
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(ResetPassword.this,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    private void ResetPassword() {
        showDialog();
        AndroidNetworking.post(URLHelper.RESET_PASSWORD)

                .addBodyParameter("email", strEmail)
                .addBodyParameter("code", strCode)
                .addBodyParameter("new_password", strNewPassword)

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

                                    if(responseObj.optString("message").equalsIgnoreCase("Password changed successfully")){
                                        JSONObject detail = responseObj.getJSONObject("detail");
                                        SharedHelper.putKey(ResetPassword.this,"id",detail.optString("id"));
                                        SharedHelper.putKey(ResetPassword.this,"user_id",detail.optString("user_id"));
                                        SharedHelper.putKey(ResetPassword.this,"auth_id",detail.optString("auth_id"));
                                        SharedHelper.putKey(ResetPassword.this,"email",detail.optString("email"));
                                        SharedHelper.putKey(ResetPassword.this,"logged_in","true");
                                        SharedHelper.putKey(ResetPassword.this,"zipcode",detail.optString("zipcode"));
                                        SharedHelper.putKey(ResetPassword.this,"date_of_birth",detail.optString("date_of_birth"));
                                        SharedHelper.putKey(ResetPassword.this,"notification",detail.optString("notfication"));
                                        SharedHelper.putKey(ResetPassword.this,"share_app_status",detail.optString("share_app_status"));
                                        SharedHelper.putKey(ResetPassword.this,"first_name",detail.optString("first_name"));
                                        SharedHelper.putKey(ResetPassword.this,"last_name",detail.optString("last_name"));

                                        showDialog(responseObj.optString("message"));



                                    }else {
                                        Toast.makeText(ResetPassword.this, responseObj.optString("message"), Toast.LENGTH_LONG).show();

                                    }

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

    boolean isEmpty_(String str){
        if(str.isEmpty()){
            return true;
        }else {
            return false;
        }
    }

    private void showDialog(String message) {
        if (!isFinishing()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater)ResetPassword.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                    Intent i = new Intent(ResetPassword.this,DealsFullScreen.class);
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
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ResetPassword.this, R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }
}
