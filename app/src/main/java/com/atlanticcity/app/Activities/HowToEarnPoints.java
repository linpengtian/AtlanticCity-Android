package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.atlanticcity.app.Adapters.PointsListAdapter;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.PointsModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HowToEarnPoints extends AppCompatActivity {
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    Toolbar toolbar;
    RecyclerView points_rv;
    PointsListAdapter pointsListAdapter;
    List<PointsModel> pointsModels = new ArrayList<>();
    RelativeLayout shop_and_earn_layout,dob_layout,zip_code_layout,share_app_layout;
    TextView shop_and_earn_points,shop_and_earn_points_item;
    TextView dob_points,dob_points_item;
    TextView zip_code_points,zip_code_points_item;
    TextView share_app_points,share_app_points_item;
    Button btn_view_deals;
    public String TAG = "HowToEarnPoints";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_earn_points);
        viewInitializer();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void viewInitializer(){
        toolbar = findViewById(R.id.toolbar);
        shop_and_earn_layout = findViewById(R.id.shop_and_earn_layout);
        dob_layout = findViewById(R.id.dob_layout);
        zip_code_layout = findViewById(R.id.zip_code_layout);
        share_app_layout = findViewById(R.id.share_app_layout);

        shop_and_earn_points = findViewById(R.id.shop_and_earn_points);
        shop_and_earn_points_item = findViewById(R.id.shop_and_earn_points_item);

        dob_points = findViewById(R.id.dob_points);
        dob_points_item = findViewById(R.id.dob_points_item);

        zip_code_points = findViewById(R.id.zip_code_points);
        zip_code_points_item = findViewById(R.id.zip_code_points_item);

        share_app_points = findViewById(R.id.share_app_points);
        share_app_points_item = findViewById(R.id.share_app_points_item);
        btn_view_deals = findViewById(R.id.btn_view_deals);

        btn_view_deals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(HowToEarnPoints.this,DealsFullScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            }
        });

     shop_and_earn_layout.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
         Intent i = new Intent(HowToEarnPoints.this,DealsFullScreen.class);
         startActivity(i);
         finish();
         }
     });

     dob_layout.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
         String date_of_birth = SharedHelper.getKey(HowToEarnPoints.this,"date_of_birth");
         if(date_of_birth.isEmpty() || date_of_birth.equalsIgnoreCase("") || date_of_birth == null || date_of_birth.equalsIgnoreCase("null")){
             Intent i = new Intent(HowToEarnPoints.this, DateOfBirth.class);
             startActivity(i);
             finish();
         }else{
             Toast.makeText(HowToEarnPoints.this, R.string.date_of_birth_exists, Toast.LENGTH_SHORT).show();
         }
         }
     });

     zip_code_layout.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
         String zipcode = SharedHelper.getKey(HowToEarnPoints.this,"zipcode");
         if(zipcode.isEmpty() || zipcode.equalsIgnoreCase("") || zipcode == null || zipcode.equalsIgnoreCase("null")){
             Intent i = new Intent(HowToEarnPoints.this, ZipCodeActivity.class);
              startActivity(i);
             finish();
         }else{
             Toast.makeText(HowToEarnPoints.this, R.string.zip_code_exists, Toast.LENGTH_SHORT).show();
         }
         }
     });

     share_app_layout.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
        Intent i = new Intent(HowToEarnPoints.this,SyncContactsActivity.class);
        startActivity(i);
         }
     });

        helper = new ConnectionHelper(HowToEarnPoints.this);
        isInternet = helper.isConnectingToInternet();
        setupToolbar();

        if(isInternet){
            HowToEarnPointsList();
        }else {
            Toast.makeText(HowToEarnPoints.this, HowToEarnPoints.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }
    }


    public void displayMessage(  String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(HowToEarnPoints.this,""+toastString,Toast.LENGTH_SHORT).show();
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
        customDialog = new CustomDialog(HowToEarnPoints.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.earn_more_points));
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



    private void HowToEarnPointsList() {
        showDialog();

        AndroidNetworking.post(URLHelper.HOW_TOEARN_POINTS)

                .addHeaders("user-id", SharedHelper.getKey(HowToEarnPoints.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(HowToEarnPoints.this,"auth_id"))
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
                                    Type listType = new TypeToken<List<PointsModel>>() {
                                    }.getType();
                                    pointsModels = gson.fromJson(responseObj.getString("detail"), listType);

                                    for(int i = 0; i <pointsModels.size();i++){
                                        PointsModel mList = pointsModels.get(i);

                                        if(pointsModels.get(i).getKey().equalsIgnoreCase("date_of_birth_points")){
                                            dob_points.setText(mList.getValue()+" points.");
                                            dob_points_item.setText(mList.getValue()+" POINTS");
                                            dob_layout.setVisibility(View.VISIBLE);
                                        }else if(pointsModels.get(i).getKey().equalsIgnoreCase("zipcode_points")){
                                            zip_code_points.setText(mList.getValue()+" points.");
                                            zip_code_points_item.setText(mList.getValue()+" POINTS");
                                            zip_code_layout.setVisibility(View.VISIBLE);
                                        }else if(pointsModels.get(i).getKey().equalsIgnoreCase("share_app_points")){
                                            share_app_points.setText(mList.getValue()+" points.");
                                            share_app_points_item.setText(mList.getValue()+" POINTS");
                                            String share_app_status = SharedHelper.getKey(HowToEarnPoints.this,"share_app_status");

                                            if(share_app_status.equalsIgnoreCase("") || share_app_status.equalsIgnoreCase("null") || share_app_status.isEmpty() || share_app_status == null || share_app_status.equalsIgnoreCase("0")){
                                                share_app_layout.setVisibility(View.VISIBLE);
                                            }else {
                                                share_app_layout.setVisibility(View.GONE);
                                            }
                                        }else if(pointsModels.get(i).getKey().equalsIgnoreCase("qrcode_points")){
                                            shop_and_earn_points.setText(mList.getValue()+" points.");
                                            shop_and_earn_points_item.setText(mList.getValue()+" POINTS");
                                            shop_and_earn_layout.setVisibility(View.VISIBLE);

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

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
