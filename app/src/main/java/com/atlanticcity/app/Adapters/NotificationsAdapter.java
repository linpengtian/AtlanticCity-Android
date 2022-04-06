package com.atlanticcity.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.atlanticcity.app.Activities.MainActivity;
import com.atlanticcity.app.Activities.Notifications;
import com.atlanticcity.app.Activities.SingleDealActivity;
import com.atlanticcity.app.Activities.ViewDeal;
import com.atlanticcity.app.Activities.ViewWeb;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.ContactModel;
import com.atlanticcity.app.Models.NotifModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;


import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {

    private List<NotifModel> notifModels;
    CustomDialog customDialog;
    private Context context;
    public String TAG = "NotificationsAdapter";
    Notifications notifications;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title,description,date;
        CardView main_layout;
        ImageView delete;
        public MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.title);
            description =  view.findViewById(R.id.description);

            date = view.findViewById(R.id.date);
            delete = view.findViewById(R.id.delete);
            main_layout = view.findViewById(R.id.main_layout);

        }
    }

    public NotificationsAdapter(Notifications notifications, List<NotifModel> notifModels) {
        this.notifications = notifications;
        this.notifModels = notifModels;

    }

    @Override
    public NotificationsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notif_li, parent, false);

        context = parent.getContext();
        return new NotificationsAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final NotificationsAdapter.MyViewHolder holder, final int position) {
        try {
            final NotifModel mList = notifModels.get(position);
            holder.title.setText(mList.getTitle());
            holder.description.setText(mList.getDescription());
            String date = returnDate(mList.getCreated_at());
            holder.date.setText(date);


            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                DeleteNotification(mList.getId());
                }
            });

            holder.main_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(mList.getItem_id()!=null  && !mList.getItem_id().isEmpty() && !mList.getItem_id().equalsIgnoreCase("")){
                        Intent i = new Intent(context, ViewDeal.class);
                        i.putExtra("item_id",mList.getItem_id());
                        context.startActivity(i);
                    }else {
                        if(mList.getUrl()!=null){
                            try {
                                Intent webView = new Intent(context, ViewWeb.class);
                                webView.putExtra("url",mList.getUrl());
                                context.startActivity(webView);
                                /*Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(mList.getUrl()));
                                context.startActivity(browserIntent1);*/
                            }catch (Exception ex){
                                Toast.makeText(context, "Not a valid url", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(context, "No url available!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });


        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return notifModels.size();
    }

    String returnDate(String date){

        try {
            // String date="Mar 10, 2016 6:30:00 PM";
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date newDate=spf.parse(date);
            spf= new SimpleDateFormat("MMM dd  yyyy");
            date = spf.format(newDate);
            System.out.println(date);
        }catch (Exception ex){
            ex.printStackTrace();
            return date;
        }

        return date;

    }


    private void DeleteNotification(final String notification) {

        showDialog();
        AndroidNetworking.post(URLHelper.DELETE_NOTIFICATION)

                .addBodyParameter("notification_id", notification)
                .addHeaders("user-id", SharedHelper.getKey(context,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(context,"auth_id"))
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
                                Log.v("delete_response",errorObj.optString("message"));
                            }else{
                                JSONObject responseObj = response.getJSONObject("response");
                                if(responseObj.optString("status").equals("1")){
                                    Log.v("delete_response",responseObj.optString("message"));
                                    notifications.GetNotifications();

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

        try{
            Toast.makeText(context,""+toastString,Toast.LENGTH_SHORT).show();
        }catch (Exception ee){
            ee.printStackTrace();
        }

    }

    void dismissDialog(){
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();
    }

    void showDialog(){
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }



}

