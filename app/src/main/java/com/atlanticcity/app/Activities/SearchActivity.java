package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.atlanticcity.app.Adapters.BusinessAdapter;

import com.atlanticcity.app.Adapters.SearchedDealsAdapter;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.BusinessModel;
import com.atlanticcity.app.Models.DealsModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.atlanticcity.app.Utils.GridSpacingItemDecoration;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    Toolbar toolbar;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    String type,searched_value;
    RecyclerView search_rv;
    List<DealsModel> dealsModels = new ArrayList<>();
    SearchedDealsAdapter searchedDealsAdapter;
    TextView no_record_found;
    BusinessAdapter businessAdapter;
    List<BusinessModel> businessModels = new ArrayList<>();
    public String TAG = "SearchActivity";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        viewInitializer();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void viewInitializer(){
        toolbar = findViewById(R.id.toolbar);
        helper = new ConnectionHelper(SearchActivity.this);
        isInternet = helper.isConnectingToInternet();
        search_rv = findViewById(R.id.search_rv);
        no_record_found = findViewById(R.id.no_record_found);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(SearchActivity.this, 1);
        search_rv.setLayoutManager(gridLayoutManager1);
        search_rv.setItemAnimator(new DefaultItemAnimator());
        search_rv.setNestedScrollingEnabled(false);
        search_rv.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        try {
            Intent i = getIntent();
            type = i.getStringExtra("type");
            searched_value = i.getStringExtra("searched_value");
          //  Toast.makeText(this, type, Toast.LENGTH_SHORT).show();

            if(isInternet){
                if(type.equalsIgnoreCase("deals")){
                    SearchDeals();
                }else {
                    GetSearchedBusiness();
                }

            }else {
                Toast.makeText(SearchActivity.this, getResources().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

        setupToolbar();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.search_result));
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



    public void displayMessage(  String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(SearchActivity.this,""+toastString,Toast.LENGTH_SHORT).show();
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
        customDialog = new CustomDialog(SearchActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }


    private void SearchDeals() {
        showDialog();
        AndroidNetworking.post(URLHelper.SEARCH)

                .addBodyParameter("searched_value", searched_value)
                .addBodyParameter("type", type)
                .addHeaders("user-id", SharedHelper.getKey(SearchActivity.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(SearchActivity.this,"auth_id"))
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {

                            dismissDialog();
                            JSONObject errorObj = response.getJSONObject("error");
                            if(errorObj.optString("status").equals("1")){
                                displayMessage(errorObj.optString("message"));
                            }else{
                                JSONObject responseObj = response.getJSONObject("response");
                                if(responseObj.optString("status").equals("1")){

                                    if(responseObj.optString("message").equalsIgnoreCase("Record not found")){

                                        search_rv.setVisibility(View.GONE);
                                        no_record_found.setVisibility(View.VISIBLE);
                                    }else {
                                        search_rv.setVisibility(View.VISIBLE);
                                        no_record_found.setVisibility(View.GONE);
                               /* JSONArray detail = responseObj.getJSONArray("detail");
                                        JSONObject detailOBj = detail.getJSONObject(1);*/
                                        Gson gson = new Gson();
                                        Type listType = new TypeToken<List<DealsModel>>() {
                                        }.getType();
                                        dealsModels = gson.fromJson(responseObj.getString("detail"), listType);
                                        searchedDealsAdapter = new SearchedDealsAdapter(dealsModels);
                                        search_rv.setAdapter(searchedDealsAdapter);
                                        searchedDealsAdapter.notifyDataSetChanged();
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
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    private void GetSearchedBusiness() {

        showDialog();
        AndroidNetworking.post(URLHelper.SEARCH)

                .addBodyParameter("searched_value", searched_value)
                .addBodyParameter("type", type)
                .addHeaders("user-id", SharedHelper.getKey(SearchActivity.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(SearchActivity.this,"auth_id"))
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {

                            dismissDialog();
                            JSONObject errorObj = response.getJSONObject("error");
                            if(errorObj.optString("status").equals("1")){
                                displayMessage(errorObj.optString("message"));
                            }else{
                                JSONObject responseObj = response.getJSONObject("response");
                                if(responseObj.optString("status").equals("1")){

                                    if(responseObj.optString("message").equalsIgnoreCase("Record not found")){

                                        search_rv.setVisibility(View.GONE);
                                        no_record_found.setVisibility(View.VISIBLE);
                                    }else {
                                        search_rv.setVisibility(View.VISIBLE);
                                        no_record_found.setVisibility(View.GONE);

                                        Gson gson = new Gson();
                                        Type listType = new TypeToken<List<BusinessModel>>() {
                                        }.getType();
                                        businessModels = gson.fromJson(responseObj.getString("detail"), listType);
                                        businessAdapter = new BusinessAdapter(businessModels);
                                        search_rv.setAdapter(businessAdapter);
                                        businessAdapter.notifyDataSetChanged();
                                    }
                                    // JSONObject detail = responseObj.getJSONObject("detail");

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
}
