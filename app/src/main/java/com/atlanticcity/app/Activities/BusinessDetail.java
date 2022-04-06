package com.atlanticcity.app.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.atlanticcity.app.CustomFont.CustomTypefaceSpan;
import com.atlanticcity.app.Models.EarnedPointsModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.atlanticcity.app.Adapters.BusinessDealAdapter;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.BusinessModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

import com.atlanticcity.app.Utils.UnlockBar;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import de.hdodenhof.circleimageview.CircleImageView;

import static com.atlanticcity.app.Activities.SplashActivity.GlobalDealsData;

public class BusinessDetail extends AppCompatActivity   implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    String strBusinessTitle,business_id;
    List<BusinessModel> businessModels = new ArrayList<>();
    CircleImageView business_logo;
    TextView address,state_city,timings,deals_text;
    RecyclerView deals_rv;
    BusinessDealAdapter businessDealAdapter;
    TextView view_all_text;
    ImageView img_phone,img_location,img_like,img_share;
    String phone_number;
    Button btn_share_and_get_points;
    BottomNavigationView bottomNavigationView;
   // RelativeLayout wheel;
    TextView no_deals_avaiable,businessNavigator, favoritesNavigator, settingsNavigator;
    String strLat,strLng;
    TextView title;
    ImageView icon_back;
    private int _xDelta;
    private int _yDelta;
    boolean isReached = false;
    RelativeLayout main_layout;
    ScrollView parentView;
    UnlockBar unlock;
    String currentTime;
    List<EarnedPointsModel> earnedPointsModel = new ArrayList<>();
    public String TAG = "BusinessDetail";
    ImageView menu;
    LinearLayout menuLayout;
    public DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView tvPoints;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_detail);
        parentView = findViewById(R.id.parent);
        title = findViewById(R.id.title);
        icon_back = findViewById(R.id.icon_back);
        main_layout = findViewById(R.id.main_layout);
        menu = findViewById(R.id.back_arrow);
        menuLayout = findViewById(R.id.menu_layout);
        businessNavigator = findViewById(R.id.business_navigator);
        favoritesNavigator = findViewById(R.id.favorites_navigator);
        settingsNavigator = findViewById(R.id.settings_navigator);
        navigationView = findViewById(R.id.navigation_view);
        tvPoints = findViewById(R.id.tvPoints);
        deals_text=findViewById(R.id.deals_text);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.getMenu().getItem(0).setChecked(true);

       // wheel = findViewById(R.id.wheel);

        unlock = (UnlockBar) findViewById(R.id.unlock);
        // Attach listener
        unlock.setOnUnlockListener(new UnlockBar.OnUnlockListener() {
            @Override
            public void onUnlock() {
            Intent i = new Intent(BusinessDetail.this,SpinWheel.class);
            startActivity(i);
            finish();
            }
        });

        parentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
            unlock.dispatchTouchEvent(motionEvent);
            return false;
            }
        });

        try {
            Intent i = getIntent();
            strBusinessTitle = i.getStringExtra("business_title");
            business_id = i.getStringExtra("business_id");
            title.setText(strBusinessTitle);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        viewInitializer();
        setUpNavigationView();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void viewInitializer(){
      //  toolbar = findViewById(R.id.toolbar);
        business_logo = findViewById(R.id.business_logo);
        address = findViewById(R.id.address);
        state_city = findViewById(R.id.state_city);
        timings = findViewById(R.id.timings);
        deals_rv = findViewById(R.id.deals_rv);
        view_all_text = findViewById(R.id.view_all_text);
        img_phone = findViewById(R.id.img_phone);
        img_location = findViewById(R.id.img_location);
        img_like = findViewById(R.id.img_like);
     //  wheel =  findViewById(R.id.wheel);
        no_deals_avaiable = findViewById(R.id.no_deals_avaiable);

        currentTime = new SimpleDateFormat("hh:mma", Locale.getDefault()).format(new Date());
        Log.v("current_time",currentTime);

        img_share = findViewById(R.id.img_share);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        btn_share_and_get_points = findViewById(R.id.btn_share_and_get_points);
        img_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(phone_number!=null  ){
                try {
                    if(isPermissionGranted()){
                        call_action();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else{
                Toast.makeText(BusinessDetail.this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
            }
        });

        img_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          if( strLat == null){

              Toast.makeText(BusinessDetail.this, "No location available for this business", Toast.LENGTH_SHORT).show();

          }else{
              Intent i = new Intent(BusinessDetail.this, MapActivity.class);
              i.putExtra("lat",strLat);
              i.putExtra("lng",strLng);
              i.putExtra("business_title",strBusinessTitle);
              startActivity(i);
          }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_hotel:
                        Intent webView = new Intent(BusinessDetail.this, ViewWeb.class);
                        webView.putExtra("url","https://secure.rezserver.com/cities/4453-800049132?cname=secure.atlanticcity.com");
                        startActivity(webView);
                        break;

                    case R.id.nav_shows:
                        Intent webView_1 = new Intent(BusinessDetail.this, ViewWeb.class);
                        webView_1.putExtra("url","http://www.atlanticcity.com/atlantic-city-shows");
                        startActivity(webView_1);
                        break;

                    case R.id.nav_wheel:
                        Intent browserIntent3 = new Intent(BusinessDetail.this, SpinWheel.class);
                        startActivity(browserIntent3);
                        finish();
                        break;

                    case R.id.nav_home:
                        Intent browserIntent2 = new Intent(BusinessDetail.this, DealsFullScreen.class);
                        startActivity(browserIntent2);
                        finish();
                        break;

                    case R.id.nav_invite:
                        Intent i3 = new Intent(BusinessDetail.this, InviteFriends.class);
                        startActivity(i3);
                        break;


                }
                return true;
            }
        });


        btn_share_and_get_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Atlantic City");
                String shareMessage= "Check out Atlantic City App, Earn Points & Get Rewards frpm the Shows. Get it for free at http://www.atlanticcity.com/";

                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch(Exception e) {
                e.printStackTrace();
            }
            }
        });

        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Atlantic City");
                String shareMessage= "Check out Atlantic City App, Get it for free at http://www.atlanticcity.com/";

                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch(Exception e) {
                //e.toString();
            }
            }
        });

        view_all_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(BusinessDetail.this,AllDeals.class);
            i.putExtra("business_id",business_id);
            startActivity(i);
            }
        });

        LinearLayoutManager layoutManagerTop = new LinearLayoutManager(this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(BusinessDetail.this) {
                    private static final float SPEED = 2000f;// Change this value (default=25f)

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
        layoutManagerTop.setOrientation(LinearLayoutManager.HORIZONTAL);
        deals_rv.setLayoutManager(layoutManagerTop);
        deals_rv.setHasFixedSize(true);
        deals_rv.setItemViewCacheSize(10);
        deals_rv.setDrawingCacheEnabled(true);
        deals_rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        img_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(isInternet){
                AddFavoriteBusiness();
            }else {
                Toast.makeText(BusinessDetail.this, getResources().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
            }
            }
        });

        helper = new ConnectionHelper(BusinessDetail.this);
        isInternet = helper.isConnectingToInternet();
       // setupToolbar();

        if(isInternet){
            GetBusinessDetails();
        }else {
            Toast.makeText(BusinessDetail.this, getResources().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(  menuLayout.getVisibility() == View.GONE){
                menuLayout.setVisibility(View.VISIBLE);
            }else {
                menuLayout.setVisibility(View.GONE);
            }
            }
        });


        businessNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuLayout.setVisibility(View.GONE);
                Intent i5 = new Intent(BusinessDetail.this, MainActivity.class);
                startActivity(i5);
                finish();
            }
        });

        favoritesNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuLayout.setVisibility(View.GONE);
                Intent i5 = new Intent(BusinessDetail.this, Favourites.class);
                startActivity(i5);
                finish();
            }
        });

        settingsNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuLayout.setVisibility(View.GONE);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(strBusinessTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent i = new Intent(BusinessDetail.this,DealsFullScreen.class);
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void GetBusinessDetails() {
        showDialog();
        AndroidNetworking.post(URLHelper.GET_BUSINESS_DETAIL)
                .addHeaders("user-id", SharedHelper.getKey(this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(this,"auth_id"))
                .addBodyParameter("business_id", business_id)
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
                                    Type listType = new TypeToken<List<BusinessModel>>() {
                                    }.getType();
                                    businessModels = gson.fromJson(responseObj.getString("detail"), listType);
                                    phone_number = businessModels.get(0).getPhone_number();
                                    strLat = businessModels.get(0).getLat();
                                    strLng = businessModels.get(0).getLng();
                                    address.setText(businessModels.get(0).getAddress());

                                    state_city.setText(businessModels.get(0).getCity()+", "+ businessModels.get(0).getState()
                                            +", "+ businessModels.get(0).getZipcode()+".");
                                    timings.setText(businessModels.get(0).getOpen_time()+"-"+businessModels.get(0).getClose_time());
                                    Picasso.get().load(businessModels.get(0).getLogo()).placeholder(getResources().getDrawable(R.drawable.logo)).into(business_logo);
                                    if(businessModels.get(0).getBusinessDeals().size()>0){
                                        businessDealAdapter = new BusinessDealAdapter(businessModels.get(0).getBusinessDeals());
                                        deals_rv.setAdapter(businessDealAdapter);
                                        businessDealAdapter.notifyDataSetChanged();
                                        no_deals_avaiable.setVisibility(View.GONE);
                                    }else {
                                        deals_text.setVisibility(View.GONE);
                                        no_deals_avaiable.setVisibility(View.VISIBLE);
                                        deals_rv.setVisibility(View.GONE);
                                        view_all_text.setVisibility(View.INVISIBLE);
                                    }

                                    if(businessModels.get(0).getIs_favorited_count().equalsIgnoreCase("true")){
                                        img_like.setImageDrawable(getResources().getDrawable(R.drawable.favorite_business));
                                    }else {
                                        img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                                    }


                                    /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mma");

                                    Date date1 = simpleDateFormat.parse(businessModels.get(0).getOpen_time());
                                    Date date2 = simpleDateFormat.parse(businessModels.get(0).getClose_time());

                                    long difference = date2.getTime() - date1.getTime();
                                    int days = (int) (difference / (1000*60*60*24));
                                    int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
                                    int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
                                    hours = (hours < 0 ? -hours : hours);*/

                                  //  Log.v("difference_in_hours",""+hours+"");
                                   // status.setText(""+hours+""+ " HOURS");

                                  //  isTimeBetweenTwoTime(businessModels.get(0).getOpen_time(),businessModels.get(0).getClose_time(),currentTime);
                                    EarnPointsList();


                                }
                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                       // dismissDialog();

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
                Toast.makeText(BusinessDetail.this,""+toastString,Toast.LENGTH_SHORT).show();
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
        customDialog = new CustomDialog(BusinessDetail.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }


    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    call_action();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    void call_action(){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone_number));
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        unlock.reset();
    }

    private void AddFavoriteBusiness() {

        showDialog();
        AndroidNetworking.post(URLHelper.ADD_FAVORITE_BUSINESS)

                .addBodyParameter("business_id", business_id)
                .addHeaders("user-id", SharedHelper.getKey(BusinessDetail.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(BusinessDetail.this,"auth_id"))
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject errorObj = response.getJSONObject("error");
                            if(errorObj.optString("status").equals("1")){
                                displayMessage(errorObj.optString("message"));
                            }else{
                                JSONObject responseObj = response.getJSONObject("response");
                                if(responseObj.optString("status").equals("1")){

                                    if(responseObj.optString("message").equalsIgnoreCase("Business un-favorited")){
                                        img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                                    }else if(responseObj.optString("message").equalsIgnoreCase("Business favorited")){
                                        img_like.setImageDrawable(getResources().getDrawable(R.drawable.favorite_business));

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



    private void EarnPointsList() {
       // showDialog();
        AndroidNetworking.post(URLHelper.EARNED_POINTS_LIST)
                .addHeaders("user-id", SharedHelper.getKey(BusinessDetail.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(BusinessDetail.this,"auth_id"))
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

                                    // JSONObject detail = responseObj.getJSONObject("detail");

                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<EarnedPointsModel>>() {
                                    }.getType();
                                    earnedPointsModel = gson.fromJson(responseObj.getString("detail"), listType);
                                    tvPoints.setText(earnedPointsModel.get(0).getUserModel().getPoints());
                                    main_layout.setVisibility(View.VISIBLE);
                                  // available_points.setText(earnedPointsModel.get(0).getUserModel().getPoints());

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
                        Intent i = new Intent(BusinessDetail.this,HowToEarnPoints.class);
                        startActivity(i);
                        return true;


                    case R.id.nav_about_us:
                     //   drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i8 = new Intent(BusinessDetail.this,AboutUs.class);
                        startActivity(i8);
                        return true;

                    case R.id.nav_invitess:
                        //   drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i11 = new Intent(BusinessDetail.this,ViewInvites.class);
                        startActivity(i11);
                        return true;

                  /*  case R.id.nav_nearby:
                        //   viewPager.setCurrentItem(1);
                        Intent i9 = new Intent(BusinessDetail.this,DealsFullScreen.class);
                        startActivity(i9);
                      //  drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
*/
                    case R.id.nav_notifications:
                   //     drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i5 = new Intent(BusinessDetail.this,Notifications.class);
                        startActivity(i5);
                        return true;

                    case R.id.nav_settings:
                       // drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i4 = new Intent(BusinessDetail.this,Settings.class);
                        startActivity(i4);

                        return true;


                    case R.id.nav_earned_points:
                     //   drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i2 = new Intent(BusinessDetail.this,EarnedPoints.class);
                        startActivity(i2);
                        return true;

                    case R.id.nav_deals:
                        //   viewPager.setCurrentItem(0);
                        Intent i6 = new Intent(BusinessDetail.this,DealsFullScreen.class);
                        startActivity(i6);
                      //  drawerLayout.closeDrawer(GravityCompat.START);
                        return true;

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
            LayoutInflater inflater = (LayoutInflater)BusinessDetail.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setMessage(getString(R.string.logout_alert));
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    helper = new ConnectionHelper(BusinessDetail.this);
                    isInternet = helper.isConnectingToInternet();
                    if(isInternet){
                        LogOut();
                    }else {
                        Toast.makeText(BusinessDetail.this, BusinessDetail.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
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
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(BusinessDetail.this, R.color.colorPrimaryDark));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(BusinessDetail.this, R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }

    private void LogOut() {
        drawerLayout.closeDrawer(GravityCompat.START);
        showDialog();
        AndroidNetworking.post(URLHelper.LOGOUT)

                .addBodyParameter("user-id", SharedHelper.getKey(BusinessDetail.this,"user_id"))
                .addHeaders("user-id", SharedHelper.getKey(BusinessDetail.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(BusinessDetail.this,"auth_id"))
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

                                    Toast.makeText(BusinessDetail.this, responseObj.optString("message"), Toast.LENGTH_SHORT).show();
                                    SharedHelper.putKey(BusinessDetail.this,"logged_in","false");
                                    SharedHelper.putKey(BusinessDetail.this,"user_id","");
                                    SharedHelper.putKey(BusinessDetail.this,"auth_id","");
                                    SharedHelper.putKey(BusinessDetail.this,"zipcode","");
                                    SharedHelper.putKey(BusinessDetail.this,"date_of_birth","");
                                    SharedHelper.putKey(BusinessDetail.this,"notification","");
                                    SharedHelper.putKey(BusinessDetail.this,"share_app_status","");
                                    SharedHelper.putKey(BusinessDetail.this,"referral_user_id","");
                                    SharedHelper.putKey(BusinessDetail.this,"first_name","");
                                    SharedHelper.putKey(BusinessDetail.this,"last_name","");
                                    GlobalDealsData.clear();
                                    Intent goToLogin = new Intent(BusinessDetail.this, WelcomeActivity.class);
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

}
