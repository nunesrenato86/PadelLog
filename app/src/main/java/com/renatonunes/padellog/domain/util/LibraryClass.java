package com.renatonunes.padellog.domain.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Renato on 26/07/2016.
 */
public class LibraryClass {
    public static String PREF = "com.renatonunes.padellog.PREF";
    private static Firebase firebase;
    private static FirebaseAuth firebaseAuth;
    private static FirebaseDatabase firebaseDatabase;

    public static FirebaseDatabase getFirebaseDatabase(){
        if( firebaseDatabase == null ){
            firebaseDatabase = FirebaseDatabase.getInstance();
        }
        return( firebaseDatabase );
    }

    public static Firebase getFirebase(){
        if( firebase == null ){
            firebase = new Firebase("https://padellog-b49b1.firebaseio.com");
        }
        return( firebase );
    }

    public static FirebaseAuth getFirebaseAuth(){
        if( firebaseAuth == null ){
            firebaseAuth = FirebaseAuth.getInstance(); //new Firebase("https://padellog-b49b1.firebaseio.com");
        }
        return( firebaseAuth );
    }

    static public void saveSP(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    static public String getSP(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String token = sp.getString(key, "");
        return( token );
    }
}
