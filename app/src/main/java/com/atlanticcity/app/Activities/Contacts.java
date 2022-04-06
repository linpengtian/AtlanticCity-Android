package com.atlanticcity.app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.atlanticcity.app.Adapters.EarnedPointsAdapter;
import com.atlanticcity.app.Models.EarnedPointsModel;
import com.atlanticcity.app.Models.InvitesMode;
import com.google.android.material.snackbar.Snackbar;
import com.atlanticcity.app.Adapters.ContactsAdapter;
import com.atlanticcity.app.Helper.DataBaseHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.ContactModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomRequestQueue;
import com.atlanticcity.app.Utils.GridSpacingItemDecoration;
import com.atlanticcity.app.Utils.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contacts extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    List<ContactModel> inviteContactsModel = new ArrayList<>();
    ProgressDialog customDialog;
    Toolbar toolbar;
    RecyclerView contacts_rv;
    ContactsAdapter contactsAdapter;
    private static final int CONTACTS_LOADER_ID = 1;
    EditText et_search;
    Button send_invite;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_FIELS = 102;
    public List<ContactModel> contactModels = new ArrayList<>();
    private AlertDialog dialog;
    SQLiteOpenHelper openHelper;
    SQLiteDatabase db;
    public DataBaseHelper mdbHelper;
    String list_phone;
    public String TAG = "Contacts";
    List<InvitesMode> invitesModeList;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        toolbar = findViewById(R.id.toolbar);
        contacts_rv = findViewById(R.id.contacts_rv);
        et_search = findViewById(R.id.et_search);
        send_invite = findViewById(R.id.send_invite);

        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(Contacts.this, 1);
        contacts_rv.setLayoutManager(gridLayoutManager1);
        contacts_rv.setItemAnimator(new DefaultItemAnimator());
        contacts_rv.setNestedScrollingEnabled(false);
        contacts_rv.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));

        setupToolbar();

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {


        }*/

       checkAppPermissions();
     //  getLoaderManager().restartLoader(1, null, this);

        send_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(inviteContactsModel.size()<=0){
                Toast.makeText(Contacts.this, "Please select at least one contact.", Toast.LENGTH_SHORT).show();
            }/*else if(inviteContactsModel.size()==1){
                Uri uri = Uri.parse("smsto:"+inviteContactsModel.get(0).mobileNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "Hi! "+inviteContactsModel.get(0).name+ " Please Download AtlanticCity Application " + SharedHelper.getKey(Contacts.this,"invite_link"));
                startActivity(intent);


            }*/else if(inviteContactsModel.size()>1 && inviteContactsModel.size()>10){
                Toast.makeText(Contacts.this, "You cannot send invite to more than 10 people.", Toast.LENGTH_SHORT).show();
            }else {

                SendMessages();

            }
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }


    public List<ContactModel> getContacts(Context ctx,Cursor cursor) {
        Log.v("get_contacts","called");
        List<ContactModel> list = new ArrayList<>();
        ContentResolver contentResolver =  ctx.getContentResolver();

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, ContactsContract.Contacts.DISPLAY_NAME+" ASC");
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                    Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                    Bitmap photo = null;
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream);
                    }
                    while (cursorInfo.moveToNext()) {
                        ContactModel info = new ContactModel();
                        info.id = Integer.valueOf(id);
                        info.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        info.mobileNumber = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(photo!=null){
                            info.photo = BitMapToString(photo);
                        }else {
                            info.photo = "";
                        }
                       // info.photo = photo;
                        info.photoURI= pURI;
                        info.isChecked = false;
                        info.is_sent = 0;
                        list.add(info);
                    }

                    cursorInfo.close();
                }
            }
            cursor.close();
        }

        return list;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_FIELS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GoNext();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Contacts.this);
                builder.setMessage("App required some permission please enable it")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                openPermissionScreen();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                            }
                        });
                dialog = builder.show();
            }
            return;
        }
    }


    void dismissDialog(){
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();
    }

    void showDialog(){
        customDialog = new ProgressDialog(Contacts.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.contacts));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent i = new Intent(Contacts.this,DealsFullScreen.class);
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

/*    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == CONTACTS_LOADER_ID) {
            return contactsLoader();
        }
        return null;
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i == CONTACTS_LOADER_ID) {
            return contactsLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        contactModels.clear();
        Log.v("cursor_size",""+cursor.getColumnCount()+"");
        contactModels = getContacts(Contacts.this,cursor);

        if(DataBaseHelper.insertDeviceContacts(Contacts.this,contactModels)){
            Toast.makeText(this, "Contacts saved successfully.", Toast.LENGTH_SHORT).show();
        }
        dismissDialog();
        GoNext();
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }

    private  Loader<Cursor> contactsLoader() {
        Uri contactsUri = ContactsContract.Contacts.CONTENT_URI; // The content URI of the phone contacts
        String selection = null;                                 //Selection criteria
        String[] selectionArgs = {};                             //Selection criteria
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME+" ASC";                                 //The sort order for the returned rows

        return new CursorLoader(
                getApplicationContext(),
                contactsUri,
                null,
                selection,
                selectionArgs,
                sortOrder);
    }

    public void openPermissionScreen() {
        // startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", Contacts.this.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void filter(String text) {
        List<ContactModel> filteredList = new ArrayList<>();
        for (ContactModel item :  contactModels) {
            if (item.name.toLowerCase().contains(text.toLowerCase()) ) {
                filteredList.add(item);
            }
        }
        contactsAdapter.filterList(filteredList);
    }


    void GoNext(){
        mdbHelper = new DataBaseHelper(this);
        openHelper = new DataBaseHelper(this);
        db = openHelper.getReadableDatabase();
        mdbHelper.getReadableDatabase();
        mdbHelper.openDatabase();
        contactModels.clear();
        contactModels = DataBaseHelper.GetDeviceContacts(Contacts.this);
        if( contactModels.size()<=0){
            showDialog();
            getLoaderManager().initLoader(CONTACTS_LOADER_ID,
                    null,
                    this);
        }else{
            inviteContactsSent();
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
                GoNext();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS,
                                  Manifest.permission.SEND_SMS
                        },
                        MY_PERMISSIONS_REQUEST_WRITE_FIELS);
            }
        } else {
            GoNext();
        }
    }

    private void SendMessages() {
        String first_name = SharedHelper.getKey(Contacts.this,"first_name");
        String last_name = SharedHelper.getKey(Contacts.this,"last_name");


        showDialog();
        JSONObject obj = new JSONObject();
        try {
            list_phone = null;
            for(int i = 0; i < inviteContactsModel.size();i++){
                if(list_phone!=null){
                    list_phone = list_phone+","+inviteContactsModel.get(i).getMobileNumber();
                }else{
                    list_phone = inviteContactsModel.get(i).getMobileNumber();
                }
            }
            obj.put("id",SharedHelper.getKey(Contacts.this,"id"));
            obj.put("list_phone",list_phone);

            if(!first_name.equalsIgnoreCase("null")){
                obj.put("message", "Your friend "+first_name+" "+last_name+"  has invited you to become part of Atlantic City. click on the link below to join the Atlantic City family and win amazing  prizes. " + SharedHelper.getKey(Contacts.this,"invite_link"));
            }else {
                obj.put("message", SharedHelper.getKey(Contacts.this,"invite_link"));
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }


        AndroidNetworking.post(URLHelper.SEND_MESSAGES)
                .addJSONObjectBody(obj)
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            JSONObject errorObj = response.getJSONObject("error");
                            if(errorObj.optString("status").equals("1")){
                                displayMessage(errorObj.optString("message"));
                            }else {
                                JSONObject responseObj = response.getJSONObject("response");
                                if (responseObj.optString("status").equals("1")) {
                                    //displayMessage(responseObj.optString("message"));


                                    showSuccessDialog("Success!","You are getting closer to 10 signups. You will be notified when your friends signup.");

                                }

                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                        dismissDialog();

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error

                        dismissDialog();
                        if (error.getErrorCode() != 0) {
                            // received error from server
                            // error.getErrorCode() - the error code from server
                            // error.getErrorBody() - the error body from server
                            // error.getErrorDetail() - just an error detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                            // get parsed error object (If ApiError is your class)
                            String errorObj = error.getErrorBody();
                            try {
                                JSONObject errorJson = new JSONObject(errorObj);
                                if(errorJson.optJSONObject("error").optString("status").equals("1")){
                                    displayMessage(errorJson.optJSONObject("error").optString("message"));
                                }
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });

    }

    public void displayMessage(  String toastString) {

        try{
            Toast.makeText(Contacts.this,""+toastString,Toast.LENGTH_SHORT).show();
        }catch (Exception ee){
            ee.printStackTrace();
        }

    }

    void Resync(){
        mdbHelper = new DataBaseHelper(this);
        openHelper = new DataBaseHelper(this);
        db = openHelper.getReadableDatabase();
        mdbHelper.getReadableDatabase();
        mdbHelper.openDatabase();
        DataBaseHelper.DeleteContactsTable(Contacts.this);
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    private void inviteContactsSent() {
        showDialog();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id",SharedHelper.getKey(Contacts.this,"id"));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        AndroidNetworking.post(URLHelper.GET_INVITE_CONTACTS)
                .addJSONObjectBody(jsonObject)
                .addHeaders("user-id", SharedHelper.getKey(Contacts.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(Contacts.this,"auth_id"))
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        dismissDialog();
                        try {

                            JSONObject errorObj = response.getJSONObject("error");
                            if(errorObj.optString("status").equals("1")){
                                displayMessage(errorObj.optString("message"));
                            }else{
                                JSONObject responseObj = response.getJSONObject("response");
                                if(responseObj.optString("status").equals("1")){

                                    // JSONObject detail = responseObj.getJSONObject("detail");
                                    invitesModeList = new ArrayList<>();
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<InvitesMode>>() {
                                    }.getType();
                                    invitesModeList = gson.fromJson(responseObj.getString("detail"), listType);

                                    contactsAdapter = new ContactsAdapter(contactModels,inviteContactsModel,invitesModeList);
                                    contacts_rv.setAdapter(contactsAdapter);
                                    contactsAdapter.notifyDataSetChanged();

                                }
                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        dismissDialog();

                        if (error.getErrorCode() != 0) {
                            // received error from server
                            // error.getErrorCode() - the error code from server
                            // error.getErrorBody() - the error body from server
                            // error.getErrorDetail() - just an error detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                            // get parsed error object (If ApiError is your class)
                            String errorObj = error.getErrorBody();
                            try {
                                JSONObject errorJson = new JSONObject(errorObj);
                                if(errorJson.optJSONObject("error").optString("status").equals("1")){
                                    displayMessage(errorJson.optJSONObject("error").optString("message"));
                                }
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
        }

    private void showSuccessDialog(String title, String message) {
        if (!isFinishing()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater)Contacts.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                    Intent i = new Intent(Contacts.this,ViewInvites.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("status","text");
                    startActivity(i);
                    finish();

                }
            });

            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(Contacts.this, R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }

}
