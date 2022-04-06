package com.atlanticcity.app.Adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.atlanticcity.app.Activities.EarnedPoints;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.EarnedPointsModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EarnedPointsAdapter extends RecyclerView.Adapter<EarnedPointsAdapter.MyViewHolder> {

    private List<EarnedPointsModel> earnedPointsModels;
    private Context context;
    CustomDialog customDialog;
    public String TAG = "EarnedPointsAdapter";
    EarnedPoints earnedPoints;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView points_title,date,points;
        public CircleImageView icon;
        ImageView delete;

        public MyViewHolder(View view) {
            super(view);
            points_title =  view.findViewById(R.id.points_title);
            date =  view.findViewById(R.id.date);
            points = view.findViewById(R.id.points);
            delete = view.findViewById(R.id.delete);
            icon = view.findViewById(R.id.icon);
        }
    }

    public EarnedPointsAdapter(EarnedPoints earnedPoints,List<EarnedPointsModel> earnedPointsModels) {
        this.earnedPointsModels = earnedPointsModels;
        this.earnedPoints = earnedPoints;
    }

    @Override
    public EarnedPointsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.earned_points_li, parent, false);

        context = parent.getContext();

        return new EarnedPointsAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(EarnedPointsAdapter.MyViewHolder holder,final int position) {
        EarnedPointsModel mList = earnedPointsModels.get(position);

        if(mList.getType().equals("2")){
            try {
                holder.icon.setImageResource(R.drawable.cake_icon);
                holder.points_title.setText(context.getResources().getString(R.string.birthday_points));
                holder.points.setText(mList.getPoints());
                String date = returnDate(mList.getCreated_at());
                holder.date.setText(date);
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }else if(mList.getType().equals("3")){
            try {
                holder.icon.setImageResource(R.drawable.location_icon);
                holder.points_title.setText(context.getResources().getString(R.string.zip_code_points));
                holder.points.setText(mList.getPoints());
                String date = returnDate(mList.getCreated_at());
                holder.date.setText(date);
            }catch (Exception ex){
                ex.printStackTrace();
            }

        } else if(mList.getType().equals("4")){
            try {
                holder.icon.setImageResource(R.drawable.registration);
                holder.points_title.setText(context.getResources().getString(R.string.registration_points));
                holder.points.setText(mList.getPoints());
                String date = returnDate(mList.getCreated_at());
                holder.date.setText(date);

            }catch (Exception ex){
                ex.printStackTrace();
            }

        } else if(mList.getType().equals("1")){
            try {
                Picasso.get().load(mList.getDealsModel().getBusinessModel().getLogo()).placeholder(context.getDrawable(R.drawable.logo)).into(holder.icon);
                holder.points_title.setText(mList.getDealsModel().getBusinessModel().getBusiness_name());
                holder.points.setText(mList.getPoints());
                String date = returnDate(mList.getCreated_at());
                holder.date.setText(date);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeletePoints(earnedPointsModels.get(position).getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return earnedPointsModels.size();
    }

    String returnDate(String date){

        try {
            // String date="Mar 10, 2016 6:30:00 PM";
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date newDate=spf.parse(date);
            spf= new SimpleDateFormat("MMM dd yyyy");
            date = spf.format(newDate);
            System.out.println(date);
        }catch (Exception ex){
            ex.printStackTrace();
            return date;
        }

        return date;

    }



    private void DeletePoints(final String notification) {

        showDialog();
        AndroidNetworking.post(URLHelper.REMOVE_EARNED_POINTS)

                .addBodyParameter("id", notification)
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
                                    earnedPoints.EarnPointsList();

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

