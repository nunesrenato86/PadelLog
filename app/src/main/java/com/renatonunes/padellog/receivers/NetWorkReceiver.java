package com.renatonunes.padellog.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.renatonunes.padellog.R;
import com.renatonunes.padellog.domain.util.LibraryClass;

/**
 * Created by Renato on 27/08/2016.
 */

public class NetworkReceiver extends BroadcastReceiver {

    private Context mContext;
    private AlertDialog.Builder builder;
    private AlertDialog alert;

    public NetworkReceiver(Context context) {
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //AlertDialog alert = alertNoNetwork();

        if (alert == null){
            alert = alertNoNetwork();
        }

        if (LibraryClass.isNetworkActive(context)) {
            //if (alert != null && alert.isShowing()) {
            if (alert != null) {
                alert.dismiss();
            }
        } else {
            if (alert != null && !alert.isShowing()) {
                alert.show();
            }
        }

    }

    public AlertDialog alertNoNetwork() {
        builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.msg_alert_no_internet)
                .setMessage(R.string.msg_verify_no_internet)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
        return builder.create();
    }
}
