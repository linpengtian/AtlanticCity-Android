package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.atlanticcity.app.Utils.CustomRequestQueue;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class SingleDealActivity extends AppCompatActivity {

    Toolbar toolbar;
    CircleImageView business_logo;
    TextView business_name,address,deal_title,deal_description,valid_till,minutes,seconds;
    Button start_countdown_btn,scan_qr_code;
    boolean counterStarted = false;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    String item_id;
    String str_deal_title,str_deal_description,str_deal_expire_at,str_business_address,str_business_name;
    CountDownTimer countDownTimer;
    String counterSeconds;
    String deal_id, business_id;
    boolean deal_valid = true;
    public String TAG = "SingleDealActivity";
    RelativeLayout main_layout;
    String strLat,strLng;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_deal);
        viewInitializer();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void viewInitializer(){
        toolbar = findViewById(R.id.toolbar);
        business_logo = findViewById(R.id.business_logo);
        business_name = findViewById(R.id.business_name);
        address = findViewById(R.id.address);
        deal_title = findViewById(R.id.deal_title);
        deal_description = findViewById(R.id.deal_description);
        valid_till = findViewById(R.id.valid_till);
        minutes = findViewById(R.id.minutes);
        seconds = findViewById(R.id.seconds);
        start_countdown_btn = findViewById(R.id.start_countdown_btn);
        scan_qr_code = findViewById(R.id.scan_qr_code);
        main_layout = findViewById(R.id.main_layout);

        setupToolbar();

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if( strLat == null){

                Toast.makeText(SingleDealActivity.this, "No location available!", Toast.LENGTH_SHORT).show();

            }else{
                Intent i = new Intent(SingleDealActivity.this, MapActivity.class);
                i.putExtra("lat",strLat);
                i.putExtra("lng",strLng);
                i.putExtra("business_title",str_business_name);
                startActivity(i);
            }
            }
        });

        try {

            Intent i = getIntent();
            item_id = i.getStringExtra("item_id");
            str_deal_title = i.getStringExtra("deal_title");
            str_deal_description = i.getStringExtra("deal_description");
            str_deal_expire_at = i.getStringExtra("deal_expire_at");
            str_business_address = i.getStringExtra("business_address");
            str_business_name = i.getStringExtra("business_name");

            deal_title.setText(str_deal_title);
            deal_description.setText(str_deal_description);
            valid_till.setText("Valid till "+str_deal_expire_at);
            address.setText(str_business_address);
            business_name.setText(str_business_name);


        }catch (Exception ex){
            ex.printStackTrace();
        }

        helper = new ConnectionHelper(SingleDealActivity.this);
        isInternet = helper.isConnectingToInternet();
        if(isInternet){
            GetSingleDeal();
        }else {
            Toast.makeText(SingleDealActivity.this, SingleDealActivity.this.getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }

        start_countdown_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(!counterStarted){
                    startCountDownTimer();
                }else{
                    Toast.makeText(SingleDealActivity.this, "Counter already started! scan QR code!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        scan_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(counterStarted){
                Intent i = new Intent(SingleDealActivity.this,ScanQR.class);
                i.putExtra("deal_id",deal_id);
                i.putExtra("business_id",business_id);
                startActivity(i);

            }else{
                Toast.makeText(SingleDealActivity.this, "Please start counter first", Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.get_this_deal));
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

    private void GetSingleDeal() {

        showDialog();
        String user_id = SharedHelper.getKey(this,"user_id");
        String auth_id = SharedHelper.getKey(this,"auth_id");
        AndroidNetworking.post(URLHelper.GET_SINGLE_DEAL)
                .addHeaders("user-id", user_id)
                .addHeaders("auth-id", auth_id)
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
                                JSONObject detailObj = responseObj.getJSONObject("detail");
                                JSONObject businessObj = detailObj.getJSONObject("business");

                                if(responseObj.optString("status").equals("1")){
                                    Glide.with(SingleDealActivity.this)
                                            .load(businessObj.optString("logo"))
                                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                            .placeholder(R.drawable.splash_bg)
                                            .error(R.drawable.splash_bg).into(business_logo);
                                    business_name.setText(businessObj.optString("business_name"));
                                    address.setText(businessObj.optString("address")+","+businessObj.optString("city"));
                                    deal_title.setText(detailObj.optString("title"));
                                    deal_description.setText(detailObj.optString("description"));
                                    String validTill = returnDate(detailObj.optString("deal_expire_at"));
                                    valid_till.setText("Valid till "+validTill);
                                    deal_id = detailObj.optString("item_id");
                                    business_id = detailObj.optString("business_user_id");

                                    strLat = businessObj.optString("lat");
                                    strLng = businessObj.optString("lng");

                                    if(detailObj.optString("timer").equalsIgnoreCase("Deal not exists")){
                                        counterStarted = false;
                                        scan_qr_code.setVisibility(View.GONE);
                                    }else{
                                        counterSeconds = detailObj.optString("timer");
                                        int integerSeconds = Integer.valueOf(counterSeconds);
                                        int _hours = integerSeconds / 3600;
                                        int _minutes = (integerSeconds % 3600) / 60;
                                        integerSeconds = integerSeconds % 60;

                                        minutes.setText(String.valueOf(_minutes));
                                        startCounter(String.valueOf(integerSeconds));
                                        counterStarted = true;
                                        start_countdown_btn.setVisibility(View.GONE);
                                        scan_qr_code.setVisibility(View.VISIBLE);

                                        //  minutes.setText("14");
                                    }
                                    main_layout.setVisibility(View.VISIBLE);

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


    public void displayMessage(  String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(SingleDealActivity.this,""+toastString,Toast.LENGTH_SHORT).show();
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
        customDialog = new CustomDialog(SingleDealActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }

    String returnDate(String date){

        try {
            // String date="Mar 10, 2016 6:30:00 PM";
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate=spf.parse(date);
            spf= new SimpleDateFormat("MMM dd,  yyyy");
            date = spf.format(newDate);
            System.out.println(date);
        }catch (Exception ex){
            ex.printStackTrace();
            return date;
        }

        return date;

    }
    public static void move(final TextView view){
        ValueAnimator va = ValueAnimator.ofFloat(0f, 3f);
        int mDuration = 3000; //in millis
        va.setDuration(mDuration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTranslationX((float)animation.getAnimatedValue());
            }
        });
        va.setRepeatCount(5);
        va.start();
    }

    void startCounter(String counter_value){
        countDownTimer = new CountDownTimer(Integer.parseInt(counter_value) * 1000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                long time_sec = millisUntilFinished / 1000;
                if(time_sec<10) {
                    seconds.setText("0" + millisUntilFinished / 1000);
                }else {
                    seconds.setText("" + millisUntilFinished / 1000);
                }

            }

            public void onFinish() {
                seconds.setText("00");
                String minutes_value = minutes.getText().toString();
                int int_value = Integer.valueOf(minutes_value);

                if(int_value==0){
                    countDownTimer.cancel();
                    Intent i = new Intent(SingleDealActivity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    int minutes_new_value = --int_value;
                    String newStringValue = String.valueOf(minutes_new_value);
                    if(minutes_new_value<10){
                        minutes.setText("0"+newStringValue);
                    }else {
                        minutes.setText(""+newStringValue);
                    }
                    startCounter("60");
                }

            }
        };

        countDownTimer.start();
    }


    private void startCountDownTimer() {

     //  GlobalDealsData.clear();
        showDialog();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URLHelper.START_COUNT_DOWN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject response_obj = new JSONObject(response);
                    JSONObject errorObj = response_obj.getJSONObject("error");
                    if(errorObj.optString("status").equals("1")){
                        displayMessage(errorObj.optString("message"));
                    }else{
                        JSONObject responseObj = response_obj.getJSONObject("response");

                        if(responseObj.optString("status").equals("1")){
                            Toast.makeText(SingleDealActivity.this, responseObj.optString("message"), Toast.LENGTH_SHORT).show();
                            counterStarted = true;
                            start_countdown_btn.setVisibility(View.GONE);
                            scan_qr_code.setVisibility(View.VISIBLE);
                            minutes.setText("14");
                            startCounter("60");

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
                dismissDialog();

                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if(errorObj.optJSONObject("error").optString("status").equals("1")){
                            displayMessage(errorObj.optJSONObject("error").optString("message"));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        GetSingleDeal();
                    }
                }

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("user-id", SharedHelper.getKey(SingleDealActivity.this,"user_id"));
                headers.put("auth-id", SharedHelper.getKey(SingleDealActivity.this,"auth_id"));
                return headers;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("deal_id", item_id);


                return params;
            }

        };

        CustomRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

}
