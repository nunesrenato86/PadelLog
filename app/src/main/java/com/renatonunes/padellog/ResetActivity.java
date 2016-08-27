package com.renatonunes.padellog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.renatonunes.padellog.domain.util.LibraryClass;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetActivity extends CommonActivity {

    private Toolbar toolbar;
    private AutoCompleteTextView email;
    private FirebaseAuth firebaseAuth;

    @BindView(R.id.btn_reset)
    Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        toolbar.setTitle( getResources().getString(R.string.reset) );
        email = (AutoCompleteTextView) findViewById(R.id.email);
    }

    public void reset( View view ){
        if (LibraryClass.isNetworkActive(this)) {
            firebaseAuth
                    .sendPasswordResetEmail( email.getText().toString() )
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if( task.isSuccessful() ){
                                email.setText("");
                                Toast.makeText(
                                        ResetActivity.this,
                                        "Recuperação de acesso iniciada. E-mail enviado.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                            else{
                                Toast.makeText(
                                        ResetActivity.this,
                                        "Falhou! Tente novamente",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //FirebaseCrash.report( e );
                        }
                    });
        }else{
            showSnackbar(btnReset , getResources().getString(R.string.msg_no_internet) );
        }
    }
}
