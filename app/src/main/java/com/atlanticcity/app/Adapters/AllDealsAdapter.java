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
import com.atlanticcity.app.Models.BusinessModel;
import com.atlanticcity.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllDealsAdapter extends RecyclerView.Adapter<AllDealsAdapter.MyViewHolder> {

    private List<BusinessModel> businessModels;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView deal_title,business_name,deal_desciption;
        public CircleImageView deal_image;
        CardView main_layout;
        public MyViewHolder(View view) {
            super(view);
            business_name =  view.findViewById(R.id.business_name);
            deal_title =  view.findViewById(R.id.deal_title);
            deal_desciption = view.findViewById(R.id.deal_desciption);
            deal_image = view.findViewById(R.id.deal_image);
            main_layout = view.findViewById(R.id.main_layout);
        }
    }

    public AllDealsAdapter(List<BusinessModel> businessModels) {
        this.businessModels = businessModels;
    }

    @Override
    public AllDealsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_deals_li, parent, false);

        context = parent.getContext();

        return new AllDealsAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(AllDealsAdapter.MyViewHolder holder, final int position) {
        final BusinessModel mList = businessModels.get(0);
        Picasso.get().load(mList.getBusinessDeals().get(position).getAvatar()).placeholder(context.getDrawable(R.drawable.logo)).into(holder.deal_image);
        holder.business_name.setText(mList.getBusiness_name());
        holder.deal_title.setText(mList.getBusinessDeals().get(position).getTitle());
        holder.deal_desciption.setText(mList.getBusinessDeals().get(position).getDescription());


        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, ViewDeal.class);
                i.putExtra("avatar",mList.getBusinessDeals().get(position).getAvatar());
                i.putExtra("deal_title",mList.getBusinessDeals().get(position).getTitle());
                i.putExtra("deal_desc",mList.getBusinessDeals().get(position).getDescription());
                i.putExtra("is_favourite",mList.getIs_favorited_count());
                i.putExtra("item_id",mList.getBusinessDeals().get(position).getItem_id());
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return businessModels.get(0).getBusinessDeals().size();
    }

}

