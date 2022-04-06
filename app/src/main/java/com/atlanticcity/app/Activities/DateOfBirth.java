package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.atlanticcity.app.Utils.Utilities;


import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class DateOfBirth extends AppCompatActivity {

    TextView skip,tv_birthday;
    Button btn_save;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
   Toolbar toolbar;
    DatePickerDialog datePickerDialog;
    String DOB;
    Utilities utils = new Utilities();
    boolean afterToday = false;
    public String TAG = "DateOfBirth";
    ImageView imageCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_of_birth);

        viewInitializer();
    }


    void viewInitializer(){
       toolbar = findViewById(R.id.toolbar);
        skip = findViewById(R.id.skip);
        btn_save = findViewById(R.id.btn_save);
        tv_birthday = findViewById(R.id.tv_birthday);
       // icon_back = findViewById(R.id.icon_back);
        helper = new ConnectionHelper(DateOfBirth.this);
        imageCalendar = findViewById(R.id.imageCalendar);
        isInternet = helper.isConnectingToInternet();
        setupToolbar();


     /*   icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(DateOfBirth.this,DealsFullScreen.class);
            startActivity(i);
            finish();
            }
        });*/

        final Calendar myCalendar = Calendar.getInstance();


       final  DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
           // updateLabel();
            }

        };

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(DateOfBirth.this,DealsFullScreen.class);
            startActivity(i);
            finish();
            }
        });
        tv_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              /*  new DatePickerDialog(DateOfBirth.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();*/
               DateofBirthPopup(tv_birthday);
            }
        });

        imageCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateofBirthPopup(tv_birthday);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            hideKeyboard(DateOfBirth.this);

            if(DOB.isEmpty() || DOB.equals("") || DOB == null || DOB.equals("null")){
                Toast.makeText(DateOfBirth.this, getString(R.string.please_select_birthday), Toast.LENGTH_SHORT).show();
            }else if(isInternet){
                AddDateofBirth();
            }else {
                Toast.makeText(DateOfBirth.this, getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent i = new Intent(DateOfBirth.this,DealsFullScreen.class);
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void DateofBirthPopup(final TextView tv){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(DateOfBirth.this, AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        // set day of month , month and year value in the edit text
                        String choosedMonth = "";
                        String choosedDate = "";
                        String choosedDateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        DOB = choosedDateFormat;
                        try {
                            choosedMonth = utils.getMonth(choosedDateFormat);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (dayOfMonth < 10) {
                            choosedDate = "0" + dayOfMonth;
                        } else {
                            choosedDate = "" + dayOfMonth;
                        }
                        afterToday = utils.isAfterToday(year, monthOfYear, dayOfMonth);
                    //    DOB = (monthOfYear+1)+"-"+choosedDate  +"-"+year;
                        DOB = year+"-"+(monthOfYear+1) +"-"+choosedDate;
                        tv.setText(+(monthOfYear+1)+"-"+choosedDate +"-"+year);
                        datePickerDialog.dismiss();
                        //  selectTime(choosedDate,choosedMonth,year);
                    }
                }, 2002, mMonth, mDay);
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.MONTH,1);
        Date today = new Date();
        Calendar c_ = Calendar.getInstance();
        c_.setTime(today);
        c_.add( Calendar.YEAR, -18 ) ;      // Subtract 6 months
        long max = c_.getTime().getTime();

        long afterTwoMonthsinMilli=cal.getTimeInMillis();
        //  datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate(max);

      //  datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    private void AddDateofBirth() {
        showDialog();

        AndroidNetworking.post(URLHelper.ADD_BIRTHDAY)

                .addBodyParameter("date_of_birth", DOB)
                .addHeaders("user-id", SharedHelper.getKey(DateOfBirth.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(DateOfBirth.this,"auth_id"))
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
                                    Toast.makeText(DateOfBirth.this, responseObj.optString("message"), Toast.LENGTH_LONG).show();
                                    //   displayMessage(responseObj.optString("message"));
                                    SharedHelper.putKey(DateOfBirth.this,"date_of_birth","added");
                                    Intent i = new Intent(DateOfBirth.this,PointsActivity.class);
                                    i.putExtra("status","birthday");
                                    i.putExtra("points",responseObj.optString("detail"));
                                    startActivity(i);
                                    finish();
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
                Toast.makeText(DateOfBirth.this,""+toastString,Toast.LENGTH_SHORT).show();
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
        customDialog = new CustomDialog(DateOfBirth.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.earn_points_now));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
