package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.SpinModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;


public class SpinWheel extends AppCompatActivity {

    Toolbar toolbar;
    // spin_wheel ;
    LuckyWheelView spin_wheel;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    List<SpinModel> spinModels = new ArrayList<>();
    List<SpinModel> activeIndex ;
    List<LuckyItem> data;
    Button spin_it;
    String spinner_id;
    TextView user_points;
    String spinner_points;
    String collected_prize;
    FrameLayout frame_layout;
    public String TAG = "SpinWheel";
    TextView you_have,points_to,bottom_text;
    ImageView cursor_;
    Dialog alertDialog;
    EditText et_name,et_address;
    Button btn_claim_deal;
    TextView tv_skip;
    int points_in_account;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_wheel);
        toolbar = findViewById(R.id.toolbar);
        spin_wheel = findViewById(R.id.spin_wheel);
        helper = new ConnectionHelper(SpinWheel.this);
        isInternet = helper.isConnectingToInternet();
        spin_it = findViewById(R.id.spin_it);
        user_points = findViewById(R.id.user_points);
        frame_layout = findViewById(R.id.frame_layout);
        you_have = findViewById(R.id.you_have);
        points_to = findViewById(R.id.points_to);
        bottom_text = findViewById(R.id.bottom_text);
        cursor_ = findViewById(R.id.cursor_);

        setupToolbar();
        if(isInternet){
            GetEarnedPoints();
        }else {
            Toast.makeText(SpinWheel.this, getResources().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }

        spin_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if(points_in_account<2){
                Toast.makeText(SpinWheel.this, getResources().getString(R.string.not_enough_points), Toast.LENGTH_SHORT).show();
            }else {
              //  spin_wheel.setData(data);
                spin_wheel.setRound(15);
                int luckyIndexToSpin = 0;
                int index = 0;
                index = getRandomIndex();

                String strId = activeIndex.get(index).getId();

                for(int i = 0 ; i <spinModels.size() ; i ++){

                    if(spinModels.get(i).getId().equalsIgnoreCase(strId)){
                        luckyIndexToSpin = i;
                    }
                }

                Log.v("spinner_random",""+luckyIndexToSpin);
                spin_wheel.startLuckyWheelWithTargetIndex(luckyIndexToSpin);
            }

            }
        });

        spin_wheel.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
            spinner_id = data.get(index).spinner_id;
            Log.v("spinner_id",spinner_id);
            Log.v("spinner_index",""+index+"");

            collected_prize = data.get(index).topText;

            Collect_Spinner_Prize();

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.spin_and_win));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent i = new Intent(SpinWheel.this,DealsFullScreen.class);
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void GetSpinWheel() {

        //  showDialog();
        AndroidNetworking.post(URLHelper.GET_SPIN_WHEEL)

                .addHeaders("user-id", SharedHelper.getKey(SpinWheel.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(SpinWheel.this,"auth_id"))
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
                                    Type listType = new TypeToken<List<SpinModel>>() {
                                    }.getType();
                                    spinModels = gson.fromJson(responseObj.getString("detail"), listType);
                                    int[] rainbow = getResources().getIntArray(R.array.rainbow);
                                    data = new ArrayList<>();
                                    for (int i=0; i<spinModels.size(); i++) {
                                        rubikstudio.library.model.LuckyItem luckyItem = new rubikstudio.library.model.LuckyItem();
                                        luckyItem.topText = spinModels.get(i).getPrize_title();
                                                luckyItem.secondaryText = spinModels.get(i).getPrize();
                                        luckyItem.color = rainbow[i];
                                        luckyItem.spinner_id = spinModels.get(i).getSpinner_id();
                                        data.add(luckyItem);
                                    }

                                    spin_wheel.setData(data);
                                    spin_wheel.setRound(15);
                                    frame_layout.setVisibility(View.VISIBLE);
                                    cursor_.setVisibility(View.VISIBLE);


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


    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(SpinWheel.this,""+toastString,Toast.LENGTH_SHORT).show();
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
        customDialog = new CustomDialog(SpinWheel.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }
    private int getRandomRound() {
        Random rand = new Random();
        return rand.nextInt(10) + 15;
    }
    private int getRandomIndex() {
        activeIndex = new ArrayList<>();
        for(int i = 0; i<spinModels.size();i++){
            if(spinModels.get(i).getActive_status().equals("1")){
                activeIndex.add(new SpinModel(spinModels.get(i).getId(),spinModels.get(i).getActive_status()));
            }
        }
        Random rand = new Random();
     //   return rand.nextInt(activeIndex.size() - 1) + 0;
      //  return rand.nextInt(activeIndex.size()) ;
        return rand.nextInt(activeIndex.size());
    }



    private void Collect_Spinner_Prize() {

        showDialog();
        AndroidNetworking.post(URLHelper.COLLECT_SPINNER_PRIZE)
                .addBodyParameter("spinner_id", spinner_id)
                .addHeaders("user-id", SharedHelper.getKey(SpinWheel.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(SpinWheel.this,"auth_id"))
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


                                    //   Toast.makeText(SpinWheel.this, responseObj.optString("message"), Toast.LENGTH_SHORT).show();
                                    showLogoutDialog(responseObj.optString("message"),collected_prize);

                                    int points = Integer.valueOf(spinner_points);
                                    user_points.setText(" "+(points - 2)+" ");
                                    spinner_points = String.valueOf(points-2);

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

    private void GetEarnedPoints() {

        showDialog();
        AndroidNetworking.post(URLHelper.GET_EARNED_POINTS)


                .addHeaders("user-id", SharedHelper.getKey(SpinWheel.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(SpinWheel.this,"auth_id"))
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

                                    JSONObject detail = responseObj.getJSONObject("detail");
                                    user_points.setText(" "+detail.optString("earned_points")+" ");
                                    points_in_account = Integer.valueOf(detail.optString("earned_points"));
                                    spinner_points = detail.optString("earned_points");
                                    you_have.setVisibility(View.VISIBLE);
                                    points_to.setVisibility(View.VISIBLE);
                                    bottom_text.setVisibility(View.VISIBLE);

                                    GetSpinWheel();

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

    private void showLogoutDialog(String title, String prize) {
        if (!isFinishing()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater)SpinWheel.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           // builder.setTitle(title);
            builder.setMessage(prize);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                 dialog.dismiss();

                 Intent i = new Intent(SpinWheel.this,ClaimDealActivity.class);
                 startActivity(i);
                 finish();
                 //   ShowClaimDealForm();
                }
            });

            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(SpinWheel.this, R.color.colorPrimaryDark));
                       }
            });
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SpinWheel.this,DealsFullScreen.class);
        startActivity(i);
        finish();
    }


}
