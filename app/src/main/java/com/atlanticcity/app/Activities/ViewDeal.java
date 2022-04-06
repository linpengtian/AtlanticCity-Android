package com.atlanticcity.app.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.atlanticcity.app.CustomFont.CustomTypefaceSpan;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.DealsModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.atlanticcity.app.Activities.SplashActivity.GlobalDealsData;

public class ViewDeal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    public TextView business_name,address,deal_title,deal_description,ppl_claimed,businessNavigator, favoritesNavigator, settingsNavigator;
    public CircleImageView business_logo;
    RoundedImageView deal_avatar;
    RelativeLayout main_layout;
    ImageView image_share,image_like;
    String str_avatar,str_deal_title,str_deal_desc,str_is_favourite,item_id;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    List<DealsModel> dealsModels = new ArrayList<>();
    String dealExpireAt,business_address,string_business_name;
    public String TAG = "ViewDeal";
    Button btn_get_this_deal;
    ImageView menu;
    LinearLayout menuLayout;

    public DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar_;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_deal);
        viewInitializer();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void viewInitializer(){
        toolbar = findViewById(R.id.toolbar);
        business_name =  findViewById(R.id.business_name);
        business_logo =  findViewById(R.id.business_logo);
        deal_avatar = findViewById(R.id.deal_avatar);
        address = findViewById(R.id.address);
        deal_title = findViewById(R.id.deal_title);
        deal_description = findViewById(R.id.deal_description);
        main_layout = findViewById(R.id.main_layout);
        image_share = findViewById(R.id.image_share);
        image_like = findViewById(R.id.image_like);
        btn_get_this_deal = findViewById(R.id.btn_get_this_deal);
        ppl_claimed = findViewById(R.id.ppl_claimed);
        menu = findViewById(R.id.back_arrow);
        menuLayout = findViewById(R.id.menu_layout);
        businessNavigator = findViewById(R.id.business_navigator);
        favoritesNavigator = findViewById(R.id.favorites_navigator);
        settingsNavigator = findViewById(R.id.settings_navigator);
        navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.getMenu().getItem(0).setChecked(true);

        helper = new ConnectionHelper(ViewDeal.this);
        isInternet = helper.isConnectingToInternet();
        setupToolbar();

        try {
            Intent i = getIntent();
            item_id = i.getStringExtra("item_id");
            str_avatar = i.getStringExtra("avatar");
            str_deal_title = i.getStringExtra("deal_title");
            str_deal_desc = i.getStringExtra("deal_desc");
            str_is_favourite = i.getStringExtra("is_favourite");

            Picasso.get().load(str_avatar).placeholder(getResources().getDrawable(R.drawable.logo)).into(deal_avatar);

            deal_title.setText(str_deal_title);
            deal_description.setText(str_deal_desc);


        }catch (Exception ex){
            ex.printStackTrace();
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

        image_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(isInternet){
                AddFavoriteDeal();
            }else {
                Toast.makeText(ViewDeal.this, ViewDeal.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
            }
            }
        });

        btn_get_this_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(ViewDeal.this, SingleDealActivity.class);
            i.putExtra("item_id",item_id);
            i.putExtra("deal_title",str_deal_title);
            i.putExtra("deal_description",str_deal_desc);
            i.putExtra("deal_expire_at",dealExpireAt);
            i.putExtra("business_address",business_address);
            i.putExtra("business_name",string_business_name);

            startActivity(i);
            }
        });

        main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            menuLayout.setVisibility(View.GONE);
            }
        });

        image_share.setOnClickListener(new View.OnClickListener() {
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

        businessNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           menuLayout.setVisibility(View.GONE);
           Intent i5 = new Intent(ViewDeal.this, MainActivity.class);
           startActivity(i5);
           finish();
            }
        });

        favoritesNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           menuLayout.setVisibility(View.GONE);
           Intent i5 = new Intent(ViewDeal.this, Favourites.class);
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

        if(isInternet){
            GetSingleDeal();
        }else {
            Toast.makeText(ViewDeal.this, ViewDeal.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }

        setUpNavigationView();
    }


    public void displayMessage(  String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(ViewDeal.this,""+toastString,Toast.LENGTH_SHORT).show();
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
        customDialog = new CustomDialog(ViewDeal.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.deal_detail));
        //toolbar.setNavigationIcon();
        toolbar.setNavigationIcon(null);
      //  toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
     //   getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     //   getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);



        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu,true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                onBackPressed();
                break;
            case R.id.business_navigator:
                Intent i5 = new Intent(ViewDeal.this, MainActivity.class);
                startActivity(i5);
                finish();
                break;
            case R.id.favorites_navigator:
                Intent i6 = new Intent(ViewDeal.this, Favourites.class);
                startActivity(i6);
                finish();
            break;
            case R.id.settings_navigator:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void GetSingleDeal() {

        showDialog();
        AndroidNetworking.post(URLHelper.GET_SINGLE_DEAL)
                .addHeaders("user-id", SharedHelper.getKey(this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(this,"auth_id"))
                .addBodyParameter("item_id", item_id)
                .addBodyParameter("deal_id", item_id)
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
                                if(responseObj.optString("message").equalsIgnoreCase("Deal not found")){
                                    showDialog(responseObj.optString("message"));
                                //    displayMessage(responseObj.optString("message"));
                                }else {
                                    JSONObject detailObj = responseObj.getJSONObject("detail");
                                    JSONObject businessObj = detailObj.getJSONObject("business");


                                    if(responseObj.optString("status").equals("1")){
                                        Picasso.get().load(businessObj.optString("logo")).into(business_logo);
                                        Glide.with(ViewDeal.this)
                                                .load( detailObj.optString("avatar"))
                                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                                .placeholder(R.drawable.splash_bg)
                                                .error(R.drawable.splash_bg)
                                                .into(deal_avatar);

                                        business_name.setText(businessObj.optString("business_name"));
                                        address.setText(businessObj.optString("address")+","+businessObj.optString("city"));
                                        deal_title.setText(detailObj.optString("title"));
                                        deal_description.setText(detailObj.optString("description"));
                                        dealExpireAt = detailObj.getString("deal_expire_at");
                                        string_business_name = businessObj.optString("business_name");
                                        business_address = businessObj.optString("address");
                                        ppl_claimed.setText(detailObj.optString("deal_views_count")+"\nViews");

                                        if (detailObj.optString("is_favorited_count").equalsIgnoreCase("true")){
                                            image_like.setImageDrawable(getResources().getDrawable(R.drawable.heart_red));
                                        }else{
                                            image_like.setImageDrawable(getResources().getDrawable(R.drawable.heart));
                                        }

                                        main_layout.setVisibility(View.VISIBLE);

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

    private void AddFavoriteDeal() {

        showDialog();
        AndroidNetworking.post(URLHelper.ADD_FAVORITE_DEAL)
                .addHeaders("user-id", SharedHelper.getKey(this, "user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(this, "auth_id"))
                .addBodyParameter("item_id", item_id)
                .addBodyParameter("deal_id", item_id)
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
                                    GlobalDealsData.clear();
                                    if(responseObj.optString("message").equalsIgnoreCase("Successfully deal un-favorited")){
                                        image_like.setImageDrawable(getResources().getDrawable(R.drawable.heart));
                                    }else if(responseObj.optString("message").equalsIgnoreCase("Successfully deal favorited")){
                                        image_like.setImageDrawable(getResources().getDrawable(R.drawable.heart_red));

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
                                if (errorJson.optJSONObject("error").optString("status").equals("1")) {
                                    displayMessage(errorJson.optJSONObject("error").optString("message"));
                                }
                            } catch (Exception ex) {
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
                    //    drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i = new Intent(ViewDeal.this,HowToEarnPoints.class);
                        startActivity(i);
                        return true;


                    case R.id.nav_about_us:
                    //    drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i8 = new Intent(ViewDeal.this,AboutUs.class);
                        startActivity(i8);
                        return true;

                    case R.id.nav_invitess:
                        //   drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i11 = new Intent(ViewDeal.this,ViewInvites.class);
                        startActivity(i11);
                        return true;

                    /*case R.id.nav_nearby:
                        //   viewPager.setCurrentItem(1);

                      //  drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i9 = new Intent(ViewDeal.this,DealsFullScreen.class);
                        startActivity(i9);
                        return true;
*/
                    case R.id.nav_notifications:
                     //   drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i5 = new Intent(ViewDeal.this,Notifications.class);
                        startActivity(i5);
                        return true;

                    case R.id.nav_settings:
                     //   drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i4 = new Intent(ViewDeal.this,Settings.class);
                        startActivity(i4);

                        return true;

                    case R.id.nav_earned_points:
                      //  drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i2 = new Intent(ViewDeal.this,EarnedPoints.class);
                        startActivity(i2);
                        return true;

                    case R.id.nav_deals:
                        //   viewPager.setCurrentItem(0);
                        Intent i6 = new Intent(ViewDeal.this,DealsFullScreen.class);
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
            LayoutInflater inflater = (LayoutInflater)ViewDeal.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setMessage(getString(R.string.logout_alert));
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    helper = new ConnectionHelper(ViewDeal.this);
                    isInternet = helper.isConnectingToInternet();
                    if(isInternet){
                        LogOut();
                    }else {
                        Toast.makeText(ViewDeal.this, ViewDeal.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
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
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ViewDeal.this, R.color.colorPrimaryDark));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ViewDeal.this, R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }

    private void LogOut() {
        drawerLayout.closeDrawer(GravityCompat.START);
        showDialog();
        AndroidNetworking.post(URLHelper.LOGOUT)

                .addBodyParameter("user-id", SharedHelper.getKey(ViewDeal.this,"user_id"))
                .addHeaders("user-id", SharedHelper.getKey(ViewDeal.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(ViewDeal.this,"auth_id"))
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

                                    Toast.makeText(ViewDeal.this, responseObj.optString("message"), Toast.LENGTH_SHORT).show();
                                    SharedHelper.putKey(ViewDeal.this,"logged_in","false");
                                    SharedHelper.putKey(ViewDeal.this,"user_id","");
                                    SharedHelper.putKey(ViewDeal.this,"auth_id","");
                                    SharedHelper.putKey(ViewDeal.this,"zipcode","");
                                    SharedHelper.putKey(ViewDeal.this,"date_of_birth","");
                                    SharedHelper.putKey(ViewDeal.this,"notification","");
                                    SharedHelper.putKey(ViewDeal.this,"share_app_status","");
                                    SharedHelper.putKey(ViewDeal.this,"referral_user_id","");
                                    SharedHelper.putKey(ViewDeal.this,"first_name","");
                                    SharedHelper.putKey(ViewDeal.this,"last_name","");
                                    GlobalDealsData.clear();
                                    Intent goToLogin = new Intent(ViewDeal.this, WelcomeActivity.class);
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
    private void showDialog(String message) {
        if (!isFinishing()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater)ViewDeal.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                  onBackPressed();
                    //   ShowClaimDealForm();
                }
            });

            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ViewDeal.this, R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }

}
