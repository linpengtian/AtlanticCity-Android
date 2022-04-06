package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.R;

public class PointsActivity extends AppCompatActivity {

    Button get_more_points_button;
    Toolbar toolbar;
    TextView main_text;
    String status,points;
   // KonfettiView konfettiView;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);
        viewInitializer();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void viewInitializer(){
        get_more_points_button = findViewById(R.id.get_more_points_button);
        toolbar = findViewById(R.id.toolbar);
        main_text = findViewById(R.id.main_text);
     //   konfettiView = findViewById(R.id.konfettiView);
        try {
            Intent i = getIntent();
            status =  i.getStringExtra("status");
            points = i.getStringExtra("points");

            main_text.setText("You have earned "+points+ " points "+ "\n"+ "for your "+status+".");
         //   main_text.setText(getString(R.string.you_have_earned)+ " " +status+".");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        setupToolbar();

//        try {
//            konfettiView.build()
//                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
//                    .setDirection(0.0, 359.0)
//                    .setSpeed(1f, 5f)
//                    .setFadeOutEnabled(true)
//                    .setTimeToLive(2000L)
//                    .addShapes(Shape.CIRCLE, Shape.RECT)
//                    .addSizes(new Size(12,5f))
//                    .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
//                    .stream(300, 5000L);
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }

        get_more_points_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date_of_birth = SharedHelper.getKey(PointsActivity.this,"date_of_birth");
                if(status.equalsIgnoreCase("registering")){
                    Intent i = new Intent(PointsActivity.this, ZipCodeActivity.class);
                    startActivity(i);
                    finish();
                }else if(status.equalsIgnoreCase("zipcode")){
                    if(date_of_birth.equalsIgnoreCase("null") || date_of_birth.equalsIgnoreCase("")){
                        Intent i = new Intent(PointsActivity.this, DateOfBirth.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(PointsActivity.this, HowToEarnPoints.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }

                }else if(status.equalsIgnoreCase("birthday")){
                    Intent i = new Intent(PointsActivity.this, HowToEarnPoints.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(PointsActivity.this, DealsFullScreen.class);
                    startActivity(i);
                    finish();
                }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.earn_points_now));
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
