package com.atlanticcity.app.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.AppConstants;
import com.atlanticcity.app.Utils.Code;
import com.atlanticcity.app.Utils.CustomDialog;
import com.atlanticcity.app.Utils.FileUtil;
import com.atlanticcity.app.Utils.RPResultListener;
import com.atlanticcity.app.Utils.RuntimePermissionUtil;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


import static com.atlanticcity.app.Activities.SplashActivity.GlobalDealsData;


public class ScanQR extends AppCompatActivity {
    private static final String cameraPerm = Manifest.permission.CAMERA;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    Toolbar toolbar;

    private SurfaceView mySurfaceView;


    boolean hasCameraPermission = false;
    String deal_id, business_id,qr_code_id;
    TextView ask_from_employee;
    public String TAG = "ScanQR";
    Vibrator vibe;
    boolean isDetected = false;
    private BarcodeView mBarcodeView;
    private BeepManager mBeepManager;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        hasCameraPermission = RuntimePermissionUtil.checkPermissonGranted(this, cameraPerm);
        vibe  = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        try{
            Intent i = getIntent();
            deal_id =  i.getStringExtra("deal_id");
            business_id = i.getStringExtra("business_id");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        viewInitializer();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void viewInitializer(){
        toolbar = findViewById(R.id.toolbar);
        ask_from_employee = findViewById(R.id.ask_from_employee);
        mBarcodeView =(BarcodeView)  findViewById(R.id.barcode_view);
        mBeepManager = new BeepManager(ScanQR.this);

        mBeepManager.setVibrateEnabled(true);
        mBeepManager.setBeepEnabled(false);


        if (hasCameraPermission) {

            doScan();


        } else {
            RuntimePermissionUtil.requestPermission(ScanQR.this, cameraPerm, 100);
        }
        setupToolbar();
    }

    private void doScan() {

        mBarcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                mBarcodeView.pause();
                mBeepManager.playBeepSoundAndVibrate();

                if (result != null
                        && !TextUtils.isEmpty(result.getText())
                        && !TextUtils.isEmpty(result.getBarcodeFormat().name())) {

                    Code code;

                    if (result.getBitmap() != null) {
                        int typeIndex = result.getBarcodeFormat().name().toLowerCase().startsWith("qr")
                                ? Code.QR_CODE : Code.BAR_CODE;
                        String type = getResources().getStringArray(R.array.code_types)[typeIndex];

                        File codeImageFile = FileUtil.getEmptyFile(ScanQR.this, AppConstants.PREFIX_IMAGE,
                                String.format(Locale.ENGLISH, getString(R.string.file_name_body),
                                        type.substring(0, type.indexOf(" Code")),
                                        String.valueOf(System.currentTimeMillis())),
                                AppConstants.SUFFIX_IMAGE,
                                Environment.DIRECTORY_PICTURES);

                        if (codeImageFile != null) {
                            try (FileOutputStream out = new FileOutputStream(codeImageFile)) {
                                result.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);

                                code = new Code(result.getText(),
                                        result.getBarcodeFormat().name().toLowerCase().startsWith("qr")
                                                ? Code.QR_CODE : Code.BAR_CODE,
                                        codeImageFile.getPath(), result.getResult().getTimestamp());
                            } catch (IOException e) {
                                if (!TextUtils.isEmpty(e.getMessage())) {
                                    Log.e(getClass().getSimpleName(), e.getMessage());
                                }

                                code = new Code(result.getText(),
                                        result.getBarcodeFormat().name().toLowerCase().startsWith("qr")
                                                ? Code.QR_CODE : Code.BAR_CODE, result.getResult().getTimestamp());
                            }
                        } else {
                            code = new Code(result.getText(),
                                    result.getBarcodeFormat().name().toLowerCase().startsWith("qr")
                                            ? Code.QR_CODE : Code.BAR_CODE, result.getResult().getTimestamp());
                        }
                    } else {
                        code = new Code(result.getText(),
                                result.getBarcodeFormat().name().toLowerCase().startsWith("qr")
                                        ? Code.QR_CODE : Code.BAR_CODE, result.getResult().getTimestamp());
                    }

                    qr_code_id = code.getContent();
                   ScanQrCode();
                } else {
                    mBarcodeView.resume();
                    doScan();
                    Toast.makeText(ScanQR.this, "Error occurred",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });
    }


    public void displayMessage(  String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(ScanQR.this,""+toastString,Toast.LENGTH_SHORT).show();
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
        customDialog = new CustomDialog(ScanQR.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar() {
        toolbar.setTitle(getResources().getString(R.string.scan_qr));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == 100) {
            RuntimePermissionUtil.onRequestPermissionsResult(grantResults, new RPResultListener() {
                @Override
                public void onPermissionGranted() {
                    if ( RuntimePermissionUtil.checkPermissonGranted(ScanQR.this, cameraPerm)) {

                        Intent i = new Intent(ScanQR.this,ScanQR.class);
                        i.putExtra("deal_id",deal_id);
                        i.putExtra("business_id",business_id);
                        startActivity(i);
                        finish();
                    }
                }

                @Override
                public void onPermissionDenied() {
                    // do nothing
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBarcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBarcodeView.pause();
    }


    private void ScanQrCode() {
       /* qrEader.releaseAndCleanup();
        qrEader.stop();*/
        vibe.vibrate(100);
        showDialog();
        AndroidNetworking.post(URLHelper.SCAN_QR)

                .addBodyParameter("business_id", business_id)
                .addBodyParameter("deal_id", deal_id)
                .addBodyParameter("qrcode_id", qr_code_id)
                .addHeaders("user-id", SharedHelper.getKey(ScanQR.this,"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(ScanQR.this,"auth_id"))
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
                            }else{
                                JSONObject responseObj = response.getJSONObject("response");


                                if(responseObj.optString("status").equals("1")){
                                    Toast.makeText(ScanQR.this, responseObj.optString("message"), Toast.LENGTH_SHORT).show();

                                    if(responseObj.optString("message").equalsIgnoreCase("Sorry, deal already availed")){

                                        Intent i = new Intent(ScanQR.this,DealsFullScreen.class);
                                        startActivity(i);
                                        finish();
                                    }else{
                                        GlobalDealsData.clear();
                                        Intent i = new Intent(ScanQR.this,QR_Success.class);
                                        i.putExtra("points",responseObj.optString("detail"));
                                        startActivity(i);
                                        finish();
                                    }


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
                                    Intent i = new Intent(ScanQR.this,QR_Failure.class);
                                    i.putExtra("deal_id",deal_id);
                                    i.putExtra("business_id",business_id);
                                    startActivity(i);
                                    finish();

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
}
