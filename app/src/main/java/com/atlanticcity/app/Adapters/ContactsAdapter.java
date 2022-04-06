package com.atlanticcity.app.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.atlanticcity.app.Models.ContactModel;
import com.atlanticcity.app.Models.InvitesMode;
import com.atlanticcity.app.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    private List<ContactModel> contactModels;
    private List<ContactModel> contactsToSend;
    List<InvitesMode> invitesModeList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,number,tvInviteSent;
        public CircleImageView deal_image;
        CardView main_layout;
        CheckBox invite_check;
        public MyViewHolder(View view) {
            super(view);
            number =  view.findViewById(R.id.business_name);
            name =  view.findViewById(R.id.deal_title);

            deal_image = view.findViewById(R.id.deal_image);
            main_layout = view.findViewById(R.id.main_layout);
            invite_check = view.findViewById(R.id.invite_check);
            tvInviteSent = view.findViewById(R.id.tvInviteSent);
        }
    }

    public ContactsAdapter(List<ContactModel> contactModels, List<ContactModel> contactsToSend, List<InvitesMode> invitesModeLists ) {
        this.contactModels = contactModels;
        this.contactsToSend = contactsToSend;
        this.invitesModeList = invitesModeLists;
    }

    @Override
    public ContactsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_li, parent, false);

        context = parent.getContext();
        return new ContactsAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ContactsAdapter.MyViewHolder holder, final int position) {
        final ContactModel mList = contactModels.get(position);

        if( !mList.photo.equals("")){
            Bitmap bitmap = StringToBitMap(mList.getPhoto());
            holder.deal_image.setImageBitmap(bitmap);
           // holder.deal_image.setImageURI(mList.photoURI);
        }else {
            holder.deal_image.setImageDrawable(context.getResources().getDrawable(R.drawable.account));
        }
        holder.invite_check.setOnCheckedChangeListener(null);

        holder.name.setText(mList.name);
        holder.number.setText(mList.mobileNumber);

        if(mList.isChecked()){
            holder.invite_check.setChecked(true);
        }else {
            holder.invite_check.setChecked(false);
        }

        if(invitesModeList.size()==0){
            holder.invite_check.setVisibility(View.VISIBLE);
        }else {
            for(int i = 0; i<invitesModeList.size();i++ ){
                String phone = mList.mobileNumber.trim().replace(" ","");
                if(phone.equalsIgnoreCase(invitesModeList.get(i).getPhone_no())){
                    if(invitesModeList.get(i).getStatus().equalsIgnoreCase("1")){
                        holder.tvInviteSent.setText("Signed up");
                        holder.tvInviteSent.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                    }else {
                        holder.tvInviteSent.setText("Sent");
                        holder.tvInviteSent.setTextColor(context.getResources().getColor(R.color.red));
                    }
                    holder.tvInviteSent.setVisibility(View.VISIBLE);
                    holder.invite_check.setVisibility(View.GONE);
                    break;
                }else {
                    holder.tvInviteSent.setVisibility(View.GONE);
                    holder.invite_check.setVisibility(View.VISIBLE);
                }
            }
        }



     //   if(mList.mobileNumber.equalsIgnoreCase(invitesModeList.get))

     //   holder.id.setText(mList.id);

        holder.invite_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    // mList.isChecked = true;
                  contactsToSend.add(new ContactModel(mList.id,mList.name, mList.mobileNumber));
                  Log.v("size",""+contactsToSend.size()+"");
                  Log.v("id_added",""+mList.id+"");
                  mList.isChecked = true;
                   //  notifyDataSetChanged();
                }else{
                   // mList.isChecked = false;
                    for(int i = 0 ; i < contactsToSend.size(); i++){
                        if(mList.id == Integer.valueOf(contactsToSend.get(i).id)){
                            Log.v("id_removed",""+contactsToSend.get(i).id+"");
                            contactsToSend.remove(i);
                            mList.isChecked = false;

                        }
                    }

                  //  notifyDataSetChanged();
                   }
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactModels.size();
    }

    public void filterList(List<ContactModel> filteredList) {

        contactModels = filteredList;
        notifyDataSetChanged();
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

}

