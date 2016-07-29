package com.renatonunes.padellog;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Renato on 26/07/2016.
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
