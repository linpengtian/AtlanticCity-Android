package com.atlanticcity.app.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.atlanticcity.app.Models.EarnedPointsModel;
import com.atlanticcity.app.Models.UserModel;
import com.atlanticcity.app.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InvitesAcceptedAdapter extends RecyclerView.Adapter<InvitesAcceptedAdapter.MyViewHolder> {

    private List<UserModel> userModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEmail,date,tvPhone;
        public CircleImageView icon;

        public MyViewHolder(View view) {
            super(view);
            tvEmail =  view.findViewById(R.id.tvEmail);
            date =  view.findViewById(R.id.date);
            tvPhone = view.findViewById(R.id.tvPhone);

            icon = view.findViewById(R.id.icon);
        }
    }

    public InvitesAcceptedAdapter(List<UserModel> userModelList) {
        this.userModelList = userModelList;
    }

    @Override
    public InvitesAcceptedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invites_accepted_li, parent, false);

        context = parent.getContext();
        return new InvitesAcceptedAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(InvitesAcceptedAdapter.MyViewHolder holder, int position) {
        UserModel mList = userModelList.get(position);

        String date = returnDate(mList.getCreated_at());
        holder.date.setText(date);

        holder.tvEmail.setText(mList.getEmail());
        holder.tvPhone.setText(mList.getPhoneno());
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    String returnDate(String date){

        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date newDate=spf.parse(date);
            spf= new SimpleDateFormat("dd MMM, yyyy");
            date = spf.format(newDate);
            System.out.println(date);
        }catch (Exception ex){
            ex.printStackTrace();
            return date;
        }

        return date;

    }

}
