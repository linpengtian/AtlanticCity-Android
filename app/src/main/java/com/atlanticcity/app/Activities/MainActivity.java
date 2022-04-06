package com.atlanticcity.app.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.atlanticcity.app.CustomFont.CustomTypefaceSpan;
import com.atlanticcity.app.Fragments.Businesses;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

import org.json.JSONObject;

import static com.atlanticcity.app.Activities.SplashActivity.GlobalDealsData;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    private DrawerLayout drawerLayout;
    NavigationView navigationView;

    ImageView back_arrow;
    BottomNavigationView bottomNavigationView;
    EditText et_search;
    ImageView search,cross_icon;
    String type,searched_value;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    public int currentIndex;
    public String TAG = "MainActivity";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        type = "business";
        viewInitializer();
        et_search.setVisibility(View.VISIBLE);
        //cross_icon.setVisibility(View.VISIBLE);
       // back_arrow.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void viewInitializer(){
       // toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        back_arrow = findViewById(R.id.back_arrow);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.getMenu().getItem(0).setChecked(true);

        search = findViewById(R.id.search);
        cross_icon = findViewById(R.id.cross_icon);

        et_search = findViewById(R.id.et_search);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new Businesses())
                .commitAllowingStateLoss();
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_hotel:
                        Intent webView = new Intent(MainActivity.this, ViewWeb.class);
                        webView.putExtra("url","https://secure.rezserver.com/cities/4453-800049132?cname=secure.atlanticcity.com");
                        startActivity(webView);
                        break;

                    case R.id.nav_shows:
                        Intent webView_1 = new Intent(MainActivity.this, ViewWeb.class);
                        webView_1.putExtra("url","http://www.atlanticcity.com/atlantic-city-shows");
                        startActivity(webView_1);
                        break;

                    case R.id.nav_wheel:
                        Intent i = new Intent(MainActivity.this, SpinWheel.class);
                        startActivity(i);
                        break;

                    case R.id.nav_invite:
                        Intent i2 = new Intent(MainActivity.this, InviteFriends.class);
                        startActivity(i2);
                        break;

                    case R.id.nav_home:
                        Intent i3 = new Intent(MainActivity.this, DealsFullScreen.class);
                        startActivity(i3);
                        break;
                }
                return true;
            }
        });


        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(MainActivity.this);
                Intent i = new Intent(MainActivity.this,SearchActivity.class);
                i.putExtra("type",type);
                i.putExtra("searched_value",et_search.getText().toString().trim());
                startActivity(i);
                return true;
            }
            return false;
            }
        });

        cross_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            et_search.setVisibility(View.GONE);
            cross_icon.setVisibility(View.GONE);
            back_arrow.setVisibility(View.VISIBLE);
            }
        });


     /*   search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

      */

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        setUpNavigationView();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(drawerLayout.isDrawerOpen(navigationView)){
            drawerLayout.closeDrawers();
        }
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
                        //startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        showLogoutDialog();
                        return true;

                    case R.id.nav_how_to_earn_points:
                      //  drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i = new Intent(MainActivity.this,HowToEarnPoints.class);
                        startActivity(i);
                        return true;

                    case R.id.nav_about_us:
                     //   drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i8 = new Intent(MainActivity.this,AboutUs.class);
                        startActivity(i8);
                        return true;

                    case R.id.nav_invitess:
                        //   drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i11 = new Intent(MainActivity.this,ViewInvites.class);
                        startActivity(i11);
                        return true;

                 /*   case R.id.nav_nearby:
                        drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
*/
                    case R.id.nav_notifications:
                      //  drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i5 = new Intent(MainActivity.this,Notifications.class);
                        startActivity(i5);
                        return true;

                    case R.id.nav_settings:
                      //  drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i4 = new Intent(MainActivity.this,Settings.class);
                        startActivity(i4);

                        return true;

                    case R.id.nav_earned_points:
                       // drawerLayout.closeDrawer(GravityCompat.START);
                        Intent i2 = new Intent(MainActivity.this,EarnedPoints.class);
                        startActivity(i2);
                        return true;

                    case R.id.nav_deals:
                     //   viewPager.setCurrentItem(0);
                        Intent i6 = new Intent(MainActivity.this,DealsFullScreen.class);
                        startActivity(i6);
                     //   drawerLayout.closeDrawer(GravityCompat.START);

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

    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater)MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setMessage(getString(R.string.logout_alert));
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    helper = new ConnectionHelper(MainActivity.this);
                    isInternet = helper.isConnectingToInternet();
                    if(isInternet){
                        LogOut();
                    }else {
                        Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
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
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "Font/Roboto-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
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


    private void LogOut() {
        drawerLayout.closeDrawer(GravityCompat.START);
        showDialog();
        AndroidNetworking.post(URLHelper.LOGOUT)

                .addBodyParameter("user-id", SharedHelper.getKey(MainActivity.this,"user_id"))
                .addHeaders("user-id", SharedHelper.getKey(MainActivity.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(MainActivity.this,"auth_id"))
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

                                    Toast.makeText(MainActivity.this, responseObj.optString("message"), Toast.LENGTH_SHORT).show();
                                    SharedHelper.putKey(MainActivity.this,"logged_in","false");
                                    SharedHelper.putKey(MainActivity.this,"user_id","");
                                    SharedHelper.putKey(MainActivity.this,"auth_id","");
                                    SharedHelper.putKey(MainActivity.this,"zipcode","");
                                    SharedHelper.putKey(MainActivity.this,"date_of_birth","");
                                    SharedHelper.putKey(MainActivity.this,"notification","");
                                    SharedHelper.putKey(MainActivity.this,"share_app_status","");
                                    SharedHelper.putKey(MainActivity.this,"referral_user_id","");
                                    SharedHelper.putKey(MainActivity.this,"first_name","");
                                    SharedHelper.putKey(MainActivity.this,"last_name","");
                                    GlobalDealsData.clear();
                                    Intent goToLogin = new Intent(MainActivity.this, WelcomeActivity.class);
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

    public void displayMessage(  String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(MainActivity.this,""+toastString,Toast.LENGTH_SHORT).show();
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
        customDialog = new CustomDialog(MainActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }
}
