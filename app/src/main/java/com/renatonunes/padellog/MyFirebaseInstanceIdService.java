package com.renatonunes.padellog;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Renato on 13/08/2016. --serve pra mandar msg pro google e receber o token
 */
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() { //teria que mandar esse token pro meu server
        //super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("TESTEMSG", "token no service: " + token);

        // TODO: Implement this method to send any registration to your app's servers.
//        sendRegistrationToServer(refreshedToken);
    }
}
