package com.atlanticcity.app.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.hbb20.CountryCodePicker;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class CreateAccount extends AppCompatActivity {

    EditText et_password,et_email_address,et_phone;
    TextView character_counter_password,character_counter_confirm_password;
    CountryCodePicker ccp;
    String selected_country_code;
    ConnectionHelper helper;
    Boolean isInternet;
    Button sign_up_btn;
    CustomDialog customDialog;
    private String mVerificationId;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    String phoneNumber;
    private CallbackManager callbackManager;
    TextView terms_of_service,privacy_policy,login_text;
    RelativeLayout fb_login_btn,btn_google_sign_in;
    int INTENT_FOR_PHONE_AUTHENTICATION = 508;
    private static final int RC_SIGN_IN = 9001;
    String tag;
    String device_UDID,device_token;
    String social_email,social_id,social_first_name,social_last_name;
    String strZipCode, strDateOfBirth;
    ImageView show_password,hide_password,show_confirm_password,hide_confirm_password;
    public String TAG = "CreateAccount";
    boolean isNumVerified = false;
    int CHANGE_NUMBER = 965;
    EditText etFirstName,etLastName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        //disableAutofill();
        viewInitializer();

    }


    void viewInitializer(){
        et_password = findViewById(R.id.et_password);
        character_counter_password = findViewById(R.id.character_counter_password);
      //  et_password_confirm = findViewById(R.id.et_password_confirm);
        et_email_address = findViewById(R.id.et_email_address);
         et_phone = findViewById(R.id.et_phone);
        fb_login_btn = findViewById(R.id.fb_login_btn);
        btn_google_sign_in = findViewById(R.id.btn_google_sign_in);
        character_counter_confirm_password = findViewById(R.id.character_counter_confirm_password);
         ccp = findViewById(R.id.ccp);
        terms_of_service = findViewById(R.id.terms_of_service);
        privacy_policy = findViewById(R.id.privacy_policy);
        sign_up_btn = findViewById(R.id.sign_up_btn);

        show_password = findViewById(R.id.show_password);
        hide_password = findViewById(R.id.hide_password);
        show_confirm_password = findViewById(R.id.show_confirm_password);
        hide_confirm_password = findViewById(R.id.hide_confirm_password);
        login_text = findViewById(R.id.login_text);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        helper = new ConnectionHelper(CreateAccount.this);
        isInternet = helper.isConnectingToInternet();


      // onCountryPickerClick(getCurrentFocus());
      //  ccp.getDefaultCountryCode();
      //  ccp.setDefaultCountryUsingNameCode("us");
        selected_country_code = ccp.getDefaultCountryCodeWithPlus();
        ccp.setNumberAutoFormattingEnabled(true);
        ccp.registerCarrierNumberEditText(et_phone);


        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        terms_of_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(CreateAccount.this,TermsAndPrivacyPolicy.class);
            i.putExtra("tag","terms");
            startActivity(i);
            }
        });

        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(CreateAccount.this,TermsAndPrivacyPolicy.class);
            i.putExtra("tag","app_guide");
            startActivity(i);
            }
        });

        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(CreateAccount.this,SigninActivity.class);
            startActivity(i);
            }
        });

        show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            show_password.setVisibility(View.GONE);
            hide_password.setVisibility(View.VISIBLE);
            et_password.setTransformationMethod(null);
            }
        });

        hide_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            hide_password.setVisibility(View.GONE);
            show_password.setVisibility(View.VISIBLE);
            et_password.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        show_confirm_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            show_confirm_password.setVisibility(View.GONE);
            hide_confirm_password.setVisibility(View.VISIBLE);
          //  et_password_confirm.setTransformationMethod(null);
            }
        });


        hide_confirm_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            hide_confirm_password.setVisibility(View.GONE);
            show_confirm_password.setVisibility(View.VISIBLE);
         //   et_password_confirm.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
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
                LoginManager.getInstance().logInWithReadPermissions(CreateAccount.this, Arrays.asList("public_profile", "email"));

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

       /* et_password_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    character_counter_confirm_password.setText(editable.toString().length()+"/6");
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
        });*/
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String email_address = et_email_address.getText().toString().trim();
            String phone_number = et_phone.getText().toString().trim();
            String password = et_password.getText().toString().trim();
            String first_name = etFirstName.getText().toString().trim();
            String last_name = etLastName.getText().toString().trim();

         //   String confirm_password = et_password_confirm.getText().toString().trim();

         //   phoneNumber = selected_country_code+phone_number;
            isInternet = helper.isConnectingToInternet();
            if(isEmpty(first_name)){
                displayMessage(getString(R.string.first_name_validation));
            }else if(isEmpty(last_name)){
                displayMessage(getString(R.string.last_name_validation));
            }else if (isEmpty(email_address)  || email_address.equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                displayMessage(getString(R.string.email_validation));
            } else if (!isValidEmail(email_address)) {
                displayMessage(getString(R.string.not_valid_email));
            } else if(isEmpty(phone_number)){
                displayMessage(getString(R.string.phone_validation));
            }else if(password_validator(password)){
                displayMessage(getString(R.string.password_validation));
            }else /*if(password_validator(confirm_password)){
                displayMessage(getString(R.string.password_confirmation_validation));
            }else if(!password.equals(confirm_password)){
                displayMessage(getString(R.string.warning_password_not_matched));
            }else*/ if(isInternet){
                phone_number = phone_number.replace("+1","");
                phoneNumber = selected_country_code+phone_number;

                if(!isNumVerified){
                    Intent i = new Intent(CreateAccount.this, CodeVerificationActivity.class);
                    i.putExtra("phone", phoneNumber);
                    startActivityForResult(i,INTENT_FOR_PHONE_AUTHENTICATION);
                }else {
                    registerAPI();
                }


            }else{
                displayMessage(getString(R.string.something_went_wrong_net));
            }
        }
    });

        GetToken();
    }

    public void onCountryPickerClick(View view) {
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
            //Alert.showMessage(RegistrationActivity.this, ccp.getSelectedCountryCodeWithPlus());
            selected_country_code = ccp.getSelectedCountryCodeWithPlus();
            }
        });
    }

    public void displayMessage(  String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(CreateAccount.this,""+toastString,Toast.LENGTH_SHORT).show();
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
        customDialog = new CustomDialog(CreateAccount.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    boolean isEmpty(String str){
        if(str.isEmpty()){
            return true;
        }else {
            return false;
        }
    }

    boolean password_validator(String password){
        if(password.isEmpty() || password.length()<6){
            return true;
        }else {
            return false;
        }
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
                                Toast.makeText(CreateAccount.this, getText(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                            // Sign in success, update UI with the signed-in user's information



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("CreateAccount", "signInWithCredential:failure", task.getException());
                            Toast.makeText(CreateAccount.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

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

                // [END_EXCLUDE]
            }
        }

        if(requestCode == INTENT_FOR_PHONE_AUTHENTICATION && resultCode == RESULT_OK){
            isNumVerified = true;
            registerAPI();
        }else if(resultCode == RESULT_CANCELED){
            isNumVerified = false;
            Toast.makeText(CreateAccount.this, "Authentication failed", Toast.LENGTH_SHORT).show();

        } else if(resultCode == CHANGE_NUMBER){
            isNumVerified = false;
            Toast.makeText(CreateAccount.this, "Change your phone number", Toast.LENGTH_LONG).show();
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


    private void GoogleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                                Toast.makeText(CreateAccount.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                            // Sign in success, update UI with the signed-in user's information


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Google", "signInWithCredential:failure", task.getException());
                            Toast.makeText(CreateAccount.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                           // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        dismissDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void registerAPI() {
        String referrerUid = SharedHelper.getKey(CreateAccount.this,"referral_user_id");
        showDialog();
        AndroidNetworking.post(URLHelper.REGISTER)

                .addBodyParameter("email", et_email_address.getText().toString().trim())
                .addBodyParameter("password", et_password.getText().toString().trim())
                .addBodyParameter("phoneno", phoneNumber)
                .addBodyParameter("first_name", etFirstName.getText().toString().trim())
                .addBodyParameter("last_name", etLastName.getText().toString().trim())
                .addBodyParameter("id", referrerUid)
                .addBodyParameter("device_id", device_UDID)
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
                                    Toast.makeText(CreateAccount.this, responseObj.optString("message"), Toast.LENGTH_LONG).show();
                                    JSONObject detail = responseObj.getJSONObject("detail");
                                    SharedHelper.putKey(CreateAccount.this,"id",detail.optString("id"));
                                    SharedHelper.putKey(CreateAccount.this,"user_id",detail.optString("user_id"));
                                    SharedHelper.putKey(CreateAccount.this,"auth_id",detail.optString("auth_id"));
                                    SharedHelper.putKey(CreateAccount.this,"email",detail.optString("email"));
                                    SharedHelper.putKey(CreateAccount.this,"logged_in","true");
                                    SharedHelper.putKey(CreateAccount.this,"zipcode",detail.optString("zipcode"));
                                    SharedHelper.putKey(CreateAccount.this,"date_of_birth",detail.optString("date_of_birth"));
                                    SharedHelper.putKey(CreateAccount.this,"notification",detail.optString("notfication"));
                                    SharedHelper.putKey(CreateAccount.this,"share_app_status",detail.optString("share_app_status"));
                                    SharedHelper.putKey(CreateAccount.this,"first_name",detail.optString("first_name"));
                                    SharedHelper.putKey(CreateAccount.this,"last_name",detail.optString("last_name"));
                                    // displayMessage(responseObj.optString("message"));
                                    getRegistrationPoints();

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

        AndroidNetworking.post(URLHelper.REGISTRATION_POINTS)
                .addHeaders("user-id", SharedHelper.getKey(this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(this,"auth_id"))

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
                                    //  Toast.makeText(CodeVerificationActivity.this, responseObj.optString("message"), Toast.LENGTH_LONG).show();
                                    //   displayMessage(responseObj.optString("message"));
                                    //   JSONObject detail = responseObj.getJSONObject("detail");

                                    Intent i = new Intent(CreateAccount.this,PointsActivity.class);
                                    i.putExtra("status","registering");
                                    i.putExtra("points",responseObj.optString("detail"));
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private void SocialLogin(final String first_name, final String last_name, final String email, final String social_id, final String tag) {
        showDialog();
        String referrerUid = SharedHelper.getKey(CreateAccount.this,"referral_user_id");
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
                                    Toast.makeText(CreateAccount.this, responseObj.optString("message"), Toast.LENGTH_LONG).show();
                                    JSONObject detail = responseObj.getJSONObject("detail");
                                    SharedHelper.putKey(CreateAccount.this,"id",detail.optString("id"));
                                    SharedHelper.putKey(CreateAccount.this,"user_id",detail.optString("user_id"));
                                    SharedHelper.putKey(CreateAccount.this,"auth_id",detail.optString("auth_id"));
                                    SharedHelper.putKey(CreateAccount.this,"email",detail.optString("email"));
                                    SharedHelper.putKey(CreateAccount.this,"logged_in","true");
                                    SharedHelper.putKey(CreateAccount.this,"zipcode",detail.optString("zipcode"));
                                    SharedHelper.putKey(CreateAccount.this,"date_of_birth",detail.optString("date_of_birth"));
                                    SharedHelper.putKey(CreateAccount.this,"notification",detail.optString("notfication"));
                                    SharedHelper.putKey(CreateAccount.this,"share_app_status",detail.optString("share_app_status"));
                                    SharedHelper.putKey(CreateAccount.this,"first_name",detail.optString("first_name"));
                                    SharedHelper.putKey(CreateAccount.this,"last_name",detail.optString("last_name"));
                                    String zip_code = detail.optString("zipcode");
                                    String date_of_birth = detail.optString("date_of_birth");
                                    String points = detail.optString("date_of_birth");
                                    if(points.equals("0")){
                                        getRegistrationPoints();
                                    }else{
                                        if(zip_code.isEmpty() || zip_code.equalsIgnoreCase("") || zip_code == null || zip_code.equalsIgnoreCase("null")){
                                            Intent i = new Intent(CreateAccount.this,ZipCodeActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
                                        }else if(date_of_birth.isEmpty() || date_of_birth.equalsIgnoreCase("") || date_of_birth == null || date_of_birth.equalsIgnoreCase("null")){
                                            Intent i = new Intent(CreateAccount.this,DateOfBirth.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
                                        }else {
                                            Intent i = new Intent(CreateAccount.this,DealsFullScreen.class);
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
            if(!SharedHelper.getKey(CreateAccount.this,"device_token").equals("") && SharedHelper.getKey(CreateAccount.this,"device_token") != null  && !SharedHelper.getKey(CreateAccount.this,"device_token").equals("null")) {
                device_token = SharedHelper.getKey(CreateAccount.this, "device_token");

                Log.i(TAG, "GCM Registration Token: " + device_token);
            }else{

                showDialog();
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        dismissDialog();
                        if(isInternet){
                            device_token = task.getResult().getToken();  // Get new Instance ID token
                            Log.i(TAG, device_token);
                        }
                        else
                        {

                            Toast.makeText(CreateAccount.this, R.string.check_connection, Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                //     device_token = ""+ FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(CreateAccount.this, "device_token",""+device_token);
                Log.i(TAG, "Failed to complete token refresh: " + device_token);
            }
        }catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }
}
