package com.atlanticcity.app.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.atlanticcity.app.Utils.CustomRequestQueue;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CodeVerificationActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView user_number;
    String mVerificationId;
    String phone;
    Button authenticate_account,resend_otp;
   // EditText code_1,code_2,code_3,code_4,code_5,code_6;
    String otp;
    CustomDialog customDialog;
    private FirebaseAuth mAuth;
    ConnectionHelper helper;
    Boolean isInternet;
    TextView tvChangeNumber;
    PhoneAuthProvider.ForceResendingToken resend_token;
    boolean isLast = false;
    PinView pinView;
    int CHANGE_NUMBER = 965;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);
        viewInitializer();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void viewInitializer(){
        toolbar = findViewById(R.id.toolbar);
        user_number = findViewById(R.id.user_number);
        authenticate_account = findViewById(R.id.authenticate_account);
        resend_otp = findViewById(R.id.resend_otp);
        pinView = findViewById(R.id.digits_layout);
        tvChangeNumber = findViewById(R.id.tvChangeNumber);
        pinView.requestFocus();
        helper = new ConnectionHelper(CodeVerificationActivity.this);
        isInternet = helper.isConnectingToInternet();
        mAuth = FirebaseAuth.getInstance();

        tvChangeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent();
            intent.putExtra("authentication_result", "Change your phone number");
            setResult(CHANGE_NUMBER, intent);
            finish();
            }
        });

        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
                resendVerificationCode(phone,resend_token);
            }
        });


     /* code_1 = findViewById(R.id.code_1);
        code_2 = findViewById(R.id.code_2);
        code_3 = findViewById(R.id.code_3);
        code_4 = findViewById(R.id.code_4);
        code_5 = findViewById(R.id.code_5);
        code_6 = findViewById(R.id.code_6);*/

    //    code_1.requestFocus();

/*
        code_1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {

                    code_1.requestFocus();

                }
                return false;
            }
        });


        code_2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    code_1.setText("");
                   code_1.requestFocus();

                }
                return false;
            }
        });

        code_3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    code_2.setText("");
                    code_2.requestFocus();

                }
                return false;
            }
        });


        code_4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    code_3.setText("");
                    code_3.requestFocus();

                }
                return false;
            }
        });


        code_5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    code_4.setText("");
                    code_4.requestFocus();

                }
                return false;
            }
        });


        code_6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {

                    code_5.setText("");
                    code_5.requestFocus();
                    *//*if(!isLast){
                        code_6.setText("");
                        code_6.requestFocus();
                        isLast = true;

                    }else {
                        isLast = false;
                        code_5.requestFocus();
                    }*//*



                }
                return false;
            }
        });







       *//* Code 1*//*
        code_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            if(editable.toString().length()>0){
                code_2.requestFocus();
            }
            }
        });

        *//*Code 2*//*
        code_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0){
                    code_3.requestFocus();
                }
            }
        });


        *//*Code 3*//*
        code_3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0){
                    code_4.requestFocus();
                }
            }
        });


        *//*Code 4*//*
        code_4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0){
                    code_5.requestFocus();
                }
            }
        });

        *//*Code 5*//*
        code_5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0){
                    code_6.requestFocus();
                }
            }
        });

        *//*Code 6*//*
        code_6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0){
                    otp = code_1.getText().toString().trim()+code_2.getText().toString().trim()+code_3.getText().toString().trim()+code_4.getText().toString().trim()+code_5.getText().toString().trim()+code_6.getText().toString().trim();
                    *//*if(otp.length()>5){
                        hideKeyboard(CodeVerificationActivity.this);
                        showDialog();
                        verifyVerificationCode(otp);
                    }*//*

                   // Toast.makeText(CodeVerificationActivity.this, otp, Toast.LENGTH_SHORT).show();


                }
            }
        });*/

        authenticate_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          //  otp = code_1.getText().toString().trim()+code_2.getText().toString().trim()+code_3.getText().toString().trim()+code_4.getText().toString().trim()+code_5.getText().toString().trim()+code_6.getText().toString().trim();
                otp = pinView.getText().toString();
            if(otp.length()>5){
                hideKeyboard(CodeVerificationActivity.this);
                showDialog();
                verifyVerificationCode(otp);
            }else{
                Toast.makeText(CodeVerificationActivity.this, "Please enter 6 digit OTP!", Toast.LENGTH_SHORT).show();
            }
            }
        });


        try {
            Intent i = getIntent();
         //   mVerificationId = i.getStringExtra("mVerificationId");
            //email = i.getStringExtra("email");
            phone = i.getStringExtra("phone");

              showDialog();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phone,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            CodeVerificationActivity.this,               // Activity (for callback binding)
                            mCallbacks);
         //   resend_token = i.getComponent("resend_token");
          //  password = i.getStringExtra("password");
            user_number.setText(phone.substring(2));
        }catch (Exception ex){
            ex.printStackTrace();
        }

        setupToolbar();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.one_time_password));
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
        customDialog = new CustomDialog(CodeVerificationActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }



    private void verifyVerificationCode(String otp) {
        //creating the credential
        if(mVerificationId!=null){
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);

            //signing the user
            signInWithPhoneAuthCredential(credential);
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(CodeVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                         dismissDialog();
                         Intent intent = new Intent();
                         intent.putExtra("authentication_result", "Phone Number Authenticated");
                         setResult(RESULT_OK, intent);
                         finish();

                        } else {

                            //verification unsuccessful.. display an error message
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Toast.makeText(CodeVerificationActivity.this, message, Toast.LENGTH_LONG).show();
                        /*    Intent intent = new Intent();
                            intent.putExtra("authentication_result", message);
                            setResult(RESULT_CANCELED, intent);
                            Toast.makeText(CodeVerificationActivity.this, message, Toast.LENGTH_LONG).show();
                            finish();*/


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
                Toast.makeText(CodeVerificationActivity.this,""+toastString,Toast.LENGTH_LONG).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent();
      //  intent.putExtra("authentication_result", "Authentication Failed");
        setResult(RESULT_CANCELED, intent);
        Toast.makeText(CodeVerificationActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
        finish();
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            dismissDialog();
            if (code != null) {

                //verifying the code
               verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(CodeVerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            dismissDialog();
        }
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            dismissDialog();
            mVerificationId = s;
            PhoneAuthProvider.ForceResendingToken mResendToken = forceResendingToken;
            resend_token = mResendToken;

        }
    };

    public void getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        SharedHelper.putKey(CodeVerificationActivity.this,"authentication_code",String.format("%06d", number));
    }

    private void SendMessages() {

        showDialog();
        JSONObject obj = new JSONObject();
        try {

            obj.put("list_phone",phone);
            obj.put("message", SharedHelper.getKey(CodeVerificationActivity.this,"authentication_code"));
        }catch (Exception ex){
            ex.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.SEND_MESSAGES, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("error"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    //   Refresh token
                                } else {
                                    displayMessage(errorObj.optString("message"));
                                }
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 422) {

                            json = CustomRequestQueue.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                if (json.startsWith("The email has already been taken")) {
                                    displayMessage(getString(R.string.email_exist));
                                }else{
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                                //displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        SendMessages();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        CustomRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }
}
