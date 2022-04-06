package com.atlanticcity.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.atlanticcity.app.R;

public class QR_Success extends AppCompatActivity {

    String points;
    Button btn_awesome;
    TextView title_text,text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_success);
        btn_awesome = findViewById(R.id.btn_awesome);
        title_text = findViewById(R.id.title_text);
        text = findViewById(R.id.text);

        try {
            Intent i = getIntent();
            points = i.getStringExtra("points");

            title_text.setText("YOU HAVE EARNED "+points+"\n"+"POINTS");
            text.setText(points+ " Points have been added");
        }catch (Exception ex){
            ex.printStackTrace();
        }

        btn_awesome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent goToLogin = new Intent(QR_Success.this, DealsFullScreen.class);
            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(goToLogin);
            finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(QR_Success.this, DealsFullScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
