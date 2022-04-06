package com.atlanticcity.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.atlanticcity.app.Activities.DealsFullScreen;
import com.atlanticcity.app.Activities.Favourites;
import com.atlanticcity.app.Activities.MainActivity;
import com.atlanticcity.app.Activities.SingleDealActivity;
import com.atlanticcity.app.Activities.ViewWeb;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.DealsModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.islamkhsh.CardSliderAdapter;

import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SliderDealAdapter extends CardSliderAdapter<SliderDealAdapter.MyViewHolder> {

    private List<DealsModel> dealsModelList;
    private Context context;
    CustomDialog customDialog;
    String item_id;

    public String TAG = "SliderDealAdapter";
    DealsFullScreen dealsFullScreen;

    public SliderDealAdapter(DealsFullScreen dealsFullScreen, List<DealsModel> dealsModelList ) {
        this.dealsFullScreen = dealsFullScreen;
        this.dealsModelList = dealsModelList;

    }

    @Override
    public int getItemCount(){

        if(dealsModelList.size() == 0){
            return 0;
        }else {
            return Integer.MAX_VALUE;
        }
      //  return dealsModels.size();
       // return dealsModels.size();
    }

    @Override
    public SliderDealAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deals_full_screen_li, parent, false);
        context = parent.getContext();
        return new SliderDealAdapter.MyViewHolder(view);
    }


    @Override
    public void bindVH(@NotNull final SliderDealAdapter.MyViewHolder holder, final int position) {

        final int pos = position % dealsModelList.size();
        final DealsModel mList = dealsModelList.get(pos);

        if(mList.getAdd_id()!=null){
            setUpAdData(holder,pos);
           holder.main_layout.setVisibility(View.GONE);
        }else {

            holder.ads_layout.setVisibility(View.GONE);
            holder.main_layout.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(mList.getAvatar())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.splash_bg)
                    .error(R.drawable.splash_bg)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // log exception
                            Log.e("TAG", "Error loading image", e);
                            try {

                                if(mList.getBusinessModel()!=null){

                                    if(mList.getBusinessModel().getAddress()!=null){
                                        holder.address.setText(mList.getBusinessModel().getAddress()+","+mList.getBusinessModel().getCity());
                                    }else {
                                        holder.address.setText("--");
                                    }

                                    if(mList.getBusinessModel().getBusiness_name()!=null){
                                        holder.business_name.setText(mList.getBusinessModel().getBusiness_name());
                                    }else {
                                        holder.business_name.setText("--");
                                    }

                                    Glide.with(context)
                                            .load(mList.getBusinessModel().getLogo())
                                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                            .placeholder(R.drawable.splash_bg)
                                            .error(R.drawable.splash_bg).into( holder.business_logo);



                                }else {
                                    holder.address.setText("--");
                                    holder.business_name.setText("--");
                                }

                                if(mList.getTitle()!=null){
                                    holder.deal_title.setText(mList.getTitle());
                                }else{
                                    holder.deal_title.setText("--");
                                }

                                if(mList.getDeal_views_count()!=null){
                                    holder.ppl_claimed.setText(mList.getDeal_views_count()+"\nViews");
                                }else{
                                    holder.ppl_claimed.setText("0"+"\nViews");
                                }

                                if(mList.getIs_favorited_count()!=null){
                                    if (mList.getIs_favorited_count().equalsIgnoreCase("true")){
                                        holder.image_like.setImageDrawable(context.getResources().getDrawable(R.drawable.new_heart_red));
                                    }else{
                                        holder.image_like.setImageDrawable(context.getResources().getDrawable(R.drawable.new_heart_green));
                                    }
                                }
                                holder.ads_layout.setVisibility(View.GONE);
                                holder.main_layout.setVisibility(View.VISIBLE);
                                holder.main_layout.setAlpha(1);

                            }catch (Exception ex){
                                ex.printStackTrace();
                                holder.main_layout.setAlpha(1);
                            }
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            try {

                                if(mList.getBusinessModel()!=null){

                                    if(mList.getBusinessModel().getAddress()!=null){
                                        holder.address.setText(mList.getBusinessModel().getAddress()+","+mList.getBusinessModel().getCity());
                                    }else {
                                        holder.address.setText("--");
                                    }

                                    if(mList.getBusinessModel().getBusiness_name()!=null){
                                        holder.business_name.setText(mList.getBusinessModel().getBusiness_name());
                                    }else {
                                        holder.business_name.setText("--");
                                    }

                                    Glide.with(context)
                                            .load(mList.getBusinessModel().getLogo())
                                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                            .placeholder(R.drawable.splash_bg)
                                            .error(R.drawable.splash_bg).into( holder.business_logo);
                                }else {
                                    holder.address.setText("--");
                                    holder.business_name.setText("--");
                                }

                                if(mList.getTitle()!=null){
                                    holder.deal_title.setText(mList.getTitle());
                                }else{
                                    holder.deal_title.setText("--");
                                }

                                if(mList.getDeal_views_count()!=null){
                                    holder.ppl_claimed.setText(mList.getDeal_views_count()+"\nViews");
                                }else{
                                    holder.ppl_claimed.setText("0"+"\nViews");
                                }

                                if(mList.getIs_favorited_count()!=null){
                                    if (mList.getIs_favorited_count().equalsIgnoreCase("true")){
                                        holder.image_like.setImageDrawable(context.getResources().getDrawable(R.drawable.new_heart_red));
                                    }else{
                                        holder.image_like.setImageDrawable(context.getResources().getDrawable(R.drawable.new_heart_green));
                                    }
                                }

                                holder.main_layout.setVisibility(View.VISIBLE);
                                holder.main_layout.setAlpha(1);

                            }catch (Exception ex){
                                holder.main_layout.setAlpha(1);
                                ex.printStackTrace();
                            }

                            return false;
                        }
                    })// scale to fill the ImageView and crop any extra
                    .into(holder.deal_avatar);


            //holder.deal_avatar.setHorizontalFadingEdgeEnabled(true);

            holder.main_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                holder.menu_layout.setVisibility(View.GONE);

                }
            });

            holder.settings_navigator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                holder.menu_layout.setVisibility(View.GONE);
                dealsFullScreen.drawerLayout.openDrawer(GravityCompat.START);
                }
            });

            holder.btn_get_this_deal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Intent i = new Intent(context, SingleDealActivity.class);
                i.putExtra("item_id",mList.getItem_id());
                i.putExtra("deal_title",mList.getTitle());
                i.putExtra("deal_description",mList.getDescription());
                i.putExtra("deal_expire_at",mList.getDeal_expire_at());
                if(mList.getBusinessModel()!=null){
                    i.putExtra("business_address",mList.getBusinessModel().getAddress());
                    i.putExtra("business_name",mList.getBusinessModel().getBusiness_name());
                }else {
                    i.putExtra("business_address","--");
                    i.putExtra("business_name","--");
                }


                context.startActivity(i);
                }
            });

            holder.image_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Atlantic City");
                    String shareMessage= "Check out Atlantic City App, Get it for free at http://www.atlanticcity.com/";

                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    context.startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
                }
            });

            holder.image_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //item_id = mList.getItem_id();
                    AddFavoriteDeal(mList.getItem_id(),holder.image_like,pos,mList,holder);
                }
            });

            holder.business_navigator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                holder.menu_layout.setVisibility(View.GONE);
                Intent i5 = new Intent(context, MainActivity.class);
                context.startActivity(i5);
                }
            });

            holder.favorites_navigator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                holder.menu_layout.setVisibility(View.GONE);
                Intent i5 = new Intent(context, Favourites.class);
                context.startActivity(i5);
                }
            });

            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                if( holder.menu_layout.getVisibility() == View.GONE){
                    holder.menu_layout.setVisibility(View.VISIBLE);
                }else {
                    holder.menu_layout.setVisibility(View.GONE);
                }
                }
            });
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView business_name,address,deal_title,deal_description,business_navigator,favorites_navigator,ppl_claimed,settings_navigator;
        public CircleImageView business_logo;
        RoundedImageView deal_avatar;
        RelativeLayout main_layout,menu,menu_ad;
        ImageView image_share,image_like;
        Button btn_get_this_deal;
        LinearLayout menu_layout;
        View ads_layout;
        ImageView imageViewBackground,image_share_ad,image_like_ad;
        TextView textViewDescription,business_navigator_ads,favorites_navigator_ads,settings_navigator_ads,ad_views,tv_title;
        LinearLayout menu_layout_ads;
        Button btn_view_add;
        public MyViewHolder(View view){
            super(view);

            business_name =  view.findViewById(R.id.business_name);
            business_logo =  view.findViewById(R.id.business_logo);
            deal_avatar = view.findViewById(R.id.deal_avatar);
            address = view.findViewById(R.id.address);
            deal_title = view.findViewById(R.id.deal_title);
            deal_description = view.findViewById(R.id.deal_description);
            main_layout = view.findViewById(R.id.main_layout);
            image_share = view.findViewById(R.id.image_share);
            image_like = view.findViewById(R.id.image_like);
            btn_get_this_deal = view.findViewById(R.id.btn_get_this_deal);
            menu = view.findViewById(R.id.back_arrow);
            ppl_claimed = view.findViewById(R.id.ppl_claimed);
            business_navigator = view.findViewById(R.id.business_navigator);
            favorites_navigator = view.findViewById(R.id.favorites_navigator);
            settings_navigator = view.findViewById(R.id.settings_navigator);
            menu_layout = view.findViewById(R.id.menu_layout);

            //For Ads Layout
            ads_layout = view.findViewById(R.id.ads_layout);
            imageViewBackground = ads_layout.findViewById(R.id.iv_auto_image_slider);
            menu_ad = ads_layout.findViewById(R.id.back_arrow);
            image_share_ad = ads_layout.findViewById(R.id.image_share);
            image_like_ad = ads_layout.findViewById(R.id.image_like);
            textViewDescription = ads_layout.findViewById(R.id.tv_auto_image_slider);
            menu_layout_ads = ads_layout.findViewById(R.id.menu_layout);
            business_navigator_ads = ads_layout.findViewById(R.id.business_navigator);
            favorites_navigator_ads = ads_layout.findViewById(R.id.favorites_navigator);
            settings_navigator_ads = ads_layout.findViewById(R.id.settings_navigator);
            btn_view_add = ads_layout.findViewById(R.id.btn_view_add);
            tv_title = ads_layout.findViewById(R.id.tv_title);
            ad_views = ads_layout.findViewById(R.id.ad_views);
        }

    }

    private void AddFavoriteDeal(final String item_id, final ImageView image_, final int position_, final DealsModel dealsModel_, final MyViewHolder holder) {

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

                                    if(responseObj.optString("message").equalsIgnoreCase("Successfully deal un-favorited")){
                                        DealsModel dealsModel = new DealsModel();
                                          dealsModel = dealsModel_;
                                          dealsModel.setIs_favorited_count("false");

                                        dealsModelList.set(position_,dealsModel);
                                        Log.v("dealsModelList_size",""+dealsModelList.size());
                                        notifyDataSetChanged();
                                        holder.image_like.setImageDrawable(context.getResources().getDrawable(R.drawable.new_heart_green));

                                    }else if(responseObj.optString("message").equalsIgnoreCase("Successfully deal favorited")){

                                        DealsModel dealsModel = new DealsModel();
                                        dealsModel = dealsModel_;
                                        dealsModel.setIs_favorited_count("true");

                                        dealsModelList.set(position_,dealsModel);

                                        Log.v("dealsModelList_size",""+dealsModelList.size());
                                        notifyDataSetChanged();
                                        holder.image_like.setImageDrawable(context.getResources().getDrawable(R.drawable.new_heart_red));


                                    }

                                }
                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
                            Log.d(TAG, "exception_in_deal_favourite"+ex.getMessage());
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


    void setUpAdData(final SliderDealAdapter.MyViewHolder holder, final int position){
        final DealsModel sliderItem = dealsModelList.get(position);
        holder.ads_layout.setVisibility(View.VISIBLE);

        holder.textViewDescription.setText(sliderItem.getDescription());
        holder.tv_title.setText(sliderItem.getTitle());
        holder.ad_views.setText(sliderItem.getAdd_views_count()+"\nViews");
        if(sliderItem.getIs_favorited_count().equalsIgnoreCase("false")){
            holder.image_like_ad.setImageDrawable(context.getResources().getDrawable(R.drawable.new_heart_green));
        }else if(sliderItem.getIs_favorited_count().equalsIgnoreCase("true")) {
            holder.image_like_ad.setImageDrawable(context.getResources().getDrawable(R.drawable.new_heart_red));

        }
        Glide.with(context)
                .load(sliderItem.getImage())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.splash_bg)
                .error(R.drawable.splash_bg)
                .into(holder.imageViewBackground);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.menu_layout_ads.setVisibility(View.GONE);

                //  Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });

        holder.settings_navigator_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.menu_layout_ads.setVisibility(View.GONE);
                dealsFullScreen.drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        holder.business_navigator_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.menu_layout_ads.setVisibility(View.GONE);
                Intent i5 = new Intent(context, MainActivity.class);
                context.startActivity(i5);
            }
        });

        holder.favorites_navigator_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.menu_layout_ads.setVisibility(View.GONE);
                Intent i5 = new Intent(context, Favourites.class);
                context.startActivity(i5);
            }
        });
        holder.btn_view_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sliderItem.getUrl()!=null){
                    updateAdViewCount(sliderItem.getAdd_id(),position,sliderItem);
                }


               /*
                if(sliderItem.getUrl()!=null){
                    try {
                        Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(sliderItem.getUrl()));
                        context.startActivity(browserIntent1);
                    }catch (Exception ex){
                        Toast.makeText(context, "Not a valid url", Toast.LENGTH_SHORT).show();
                    }

                }*/
            }
        });

        holder.menu_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( holder.menu_layout_ads.getVisibility() == View.GONE){
                    holder.menu_layout_ads.setVisibility(View.VISIBLE);
                }else {
                    holder.menu_layout_ads.setVisibility(View.GONE);
                }
            }
        });

        holder.image_share_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(sliderItem.getUrl()!=null){
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Atlantic City");
                        String shareMessage= "Hey check out this add "+sliderItem.getUrl();

                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        context.startActivity(Intent.createChooser(shareIntent, "choose one"));
                    }

                } catch(Exception e) {
                    Toast.makeText(context, "Not a valid url", Toast.LENGTH_SHORT).show();

                }
            }
        });

        holder.image_like_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddFavouriteAdd(sliderItem.getAdd_id(),holder.image_like_ad,position,sliderItem);
            }
        });
    }

    private void AddFavouriteAdd(final String add_id, final ImageView imageView, final int position,final DealsModel sliderModel_) {

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


                            JSONObject errorObj = response.getJSONObject("error");
                            if(errorObj.optString("status").equals("1")){
                                displayMessage(errorObj.optString("message"));
                            }else{
                                JSONObject responseObj = response.getJSONObject("response");
                                if(responseObj.optString("status").equals("1")){

                                    if(responseObj.optString("message").equalsIgnoreCase("Successfully add un-favorited")){
                                        DealsModel sliderModel = new DealsModel();
                                        sliderModel.setAdd_id(sliderModel_.getAdd_id());
                                        sliderModel.setDate(sliderModel_.getDate());
                                        sliderModel.setDescription(sliderModel_.getDescription());
                                        sliderModel.setId(sliderModel_.getId());
                                        sliderModel.setImage(sliderModel_.getImage());
                                        sliderModel.setStatus(sliderModel_.getStatus());
                                        sliderModel.setTitle(sliderModel_.getTitle());
                                        sliderModel.setType(sliderModel_.getType());
                                        sliderModel.setUrl(sliderModel_.getUrl());
                                        sliderModel.setAdd_views_count(sliderModel_.getAdd_views_count());
                                        sliderModel.setIs_favorited_count("false");
                                        dealsModelList.set(position,sliderModel);
                                        notifyDataSetChanged();
                                        //   AdsSliderAdapter.this.notify();
                                        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.new_heart_green));
                                    }else if(responseObj.optString("message").equalsIgnoreCase("Successfully add favorited")){
                                        DealsModel sliderModel = new DealsModel();
                                        sliderModel.setAdd_id(sliderModel_.getAdd_id());
                                        sliderModel.setDate(sliderModel_.getDate());
                                        sliderModel.setDescription(sliderModel_.getDescription());
                                        sliderModel.setId(sliderModel_.getId());
                                        sliderModel.setImage(sliderModel_.getImage());
                                        sliderModel.setStatus(sliderModel_.getStatus());
                                        sliderModel.setTitle(sliderModel_.getTitle());
                                        sliderModel.setType(sliderModel_.getType());
                                        sliderModel.setUrl(sliderModel_.getUrl());
                                        sliderModel.setAdd_views_count(sliderModel_.getAdd_views_count());
                                        sliderModel.setIs_favorited_count("true");
                                        dealsModelList.set(position,sliderModel);
                                        notifyDataSetChanged();
                                        //  AdsSliderAdapter.this.notify();
                                        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.new_heart_red));

                                    }

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


    private void updateAdViewCount(final String add_id, final int position,final DealsModel sliderModel_) {

        showDialog();
        AndroidNetworking.post(URLHelper.UPDATE_ADD_ID)

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
                                    if(sliderModel_.getUrl()!=null){
                                        try {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject = responseObj.getJSONObject("detail");
                                            DealsModel sliderModel = new DealsModel();
                                            sliderModel.setId(sliderModel_.getId());
                                            sliderModel.setAdd_id(sliderModel_.getAdd_id());
                                            sliderModel.setTitle(sliderModel_.getTitle());
                                            sliderModel.setDescription(sliderModel_.getDescription());
                                            sliderModel.setDate(sliderModel_.getDate());
                                            sliderModel.setUrl(sliderModel_.getUrl());
                                            sliderModel.setImage(sliderModel_.getImage());
                                            sliderModel.setType(sliderModel_.getType());
                                            sliderModel.setAdd_views_count(jsonObject.optString("add_views_count"));
                                            sliderModel.setStatus(sliderModel_.getStatus());
                                            sliderModel.setIs_favorited_count(sliderModel_.getAdd_views_count());
                                            dealsModelList.set(position,sliderModel);
                                            notifyDataSetChanged();

                                            Intent i = new Intent(context, ViewWeb.class);
                                            i.putExtra("url",sliderModel_.getUrl());
                                            context.startActivity(i);

                                            /*Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(sliderModel_.getUrl()));
                                            context.startActivity(browserIntent1);*/
                                        }catch (Exception ex){
                                            Toast.makeText(context, "Not a valid url", Toast.LENGTH_SHORT).show();
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

}