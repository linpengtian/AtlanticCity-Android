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
import android.widget.EditText;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HelpFeedback extends AppCompatActivity {

    Toolbar toolbar;
    Button btn_send;
    EditText feedback;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    String feedback_text;
    TextView header_text;
    public String TAG = "HelpFeedback";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_feedback);
        toolbar = findViewById(R.id.toolbar);
        btn_send = findViewById(R.id.btn_send);
        feedback = findViewById(R.id.feedback);
        header_text = findViewById(R.id.header_text);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper = new ConnectionHelper(HelpFeedback.this);
                isInternet = helper.isConnectingToInternet();
                feedback_text = feedback.getText().toString();

                if(isInternet){

                    if(feedback_text.isEmpty()){
                        Toast.makeText(HelpFeedback.this, HelpFeedback.this.getString(R.string.please_enter_feedback), Toast.LENGTH_SHORT).show();
                    }  else {
                        SendFeedback();
                    }

                }else {
                    Toast.makeText(HelpFeedback.this, HelpFeedback.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
                }
            }
        });
        setupToolbar();

        getData();
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.help_feedback));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent i = new Intent(HelpFeedback.this,DealsFullScreen.class);
                startActivity(i);
                finish();
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
                Toast.makeText(HelpFeedback.this,""+toastString,Toast.LENGTH_SHORT).show();
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
        customDialog = new CustomDialog(HelpFeedback.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }



    private void SendFeedback() {
        showDialog();
        AndroidNetworking.post(URLHelper.SEND_FEEDBACK)

                .addBodyParameter("feedback", feedback_text)
                .addHeaders("user-id", SharedHelper.getKey(HelpFeedback.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(HelpFeedback.this,"auth_id"))
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

                                    Toast.makeText(HelpFeedback.this, responseObj.optString("message"), Toast.LENGTH_SHORT).show();
                                    // displayMessage(responseObj.optString("message"));
                                    Intent i = new Intent(HelpFeedback.this,DealsFullScreen.class);
                                    startActivity(i);
                                    finish();

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


    private void getData( ) {
        showDialog();
        AndroidNetworking.get(URLHelper.GET_DATA)
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {

                            JSONObject res = response.getJSONObject("response");
                            JSONArray detail = res.getJSONArray("detail");

                            for(int i = 0 ; i <detail.length();i++){
                                if(detail.getJSONObject(i).optString("key").equalsIgnoreCase("help_text")){
                                    header_text.setText(detail.getJSONObject(i).optString("value"));
                                }
                            }

                        }catch (JSONException ex){
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
