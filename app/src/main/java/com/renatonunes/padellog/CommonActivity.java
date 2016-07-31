package com.renatonunes.padellog;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by Renato on 26/07/2016.
 */
abstract public class CommonActivity extends AppCompatActivity {

    protected AutoCompleteTextView email;
    protected EditText password;
    protected ProgressBar progressBar;

    protected void showSnackbar(String message ){
        Snackbar.make(progressBar,
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

    abstract protected void initViews();

//    abstract protected void initPlayer(String email,
//                                       String password,
//                                       String photoUrl,
//                                       String displayName);
    abstract protected void initUser();
}
