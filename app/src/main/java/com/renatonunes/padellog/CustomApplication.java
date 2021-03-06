package com.renatonunes.padellog;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

/**
 * Created by Renato on 26/07/2016.
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true); //para trabalhar offline tb

//        Picasso picasso = new Picasso.Builder(getApplicationContext())
//                .indicatorsEnabled(true)
//                .build();
//        Picasso.setSingletonInstance(picasso);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
