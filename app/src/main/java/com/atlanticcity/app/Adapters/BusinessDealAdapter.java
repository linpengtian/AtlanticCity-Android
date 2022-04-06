package com.atlanticcity.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.atlanticcity.app.Activities.ViewDeal;
import com.atlanticcity.app.Models.BusinessDeals;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.FadingImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BusinessDealAdapter extends RecyclerView.Adapter<BusinessDealAdapter.MyViewHolder> {

    private List<BusinessDeals> businessDeals;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView deal_title,deal_description;
        public FadingImageView deal_logo;
        CardView main_layout;
        public MyViewHolder(View view) {
            super(view);
            deal_title =  view.findViewById(R.id.deal_title);
            deal_logo =  view.findViewById(R.id.deal_logo);
            deal_description = view.findViewById(R.id.deal_description);


            deal_logo.setFadeBottom(true);
            deal_logo.setEdgeLength(70);


            main_layout = view.findViewById(R.id.main_layout);
        }
    }

    public BusinessDealAdapter(List<BusinessDeals> businessDeals) {
        this.businessDeals = businessDeals;
    }

    @Override
    public BusinessDealAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business_deal_li, parent, false);

        context = parent.getContext();

        return new BusinessDealAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(BusinessDealAdapter.MyViewHolder holder, int position) {
        final BusinessDeals mList = businessDeals.get(position);
        Glide.with(context)
                .load(mList.getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.splash_bg)
                .error(R.drawable.splash_bg).into( holder.deal_logo);

      //  Picasso.get().load(mList.getAvatar()).placeholder(context.getDrawable(R.drawable.logo)).into(holder.deal_logo);
        holder.deal_title.setText(mList.getTitle());
        holder.deal_description.setText(mList.getDescription());


        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, ViewDeal.class);
                i.putExtra("avatar",mList.getAvatar());
                i.putExtra("deal_title",mList.getTitle());
                i.putExtra("deal_desc",mList.getDescription());
                i.putExtra("is_favourite",mList.getIs_favorited_count());
                i.putExtra("item_id",mList.getItem_id());
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return businessDeals.size();
    }

}
