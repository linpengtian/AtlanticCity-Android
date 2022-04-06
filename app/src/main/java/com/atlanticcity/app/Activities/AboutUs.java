package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atlanticcity.app.R;

public class AboutUs extends AppCompatActivity {

    Toolbar toolbar;
    RelativeLayout privacy_policy_layout,terms_of_service_layout,help_feedback_layout,faq_layout;
    TextView tvVersionName;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        toolbar = findViewById(R.id.toolbar);
        privacy_policy_layout = findViewById(R.id.privacy_policy_layout);
        terms_of_service_layout = findViewById(R.id.terms_of_service_layout);
        help_feedback_layout = findViewById(R.id.help_feedback_layout);
        faq_layout = findViewById(R.id.faq_layout);
        tvVersionName = findViewById(R.id.tvVersionName);

        privacy_policy_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AboutUs.this,TermsAndPrivacyPolicy.class);
                i.putExtra("tag","app_guide");
                startActivity(i);
            }
        });

        terms_of_service_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AboutUs.this,TermsAndPrivacyPolicy.class);
                i.putExtra("tag","terms");
                startActivity(i);
            }
        });

        help_feedback_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AboutUs.this,HelpFeedback.class);
                startActivity(i);
            }
        });

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            tvVersionName.setText("Version: "+version);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        setupToolbar();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.about_us));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
