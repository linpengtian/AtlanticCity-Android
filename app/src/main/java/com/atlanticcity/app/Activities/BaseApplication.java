package com.atlanticcity.app.Activities;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.androidnetworking.AndroidNetworking;
import com.google.firebase.FirebaseApp;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class BaseApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
        FirebaseApp.initializeApp(this);

       /* Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();

        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);*/
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }
}

