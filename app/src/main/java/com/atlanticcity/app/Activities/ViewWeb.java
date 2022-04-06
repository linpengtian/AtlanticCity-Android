package com.atlanticcity.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;

public class ViewWeb extends AppCompatActivity {
    WebView webView;
    String url;
    CustomDialog customDialog;
    ImageView btnClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_web);
        webView = findViewById(R.id.webview);
        btnClose = findViewById(R.id.btnClose);
        WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.getDatabaseEnabled();
        mWebSettings.supportMultipleWindows();
        mWebSettings.setDomStorageEnabled(true);
        WebViewClient mWebViewClient = new WebViewClient();
        webView.setWebViewClient(mWebViewClient);

//load the page with cache
        if (Build.VERSION.SDK_INT >= 19) {
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        try {
            Intent i =getIntent();
            url =  i.getStringExtra("url");
        }catch (Exception ex){
            ex.printStackTrace();
        }

        showDialog();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
               // view.loadUrl(url);
                return false;
            }
            @Override
            public void onLoadResource(WebView view, String url) {
              //  view.loadUrl(url);
                super.onLoadResource(view, url);
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
               // view.loadUrl(url);
                super.onPageStarted(view, url, favicon);
            }

            public void onPageFinished(WebView view, String url) {
                Log.i("page_finished", "Finished loading URL: " + url);
                dismissDialog();

            }


        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.v("value_of_prog", String.valueOf(newProgress));
                //put your code here if your want to show the progress with progressbar
            }
        });
        webView.loadUrl(url);
    }

    void dismissDialog(){
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();
    }

    void showDialog(){
        customDialog = new CustomDialog(ViewWeb.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }

}