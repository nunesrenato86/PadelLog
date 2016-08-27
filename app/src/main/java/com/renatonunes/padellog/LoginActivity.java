package com.renatonunes.padellog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.renatonunes.padellog.domain.Player;
import com.renatonunes.padellog.domain.util.AlertUtils;
import com.renatonunes.padellog.domain.util.LibraryClass;
import com.renatonunes.padellog.domain.util.PermissionUtils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends CommonActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN_GOOGLE = 7859;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Player player;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private Resources resources;
    private Context context;

    @BindView(R.id.email)
    AutoCompleteTextView email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.email_sign_in_button)
    Button btnLoginEmail;

    @BindView(R.id.email_sign_in_google_button)
    Button btnLoginGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        String permissions[] = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.GET_ACCOUNTS,
            //Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_NETWORK_STATE
        };

        boolean ok = PermissionUtils.validate(this, 0, permissions);

        if (ok){
            Log.i("RNN", "Permissions OK");
        }

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        // FACEBOOK
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessFacebookLoginData( loginResult.getAccessToken() );
            }

            @Override
            public void onCancel() {
                closeProgressBar();
            }

            @Override
            public void onError(FacebookException error) {
                //FirebaseCrash.report( error );
                closeProgressBar();
                showSnackbar(btnLoginEmail, error.getMessage() );
            }
        });

        // GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "entrou no onClick botao google");
                if (LibraryClass.isNetworkActive(context)){
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
                }else{
                    showSnackbar(btnLoginGoogle, getResources().getString(R.string.msg_no_internet));
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();
        initViews();
        initUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == RC_SIGN_IN_GOOGLE ){

            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent( data );
            GoogleSignInAccount account = googleSignInResult.getSignInAccount();

            if( account == null ){
                showSnackbar(btnLoginEmail, "Google login falhou, tente novamente");
                return;
            }

            accessGoogleLoginData( account.getIdToken() );
        }
        else{
            callbackManager.onActivityResult( requestCode, resultCode, data );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyLogged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if( mAuthListener != null ){
            mAuth.removeAuthStateListener( mAuthListener );
        }
    }


    private void accessFacebookLoginData(AccessToken accessToken){
        accessLoginData(
                "facebook",
                (accessToken != null ? accessToken.getToken() : null)
        );
    }

    private void accessGoogleLoginData(String accessToken){
        accessLoginData(
                "google",
                accessToken
        );
    }

    private void accessLoginData( String provider, String... tokens ){
        if( tokens != null
                && tokens.length > 0
                && tokens[0] != null ){

            AuthCredential credential = FacebookAuthProvider.getCredential( tokens[0]);
            credential = provider.equalsIgnoreCase("google") ? GoogleAuthProvider.getCredential( tokens[0], null) : credential;

            player.saveProviderSP( LoginActivity.this, provider );
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if( !task.isSuccessful() ){
                                closeProgressBar();
                                showSnackbar(btnLoginEmail, "Login falhou");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            closeProgressBar();
                            //FirebaseCrash.report( e );
                        }
                    });
        }
        else{
            mAuth.signOut();
        }
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                if( userFirebase == null ){
                    return;
                }

                if( player.getId() == null
                        && isNameOk( player, userFirebase ) ){

                    player.setId( userFirebase.getUid() );
                    player.setNameIfNull( userFirebase.getDisplayName() );
                    player.setEmailIfNull( userFirebase.getEmail() );
                    player.setPhotoUrl( userFirebase.getPhotoUrl().toString() );
                    player.saveDB();
                }

                callMainActivity();
            }
        };
        return( callback );
    }

    private boolean isNameOk( Player player, FirebaseUser firebaseUser ){
        return(
                player.getName() != null
                        || firebaseUser.getDisplayName() != null
        );
    }

    protected void initViews(){
        resources = getResources();

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

        email.addTextChangedListener(textWatcher);

        password.addTextChangedListener(textWatcher);

        progressBar = (ProgressBar) findViewById(R.id.login_progress);

        btnLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLoginData(view);
            }
        });
    }

    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(email);
        }
    }

    protected void initUser(){
        player = new Player();
        player.setEmail( email.getText().toString() );
        player.setPassword( password.getText().toString() );
    }

    public void callSignUp(View view){
        Intent intent = new Intent( this, SignUpActivity.class );
        startActivity(intent);
    }

    public void callReset(View view){
        Intent intent = new Intent( this, ResetActivity.class );
        startActivity(intent);
    }

    public void sendLoginData(View view){
//        FirebaseCrash.log("LoginActivity:clickListener:button:sendLoginData()");
        if (validateFields()) {
            if (LibraryClass.isNetworkActive(context)) {
                openProgressBar();
                initUser();
                verifyLogin();
            }else{
                showSnackbar(btnLoginEmail, getResources().getString(R.string.msg_no_internet) );
            }
        }
    }

    public void sendLoginFacebookData( View view ){
//        FirebaseCrash.log("LoginActivity:clickListener:button:sendLoginFacebookData()");
        if (LibraryClass.isNetworkActive(context)) {
            LoginManager
                    .getInstance()
                    .logInWithReadPermissions(
                            this,
                            Arrays.asList("public_profile", "user_friends", "email")
                    );
        }else{
            showSnackbar(btnLoginEmail, getResources().getString(R.string.msg_no_internet) );
        }
    }

//    public void sendLoginGoogleData( View view ){
//
////        FirebaseCrash.log("LoginActivity:clickListener:button:sendLoginGoogleData()");
//
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
//    }



    private void callMainActivity(){
        Intent intent = new Intent( this, MainActivity.class );
        startActivity(intent);
        finish();
    }


    private void verifyLogged(){
        if( mAuth.getCurrentUser() != null ){
            callMainActivity();
        }
        else{
            mAuth.addAuthStateListener( mAuthListener );
        }
    }

    private void verifyLogin(){

//        FirebaseCrash.log("LoginActivity:verifyLogin()");
        player.saveProviderSP( LoginActivity.this, "" );
        mAuth.signInWithEmailAndPassword(
                player.getEmail(),
                player.getPassword()
        )
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if( !task.isSuccessful() ){
                            closeProgressBar();
                            showSnackbar(btnLoginEmail,"Login falhou");
                            return;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                closeProgressBar();
//                FirebaseCrash.report( e );
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        FirebaseCrash
//                .report(
//                        new Exception(
//                                connectionResult.getErrorCode()+": "+connectionResult.getErrorMessage()
//                        )
//                );
        showSnackbar(btnLoginEmail, connectionResult.getErrorMessage() );
    }

    private boolean validateFields() {
        String user = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        return (!isEmptyFields(user, pass) && hasSizeValid(user, pass));
    }

    private boolean isEmptyFields(String user, String pass) {
        if (TextUtils.isEmpty(user)) {
            email.requestFocus();
            email.setError(resources.getString(R.string.msg_field_required));
            return true;
        } else if (TextUtils.isEmpty(pass)) {
            password.requestFocus();
            password.setError(resources.getString(R.string.msg_field_required));
            return true;
        }
        return false;
    }

    private boolean hasSizeValid(String login, String pass) {

        if (!LibraryClass.isEmailValid(login)) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int result : grantResults){
            if (result == getPackageManager().PERMISSION_DENIED){
                AlertUtils.alert(this,
                        R.string.app_name,
                        R.string.msg_alert_permission,
                        R.string.msg_alert_OK,
                        new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                return;
            }
        }
        //OK can login
    }
}