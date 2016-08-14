package com.renatonunes.padellog;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Renato on 13/08/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //xmpp - para o device poder postar no FCM

        Map<String, String> data = remoteMessage.getData();
        for (Map.Entry<String, String> entry : data.entrySet()){
            Log.e("TESTEMSG", "key: " + entry.getKey());
            Log.e("TESTEMSG", "value: " + entry.getValue());
        }
    }
}
