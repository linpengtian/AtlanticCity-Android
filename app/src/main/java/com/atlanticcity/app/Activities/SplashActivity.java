package com.atlanticcity.app.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.atlanticcity.app.Models.SliderModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.DealsModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    public static final String TAG = "SplashActivity";

    public static List<DealsModel> GlobalDealsData  = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        runSplash();

    }

    void runSplash(){
        Handler handleCheckStatus;
        handleCheckStatus = new Handler();
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
               // SharedHelper.putKey(context,"logged_in","false");
                String login_status = SharedHelper.getKey(SplashActivity.this,"logged_in");
                Log.v("login_status",login_status);
                if(login_status.equals("true")){

                    String zipcode = SharedHelper.getKey(SplashActivity.this,"zipcode");
                    Log.v(TAG,zipcode);

                    String date_of_birth = SharedHelper.getKey(SplashActivity.this,"date_of_birth");
                    Log.v(TAG,date_of_birth);
                    GetDeals();

                }else{
                    Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        },1000);
      //  printHashKey(SplashActivity.this);
    }

    private void GetDeals() {
        Log.d("user-id",SharedHelper.getKey(SplashActivity.this,"user_id"));
        Log.d("auth-id",SharedHelper.getKey(SplashActivity.this,"auth_id"));
        AndroidNetworking.post(URLHelper.GET_DEALS)

                .addHeaders("user-id", SharedHelper.getKey(SplashActivity.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(SplashActivity.this,"auth_id"))
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

                                    // JSONObject detail = responseObj.getJSONObject("detail");

                                    GlobalDealsData.clear();
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<DealsModel>>() {
                                    }.getType();
                                    GlobalDealsData = gson.fromJson(responseObj.getString("detail"), listType);
                                    Intent i = new Intent(SplashActivity.this,DealsFullScreen.class);
                                    startActivity(i);
                                    finish();
                                }
                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
                            Intent i = new Intent(SplashActivity.this,DealsFullScreen.class);
                            startActivity(i);
                            finish();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error

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
                                    if(errorJson.optJSONObject("error").optString("message").equalsIgnoreCase("Un-authorized user")){
                                        Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            }catch (Exception ex){
                                ex.printStackTrace();
                                Toast.makeText(SplashActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                            Toast.makeText(SplashActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SplashActivity.this,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG,"triggered");
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        Log.v(TAG,"triggered");
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            String referrerUid = deepLink.getQueryParameter("invitedby");
                            //   Toast.makeText(activity, referrerUid, Toast.LENGTH_SHORT).show();


                            SharedHelper.putKey(SplashActivity.this,"referral_user_id",referrerUid);
                            //   AddInvitePoints(referrerUid);


                            //  Toast.makeText(SplashActivity.this, referrerUid, Toast.LENGTH_SHORT).show();
                        }
                        //
                        // If the user isn't signed in and the pending Dynamic Link is
                        // an invitation, sign in the user anonymously, and record the
                        // referrer's UID.
                        //


                     /*   FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null
                                && deepLink != null
                                && deepLink.getBooleanQueryParameter("invitedby", false)) {

                           // createAnonymousAccountWithReferrerInfo(referrerUid);
                        }*/
                    }
                });
    }
}
