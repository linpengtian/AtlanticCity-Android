package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atlanticcity.app.Helper.DataBaseHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Models.ContactModel;
import com.atlanticcity.app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SyncContactsActivity extends AppCompatActivity {

    Toolbar toolbar;
    RelativeLayout layout_phone_contacts,layout_fb_contacts,gmail_contacts;
    TextView device_contacts_sync_text;
    SQLiteOpenHelper openHelper;
    SQLiteDatabase db;
    public DataBaseHelper mdbHelper;
    public List<ContactModel> contactModels = new ArrayList<>();
    public static final int MY_PERMISSIONS_REQUEST_WRITE_FIELS = 102;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_contacts);
        toolbar = findViewById(R.id.toolbar);
        layout_phone_contacts = findViewById(R.id.layout_phone_contacts);
        layout_fb_contacts = findViewById(R.id.layout_fb_contacts);
        device_contacts_sync_text = findViewById(R.id.device_contacts_sync_text);
        gmail_contacts = findViewById(R.id.gmail_contacts);

        try {
            mdbHelper = new DataBaseHelper(this);
            openHelper = new DataBaseHelper(this);
            db = openHelper.getReadableDatabase();
            mdbHelper.getReadableDatabase();
            mdbHelper.openDatabase();
            contactModels = DataBaseHelper.GetDeviceContacts(SyncContactsActivity.this);

            if(contactModels.size()<=0){
                device_contacts_sync_text.setText("Sync now");

            }else {
                device_contacts_sync_text.setText("Resync");

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }


        device_contacts_sync_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(device_contacts_sync_text.getText().toString().equalsIgnoreCase("Resync")){
                DataBaseHelper.DeleteContactsTable(SyncContactsActivity.this);
                contactModels = DataBaseHelper.GetDeviceContacts(SyncContactsActivity.this);
                Log.v("contacts_size",""+contactModels.size()+"");
                  Intent i = new Intent(SyncContactsActivity.this,Contacts.class);
                  startActivity(i);

            }else {
              checkAppPermissions();

            }
            }
        });

        layout_phone_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent i = new Intent(SyncContactsActivity.this,Contacts.class);
                startActivity(i);*/
            checkAppPermissions();
            }
        });
        layout_fb_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent
                        .putExtra(Intent.EXTRA_TEXT,
                                SharedHelper.getKey(SyncContactsActivity.this,"invite_link"));
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.facebook.orca");
                try {
                    startActivity(sendIntent);
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SyncContactsActivity.this,"Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
                }

      /*      Intent i = new Intent(SyncContactsActivity.this, FbFriendsList.class);
            startActivity(i);*/
            }
        });

        gmail_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(SyncContactsActivity.this, GoogleContacts.class);
            startActivity(i);
            }
        });
        setupToolbar();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.sync_contacts));
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

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mdbHelper = new DataBaseHelper(this);
            openHelper = new DataBaseHelper(this);
            db = openHelper.getReadableDatabase();
            mdbHelper.getReadableDatabase();
            mdbHelper.openDatabase();
            contactModels = DataBaseHelper.GetDeviceContacts(SyncContactsActivity.this);

            if(contactModels.size()<=0){
                device_contacts_sync_text.setText("Sync now");
                device_contacts_sync_text.setTextColor(getResources().getColor(R.color.black));
            }else {
                device_contacts_sync_text.setText("Resync");
                device_contacts_sync_text.setTextColor(getResources().getColor(R.color.red));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void checkAppPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)   ) {
               Intent i = new Intent(SyncContactsActivity.this,Contacts.class);
               startActivity(i);
            } else {
                Intent i = new Intent(SyncContactsActivity.this,AllowAccess.class);
                startActivity(i);
                finish();
            }
        } else {
            Intent i = new Intent(SyncContactsActivity.this,Contacts.class);
            startActivity(i);
        }
    }
}
