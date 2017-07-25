package com.renatonunes.padellog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
//import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.identity.intents.AddressConstants;
import com.google.android.gms.identity.intents.UserAddressRequest;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.renatonunes.padellog.domain.Player;
import com.renatonunes.padellog.domain.util.AlertUtils;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.LibraryClass;
import com.renatonunes.padellog.domain.util.PermissionUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends CommonActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN_GOOGLE = 7859;
    //private static final int REQUEST_CODE = 7860;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Player player;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private Resources resources;
    private Context context;

    @BindView(R.id.edt_email_login)
    AutoCompleteTextView email;

    @BindView(R.id.img_login)
    ImageView imgLogin;

    @BindView(R.id.edt_password_login)
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

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

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

        // FACEBOOK
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager
                .getInstance()
                .registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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

//        LoginManager
//                .getInstance()
//                .logInWithReadPermissions(
//                        this,
//                        Arrays.asList("public_profile", "email", "user_location")
//                );

        // GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                //.requestScopes(new Scope("https://www.googleapis.com/auth/user.addresses.read"))
                //.requestScopes(new Scope(Scopes.PROFILE))
                .build();

        //com.google.android.gms.identity.intents.Address.AddressOptions options =
        //        new com.google.android.gms.identity.intents.Address.AddressOptions(AddressConstants.Themes.THEME_LIGHT);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                //.addApi(com.google.android.gms.identity.intents.Address.API, options)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "entrou no onClick botao google");
                if (LibraryClass.isNetworkActive(context)){
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
                    openProgressBar();
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
                showSnackbar(btnLoginEmail, resources.getString(R.string.msg_google_login_error));
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

        Log.e("RNN2", accessToken.getToken());

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
                                showSnackbar(btnLoginEmail, resources.getString(R.string.msg_login_error));
                            }
                            closeProgressBar();
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

    private void createNewPlayerAndCallMainActivity(FirebaseUser userFirebase){
        //creates a new player
        initUser();

        if( player.getId() == null
                && isNameOk( player, userFirebase ) ){

            player.setId( userFirebase.getUid() );
            player.setNameIfNull( userFirebase.getDisplayName() );
            player.setEmailIfNull( userFirebase.getEmail() );
//                    player.setPhotoUrl( userFirebase.getPhotoUrl().toString() );

            Picasso.with(context).load(userFirebase.getPhotoUrl().toString()).into(imgLogin, new Callback() {
                @Override
                public void onSuccess() {
                    String img = ImageFactory.getBase64Image(((BitmapDrawable)imgLogin.getDrawable()).getBitmap());

                    player.setImageStr(img);

                    Bitmap bitmap = ImageFactory.imgStrToImage(img);

                    if (bitmap != null) { //when user cancel the action and click in save

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                        byte[] bytes = baos.toByteArray();

                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        // Create a storage reference from our app
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://padellog-b49b1.appspot.com");

                        String Id = "images/players/";

                        Id = Id.concat(player.getId()).concat(".jpg");

                        StorageReference playersRef = storageRef.child(Id);

                        UploadTask uploadTask = playersRef.putBytes(bytes);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                player.setPhotoUrl(downloadUrl.toString());
                                player.setImageStr(null);

                                player.saveDB(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        updatePlaceAndCallMainActivity();
                                    }
                                });
                            }
                        });
                    } else {
                        player.saveDB(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                updatePlaceAndCallMainActivity();
                            }
                        });
                    }
                }

                @Override
                public void onError() {

                }
            });
        }else{
            //player is already logged
//            MainActivity.start(context, player);
//            finish();
            //callMainActivity();
        }
    }

    private void callMainActivityAndFinish(){
        MainActivity.start(context, player);
        finish();
    }

    private void updatePlaceAndCallMainActivity(){

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken != null) {
            //mProgressDialog.setMessage(getResources().getString(R.string.msg_update_place));

            //mProgressDialog.show();

            //Log.e("RNN2", accessToken.getToken());

            new GraphRequest(
                    accessToken,
                    "/me?fields=location", //+ userProfile.getId(),
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            //Log.d("RNN2", response.getRawResponse());

                            try {
                                String rawJson = response.getJSONObject().getString("location");
                                //Log.d("RNN2", rawJson);
                                try {

                                    JSONObject obj = new JSONObject(rawJson);

                                    String city = obj.getString("name");

                                    //Log.d("RNN2", "cidade:" + city);

                                    if (city.equals("")){
                                        callMainActivityAndFinish();
                                    }else {
                                        getLatLng(city);
                                    }

                                } catch (Throwable t) {
                                    Log.e("RNN2", "Could not parse malformed JSON: \"" + rawJson + "\"");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                callMainActivityAndFinish();
                            }
                        }
                    }
            ).executeAsync();
        } else {
            callMainActivityAndFinish();
        }
    }


    //https://stackoverflow.com/questions/10008108/how-to-get-the-latitude-and-longitude-from-city-name-in-android
    private void getLatLng(String location){
        try {
            Geocoder gc = new Geocoder(this);
            List<android.location.Address> addresses = gc.getFromLocationName(location, 1); // get the found Address Objects

            //List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available

            if ((addresses == null) || (addresses.isEmpty())){
                callMainActivityAndFinish();
            } else {
                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {

                        //Log.d("RNN", new LatLng(a.getLatitude(), a.getLongitude()).toString());

                        player.setPlace(location);
                        player.setLat(a.getLatitude());
                        player.setLng(a.getLongitude());

                        //Log.d("RNN", mPlayer.getPlace() + "/" + mPlayer.getLat() + "/" + mPlayer.getLng());

                        player.updateDB(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                callMainActivityAndFinish();
                            }
                        });
                        //ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    } else {
                        callMainActivityAndFinish();
                    }
                }
            }
        } catch (IOException e) {
            callMainActivityAndFinish();
            // handle the exception
        }
    }

//    private void updatePlaceAndCallMainActivity(){
//        MainActivity.start(context, player);
//        finish();
//    }

    private void initPlayerAndCallMainActivity(final FirebaseUser userFirebase){
        final String playerId = userFirebase.getUid();

        FirebaseDatabase.getInstance().getReference().child("players").child( playerId ).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        player = dataSnapshot.getValue(Player.class);

                        if (player != null) {
                            player.setId(playerId);
                            callMainActivityAndFinish();
                        }else{
                            createNewPlayerAndCallMainActivity(userFirebase);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("RNN", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                if( userFirebase == null ){
                    return;
                }

                initPlayerAndCallMainActivity(userFirebase);

//                //creates a new player
//                if( player.getId() == null
//                        && isNameOk( player, userFirebase ) ){
//
//                    player.setId( userFirebase.getUid() );
//                    player.setNameIfNull( userFirebase.getDisplayName() );
//                    player.setEmailIfNull( userFirebase.getEmail() );
////                    player.setPhotoUrl( userFirebase.getPhotoUrl().toString() );
//
//                    Picasso.with(context).load(userFirebase.getPhotoUrl().toString()).into(imgLogin, new Callback() {
//                        @Override
//                        public void onSuccess() {
//                            String img = ImageFactory.getBase64Image(((BitmapDrawable)imgLogin.getDrawable()).getBitmap());
//
//                            player.setImageStr(img);
//                            player.saveDB();
//                            MainActivity.start(context, player);
//                            //callMainActivity();
//                        }
//
//                        @Override
//                        public void onError() {
//
//                        }
//                    });
//                }else{
//                    //player is already logged
//                    MainActivity.start(context, player);
//                    //callMainActivity();
//                }

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
                hideKeyboard();
                sendLoginData(view);
            }
        });

        hideKeyboard();
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
                            Arrays.asList("public_profile", "email", "user_location")
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
                            showSnackbar(btnLoginEmail, resources.getString(R.string.msg_login_error));
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

    //TODO: get place from facebook and google

}