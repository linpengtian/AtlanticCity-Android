package com.atlanticcity.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.atlanticcity.app.R;

public class QR_Failure extends AppCompatActivity {

    Button btn_try_again;
    String deal_id, business_id,qr_code_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_failure);
        btn_try_again = findViewById(R.id.btn_try_again);

        try{
            Intent i = getIntent();
            deal_id =  i.getStringExtra("deal_id");
            business_id = i.getStringExtra("business_id");
        }catch (Exception ex){
            ex.printStackTrace();
        }

        btn_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(QR_Failure.this, ScanQR.class);
            i.putExtra("deal_id",deal_id);
            i.putExtra("business_id",business_id);
            startActivity(i);
            finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(QR_Failure.this, ScanQR.class);
        i.putExtra("deal_id",deal_id);
        i.putExtra("business_id",business_id);
        startActivity(i);
        finish();
    }
}
