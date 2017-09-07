package com.renatonunes.padellog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.renatonunes.padellog.domain.Player;
import com.renatonunes.padellog.domain.util.AlertUtils;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.LibraryClass;
import com.renatonunes.padellog.domain.util.PermissionUtils;
import com.renatonunes.padellog.domain.util.PhotoTaker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends CommonActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    Animation fabRotateClockwise;
    Animation fabRotateAntiClockwise;

    @BindView(R.id.edit_profile_layout)
    LinearLayout linearLayout;

    @BindView(R.id.spinner_profile_category)
    Spinner spinnerCategory;

    @BindView(R.id.fab_profile_photo_add)
    FloatingActionButton fabProfilePhotoAdd;

    @BindView(R.id.fab_profile_photo_camera)
    FloatingActionButton fabProfilePhotoCamera;

    @BindView(R.id.fab_profile_photo_gallery)
    FloatingActionButton fabProfilePhotoGallery;

    @BindView(R.id.fab_profile_photo_delete)
    FloatingActionButton fabProfilePhotoDelete;

    @BindView(R.id.lbl_profile_display_name)
    TextView lblProfileDisplayName;

    @BindView(R.id.lbl_profile_email)
    TextView lblProfileEmail;

    @BindView(R.id.lbl_all_championships)
    TextView lblAllChampionshipsCount;

    @BindView(R.id.lbl_champions)
    TextView lblChampionCount;

    @BindView(R.id.lbl_vices)
    TextView lblVicesCount;

    @BindView(R.id.edt_profile_place)
    EditText edtProfilePlace;

    @BindView(R.id.switch_profile_public)
    Switch switchProfilePublic;

    private boolean isVisible = false;
    private boolean hasPhoto = false;
    private static Player currentPlayer = null;
    private boolean playerImageHasChanged = false;
    private static Boolean mIsReadOnly = false;

    private boolean mPhotoWasDeleted = false;

    private final Activity mActivity = this;
    private String mCurrentPlayerImageStr = "";

    private ArrayAdapter<String> dataAdapter;
    private Resources resources;

    static final int REQUEST_PLACE_PICKER = 203;

    //to handle place
    private LatLng mCurrentLatLng;

    //to handle images
    @BindView(R.id.img_edit_profile)
    ImageView imgProfile;

    private Uri downloadUrl;

    private Uri mCurrentPhotoUri;
    private PhotoTaker mPhotoTaker;

    //google places api
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        resources = getResources();

        mPhotoTaker = new PhotoTaker(this);

        initSpinner();

        fabRotateClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabRotateAntiClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

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

        updateUi();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permissions[] = new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA,
            };

            boolean ok = PermissionUtils.validate(this, 0, permissions);

            if (ok) {
                Log.i("RNN", "Permissions OK");
            }
        }

        //google places api
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        edtProfilePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Construct an intent for the place picker
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(mActivity);
                    // Start the intent by requesting a result,
                    // identified by a request code.
                    startActivityForResult(intent, REQUEST_PLACE_PICKER);

                } catch (GooglePlayServicesRepairableException e) {
                    // ...
                } catch (GooglePlayServicesNotAvailableException e) {
                    // ...
                }
            }
        });

        //convertPhoto();
    }

    private void convertPhoto(){
        if ((currentPlayer.isImgStrValid()) && (!currentPlayer.isImgFirebase())){
            Bitmap bitmap = ImageFactory.imgStrToImage(currentPlayer.getImageStr());

            if (bitmap != null) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);

                byte[] bytes = baos.toByteArray();

                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageRef = storage.getReferenceFromUrl("gs://padellog-b49b1.appspot.com");

                String Id = "images/players/";

                Id = Id.concat(currentPlayer.getId()).concat(".jpg");

                StorageReference playersRef = storageRef.child(Id);

                final ProgressDialog progressDialog = new ProgressDialog(this);
                //progressDialog.setTitle(getResources().getString(R.string.photo_processing));
                progressDialog.show();

                UploadTask uploadTask = playersRef.putBytes(bytes);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        currentPlayer.setPhotoUrl(downloadUrl.toString());
                        currentPlayer.setImageStr(null);

                        currentPlayer.updateDB();

                        showSnackbar(linearLayout,
                                getResources().getString(R.string.msg_championship_converted)
                        );
                    }
                });

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        Log.e("RNN", ((int) progress + "% " + getResources().getString(R.string.photo_complete)));

                        progressDialog.setMessage(getResources().getString(R.string.msg_converting));
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                    }
                });

            }
        }
    }

    private void updateUi(){
        if (mIsReadOnly){
            fabProfilePhotoDelete.setVisibility(View.GONE);
            fabProfilePhotoGallery.setVisibility(View.GONE);
            fabProfilePhotoCamera.setVisibility(View.GONE);
            fabProfilePhotoAdd.setVisibility(View.GONE);

            spinnerCategory.setEnabled(false);
            switchProfilePublic.setVisibility(View.GONE);
            edtProfilePlace.setEnabled(false);
        } else {
            fabProfilePhotoDelete.animate().scaleY(0).scaleX(0).setDuration(0).start();
            fabProfilePhotoGallery.animate().scaleY(0).scaleX(0).setDuration(0).start();
            fabProfilePhotoCamera.animate().scaleY(0).scaleX(0).setDuration(0).start();

        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if ((user != null) && (currentPlayer != null)) {
            if (currentPlayer.getName() != ""){
                lblProfileDisplayName.setText(currentPlayer.getName());
            }else {
                lblProfileDisplayName.setText(user.getDisplayName());
            }

            if (!mIsReadOnly) { //only show logged suer email
                lblProfileEmail.setText(user.getEmail());
            }else{
                lblProfileEmail.setVisibility(View.GONE);
            }

            spinnerCategory.setSelection(currentPlayer.getCategory());
            edtProfilePlace.setText(currentPlayer.getPlace());
            switchProfilePublic.setChecked(currentPlayer.getIsPublic());

            lblChampionCount.setText( String.valueOf(currentPlayer.getTotalFirstPlace()));
            lblVicesCount.setText( String.valueOf(currentPlayer.getTotalSecondPlace()));
            lblAllChampionshipsCount.setText( String.valueOf(currentPlayer.getTotalChampionship()));

            if (currentPlayer.getPhotoUriDownloaded() != null) {
                Picasso.with(getApplicationContext()).load(currentPlayer.getPhotoUriDownloaded().toString()).into(imgProfile);
                hasPhoto = true;
            } else if (currentPlayer.isImgFirebase()) {
                hasPhoto = true;
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference httpsReference = storage.getReferenceFromUrl(currentPlayer.getPhotoUrl());

                httpsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getApplicationContext()).load(uri.toString()).into(imgProfile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            }else if (currentPlayer.isImgStrValid()){
                mCurrentPlayerImageStr = currentPlayer.getImageStr();
                imgProfile.setImageBitmap(ImageFactory.imgStrToImage(mCurrentPlayerImageStr));
                hasPhoto = true;
            }else {
                //dont take anymore the user photo
                //if (user.getPhotoUrl() != null) {
                //    Picasso.with(this).load(user.getPhotoUrl()).into(imgProfile);
                //    hasPhoto = true;
                //} else {
                    imgProfile.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
                    hasPhoto = false;
                //}
            }
        }
    }

    private void initSpinner(){
        // Spinner element
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add(resources.getString(R.string.category_pro));
        categories.add(resources.getString(R.string.category_open));
        categories.add(resources.getString(R.string.category_2nd));
        categories.add(resources.getString(R.string.category_3th));
        categories.add(resources.getString(R.string.category_4th));
        categories.add(resources.getString(R.string.category_5th));
        categories.add(resources.getString(R.string.category_6th));
        categories.add(resources.getString(R.string.category_7th));

        categories.add(resources.getString(R.string.category_3035a));
        categories.add(resources.getString(R.string.category_3035b));
        categories.add(resources.getString(R.string.category_3035c));

        categories.add(resources.getString(R.string.category_4045a));
        categories.add(resources.getString(R.string.category_4045b));
        categories.add(resources.getString(R.string.category_4045c));

        categories.add(resources.getString(R.string.category_5055a));
        categories.add(resources.getString(R.string.category_5055b));
        categories.add(resources.getString(R.string.category_5055c));

        categories.add(resources.getString(R.string.category_mixedA));
        categories.add(resources.getString(R.string.category_mixedB));
        categories.add(resources.getString(R.string.category_mixedC));
        categories.add(resources.getString(R.string.category_mixedD));

        categories.add(resources.getString(R.string.category_sub12));
        categories.add(resources.getString(R.string.category_sub14));
        categories.add(resources.getString(R.string.category_sub16));
        categories.add(resources.getString(R.string.category_sub18));
        categories.add(resources.getString(R.string.category_sub20));

        categories.add(resources.getString(R.string.category_other));

        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);

        // attaching data adapter to spinner
        spinnerCategory.setAdapter(dataAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("RNN", "connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("RNN", "supended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("RNN", "failed");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int result : grantResults) {
            if (result == getPackageManager().PERMISSION_DENIED) {
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
        //OK can use storage and camera
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (!mIsReadOnly) {
            getMenuInflater().inflate(R.menu.menu_add_activity, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
            if (validateFields()) {
                if (canBePublic()) {
                    savePlayer();
                }else{
                    showSnackbar(linearLayout, resources.getString(R.string.msg_profile_cant_be_public));
                }
            }

            return true;
        }else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PhotoTaker.REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
            } else {
                showSnackbar(linearLayout,
                        getResources().getString(R.string.msg_photo_erros));
            }
        }else if (requestCode == PhotoTaker.REQUEST_PICK_PHOTO){
            if (resultCode == RESULT_OK) {
                previewPickedImage(data);
            } else if (resultCode == RESULT_CANCELED) {
            } else {
                showSnackbar(linearLayout,
                        getResources().getString(R.string.msg_photo_erros));
            }
        }else if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            mCurrentLatLng = place.getLatLng();
            MainActivity.playerPlaceHasChanged = true; //to refresh player
            String attributions = PlacePicker.getAttributions(data);

            if (attributions == null) {
                attributions = "";
            }

            edtProfilePlace.setText(address + " " + Html.fromHtml(attributions));
            edtProfilePlace.setError(null);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.fab_profile_photo_delete)
    public void deletePhoto(){
        toggleFabs();

        hasPhoto = false;
        playerImageHasChanged = true;
        imgProfile.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (user.getPhotoUrl() != null) { //if facebook or google user (not player) have photo, use it
//
//            String url = user.getPhotoUrl().toString();
//
//            Picasso.with(this).load(url).into(imgProfile, new Callback() {
//                @Override
//                public void onSuccess() {
//                    mCurrentPlayerImageStr = ImageFactory.getBase64Image(((BitmapDrawable) imgProfile.getDrawable()).getBitmap());
//                    hasPhoto = true;
//                    playerImageHasChanged = true; //to reload the photo on mainactivity
//                }
//
//                @Override
//                public void onError() {
//
//                }
//            });
//        }else{
//            hasPhoto = false;
//            imgProfile.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
//            //Picasso.with(this).load(R.drawable.com_facebook_profile_picture_blank_square).into(imgProfile);
//        }
    }

    @OnClick(R.id.fab_profile_photo_camera)
    public void takePhoto(){
        if (isVisible){
            File placeholderFile = ImageFactory.newFile();
            mCurrentPhotoUri = Uri.fromFile(placeholderFile);

            if (!mPhotoTaker.takePhoto(placeholderFile, this)) {
                displayPhotoError();
            }
        };

        toggleFabs();
    }

    @OnClick(R.id.fab_profile_photo_gallery)
    public void pickPhoto(){
        toggleFabs();

        File placeholderFile = ImageFactory.newFile();

        mCurrentPhotoUri = Uri.fromFile(placeholderFile);

        if (!mPhotoTaker.pickPhoto(placeholderFile)) {
            displayPhotoError();
        }
    }

    private void previewCapturedImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // downsizing image as it throws OutOfMemory Exception for larger images
        options.inSampleSize = 7;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoUri.getPath(), options);
        imgProfile.setImageBitmap(bitmap);
        hasPhoto = true;
        playerImageHasChanged = true;
    }

    private void previewPickedImage(Intent data){
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        mCurrentPhotoUri = Uri.parse(picturePath);

        BitmapFactory.Options options = new BitmapFactory.Options();

        if (ImageFactory.imgIsLarge(mCurrentPhotoUri)) {
            options.inSampleSize = 7;
        }

        imgProfile.setImageBitmap(BitmapFactory.decodeFile(picturePath, options));
        hasPhoto = true;
        playerImageHasChanged = true;
    }

    private void displayPhotoError() {
        showSnackbar(linearLayout,
                resources.getString(R.string.msg_error_img_file));
    }

    public static void start(Context c, Player player, Boolean isReadOnly) {
        currentPlayer = player;
        mIsReadOnly = isReadOnly;

        c.startActivity(new Intent(c, EditProfileActivity.class));
    }

    private void savePlayer(){
        if (LibraryClass.isNetworkActive(this)) {
            uploadPhotoAndSaveToDB();

        }else{
            showSnackbar(linearLayout, getResources().getString(R.string.msg_no_internet) );
        }
    }

    /*
    private void savePlayer(){
        if (LibraryClass.isNetworkActive(this)) {
            currentPlayer.setImageStr(this.getImageStr());

            MainActivity.playerImageHasChanged = this.playerImageHasChanged;

            currentPlayer.setCategory(spinnerCategory.getSelectedItemPosition());
            currentPlayer.setPlace(edtProfilePlace.getText().toString());

            currentPlayer.setIsPublic(switchProfilePublic.isChecked());

            if (mCurrentLatLng != null) {
                currentPlayer.setLat(mCurrentLatLng.latitude);
                currentPlayer.setLng(mCurrentLatLng.longitude);
            }

            currentPlayer.updateDB();
            MainActivity.mPlayer = currentPlayer;

            showSnackbar(linearLayout, resources.getString(R.string.msg_profile_updated));
        }else{
            showSnackbar(linearLayout, getResources().getString(R.string.msg_no_internet) );
        }
    }
    */

    public void uploadPhotoAndSaveToDB(){

        //currentPlayer.setImageStr(base64Image);
        currentPlayer.setCategory(spinnerCategory.getSelectedItemPosition());
        currentPlayer.setPlace(edtProfilePlace.getText().toString());

        currentPlayer.setIsPublic(switchProfilePublic.isChecked());

        if (mCurrentLatLng != null) {
            currentPlayer.setLat(mCurrentLatLng.latitude);
            currentPlayer.setLng(mCurrentLatLng.longitude);
        }

        if (mCurrentPhotoUri != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            if (ImageFactory.imgIsLarge(mCurrentPhotoUri)){
                options.inSampleSize = 7; // shrink it down otherwise we will use stupid amounts of memory
            }

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoUri.getPath(), options);

            if (bitmap != null) { //when user cancel the action and click in save

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);

                byte[] bytes = baos.toByteArray();

                FirebaseStorage storage = FirebaseStorage.getInstance();

                // Create a storage reference from our app
                StorageReference storageRef = storage.getReferenceFromUrl("gs://padellog-b49b1.appspot.com");

                String Id = "images/players/";

                Id = Id.concat(currentPlayer.getId()).concat(".jpg");

                StorageReference playersRef = storageRef.child(Id);

                final ProgressDialog progressDialog = new ProgressDialog(this);
                //progressDialog.setTitle(getResources().getString(R.string.photo_processing));
                progressDialog.show();

                UploadTask uploadTask = playersRef.putBytes(bytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        downloadUrl = taskSnapshot.getDownloadUrl();

                        MainActivity.playerImageHasChanged = playerImageHasChanged;
                        MainActivity.mPlayer.setPhotoUriDownloaded(downloadUrl);

                        currentPlayer.setPhotoUrl(downloadUrl.toString());
                        currentPlayer.setImageStr(null);

                        currentPlayer.updateDB();
                        MainActivity.mPlayer = currentPlayer;

                        showSnackbar(linearLayout, resources.getString(R.string.msg_profile_updated));
                    }
                });

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //progressDialog.setMessage((int)progress + "% " + getResources().getString(R.string.photo_complete));

                        progressDialog.setMessage(getResources().getString(R.string.msg_saving));
                        //progressDialog.setProgress((int)progress);

                        //System.out.println("Upload is " + progress + "% done");
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        //System.out.println("Upload is paused");
                    }
                });
            }else{ //pick or take photo but cancel the action and keep the old one
                savePlayerWithoutPhoto();
            }
        }else { //deleted the photo
            savePlayerWithoutPhoto();
        }
    }

    private void savePlayerWithoutPhoto(){
        if (!hasPhoto) {
            currentPlayer.setPhotoUrl(null);
        }

        currentPlayer.updateDB();
        MainActivity.mPlayer = currentPlayer;
        MainActivity.playerImageHasChanged = playerImageHasChanged;

        showSnackbar(linearLayout, resources.getString(R.string.msg_profile_updated));
    }

    /*
    public String getImageStr(){
        if (mCurrentPhotoUri != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            if (ImageFactory.imgIsLarge(mCurrentPhotoUri)){
                options.inSampleSize = 7; // shrink it down otherwise we will use stupid amounts of memory
            }

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoUri.getPath(), options);

            if (bitmap != null) { //when user cancel the action and click in save

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                byte[] bytes = baos.toByteArray();

                String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

                return base64Image;
            }else{
                return mCurrentPlayerImageStr;
            }
        }else
            return mCurrentPlayerImageStr;
    }

    */


    @OnClick(R.id.fab_profile_photo_add)
    public void toggleFabs(){
        if (isVisible){
            fabProfilePhotoDelete.animate().scaleY(0).scaleX(0).setDuration(200).start();
            fabProfilePhotoGallery.animate().scaleY(0).scaleX(0).setDuration(200).start();
            fabProfilePhotoCamera.animate().scaleY(0).scaleX(0).setDuration(200).start();

            fabProfilePhotoAdd.startAnimation(fabRotateAntiClockwise);
        }else{
            fabProfilePhotoDelete.animate().scaleY(1).scaleX(1).setDuration(200).start();
            fabProfilePhotoGallery.animate().scaleY(1).scaleX(1).setDuration(200).start();
            fabProfilePhotoCamera.animate().scaleY(1).scaleX(1).setDuration(200).start();

            fabProfilePhotoAdd.startAnimation(fabRotateClockwise);
        }
        isVisible = !isVisible;
    }

    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(edtProfilePlace);
        }
    }

    private boolean validateFields() {
        String place = edtProfilePlace.getText().toString().trim();

        return (!isEmptyFields(place));
//                && hasSizeValid(set1Score1, set1Score2));
    }

    private boolean isEmptyFields(String place) {
        if (TextUtils.isEmpty(place)) {
            edtProfilePlace.requestFocus();
            edtProfilePlace.setError(resources.getString(R.string.msg_field_required));
            return true;
        }

        return false;
    }

    private void clearErrorFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setError(null);
        }
    }

    private boolean canBePublic(){
        if (switchProfilePublic.isChecked()){
            return (hasPhoto) && (!edtProfilePlace.getText().equals(""));
        }else{
            return true;
        }
    }

}
