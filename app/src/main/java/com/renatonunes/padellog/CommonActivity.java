package com.renatonunes.padellog;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.renatonunes.padellog.receivers.NetworkChangeReceiver;

/**
 * Created by Renato on 26/07/2016.
 */
abstract public class CommonActivity extends AppCompatActivity {

//    protected AutoCompleteTextView email;
//    protected EditText password;
    protected ProgressBar progressBar;
    private NetworkChangeReceiver mNetworkChangeReceiver;

    protected void showSnackbar(View v, String message ){
        Snackbar.make(v,
                message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void showToast( String message ){
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    protected void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );
    }

    protected void closeProgressBar(){
        progressBar.setVisibility( View.GONE );
    }

//    abstract protected void initViews();

//    abstract protected void initPlayer(String email,
//                                       String password,
//                                       String photoUrl,
//                                       String displayName);
//    abstract protected void initUser();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetworkChangeReceiver = new NetworkChangeReceiver(this);
    }

    @Override
    protected void onResume() {
        registerReceiver(mNetworkChangeReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();
    }

    protected void hideKeyboard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            if (email != null) {
//                imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
//            }
//
//            if (password != null) {
//                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mNetworkChangeReceiver);
    }
}
