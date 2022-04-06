package com.atlanticcity.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.MyViewHolder> {

    private List<DealsModel> dealsModels;
    private Context context;
    CustomDialog customDialog;
    String item_id;

    public String TAG = "DealsAdapter";
    DealsFullScreen dealsFullScreen;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView business_name,address,deal_title,deal_description,business_navigator,favorites_navigator,ppl_claimed,settings_navigator;;
        public CircleImageView business_logo;
        RoundedImageView deal_avatar;
        RelativeLayout main_layout;
        ImageView image_share,image_like,menu;
        Button btn_get_this_deal;
        LinearLayout menu_layout;
        public MyViewHolder(View view) {
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
            menu_layout = view.findViewById(R.id.menu_layout);
            settings_navigator = view.findViewById(R.id.settings_navigator);
        }
    }

    public DealsAdapter(DealsFullScreen dealsFullScreen,List<DealsModel> dealsModels) {
        this.dealsModels = dealsModels;
        this.dealsFullScreen = dealsFullScreen;
    }

    @Override
    public DealsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deals_full_screen_li, parent, false);

        context = parent.getContext();
        return new DealsAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final DealsAdapter.MyViewHolder holder, int position) {
        final DealsModel mList = dealsModels.get(position);
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
                                    holder.address.setText(mList.getBusinessModel().getAddress());
                                }else {
                                    holder.address.setText("--");
                                }



                                if(mList.getBusinessModel().getBusiness_name()!=null){
                                    holder.business_name.setText(mList.getBusinessModel().getBusiness_name());
                                }else {
                                    holder.business_name.setText("--");
                                }


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
                            Picasso.get().load(mList.getBusinessModel().getLogo()) .error(R.mipmap.ic_launcher).into( holder.business_logo);
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
                                    holder.address.setText(mList.getBusinessModel().getAddress());
                                }else {
                                    holder.address.setText("--");
                                }



                                if(mList.getBusinessModel().getBusiness_name()!=null){
                                    holder.business_name.setText(mList.getBusinessModel().getBusiness_name());
                                }else {
                                    holder.business_name.setText("--");
                                }


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

                            Picasso.get().load(mList.getBusinessModel().getLogo()) .error(R.mipmap.ic_launcher).into( holder.business_logo);
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
            /*    Intent i = new Intent(context, SingleDealActivity.class);
                i.putExtra("item_id",mList.getItem_id());
                i.putExtra("deal_title",mList.getTitle());
                i.putExtra("deal_description",mList.getDescription());
                i.putExtra("deal_expire_at",mList.getDeal_expire_at());
                i.putExtra("business_address",mList.getBusinessModel().getAddress());
                i.putExtra("business_name",mList.getBusinessModel().getBusiness_name());

                context.startActivity(i);*/

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
                item_id = mList.getItem_id();
                AddFavoriteDeal(item_id,holder);
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

    @Override
    public int getItemCount() {
        return dealsModels.size();
    }

  /*  private void AddFavoriteDeal(final String item_id,final MyViewHolder holder) {

        showDialog();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URLHelper.ADD_FAVORITE_DEAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //  if ((customDialog != null) && (customDialog.isShowing()))
                //customDialog.dismiss();

                try {

                    JSONObject response_obj = new JSONObject(response);
                    JSONObject errorObj = response_obj.getJSONObject("error");
                    if(errorObj.optString("status").equals("1")){
                        displayMessage(errorObj.optString("message"));
                    }else{
                        JSONObject responseObj = response_obj.getJSONObject("response");
                        if(responseObj.optString("status").equals("1")){

                            if(responseObj.optString("message").equalsIgnoreCase("successfully deal un-favorited")){
                                holder.image_like.setImageDrawable(context.getResources().getDrawable(R.drawable.heart));
                            }else if(responseObj.optString("message").equalsIgnoreCase("successfully deal favorited")){
                                holder.image_like.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_red));

                            }

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
                        displayMessage(context.getString(R.string.something_went_wrong));
                    }


                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(context.getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(context.getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        AddFavoriteDeal(item_id,holder);
                    }
                }

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("user-id", SharedHelper.getKey(context,"user_id"));
                headers.put("auth-id", SharedHelper.getKey(context,"auth_id"));
                return headers;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("item_id", item_id);
                params.put("deal_id", item_id);


                return params;
            }
        };

        CustomRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }*/

    private void AddFavoriteDeal(final String item_id,final MyViewHolder holder) {

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
                                        holder.image_like.setImageDrawable(context.getResources().getDrawable(R.drawable.new_heart_green));
                                    }else if(responseObj.optString("message").equalsIgnoreCase("Successfully deal favorited")){
                                        holder.image_like.setImageDrawable(context.getResources().getDrawable(R.drawable.new_heart_red));

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

