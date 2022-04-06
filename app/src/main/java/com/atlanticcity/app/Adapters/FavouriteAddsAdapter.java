package com.atlanticcity.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.atlanticcity.app.Activities.ViewDeal;
import com.atlanticcity.app.Activities.ViewWeb;
import com.atlanticcity.app.Fragments.FavoriteAdds;
import com.atlanticcity.app.Fragments.FavoritesDeals;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.DetailModel;
import com.atlanticcity.app.Models.FavouriteAddsClass;
import com.atlanticcity.app.Models.NotifModel;
import com.atlanticcity.app.Models.SliderModel;
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

public class FavouriteAddsAdapter extends RecyclerView.Adapter<FavouriteAddsAdapter.MyViewHolder> {

    private List<FavouriteAddsClass> favouriteAddsModel;
    private Context context;
    CustomDialog customDialog;
    String item_id;

    public String TAG = "DealsAdapter";
    FavoritesDeals favoritesDeals;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title,description,deal_desciption;
        public CircleImageView add_image;
        CardView main_layout;
        ImageView img_like;
        public MyViewHolder(View view) {
            super(view);
            description =  view.findViewById(R.id.description);
            title =  view.findViewById(R.id.title);
            add_image = view.findViewById(R.id.add_image);
            main_layout = view.findViewById(R.id.main_layout);
            img_like = view.findViewById(R.id.img_like);
        }
    }

    public FavouriteAddsAdapter(List<FavouriteAddsClass> favouriteAddsModel, FavoritesDeals favoritesDeals) {
        this.favouriteAddsModel = favouriteAddsModel;
        this.favoritesDeals = favoritesDeals;
    }

    @Override
    public FavouriteAddsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_adds_li, parent, false);
        context = parent.getContext();
        return new FavouriteAddsAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(FavouriteAddsAdapter.MyViewHolder holder, final int position) {
        final FavouriteAddsClass mList = favouriteAddsModel.get(position);

        if(mList.getSliderModel()!=null){
            if(mList.getSliderModel().getImage()!=null){
               // Picasso.get().load(mList.getSliderModel().getImage()).placeholder(context.getDrawable(R.drawable.logo)).noFade().error(context.getDrawable(R.drawable.logo)).into(holder.add_image);
                Glide.with(context)
                        .load(mList.getSliderModel().getImage())
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .placeholder(R.drawable.splash_bg)
                        .error(R.drawable.splash_bg)
                        .into(holder.add_image);

                holder.title.setText(mList.getSliderModel().getTitle());
                holder.description.setText(mList.getSliderModel().getDescription());

            }
        }

        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            try {
                if(mList.getSliderModel().getUrl()!=null){
                    try {

                        Intent i = new Intent(context, ViewWeb.class);
                        i.putExtra("url",mList.getSliderModel().getUrl());
                        context.startActivity(i);

                      /*  Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(mList.getSliderModel().getUrl()));
                        context.startActivity(browserIntent1);*/
                    }catch (Exception ex){
                        Toast.makeText(context, "Not a valid url", Toast.LENGTH_SHORT).show();
                    }

                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            }
        });

        holder.img_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item_id = mList.getAdd_id();
                AddFavouriteAdd(item_id);
            }
        });

    }

    @Override
    public int getItemCount() {
        return favouriteAddsModel.size();
    }

    private void AddFavouriteAdd(final String add_id ) {

        showDialog();
        AndroidNetworking.post(URLHelper.ADD_FAVOURITE_ADD)

                .addBodyParameter("add_id", add_id)

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
                            dismissDialog();

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
                            dismissDialog();
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

    public void displayMessage(String toastString) {

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
