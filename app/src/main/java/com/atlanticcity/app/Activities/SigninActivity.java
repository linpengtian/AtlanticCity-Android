package com.atlanticcity.app.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.atlanticcity.app.Adapters.SliderDealAdapter;
import com.atlanticcity.app.Models.DealsModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.atlanticcity.app.Utils.CustomRequestQueue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.ktx.Firebase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.atlanticcity.app.Activities.SplashActivity.GlobalDealsData;

public class SigninActivity extends AppCompatActivity {

    EditText etEmailAddress,etPassword;
    TextView character_counter_password,create_account,tv_forgot_password;
    Button sign_in_btn;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    RelativeLayout fb_login_btn,btn_google_sign_in;
    private static final int RC_SIGN_IN = 9001;
    String tag;
    String social_email,social_id,social_first_name,social_last_name;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    ImageView show_password,hide_password;
    String device_UDID,device_token;
    public String TAG = "SigninActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        viewInitializer();
        ClickListener();
    }

    void viewInitializer(){
        etEmailAddress = findViewById(R.id.et_email_address);
        etPassword = findViewById(R.id.et_password);
        character_counter_password = findViewById(R.id.character_counter_password);
        create_account = findViewById(R.id.create_account);
        sign_in_btn = findViewById(R.id.sign_in_btn);
        fb_login_btn = findViewById(R.id.fb_login_btn);
        btn_google_sign_in = findViewById(R.id.btn_google_sign_in);
        helper = new ConnectionHelper(SigninActivity.this);
        isInternet = helper.isConnectingToInternet();
        show_password = findViewById(R.id.show_password);
        hide_password = findViewById(R.id.hide_password);
        tv_forgot_password = findViewById(R.id.tv_forgot_password);
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    character_counter_password.setText(editable.toString().length()+"/6");
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
        });

        fb_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tag = "facebook";
                //   LoginManager.getInstance().logInWithReadPermissions(CreateAccount.this, Arrays.asList("public_profile", "email"));
                LoginManager.getInstance().logInWithReadPermissions(SigninActivity.this, Arrays.asList("public_profile", "email"));

                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("CreateAccount", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("CreateAccount", "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("CreateAccount", "facebook:onError", error);
                        // ...
                    }
                });
            }
        });


        btn_google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tag = "google";
                GoogleSignIn();
            }
        });

        GetToken();
    }

    private void GoogleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    boolean isEmpty(String str){
        if(str.isEmpty()){
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
                Toast.makeText(SigninActivity.this,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    void ClickListener(){
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SigninActivity.this,CreateAccount.class);
                startActivity(i);
                finish();
            }
        });

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SigninActivity.this, PasswordRecovery.class);
                startActivity(i);
            }
        });

        show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_password.setVisibility(View.GONE);
                hide_password.setVisibility(View.VISIBLE);
                etPassword.setTransformationMethod(null);
            }
        });

        hide_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide_password.setVisibility(View.GONE);
                show_password.setVisibility(View.VISIBLE);
                etPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmailAddress.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                isInternet = helper.isConnectingToInternet();

                if(isEmpty(email)){
                    displayMessage(getString(R.string.email_validation));
                }else if(isEmpty(password)){
                    displayMessage(getString(R.string.enter_password));
                }else if(isInternet){
                    hideKeyboard(SigninActivity.this);
                    signinAPI(email,password);
                }else{
                    displayMessage(getString(R.string.something_went_wrong_net));
                }

            }
        });
    }


    private void signinAPI(final String email,final String password) {
        showDialog();

        AndroidNetworking.post(URLHelper.LOGIN)

                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
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
                                    Toast.makeText(SigninActivity.this, responseObj.optString("message"), Toast.LENGTH_LONG).show();
                                    JSONObject detail = responseObj.getJSONObject("detail");
                                    SharedHelper.putKey(SigninActivity.this,"id",detail.optString("id"));
                                    SharedHelper.putKey(SigninActivity.this,"user_id",detail.optString("user_id"));
                                    SharedHelper.putKey(SigninActivity.this,"auth_id",detail.optString("auth_id"));
                                    SharedHelper.putKey(SigninActivity.this,"email",detail.optString("email"));
                                    SharedHelper.putKey(SigninActivity.this,"logged_in","true");
                                    SharedHelper.putKey(SigninActivity.this,"zipcode",detail.optString("zipcode"));
                                    SharedHelper.putKey(SigninActivity.this,"date_of_birth",detail.optString("date_of_birth"));
                                    SharedHelper.putKey(SigninActivity.this,"notification",detail.optString("notfication"));
                                    SharedHelper.putKey(SigninActivity.this,"share_app_status",detail.optString("share_app_status"));
                                    SharedHelper.putKey(SigninActivity.this,"first_name",detail.optString("first_name"));
                                    SharedHelper.putKey(SigninActivity.this,"last_name",detail.optString("last_name"));
                                    String zip_code = detail.optString("zipcode");
                                    String date_of_birth = detail.optString("date_of_birth");

                                    String shareAppStatus =  detail.optString("share_app_status");
                                    if(zip_code.isEmpty() || zip_code.equalsIgnoreCase("") || zip_code == null || zip_code.equalsIgnoreCase("null")){
                                        Intent i = new Intent(SigninActivity.this,ZipCodeActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }else if(date_of_birth.isEmpty() || date_of_birth.equalsIgnoreCase("") || date_of_birth == null || date_of_birth.equalsIgnoreCase("null")){
                                        Intent i = new Intent(SigninActivity.this,DealsFullScreen.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();

                                    }else {

                                        Intent i = new Intent(SigninActivity.this,DealsFullScreen.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                     finish();
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

    void dismissDialog(){
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();
    }

    void showDialog(){
        customDialog = new CustomDialog(SigninActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("CreateAccount", "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        showDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            try {
                                Log.d("CreateAccount", "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.v("user_email",user.getEmail());
                                Log.v("user_name",user.getDisplayName());
                                Log.v("user_id",user.getUid());
                                String[] parts = user.getDisplayName().split("\\s+");
                                if(parts.length>1){
                                    social_first_name = parts[0];
                                    social_last_name = parts[1];
                                }else {
                                    social_first_name = parts[0];
                                    social_last_name = parts[0];
                                }

                                social_id = user.getUid();
                                social_email = user.getEmail();

                                SocialLogin(social_first_name,social_last_name,social_email,social_id,tag);
                            }catch (Exception ex){
                                ex.printStackTrace();
                                Toast.makeText(SigninActivity.this, getText(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                            // Sign in success, update UI with the signed-in user's information


                            // Toast.makeText(CreateAccount.this, user.getEmail() + user.getDisplayName() + user.getUid(), Toast.LENGTH_SHORT).show();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("CreateAccount", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //  updateUI(null);
                        }

                        // [START_EXCLUDE]
                        dismissDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google", "Google sign in failed", e);
                // [START_EXCLUDE]
                // updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //  FirebaseUser currentUser = mAuth.getCurrentUser();

        try {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            mAuth.signOut();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        // updateUI(currentUser);
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Google", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            try {
                                Log.d("Google", "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.v("user_email",user.getEmail());
                                Log.v("user_name",user.getDisplayName());
                                Log.v("user_id",user.getUid());
                                String[] parts = user.getDisplayName().split("\\s+");
                                if(parts.length>1){
                                    social_first_name = parts[0];
                                    social_last_name = parts[1];
                                }else {
                                    social_first_name = parts[0];
                                    social_last_name = parts[0];
                                }

                                social_email = user.getEmail();
                                social_id = user.getUid();
                                SocialLogin(social_first_name,social_last_name,social_email,social_id,tag);
                            }catch (Exception ex){
                                ex.printStackTrace();
                                Toast.makeText(SigninActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                            // Sign in success, update UI with the signed-in user's information

                            //   Toast.makeText(CreateAccount.this, user.getEmail()  + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Google", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]
                        dismissDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void SocialLogin(final String first_name, final String last_name, final String email, final String social_id, final String tag) {
        showDialog();
        String referrerUid = SharedHelper.getKey(SigninActivity.this,"referral_user_id");
        AndroidNetworking.post(URLHelper.SOCIAL_LOGIN+tag)

                .addBodyParameter("email", email)
                .addBodyParameter("first_name", first_name)
                .addBodyParameter("last_name", last_name)
                .addBodyParameter(tag+"_id", social_id)
                .addBodyParameter("id", referrerUid)
                .addBodyParameter("device_id", device_UDID)
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
                                    Toast.makeText(SigninActivity.this, responseObj.optString("message"), Toast.LENGTH_LONG).show();
                                    JSONObject detail = responseObj.getJSONObject("detail");
                                    SharedHelper.putKey(SigninActivity.this,"id",detail.optString("id"));
                                    SharedHelper.putKey(SigninActivity.this,"user_id",detail.optString("user_id"));
                                    SharedHelper.putKey(SigninActivity.this,"auth_id",detail.optString("auth_id"));
                                    SharedHelper.putKey(SigninActivity.this,"email",detail.optString("email"));
                                    SharedHelper.putKey(SigninActivity.this,"logged_in","true");
                                    SharedHelper.putKey(SigninActivity.this,"zipcode",detail.optString("zipcode"));
                                    SharedHelper.putKey(SigninActivity.this,"date_of_birth",detail.optString("date_of_birth"));
                                    SharedHelper.putKey(SigninActivity.this,"notification",detail.optString("notfication"));
                                    SharedHelper.putKey(SigninActivity.this,"share_app_status",detail.optString("share_app_status"));
                                    SharedHelper.putKey(SigninActivity.this,"first_name",detail.optString("first_name"));
                                    SharedHelper.putKey(SigninActivity.this,"last_name",detail.optString("last_name"));
                                    String zip_code = detail.optString("zipcode");
                                    String date_of_birth = detail.optString("date_of_birth");
                                    String points = detail.optString("date_of_birth");
                                    if(points.equals("0")){
                                        getRegistrationPoints();
                                    }else{
                                        if(zip_code.isEmpty() || zip_code.equalsIgnoreCase("") || zip_code == null || zip_code.equalsIgnoreCase("null")){
                                            Intent i = new Intent(SigninActivity.this,ZipCodeActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
                                        }else if(date_of_birth.isEmpty() || date_of_birth.equalsIgnoreCase("") || date_of_birth == null || date_of_birth.equalsIgnoreCase("null")){
                                            Intent i = new Intent(SigninActivity.this,DateOfBirth.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
                                         // GetDeals();
                                        }else {
                                            Intent i = new Intent(SigninActivity.this,DealsFullScreen.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();

                                        }
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

    private void getRegistrationPoints( ) {
        // showDialog();

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URLHelper.REGISTRATION_POINTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //  if ((customDialog != null) && (customDialog.isShowing()))
                //customDialog.dismiss();
                dismissDialog();
                try {

                    JSONObject response_obj = new JSONObject(response);
                    JSONObject errorObj = response_obj.getJSONObject("error");
                    if(errorObj.optString("status").equals("1")){
                        displayMessage(errorObj.optString("message"));
                    }else{
                        JSONObject responseObj = response_obj.getJSONObject("response");
                        if(responseObj.optString("status").equals("1")){

                            Intent i = new Intent(SigninActivity.this,PointsActivity.class);
                            i.putExtra("status","registering");
                            startActivity(i);
                            finish();
                        }
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissDialog();

                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if(errorObj.optJSONObject("error").optString("status").equals("1")){
                            // displayMessage(errorObj.optJSONObject("error").optString("message"));
                            Intent i = new Intent(SigninActivity.this,DealsFullScreen.class);
                            startActivity(i);
                            finish();
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
                        getRegistrationPoints();
                    }
                }

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("user-id", SharedHelper.getKey(SigninActivity.this, "user_id"));
                headers.put("auth-id", SharedHelper.getKey(SigninActivity.this, "auth_id"));
                return headers;
            }

        };

        CustomRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    public void GetToken() {

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.i(TAG, "Device UDID:" + device_UDID);
        }catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
        try {
            if(!SharedHelper.getKey(SigninActivity.this,"device_token").equals("") && SharedHelper.getKey(SigninActivity.this,"device_token") != null  && !SharedHelper.getKey(SigninActivity.this,"device_token").equals("null")) {
                device_token = SharedHelper.getKey(SigninActivity.this, "device_token");

                Log.i(TAG, "GCM Registration Token: " + device_token);
            }else{

                showDialog();
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        dismissDialog();
                        if(isInternet) {
                            device_token = task.getResult().getToken();  // Get new Instance ID token
                            Log.i(TAG, device_token);
                        }
                        else
                            {
                                Toast.makeText(SigninActivity.this, R.string.check_connection, Toast.LENGTH_SHORT).show();
                            }

                        //   Toast.makeText(SplashActivity.this, device_token, Toast.LENGTH_SHORT).show();
                    }
                });
                //     device_token = ""+ FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(SigninActivity.this, "device_token",""+device_token);
                Log.i(TAG, "Failed to complete token refresh: " + device_token);
            }
        }catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }
}
