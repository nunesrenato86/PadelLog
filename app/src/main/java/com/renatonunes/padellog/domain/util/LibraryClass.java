package com.renatonunes.padellog.domain.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Renato on 26/07/2016.
 */
public class LibraryClass {
    public static String PREF = "com.renatonunes.padellog.PREF";

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
