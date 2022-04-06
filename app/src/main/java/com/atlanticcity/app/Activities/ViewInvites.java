package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.atlanticcity.app.Adapters.InvitesAdapter;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.dueeeke.tablayout.SegmentTabLayout;
import com.dueeeke.tablayout.listener.OnTabSelectListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

public class ViewInvites extends AppCompatActivity {

    SegmentTabLayout tl_1;
    ViewPager viewPager;
    Toolbar toolbar;
    private String[] mTitles = {"Earned", "Accepted", "Sent"};
    InvitesAdapter invitesAdapter;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    public String TAG = "ViewInvites";
    String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invites);
        viewInitializer();
        setupToolbar();
    }

    void viewInitializer(){
        toolbar = findViewById(R.id.toolbar);

        tl_1 = findViewById(R.id.tl_1);
        tl_1.setTabData(mTitles);

        viewPager = (ViewPager) findViewById(R.id.pager);
        invitesAdapter = new InvitesAdapter(getSupportFragmentManager(), tl_1.getTabCount());
        viewPager.setAdapter(invitesAdapter);


        try {
            Intent i = getIntent();
            status = i.getStringExtra("status");
            if(status!=null){
                tl_1.setCurrentTab(2);
                viewPager.setCurrentItem(2);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tl_1.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tl_1.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.your_invites));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();

                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    public void displayMessage(  String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(ViewInvites.this,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    void dismissDialog(){
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();
    }

    void showDialog(){
        customDialog = new CustomDialog(ViewInvites.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(status!=null){
            Intent i = new Intent(ViewInvites.this,DealsFullScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }else {
           finish();
        }
    }
}
