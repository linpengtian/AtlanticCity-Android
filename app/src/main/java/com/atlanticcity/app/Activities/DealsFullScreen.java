package com.atlanticcity.app.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.atlanticcity.app.Adapters.DealsAdapter;


import com.atlanticcity.app.Adapters.SliderDealAdapter;
import com.atlanticcity.app.Models.SliderModel;

import com.github.islamkhsh.CardSliderViewPager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.atlanticcity.app.CustomFont.CustomTypefaceSpan;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.DealsModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;


import static com.atlanticcity.app.Activities.SplashActivity.GlobalDealsData;

public class DealsFullScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    View view;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    List<DealsModel> dealsModel = new ArrayList<>();
    List<SliderModel> sliderModels = new ArrayList<>();
    public String TAG = "DEALS";
    CardStackLayoutManager cardStackLayoutManager;
    BottomNavigationView bottomNavigationView;
    public DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    RelativeLayout main_layout;
    String device_UDID,device_token;
    public SliderDealAdapter dealsAdapter;
    int current_position;
    int card_position = 0;
    public CardSliderViewPager card_stack_view;
    boolean dealExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals_full_screen);
        viewInitializer();
    }

    void viewInitializer(){

        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        main_layout = findViewById(R.id.main_layout);

        card_stack_view = findViewById(R.id.card_stack_view);


        helper = new ConnectionHelper(DealsFullScreen.this);
        isInternet = helper.isConnectingToInternet();

        if(isInternet){
            if(GlobalDealsData.size()>0){

                dealsAdapter = new SliderDealAdapter(DealsFullScreen.this,GlobalDealsData);

                card_stack_view.setAdapter(dealsAdapter);
                try {
                    int valueToSet = Integer.MAX_VALUE / 2;
                    int value =  valueToSet % GlobalDealsData.size();
                    for(int i = 0 ; i < GlobalDealsData.size(); i ++){
                        if(CheckIfDeal(i)){
                            dealExists = true;
                            break;
                        }else {
                            dealExists = false;
                        }
                    }

                    if(dealExists){
                        Reshuffle(value,valueToSet);
                    }else {
                        card_stack_view.setCurrentItem(valueToSet,false);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                GetToken();
            }

        }else {
            Toast.makeText(DealsFullScreen.this, DealsFullScreen.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_hotel:
                        Intent webView = new Intent(DealsFullScreen.this, ViewWeb.class);
                        webView.putExtra("url","https://secure.rezserver.com/cities/4453-800049132?cname=secure.atlanticcity.com");
                        startActivity(webView);
                        /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://secure.rezserver.com/cities/4453-800049132?cname=secure.atlanticcity.com\n"));
                        startActivity(browserIntent);*/
                        break;

                    case R.id.nav_shows:

                        Intent webView_1 = new Intent(DealsFullScreen.this, ViewWeb.class);
                        webView_1.putExtra("url","http://www.atlanticcity.com/atlantic-city-shows");
                        startActivity(webView_1);
                     /*   Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.atlanticcity.com/atlantic-city-shows"));
                        startActivity(browserIntent1);*/
                        break;

                    case R.id.nav_wheel:
                        Intent i = new Intent(DealsFullScreen.this, SpinWheel.class);
                        startActivity(i);
                        break;
                    case R.id.nav_home:
                        Intent i2 = new Intent(DealsFullScreen.this, DealsFullScreen.class);
                        startActivity(i2);
                        break;

                    case R.id.nav_invite:
                        Intent i3 = new Intent(DealsFullScreen.this, InviteFriends.class);
                        startActivity(i3);
                        break;

                }
                return true;
            }
        });

        setUpNavigationView();
    }


    private void GetDeals() {
        showDialog();
        AndroidNetworking.post(URLHelper.GET_DEALS)
                .addHeaders("user-id", SharedHelper.getKey(DealsFullScreen.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(DealsFullScreen.this,"auth_id"))
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

                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<DealsModel>>() {
                                    }.getType();

                                    GlobalDealsData = gson.fromJson(responseObj.getString("detail"), listType);
                                    if(GlobalDealsData.size()>0){

                                        dealsAdapter = new SliderDealAdapter(DealsFullScreen.this,GlobalDealsData);
                                        card_stack_view.setAdapter(dealsAdapter);
                                        try {
                                         //   dealExists = false;
                                            int valueToSet = Integer.MAX_VALUE / 2;
                                            int value =  valueToSet % GlobalDealsData.size();
                                            for(int i = 0 ; i < GlobalDealsData.size(); i ++){
                                                if(CheckIfDeal(i)){
                                                    dealExists = true;
                                                    break;
                                                }else {
                                                    dealExists = false;
                                                }
                                            }

                                            if(dealExists){
                                                Reshuffle(value,valueToSet);
                                            }else {
                                                card_stack_view.setCurrentItem(valueToSet,false);
                                            }
                                        }catch (Exception ex){
                                            ex.printStackTrace();
                                        }

                                    }

                                    dismissDialog();
                                    GetToken();

                                }
                            }

                        }catch (Exception ex){
                            ex.printStackTrace();

                            GetToken();

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
                Toast.makeText(DealsFullScreen.this,""+toastString,Toast.LENGTH_SHORT).show();
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
        customDialog = new CustomDialog(DealsFullScreen.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    case R.id.nav_signout:
                        // launch new intent instead of loading fragment

                        showLogoutDialog();
                        return true;

                    case R.id.nav_how_to_earn_points:
                      //  drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i = new Intent(DealsFullScreen.this,HowToEarnPoints.class);
                        startActivity(i);
                        return true;

                    case R.id.nav_about_us:
                      //  drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i8 = new Intent(DealsFullScreen.this,AboutUs.class);
                        startActivity(i8);
                        return true;

                    case R.id.nav_invitess:
                        //   drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i11 = new Intent(DealsFullScreen.this,ViewInvites.class);
                        startActivity(i11);
                        return true;

                 /*   case R.id.nav_nearby:
                        //   viewPager.setCurrentItem(1);

                       drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
*/
                    case R.id.nav_notifications:
                       // drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i5 = new Intent(DealsFullScreen.this,Notifications.class);
                        startActivity(i5);
                        return true;

                    case R.id.nav_settings:
                      //  drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i4 = new Intent(DealsFullScreen.this,Settings.class);
                        startActivity(i4);

                        return true;

                    case R.id.nav_earned_points:
                      //  drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i2 = new Intent(DealsFullScreen.this,EarnedPoints.class);
                        startActivity(i2);
                        return true;

                    case R.id.nav_deals:
                        //   viewPager.setCurrentItem(0);
                        Intent i6 = new Intent(DealsFullScreen.this,DealsFullScreen.class);
                        startActivity(i6);
                      //  drawerLayout.closeDrawer(GravityCompat.START);
                }

                return true;
            }
        });

        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {
            MenuItem menuItem = m.getItem(i);
            applyFontToMenuItem(menuItem);

        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "Font/Roboto-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater)DealsFullScreen.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setMessage(getString(R.string.logout_alert));
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                helper = new ConnectionHelper(DealsFullScreen.this);
                isInternet = helper.isConnectingToInternet();
                if(isInternet){
                    LogOut();
                }else {
                    Toast.makeText(DealsFullScreen.this, DealsFullScreen.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
                }

                }
            });
            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
                }
            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(DealsFullScreen.this, R.color.colorPrimaryDark));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(DealsFullScreen.this, R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }

    private void LogOut() {
        drawerLayout.closeDrawer(GravityCompat.START);
        showDialog();
        AndroidNetworking.post(URLHelper.LOGOUT)

                .addBodyParameter("user-id", SharedHelper.getKey(DealsFullScreen.this,"user_id"))
                .addHeaders("user-id", SharedHelper.getKey(DealsFullScreen.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(DealsFullScreen.this,"auth_id"))
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

                                    Toast.makeText(DealsFullScreen.this, responseObj.optString("message"), Toast.LENGTH_SHORT).show();
                                    SharedHelper.putKey(DealsFullScreen.this,"logged_in","false");
                                    SharedHelper.putKey(DealsFullScreen.this,"user_id","");
                                    SharedHelper.putKey(DealsFullScreen.this,"auth_id","");
                                    SharedHelper.putKey(DealsFullScreen.this,"zipcode","");
                                    SharedHelper.putKey(DealsFullScreen.this,"date_of_birth","");
                                    SharedHelper.putKey(DealsFullScreen.this,"notification","");
                                    SharedHelper.putKey(DealsFullScreen.this,"share_app_status","");
                                    SharedHelper.putKey(DealsFullScreen.this,"referral_user_id","");
                                    SharedHelper.putKey(DealsFullScreen.this,"first_name","");
                                    SharedHelper.putKey(DealsFullScreen.this,"last_name","");
                                    GlobalDealsData.clear();
                                    Intent goToLogin = new Intent(DealsFullScreen.this, WelcomeActivity.class);
                                    goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(goToLogin);
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

    private void UpdateDeviceToken() {

        AndroidNetworking.post(URLHelper.UPDATE_DEVICE_TOKEN)
                .addBodyParameter("device_token", device_token)
                .addBodyParameter("user-id", SharedHelper.getKey(DealsFullScreen.this,"user_id"))
                .addHeaders("user-id", SharedHelper.getKey(DealsFullScreen.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(DealsFullScreen.this,"auth_id"))
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

                                Log.v("adds_status",responseObj.optString("status"));

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
            if(!SharedHelper.getKey(DealsFullScreen.this,"device_token").equals("") && SharedHelper.getKey(DealsFullScreen.this,"device_token") != null  && !SharedHelper.getKey(DealsFullScreen.this,"device_token").equals("null")) {
                device_token = SharedHelper.getKey(DealsFullScreen.this, "device_token");
                UpdateDeviceToken();
                Log.i(TAG, "GCM Registration Token: " + device_token);
            }else{

                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        device_token = task.getResult().getToken();  // Get new Instance ID token
                        UpdateDeviceToken();
                        Log.i(TAG, device_token);

                        //   Toast.makeText(SplashActivity.this, device_token, Toast.LENGTH_SHORT).show();
                    }
                });
                //     device_token = ""+ FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(DealsFullScreen.this, "device_token",""+device_token);
                Log.i(TAG, "Failed to complete token refresh: " + device_token);
            }
        }catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh", e);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isInternet){
            if(GlobalDealsData.size()<=0){
                GetDeals();
               // GetToken();
            }

        }else {
            Toast.makeText(DealsFullScreen.this, DealsFullScreen.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }

    }

    boolean CheckIfDeal(int value) {
        try {
            if (GlobalDealsData.get(value).getItem_id() != null) {
                return true;
            } else {
                return false;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }

    }

    void Reshuffle(int value, int valueToSet){
        try {
            if(CheckIfDeal(value)){
                card_stack_view.setCurrentItem(valueToSet,false);
            }else {
                Collections.shuffle(GlobalDealsData);
                Reshuffle(value,valueToSet);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
