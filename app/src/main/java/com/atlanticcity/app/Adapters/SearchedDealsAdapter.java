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
import com.atlanticcity.app.Models.DealsModel;
import com.atlanticcity.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchedDealsAdapter extends RecyclerView.Adapter<SearchedDealsAdapter.MyViewHolder> {

    private List<DealsModel> dealsModels;
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

    public SearchedDealsAdapter(List<DealsModel> dealsModels) {
        this.dealsModels = dealsModels;
    }

    @Override
    public SearchedDealsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_deals_li, parent, false);

        context = parent.getContext();

        return new SearchedDealsAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(SearchedDealsAdapter.MyViewHolder holder, final int position) {
        final DealsModel mList = dealsModels.get(position);
        if(mList!=null){
            Picasso.get().load(mList.getAvatar()).placeholder(context.getDrawable(R.drawable.logo)).into(holder.deal_image);
            holder.business_name.setText(mList.getBusinessModel().getBusiness_name());
            holder.deal_title.setText(mList.getTitle());
            holder.deal_desciption.setText(mList.getDescription());
        }



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
        return dealsModels.size();
    }

}

