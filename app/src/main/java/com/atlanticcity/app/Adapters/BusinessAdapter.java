package com.atlanticcity.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.atlanticcity.app.Activities.BusinessDetail;
import com.atlanticcity.app.Models.BusinessModel;
import com.atlanticcity.app.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.MyViewHolder> {

    private List<BusinessModel> businessModels;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView business_name,deal_timgs,address,no_of_deals,deal_desc;
        public CircleImageView business_image;
        CardView main_layout;
        ImageView favourite;
        public MyViewHolder(View view) {
            super(view);
            business_name =  view.findViewById(R.id.business_name);
            business_image =  view.findViewById(R.id.business_image);
            deal_timgs = view.findViewById(R.id.deal_timgs);
            address = view.findViewById(R.id.address);
            no_of_deals = view.findViewById(R.id.no_of_deals);
            main_layout = view.findViewById(R.id.main_layout);
            favourite = view.findViewById(R.id.favourite);
            deal_desc=view.findViewById(R.id.deal_desc);
        }
    }

    public BusinessAdapter(List<BusinessModel> businessModels) {
        this.businessModels = businessModels;
    }

    @Override
    public BusinessAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business_li, parent, false);

        context = parent.getContext();

        return new BusinessAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(BusinessAdapter.MyViewHolder holder, int position) {
        final BusinessModel mList = businessModels.get(position);

        Glide.with(context)
                .load(mList.getLogo())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.splash_bg)
                .error(R.drawable.splash_bg).into( holder.business_image);


     //   Picasso.get().load(mList.getLogo()).placeholder(context.getDrawable(R.drawable.logo)).into(holder.business_image);
        holder.business_name.setText(mList.getBusiness_name());
        holder.address.setText(mList.getAddress());
        holder.deal_timgs.setText("Hours: "+mList.getOpen_time()+ " - "+mList.getClose_time());
        if (mList.getBusiness_detail() == null)
        {
            holder.deal_desc.setVisibility(View.GONE);
        }
        else
            {
        holder.deal_desc.setText(mList.getBusiness_detail());
            }
        holder.no_of_deals.setText(mList.getDeals_count() + " Deals");

        /*if(mList.getIs_favorited_count().equals("true")){
            holder.favourite.setVisibility(View.VISIBLE);
        }else {
            holder.favourite.setVisibility(View.GONE);
        }*/

        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(context, BusinessDetail.class);
                i.putExtra("business_title",mList.getBusiness_name());
                i.putExtra("business_id",mList.getBusiness_user_id());
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return businessModels.size();
    }

}
