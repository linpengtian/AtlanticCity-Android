package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.atlanticcity.app.Adapters.EarnedPointsAdapter;
import com.atlanticcity.app.Adapters.NotificationsAdapter;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.DealsModel;
import com.atlanticcity.app.Models.NotifModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.atlanticcity.app.Utils.CustomRequestQueue;
import com.atlanticcity.app.Utils.GridSpacingItemDecoration;
import com.google.android.material.snackbar.Snackbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notifications extends AppCompatActivity {
    ConnectionHelper helper;
    Boolean isInternet;
    Toolbar toolbar;
    public static final String TAG = "SplashActivity";
    CustomDialog customDialog;
    List<NotifModel> notifModels = new ArrayList<>();
    NotificationsAdapter notificationsAdapter;
    RecyclerView notifications_rv;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        toolbar = findViewById(R.id.toolbar);
        setupToolbar();
        notifications_rv = findViewById(R.id.notifications_rv);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(Notifications.this, 1);
        notifications_rv.setLayoutManager(gridLayoutManager1);
        notifications_rv.setItemAnimator(new DefaultItemAnimator());
        notifications_rv.setNestedScrollingEnabled(false);
        notifications_rv.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        helper = new ConnectionHelper(Notifications.this);
        isInternet = helper.isConnectingToInternet();

        try {


            Intent i = getIntent();
            String  item_id = i.getStringExtra("item_id");

            if(item_id!=null && !item_id.isEmpty() && !item_id.equalsIgnoreCase("")){
                showDeal(i.getStringExtra("title"),i.getStringExtra("body"),i.getStringExtra("item_id"));
            }else {
                Bundle bundle = getIntent().getExtras();
                JSONObject json = new JSONObject();
                if(bundle!=null) {
                    for (String key : bundle.keySet()) {
                        Object value = bundle.get(key);
                        Log.d("DATA_SENT", String.format("%s %s (%s)", key,
                                value.toString(), value.getClass().getName()));
                        //  Toast.makeText(this, String.format("%s %s (%s)", key, value.toString(), value.getClass().getName()), Toast.LENGTH_SHORT).show();
                        try {
                            // json.put(key, bundle.get(key)); see edit below
                            json.put(key, JSONObject.wrap(value.toString()));
                        } catch(JSONException e) {
                            //Handle exception here
                        }

                    }
                    Log.v("deal_notification",json.toString());

                }

                if(json.optString("item_id")!=null && !json.optString("item_id").isEmpty() && !json.optString("item_id").equalsIgnoreCase("")){
                    //  Toast.makeText(this, json.optString("item_id"), Toast.LENGTH_SHORT).show();
                    showDeal(json.optString("title"),json.optString("body"),json.optString("item_id"));
                }
            }


        }catch (Exception ex){

        }

        if(isInternet){
            GetNotifications();

        }else {
            Toast.makeText(Notifications.this, Notifications.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.notifications));
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


    public void GetNotifications() {

        showDialog();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, URLHelper.GET_NOTIFICATIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject obj = new JSONObject(response);
                    JSONObject errorObj = obj.getJSONObject("error");
                    if(errorObj.optString("status").equals("1")){
                        displayMessage(errorObj.optString("message"));
                    }else {
                        JSONObject responseObj = obj.getJSONObject("response");
                        if (responseObj.optString("status").equals("1")) {
                            notifModels = new ArrayList<>();
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<NotifModel>>() {
                            }.getType();
                            notifModels = gson.fromJson(responseObj.getString("detail"), listType);
                            notificationsAdapter = new NotificationsAdapter(Notifications.this,notifModels);
                            notifications_rv.setAdapter(notificationsAdapter);

                        }

                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }

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
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("user-id", SharedHelper.getKey(Notifications.this,"user_id"));
                headers.put("auth-id", SharedHelper.getKey(Notifications.this,"auth_id"));
                return headers;
            }
        };

        CustomRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    public void displayMessage(  String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(Notifications.this,""+toastString,Toast.LENGTH_SHORT).show();
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
        customDialog = new CustomDialog(Notifications.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    private void showDeal(String title, String body, final String item_id) {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater)Notifications.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setTitle(title);
            builder.setMessage(body);
            builder.setPositiveButton("View", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Notifications.this,ViewDeal.class);
                    i.putExtra("item_id",item_id);
                    startActivity(i);

                }
            });
            builder.setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Reset to previous seletion menu in navigation
                    dialog.dismiss();

                }
            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(Notifications.this, R.color.colorPrimaryDark));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(Notifications.this, R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }
}
