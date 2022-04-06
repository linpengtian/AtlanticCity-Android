package com.atlanticcity.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.atlanticcity.app.Activities.DateOfBirth;
import com.atlanticcity.app.Activities.ViewDeal;
import com.atlanticcity.app.Fragments.FavoritesDeals;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.DetailModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.atlanticcity.app.Activities.SplashActivity.GlobalDealsData;

public class FavouriteDealsAdapter extends RecyclerView.Adapter<FavouriteDealsAdapter.MyViewHolder> {

    private List<DetailModel> detailModels;
    private Context context;
    CustomDialog customDialog;
    String item_id;

    public String TAG = "DealsAdapter";
    FavoritesDeals favoritesDeals;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView deal_title,business_name,deal_desciption;
        public CircleImageView deal_image;
        CardView main_layout;
        ImageView img_like;
        public MyViewHolder(View view) {
            super(view);
            business_name =  view.findViewById(R.id.business_name);
            deal_title =  view.findViewById(R.id.deal_title);
            deal_desciption = view.findViewById(R.id.deal_desciption);
            deal_image = view.findViewById(R.id.deal_image);
            main_layout = view.findViewById(R.id.main_layout);
            img_like = view.findViewById(R.id.img_like);
        }
    }

    public FavouriteDealsAdapter(List<DetailModel> detailModels, FavoritesDeals favoritesDeals) {
        this.detailModels = detailModels;
        this.favoritesDeals = favoritesDeals;
    }

    @Override
    public FavouriteDealsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_deals_li, parent, false);
        context = parent.getContext();
        return new FavouriteDealsAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(FavouriteDealsAdapter.MyViewHolder holder, final int position) {
        final DetailModel mList = detailModels.get(position);

        if(mList.getDealsModels()!=null){

            Glide.with(context)
                    .load(mList.getDealsModels().getAvatar())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.splash_bg)
                    .error(R.drawable.splash_bg).into( holder.deal_image);


            holder.business_name.setText(mList.getDealsModels().getBusinessModel().getBusiness_name());
            holder.deal_title.setText(mList.getDealsModels().getTitle());
            holder.deal_desciption.setText(mList.getDealsModels().getDescription());
        }else {
            holder.main_layout.setVisibility(View.GONE);
        }

        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try {
                Intent i = new Intent(context, ViewDeal.class);
                i.putExtra("avatar",mList.getDealsModels().getAvatar());
                i.putExtra("deal_title",mList.getDealsModels().getTitle());
                i.putExtra("deal_desc",mList.getDealsModels().getDescription());
                i.putExtra("is_favourite",mList.getDealsModels().getIs_favorited_count());
                i.putExtra("item_id",mList.getItem_id());
                context.startActivity(i);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            }
        });

        holder.img_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item_id = mList.getItem_id();
                AddFavoriteDeal(item_id);
            }
        });

    }

    @Override
    public int getItemCount() {
        return detailModels.size();
    }

    private void AddFavoriteDeal(final String item_id ) {

        showDialog();
        AndroidNetworking.post(URLHelper.ADD_FAVORITE_DEAL)

                .addBodyParameter("item_id", item_id)
                .addBodyParameter("deal_id", item_id)
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
                                favoritesDeals.GetFavouriteDeals();
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
