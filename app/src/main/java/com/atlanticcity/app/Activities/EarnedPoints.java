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
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.atlanticcity.app.Adapters.EarnedPointsAdapter;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.EarnedPointsModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.atlanticcity.app.Utils.GridSpacingItemDecoration;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EarnedPoints extends AppCompatActivity {
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    Toolbar toolbar;
    RecyclerView earned_points_rv;
    TextView earned_points,available_points;
    List<EarnedPointsModel> earnedPointsModel = new ArrayList<>();
    EarnedPointsAdapter earnedPointsAdapter;
    String strEarnedPoints;
    int totalpoints;
    public String TAG = "EarnedPoints";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earned_points);
        viewInitializer();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void viewInitializer(){
        toolbar = findViewById(R.id.toolbar);
        earned_points = findViewById(R.id.earned_points);
        available_points = findViewById(R.id.available_points);
        earned_points_rv = findViewById(R.id.earned_points_rv);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(EarnedPoints.this, 1);
        earned_points_rv.setLayoutManager(gridLayoutManager1);
        earned_points_rv.setItemAnimator(new DefaultItemAnimator());
        earned_points_rv.setNestedScrollingEnabled(false);
        earned_points_rv.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        helper = new ConnectionHelper(EarnedPoints.this);
        isInternet = helper.isConnectingToInternet();

        if(isInternet){
            EarnPointsList();

        }else {
            Toast.makeText(EarnedPoints.this, EarnedPoints.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }
        setupToolbar();
    }

    void dismissDialog(){
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();
    }

    void showDialog(){
        customDialog = new CustomDialog(EarnedPoints.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.earned_points));
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
                Toast.makeText(EarnedPoints.this,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    public void EarnPointsList() {
        showDialog();
        AndroidNetworking.post(URLHelper.EARNED_POINTS_LIST)
                .addHeaders("user-id", SharedHelper.getKey(EarnedPoints.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(EarnedPoints.this,"auth_id"))
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
                                    earnedPointsModel = new ArrayList<>();
                                    earnedPointsModel = gson.fromJson(responseObj.getString("detail"), listType);
                                    available_points.setText(earnedPointsModel.get(0).getUserModel().getPoints());
                                    totalpoints = 0;
                                    for(int i = 0 ; i < earnedPointsModel.size() ; i++){

                                        totalpoints = totalpoints+Integer.valueOf(earnedPointsModel.get(i).getPoints());
                                    }
                                    earned_points.setText(""+totalpoints+"");

                                    List<EarnedPointsModel> earnedPointsModelList = new ArrayList<>();
                                    for(int i = 0 ; i < earnedPointsModel.size();i++){
                                        if(earnedPointsModel.get(i).getStatus().equalsIgnoreCase("1")){
                                            earnedPointsModelList.add(earnedPointsModel.get(i));
                                        }
                                    }
                                    earnedPointsAdapter = new EarnedPointsAdapter(EarnedPoints.this,earnedPointsModelList);
                                    earned_points_rv.setAdapter(earnedPointsAdapter);
                                    earnedPointsAdapter.notifyDataSetChanged();

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
}
