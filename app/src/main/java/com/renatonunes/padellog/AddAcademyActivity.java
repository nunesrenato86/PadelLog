package com.renatonunes.padellog;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.renatonunes.padellog.domain.Academy;
import com.renatonunes.padellog.domain.util.AlertUtils;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.LibraryClass;
import com.renatonunes.padellog.domain.util.PermissionUtils;
import com.renatonunes.padellog.domain.util.PhotoTaker;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddAcademyActivity extends CommonActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{


    //fields
    @BindView(R.id.edt_add_academy_name)
    EditText edtName;

    @BindView(R.id.edt_add_academy_phone)
    EditText edtPhone;

    @BindView(R.id.edt_add_academy_place)
    EditText edtPlace;

    @BindView(R.id.edt_add_academy_email)
    EditText edtEmail;

    @BindView(R.id.switch_academy_verified)
    Switch switchVerified;

    static final int REQUEST_PLACE_PICKER = 103;

    @BindView(R.id.fab_academy_photo_camera)
    FloatingActionButton fabMenuAcademyPhoto;

    @BindView(R.id.fab_academy_photo_gallery)
    FloatingActionButton fabMenuAcademyPhotoGallery;

    @BindView(R.id.fab_academy_photo_delete)
    FloatingActionButton fabMenuAcademyPhotoDelete;

    @BindView(R.id.fab_academy_photo_add)
    FloatingActionButton fabMenuAcademyPhotoAdd;

    private Boolean isVisible = false;

    private final Activity mActivity = this;
    private static Academy currentAcademy = null;
    private String mCurrentAcademyImageStr = "";
    private static Boolean mIsModeReadOnly = true;

    private boolean hasPhoto = false;

    //to handle place
    private LatLng mCurrentLatLng;

    //to handle images
    @BindView(R.id.img_academy)
    ImageView mThumbnailPreview;

    private Uri mCurrentPhotoUri;
    private PhotoTaker mPhotoTaker;

    private Uri downloadUrl;

    //google places api
    private GoogleApiClient mGoogleApiClient;

    private Resources resources;
//    private Boolean isFabOnScreen = false;

    Animation fabRotateClockwise;
    Animation fabRotateAntiClockwise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_academy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permissions[] = new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA,
            };

            Boolean ok = PermissionUtils.validate(this, 0, permissions);

            if (ok) {
                Log.i("RNN", "Permissions OK");
            }
        }

        resources = getResources();

        mPhotoTaker = new PhotoTaker(this);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        //google places api
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        edtPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Construct an intent for the place picker
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(mActivity);

                    //intent.putExtra("primary_color", getResources().getColor(R.color.colorPrimary));
                    //intent.putExtra("primary_color_dark", getResources().getColor(R.color.colorPrimaryDark));

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

        fabRotateClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabRotateAntiClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        updateUI();

    }

    @OnClick(R.id.fab_academy_photo_add)
    public void toggleFabs(){
        if (isVisible){
            fabMenuAcademyPhotoDelete.animate().scaleY(0).scaleX(0).setDuration(200).start();
            fabMenuAcademyPhotoGallery.animate().scaleY(0).scaleX(0).setDuration(200).start();
            fabMenuAcademyPhoto.animate().scaleY(0).scaleX(0).setDuration(200).start();

            fabMenuAcademyPhotoAdd.startAnimation(fabRotateAntiClockwise);
            //fabMenuChampionshipPhotoAdd.animate().rotationY(25).setDuration(200).start();
        }else{
            fabMenuAcademyPhotoDelete.animate().scaleY(1).scaleX(1).setDuration(200).start();
            fabMenuAcademyPhotoGallery.animate().scaleY(1).scaleX(1).setDuration(200).start();
            fabMenuAcademyPhoto.animate().scaleY(1).scaleX(1).setDuration(200).start();

            fabMenuAcademyPhotoAdd.startAnimation(fabRotateClockwise);

            //fabMenuChampionshipPhotoAdd.animate().rotationY(0).setDuration(200).start();
        }

        isVisible = !isVisible;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentAcademy = null;
    }

    private void updateUI(){
        if (mIsModeReadOnly){
            switchVerified.setVisibility(View.GONE);
        }else{
            switchVerified.setVisibility(View.VISIBLE);
        }


        fabMenuAcademyPhotoDelete.animate().scaleY(0).scaleX(0).setDuration(0).start();
        fabMenuAcademyPhotoGallery.animate().scaleY(0).scaleX(0).setDuration(0).start();
        fabMenuAcademyPhoto.animate().scaleY(0).scaleX(0).setDuration(0).start();

        if (currentAcademy != null){

            if (currentAcademy.getPhotoUriDownloaded() != null) {
                Picasso.with(getApplicationContext()).load(currentAcademy.getPhotoUriDownloaded().toString()).into(mThumbnailPreview);
                hasPhoto = true;
            }else if (currentAcademy.isImgFirebase()) {
                hasPhoto = true;
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference httpsReference = storage.getReferenceFromUrl(currentAcademy.getPhotoUrl());

                httpsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getApplicationContext()).load(uri.toString()).into(mThumbnailPreview);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            }else {
                deletePhoto();
            }

            edtName.setText(currentAcademy.getName());
            edtPhone.setText(currentAcademy.getPhone());
            edtPlace.setText(currentAcademy.getPlace());
            edtEmail.setText(currentAcademy.getEmail());
            switchVerified.setChecked(currentAcademy.getVerified());
        }
    }

    @OnClick(R.id.fab_academy_photo_delete)
    public void deletePhoto(){
        toggleFabs();

        hasPhoto = false;

        mCurrentAcademyImageStr = "";

        mThumbnailPreview.setImageBitmap(null);
        mThumbnailPreview.setBackgroundResource(R.drawable.no_photo);
    }

    @OnClick(R.id.fab_academy_photo_camera)
    public void takePhoto(View view){
        if (isVisible){
            File placeholderFile = ImageFactory.newFile();

            mCurrentPhotoUri = Uri.fromFile(placeholderFile);

            if (!mPhotoTaker.takePhoto(placeholderFile, this)) {
                displayPhotoError();
            }
        };

        toggleFabs();
    }

    @OnClick(R.id.fab_academy_photo_gallery)
    public void pickPhoto(View view){
        toggleFabs();

        File placeholderFile = ImageFactory.newFile();

        mCurrentPhotoUri = Uri.fromFile(placeholderFile);

        if (!mPhotoTaker.pickPhoto(placeholderFile)) {
            displayPhotoError();
        }
    }

    private void previewCapturedImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // downsizing image as it throws OutOfMemory Exception for larger
        // images

        //options.inSampleSize = 8;

        String picturePath = mCurrentPhotoUri.getPath();

        if (ImageFactory.imgIsLarge(mCurrentPhotoUri)) {
            //options.inSampleSize = 8;

            ImageFactory.mContext = this;
            picturePath = ImageFactory.compressImage(picturePath);
            mCurrentPhotoUri = Uri.parse(picturePath);
        }

        Bitmap bitmap = BitmapFactory.decodeFile(picturePath, options);
        mThumbnailPreview.setImageBitmap(bitmap);
        hasPhoto = true;
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
            //options.inSampleSize = 8;

            ImageFactory.mContext = this;
            picturePath = ImageFactory.compressImage(picturePath);
            mCurrentPhotoUri = Uri.parse(picturePath);
        }

        mThumbnailPreview.setImageBitmap(BitmapFactory.decodeFile(picturePath, options));
        hasPhoto = true;
    }

    private void displayPhotoError() {
        Snackbar.make(fabMenuAcademyPhoto,
                resources.getString(R.string.msg_error_img_file),
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PhotoTaker.REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                previewCapturedImage();
                //storeImageToFirebase();
            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(getApplicationContext(),
//                        "User cancelled image capture", Toast.LENGTH_SHORT)
//                        .show();
            } else {
                showSnackbar(fabMenuAcademyPhoto,
                        getResources().getString(R.string.msg_photo_erros));
            }
        }else if (requestCode == PhotoTaker.REQUEST_PICK_PHOTO){
            if (resultCode == RESULT_OK) {
                previewPickedImage(data);
            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(getApplicationContext(),
//                        "User cancelled image capture", Toast.LENGTH_SHORT)
//                        .show();
            } else {
                showSnackbar(fabMenuAcademyPhoto,
                        getResources().getString(R.string.msg_photo_erros));
            }
        }else if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence phone = place.getPhoneNumber();
            final CharSequence address = place.getAddress();

            mCurrentLatLng = place.getLatLng();
            //String attributions = PlacePicker.getAttributions(data);

//            if (attributions == null) {
//                attributions = "";
//            }

//            edtPlace.setText(name + " " + address + " " + Html.fromHtml(attributions));
            //edtPlace.setText(address + " " + Html.fromHtml(attributions));
            edtPlace.setText(address);
            edtPlace.setError(null);

            if (!name.equals("")){
                edtName.setText(name);
                edtName.setError(null);
            }

            if (!phone.equals("")){
                edtPhone.setText(phone);
                edtPhone.setError(null);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void uploadPhotoAndSaveToDB(){
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //if (user != null) {
            Boolean isNewAcademy = false;

            if (currentAcademy == null) { //not editing
                isNewAcademy = true;
                currentAcademy = new Academy();
            }

            currentAcademy.setName(edtName.getText().toString());
            currentAcademy.setPhone(edtPhone.getText().toString());
            currentAcademy.setEmail(edtEmail.getText().toString());
            currentAcademy.setPlace(edtPlace.getText().toString());
            currentAcademy.setVerified(switchVerified.isChecked());

            if (mCurrentLatLng != null){
                currentAcademy.setLat(mCurrentLatLng.latitude);
                currentAcademy.setLng(mCurrentLatLng.longitude);
            }

            if (mCurrentPhotoUri != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();

                //if (ImageFactory.imgIsLarge(mCurrentPhotoUri)) {
                //    options.inSampleSize = 8; // shrink it down otherwise we will use stupid amounts of memory
                //}

                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoUri.getPath(), options);

                if (bitmap != null) { //when user cancel the action and click in save

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);

                    byte[] bytes = baos.toByteArray();

                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    // Create a storage reference from our app
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://padellog-b49b1.appspot.com");

                    String Id = "images/academies/";

                    if (currentAcademy.getId() == null){
                        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();

                        String academyId = firebase.child("academies").push().getKey();

                        currentAcademy.setId(academyId);
                    }

                    Id = Id.concat(currentAcademy.getId()).concat(".jpg");

                    StorageReference playersRef = storageRef.child(Id);

                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    //progressDialog.setTitle(getResources().getString(R.string.photo_processing));
                    progressDialog.show();

                    UploadTask uploadTask = playersRef.putBytes(bytes);
                    final boolean finalIsNewAcademy = isNewAcademy;

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

                            currentAcademy.setPhotoUrl(downloadUrl.toString());

                            if (finalIsNewAcademy) {
                                currentAcademy.saveDB();
                            }else{
                                currentAcademy.updateDB();
                            }

                            //AcademyInfoActivity.currentAcademy = currentAcademy;
                            //AcademyInfoActivity.currentAcademy.setPhotoUriDownloaded(downloadUrl);
                            AcademyListActivity.mNeedToRefreshData = true;

                            if (mIsModeReadOnly){
                                showSnackbar(fabMenuAcademyPhoto,
                                        getResources().getString(R.string.msg_academy_saved_temp)
                                );
                            }else{
                                showSnackbar(fabMenuAcademyPhoto,
                                        getResources().getString(R.string.msg_academy_saved)
                                );
                            }

                        }
                    });

                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            Log.e("RNN", ((int)progress + "% " + getResources().getString(R.string.photo_complete)));

                            progressDialog.setMessage(getResources().getString(R.string.msg_saving));
                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                        }
                    });
                }else{
                    //saveAcademyWithoutPhoto(isNewAcademy);
                    showSnackbar(fabMenuAcademyPhoto, getResources().getString(R.string.msg_academy_need_photo));
                }
            }else {
                if (!mIsModeReadOnly){
                    saveAcademyWithoutPhoto(isNewAcademy);
                }else
                    showSnackbar(fabMenuAcademyPhoto, getResources().getString(R.string.msg_academy_need_photo));
            }

        //}
    }

    private void saveAcademyWithoutPhoto(Boolean isNew){
        //currentAcademy.setImageStr(mCurrentChampionshipImageStr);

        if (!hasPhoto) {
            currentAcademy.setPhotoUrl(null);
            currentAcademy.setPhotoUriDownloaded(null);
        }

        if (isNew) {
            currentAcademy.saveDB();
        }else{
            currentAcademy.updateDB();
        }


        //AcademyInfoActivity.currentAcademy = currentAcademy;
        AcademyListActivity.mNeedToRefreshData = true;

        if (mIsModeReadOnly){
            showSnackbar(fabMenuAcademyPhoto,
                    getResources().getString(R.string.msg_academy_saved_temp)
            );
        }else{
            showSnackbar(fabMenuAcademyPhoto,
                    getResources().getString(R.string.msg_academy_saved)
            );
        }

    }

    public void saveAcademy(){
        if (LibraryClass.isNetworkActive(this)) {
            uploadPhotoAndSaveToDB();
        }else{
            showSnackbar(fabMenuAcademyPhoto, getResources().getString(R.string.msg_no_internet) );
        }
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

    public static void start(Context c, Academy academy, Boolean isReadOnly) {

//        mNeedToRefreshData = true;
        mIsModeReadOnly = isReadOnly;
        currentAcademy = academy;

        c.startActivity(new Intent(c, AddAcademyActivity.class));
    }

//    public static void start(Context c,
//                             Academy academy) {
//        currentAcademy = academy;
//
//        c.startActivity(new Intent(c, AddAcademyActivity.class));
//    }

    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(edtName);
        }
    }

    private Boolean validateFields() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String place = edtPlace.getText().toString().trim();

        return (!isEmptyFields(name,
                phone,
                email,
                place));
//                && hasSizeValid(set1Score1, set1Score2));
    }

    private Boolean isEmptyFields(String name,
                                  String phone,
                                  String email,
                                  String place) {
        if (TextUtils.isEmpty(name)) {
            edtName.requestFocus();
            edtName.setError(resources.getString(R.string.msg_field_required));
            return true;
        } else if (TextUtils.isEmpty(place)) {
            edtPlace.requestFocus();
            edtPlace.setError(resources.getString(R.string.msg_field_required));
            return true;
        } else if (TextUtils.isEmpty(phone)) {
            edtPhone.requestFocus();
            edtPhone.setError(resources.getString(R.string.msg_field_required));
            return true;
//        } else if (TextUtils.isEmpty(email)) {
//            edtEmail.requestFocus();
//            edtEmail.setError(resources.getString(R.string.msg_field_required));
//            return true;
        }

        return false;
    }

    private void clearErrorFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setError(null);
        }
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
        getMenuInflater().inflate(R.menu.menu_add_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            if (validateFields()) {
                saveAcademy();
            }

            return true;
        }else if (id == android.R.id.home) {
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
