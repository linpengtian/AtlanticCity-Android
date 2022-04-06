package com.atlanticcity.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.atlanticcity.app.Activities.DateOfBirth;
import com.atlanticcity.app.Activities.ZipCodeActivity;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Models.PointsModel;
import com.atlanticcity.app.R;

import java.util.List;

public class PointsListAdapter extends RecyclerView.Adapter<PointsListAdapter.MyViewHolder> {

    private List<PointsModel> pointsModels;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView points_title,get_points,points,points_item;
        public ImageView icon;
        RelativeLayout main_layout;

        public MyViewHolder(View view) {
            super(view);
            points_title =  view.findViewById(R.id.points_title);
            get_points =  view.findViewById(R.id.get_points);
            points = view.findViewById(R.id.points);
            points_item = view.findViewById(R.id.points_item);
            icon = view.findViewById(R.id.icon);
            main_layout = view.findViewById(R.id.main_layout);
        }
    }

    public PointsListAdapter(List<PointsModel> pointsModels) {
        this.pointsModels = pointsModels;
    }

    @Override
    public PointsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.points_li, parent, false);

        context = parent.getContext();
        return new PointsListAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(PointsListAdapter.MyViewHolder holder, int position) {
       final PointsModel mList = pointsModels.get(position);

        if(mList.getKey().equals("date_of_birth_points")){
            holder.icon.setImageResource(R.drawable.reward);
            holder.points_title.setText(context.getResources().getString(R.string.enter_dob));
            holder.get_points.setText(context.getResources().getString(R.string.enter_your_dob_and_get));
            holder.points.setText(mList.getValue()+" points.");
            holder.points_item.setText(mList.getValue()+" POINTS");

        }else if(mList.getKey().equals("zipcode_points")){
            holder.icon.setImageResource(R.drawable.reward);
            holder.points_title.setText(context.getResources().getString(R.string.enter_zip));
            holder.get_points.setText(context.getResources().getString(R.string.enter_zip_and_you_get));
            holder.points.setText(mList.getValue()+" points.");
            holder.points_item.setText(mList.getValue()+" POINTS");

        } else if(mList.getKey().equals("share_app_points")){
            holder.icon.setImageResource(R.drawable.share_and_earn);
            holder.points_title.setText(context.getResources().getString(R.string.share_the_app));
            holder.get_points.setText(context.getResources().getString(R.string.share_app));
            holder.points.setText(mList.getValue()+" points.");
            holder.points_item.setText(mList.getValue()+" POINTS");

        }else if(mList.getKey().equals("qrcode_points")){
            holder.icon.setImageResource(R.drawable.save_money);
            holder.points_title.setText(context.getResources().getString(R.string.shop_and_earn));
            holder.get_points.setText(context.getResources().getString(R.string.find_deals_and_you_get));
            holder.points.setText(mList.getValue()+" points.");
            holder.points_item.setText(mList.getValue()+" POINTS");
        }

        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(mList.getKey().equals("date_of_birth_points")){
                    String date_of_birth = SharedHelper.getKey(context,"date_of_birth");
                    if(date_of_birth.isEmpty() || date_of_birth.equalsIgnoreCase("") || date_of_birth == null || date_of_birth.equalsIgnoreCase("null")){
                        Intent i = new Intent(context, DateOfBirth.class);
                        context.startActivity(i);
                    }else{
                        Toast.makeText(context, "Date of Birth already added.", Toast.LENGTH_SHORT).show();
                    }

                }else if(mList.getKey().equals("zipcode_points")){
                    String zipcode = SharedHelper.getKey(context,"zipcode");
                    if(zipcode.isEmpty() || zipcode.equalsIgnoreCase("") || zipcode == null || zipcode.equalsIgnoreCase("null")){
                        Intent i = new Intent(context, ZipCodeActivity.class);
                        context.startActivity(i);
                    }else{
                        Toast.makeText(context, "Zip Code already added", Toast.LENGTH_SHORT).show();
                    }
                }else if(mList.getKey().equals("share_app_points")){

                   /* Intent i = new Intent(context, InviteFriends.class);
                    context.startActivity(i);*/
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return pointsModels.size();
    }

}
