package com.renatonunes.padellog;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.renatonunes.padellog.domain.Player;
import com.renatonunes.padellog.domain.util.LibraryClass;

public class SignUpActivity extends CommonActivity  {

    private final String TAG = "RNN";
    private Firebase firebase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Player player;
    private AutoCompleteTextView name;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // Player is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + firebaseUser.getUid());

                    if (player != null){
                        player.setId(firebaseUser.getUid());
                        player.saveDB();
                        //firebaseAuth.unauth();
                        showToast("Conta criada com sucesso!");
                        closeProgressBar();
                        finish();//irá voltar para a activity de login
                    }
                } else {
                    // Player is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        initViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void initViews() {
        resources = getResources();

        name = (AutoCompleteTextView) findViewById(R.id.name);
        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.sign_up_progress);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                callClearErrors(s);
            }
        };

        name.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
    }

    @Override
    protected void initUser(){
        player = new Player();
        player.setName( name.getText().toString() );
        player.setEmail( email.getText().toString() );
        player.setPassword( password.getText().toString() );

    }
//    protected void initPlayer(String email,
//                              String password,
//                              String photoUrl,
//                              String displayName) {
//        player = new Player();
//        player.setName( displayName );
//        player.setEmail( email );
//        player.setPassword( password );
//        player.setPhotoUrl( photoUrl );
//    }

    public void sendSignUpData(View view){
        if (validateFields()) {
            openProgressBar();

            initUser();

//        initPlayer(email.getText().toString(),
//                password.getText().toString(),
//                "",
//                name.getText().toString());

            saveUser();
        }
    }

    private void saveUser(){
        mAuth.createUserWithEmailAndPassword(player.getEmail(), player.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the player. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in player can be handled in the listener.
                        if (!task.isSuccessful()) {
                            showToast("Erro criando conta: " + task.getException().getMessage());
                            closeProgressBar();
                        }
                    }
                });
    }

    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(name);
        }
    }

    private boolean validateFields() {
        String login = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String displayName = name.getText().toString().trim();
        return (!isEmptyFields(login, pass, displayName) && hasSizeValid(login, pass, displayName));
    }

    private boolean isEmptyFields(String login, String pass, String displayName) {
        if (TextUtils.isEmpty(displayName)) {
            name.requestFocus();
            name.setError(resources.getString(R.string.login_display_name_required));
            return true;
        } else if (TextUtils.isEmpty(login)) {
            email.requestFocus();
            email.setError(resources.getString(R.string.login_user_required));
            return true;
        } else if (TextUtils.isEmpty(pass)) {
            password.requestFocus();
            password.setError(resources.getString(R.string.login_password_required));
            return true;
        }
        return false;
    }

    private boolean hasSizeValid(String login, String pass, String displayName) {

        if (!(displayName.length() > 5)) {
            name.requestFocus();
            name.setError(resources.getString(R.string.login_display_name_size_invalid));
            return false;
        } else if (!(LibraryClass.isEmailValid(login))) {
            email.requestFocus();
            email.setError(resources.getString(R.string.login_invalid));
            return false;
        } else if (!(pass.length() > 5)) {
            password.requestFocus();
            password.setError(resources.getString(R.string.login_pass_size_invalid));
            return false;
        }
        return true;
    }

    private void clearErrorFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setError(null);
        }
    }
}
