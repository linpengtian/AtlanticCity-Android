package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
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

public class Settings extends AppCompatActivity {

    Toolbar toolbar;
    RelativeLayout profile_section,change_password_layout;
    Switch notif_switch;
    String strNotifStatus;
    Button save;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    Boolean checkChanged = false;
    public String TAG = "Settings";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = findViewById(R.id.toolbar);
        profile_section = findViewById(R.id.profile_section);
        notif_switch = findViewById(R.id.notif_switch);
        save = findViewById(R.id.save);
        change_password_layout = findViewById(R.id.change_password_layout);
        helper = new ConnectionHelper(Settings.this);
        isInternet = helper.isConnectingToInternet();

        strNotifStatus = SharedHelper.getKey(Settings.this,"notification");
        if(strNotifStatus.equals("") || strNotifStatus.equals("null") || strNotifStatus == null || strNotifStatus.equals("1")){
            notif_switch.setChecked(true);
        }else {
            notif_switch.setChecked(false);
        }

        profile_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(Settings.this,Profile.class);
            startActivity(i);
            }
        });

        change_password_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Settings.this,ChangePassword.class);
                startActivity(i);
            }
        });
        setupToolbar();

        notif_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkChanged = true;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInternet){

                    if(checkChanged){
                        NotificationStatus();
                    }

                }else {
                    Toast.makeText(Settings.this, Settings.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.settings));
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

    public void displayMessage(  String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(Settings.this,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    void dismissDialog(){
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss(); }

    void showDialog(){
        customDialog = new CustomDialog(Settings.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }


    private void NotificationStatus() {

        showDialog();
        AndroidNetworking.post(URLHelper.NOTIFICATION_STATUS)

                .addBodyParameter("user-id", SharedHelper.getKey(Settings.this,"user_id"))
                .addHeaders("user-id", SharedHelper.getKey(Settings.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(Settings.this,"auth_id"))
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {

                            JSONObject errorObj = response.getJSONObject("error");
                            if(errorObj.optString("status").equals("1")){
                                displayMessage(errorObj.optString("message"));
                            }else{
                                JSONObject responseObj = response.getJSONObject("response");


                                if(responseObj.optString("status").equals("1")){

                                    Toast.makeText(Settings.this, responseObj.optString("message"), Toast.LENGTH_SHORT).show();
                                    checkChanged = false;
                                    // displayMessage(responseObj.optString("message"));
                                    if(responseObj.optString("message").equalsIgnoreCase("Notification turned on")){

                                        SharedHelper.putKey(Settings.this,"notification","1");
                                        notif_switch.setChecked(true);

                                    }else if(responseObj.optString("message").equalsIgnoreCase("Notification turned off")){
                                        SharedHelper.putKey(Settings.this,"notification","0");
                                        notif_switch.setChecked(false);

                                    }

                                }
                            }

                        }catch (Exception ex){
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
