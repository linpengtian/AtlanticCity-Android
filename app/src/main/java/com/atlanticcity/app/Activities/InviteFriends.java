package com.atlanticcity.app.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

public class InviteFriends extends AppCompatActivity {

    Toolbar toolbar;
    Button btn_invite_friends;
    TextView skip_btn,link;
    CustomDialog customDialog;
    RelativeLayout main_layout;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        toolbar = findViewById(R.id.toolbar);
        btn_invite_friends = findViewById(R.id.btn_invite_friends);
        skip_btn = findViewById(R.id.skip_btn);
        link = findViewById(R.id.link);
        main_layout = findViewById(R.id.main_layout);
        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(InviteFriends.this, DealsFullScreen.class);
            startActivity(i);
            finish();
            }
        });

        btn_invite_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(InviteFriends.this, SyncContactsActivity.class);
            startActivity(i);
            finish();
            }
        });

        setupToolbar();
        CreateURL();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.invite_friends_));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent i = new Intent(InviteFriends.this,DealsFullScreen.class);
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void CreateURL(){

       /* showDialog();
        String link_ = "https://www.atlanticcity.com/?invitedby=" + SharedHelper.getKey(InviteFriends.this,"id");
        Task<ShortDynamicLink> dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link_))
                .setDomainUriPrefix("https://atlanticcity1.page.link")

                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder(getPackageName()).build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.floridainc.AtlanticCity").build())
                .buildShortDynamicLink().addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        dismissDialog();
                        main_layout.setVisibility(View.VISIBLE);
                        link.setText(""+shortDynamicLink.getShortLink()+"");
                        SharedHelper.putKey(InviteFriends.this,"invite_link",shortDynamicLink.getShortLink().toString());
                    }
                });*/

       /*  Uri dynamicLinkUri = dynamicLink.getUri();



        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLinkUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            link.setText(""+shortLink+"");
                            Log.d("test_it_main","short_link"+shortLink);
                            Log.d("test_it_main","short_link"+flowchartLink);
                        } else {
                            Toast.makeText(InviteFriends.this, "fail", Toast.LENGTH_SHORT).show();
                            // Error
                            // ...
                        }
                    }
                });*/

        showDialog();
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.atlanticcity.com/?invitedby=" + SharedHelper.getKey(InviteFriends.this,"id")))
                .setDomainUriPrefix("https://atlanticcity1.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder(getPackageName()).build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.floridainc.AtlanticCity").build())
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            dismissDialog();
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            main_layout.setVisibility(View.VISIBLE);
                            link.setText(""+shortLink.toString()+"");
                            SharedHelper.putKey(InviteFriends.this,"invite_link",shortLink.toString());
                        } else {
                            dismissDialog();
                            // Error
                            // ...
                        }
                    }
                });

    }

    void dismissDialog(){
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();
    }

    void showDialog(){
        customDialog = new CustomDialog(InviteFriends.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }
}
