package com.atlanticcity.app.Utils;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utilities {


    public static String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    public static boolean isAfterToday(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        Calendar myDate = Calendar.getInstance();

        myDate.set(year, month, day);

        if (myDate.before(today))
        {
            return false;
        }
        return true;
    }

    public static void dismissDialog(Context context){
         CustomDialog customDialog = new CustomDialog(context);
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();
    }

    boolean showLog = true;
    public void print(String tag, String message) {
        if(showLog){
            Log.v(tag,message);
        }
    }
}
