package com.atlanticcity.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.atlanticcity.app.Activities.BusinessDetail;
import com.atlanticcity.app.Fragments.FavoriteBusinesses;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.DetailBusinessModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.atlanticcity.app.Activities.SplashActivity.GlobalDealsData;

public class FavoriteBusinessAdapter extends RecyclerView.Adapter<FavoriteBusinessAdapter.MyViewHolder> {

    private List<DetailBusinessModel> detailBusinessModels;
    private Context context;
    CustomDialog customDialog;
    String business_id;
    public String TAG = "FavoriteBusinessAdapter";
    FavoriteBusinesses favoriteBusinesses;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView business_name,deal_timgs,address,no_of_deals;
        public CircleImageView business_image;
        CardView main_layout;
        ImageView favourite;
        RelativeLayout rlInnner;
        public MyViewHolder(View view) {
            super(view);
            business_name =  view.findViewById(R.id.business_name);
            business_image =  view.findViewById(R.id.business_image);
            deal_timgs = view.findViewById(R.id.deal_timgs);
            address = view.findViewById(R.id.address);
            no_of_deals = view.findViewById(R.id.no_of_deals);
            main_layout = view.findViewById(R.id.main_layout);
            favourite = view.findViewById(R.id.favourite);
            rlInnner = view.findViewById(R.id.rlInnner);
        }
    }

    public FavoriteBusinessAdapter(List<DetailBusinessModel> detailBusinessModels,FavoriteBusinesses favoriteBusinesses) {
        this.detailBusinessModels = detailBusinessModels;
        this.favoriteBusinesses = favoriteBusinesses;
    }

    @Override
    public FavoriteBusinessAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_business_li, parent, false);

        context = parent.getContext();

        return new FavoriteBusinessAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(FavoriteBusinessAdapter.MyViewHolder holder, int position) {
        final DetailBusinessModel mList = detailBusinessModels.get(position);
        try {

            if(mList.getBusinessModel()!=null){
                Picasso.get().load(mList.getBusinessModel().getLogo()).placeholder(context.getDrawable(R.drawable.logo)).into(holder.business_image);
                holder.business_name.setText(mList.getBusinessModel().getBusiness_name());
                holder.address.setText(mList.getBusinessModel().getAddress());
                holder.deal_timgs.setText("Open "+mList.getBusinessModel().getOpen_time()+ " Closes "+mList.getBusinessModel().getClose_time());
                holder.no_of_deals.setText(mList.getBusinessModel().getDeals_count() + " Deals");
                holder.main_layout.setVisibility(View.VISIBLE);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }



        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                business_id = mList.getBusiness_id();
                RemoveFavoriteBusiness(business_id);
            }
        });

        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(context, BusinessDetail.class);
                i.putExtra("business_title",mList.getBusinessModel().getBusiness_name());
                i.putExtra("business_id",mList.getBusinessModel().getBusiness_user_id());
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return detailBusinessModels.size();
    }


    private void RemoveFavoriteBusiness(final String business_id ) {

        showDialog();
        AndroidNetworking.post(URLHelper.ADD_FAVORITE_BUSINESS)

                .addBodyParameter("business_id", business_id)

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
                            }else{
                                JSONObject responseObj = response.getJSONObject("response");
                                if(responseObj.optString("status").equals("1")){
                                    GlobalDealsData.clear();
                                    favoriteBusinesses.GetBusinesses();
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
