package com.renatonunes.padellog;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.*;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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
import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.Player;
import com.renatonunes.padellog.domain.util.AlertUtils;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.LibraryClass;
import com.renatonunes.padellog.domain.util.PermissionUtils;
import com.renatonunes.padellog.domain.util.PhotoTaker;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddChampionshipActivity extends CommonActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DatePickerDialog.OnDateSetListener,
        DialogInterface.OnCancelListener{

    //fields
    @BindView(R.id.edt_add_championship_name)
    EditText edtName;

    @BindView(R.id.spinner_championship_category)
    Spinner spinnerCategory;

    @BindView(R.id.edt_add_partner_name)
    EditText edtPartner;

    @BindView(R.id.edt_add_initial_date)
    EditText edtInitialDate;

    @BindView(R.id.edt_add_final_date)
    EditText edtFinalDate;

    @BindView(R.id.edt_add_place)
    EditText edtPlace;

    static final int REQUEST_PLACE_PICKER = 103;

    @BindView(R.id.fab_championship_photo_camera)
    FloatingActionButton fabMenuChampionshipPhoto;

    @BindView(R.id.fab_championship_photo_gallery)
    FloatingActionButton fabMenuChampionshipPhotoGallery;

    @BindView(R.id.fab_championship_photo_delete)
    FloatingActionButton fabMenuChampionshipPhotoDelete;

    @BindView(R.id.fab_championship_photo_add)
    FloatingActionButton fabMenuChampionshipPhotoAdd;

    Context mContext;

    private Boolean isVisible = false;
    private Boolean isPickingAcademy = false;

    private final Activity mActivity = this;
    private static Championship currentChampionship = null;
    private String mCurrentChampionshipImageStr = "";

    public static Academy selectedAcademy = null;

    private boolean hasPhoto = false;

    private ArrayAdapter<String> dataAdapter;

    private static int mLoggedPlayerDefaultCategory;
    private static Player mPlayer;
    private static Boolean mAddingChampionship;

    //to handle dates
    private int year;
    private int month;
    private int day;
    private Long mInitialDate = Long.valueOf(0);
    private Long mFinalDate = Long.valueOf(0);

    //to handle place
    private LatLng mCurrentLatLng;

    //to handle images
    @BindView(R.id.img_championship)
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
        setContentView(R.layout.activity_add_championship);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permissions[] = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
            };

            Boolean ok = PermissionUtils.validate(this, 0, permissions);

            if (ok) {
                Log.i("RNN", "Permissions OK");
            }
        }

        mContext = this;

        resources = getResources();

        mPhotoTaker = new PhotoTaker(this);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        initSpinner();

        //google places api
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        edtInitialDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(true);
            }
        });

        edtFinalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(false);
            }
        });

        edtPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                dialogBuilder.setTitle(resources.getString(R.string.title_dlg_select_place));
                dialogBuilder.setItems(resources.getStringArray(R.array.location_type), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0:
                                callAcademyList();
                                break;
                            default:
                                openPlacePicker();
                                break;
                        }
                    }

                });
                dialogBuilder.create().show();
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

    private void openPlacePicker(){
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

    @Override
    protected void onResume() {
        super.onResume();

        if (isPickingAcademy){
            isPickingAcademy = false;

            if (selectedAcademy != null){
                edtPlace.setText(selectedAcademy.getName());
                mCurrentLatLng = new LatLng(selectedAcademy.getLat(), selectedAcademy.getLng());

                selectedAcademy = null;
            }
        }
    }

    private void callAcademyList(){
        isPickingAcademy = true;
        AcademyListActivity.start(this, !isMasterUser(), true);
    }

    private boolean isMasterUser(){
        if (mPlayer == null){
            return false;
        }else{
            return mPlayer.getId().equals(getResources().getString(R.string.control_key));
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

        spinnerCategory.setSelection(mLoggedPlayerDefaultCategory);
    }

    @OnClick(R.id.fab_championship_photo_add)
    public void toggleFabs(){
        if (isVisible){
            fabMenuChampionshipPhotoDelete.animate().scaleY(0).scaleX(0).setDuration(200).start();
            fabMenuChampionshipPhotoGallery.animate().scaleY(0).scaleX(0).setDuration(200).start();
            fabMenuChampionshipPhoto.animate().scaleY(0).scaleX(0).setDuration(200).start();

            fabMenuChampionshipPhotoAdd.startAnimation(fabRotateAntiClockwise);
            //fabMenuChampionshipPhotoAdd.animate().rotationY(25).setDuration(200).start();
        }else{
            fabMenuChampionshipPhotoDelete.animate().scaleY(1).scaleX(1).setDuration(200).start();
            fabMenuChampionshipPhotoGallery.animate().scaleY(1).scaleX(1).setDuration(200).start();
            fabMenuChampionshipPhoto.animate().scaleY(1).scaleX(1).setDuration(200).start();

            fabMenuChampionshipPhotoAdd.startAnimation(fabRotateClockwise);

            //fabMenuChampionshipPhotoAdd.animate().rotationY(0).setDuration(200).start();
        }

        isVisible = !isVisible;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentChampionship = null;
    }

    private void updateUI(){
        fabMenuChampionshipPhotoDelete.animate().scaleY(0).scaleX(0).setDuration(0).start();
        fabMenuChampionshipPhotoGallery.animate().scaleY(0).scaleX(0).setDuration(0).start();
        fabMenuChampionshipPhoto.animate().scaleY(0).scaleX(0).setDuration(0).start();

        if (currentChampionship != null){
            edtName.setText(currentChampionship.getName());
            edtPartner.setText(currentChampionship.getPartner());
            spinnerCategory.setSelection(currentChampionship.getCategory());

            mInitialDate = currentChampionship.getInitialDate();
            edtInitialDate.setText(currentChampionship.getInitialDateStr());

            mFinalDate = currentChampionship.getFinalDate();
            edtFinalDate.setText(currentChampionship.getFinalDateStr());

            edtPlace.setText(currentChampionship.getPlace());

            mCurrentChampionshipImageStr = currentChampionship.getImageStr();

            if (currentChampionship.getPhotoUriDownloaded() != null) {
                Picasso.with(getApplicationContext()).load(currentChampionship.getPhotoUriDownloaded().toString()).into(mThumbnailPreview);
                hasPhoto = true;
            }else if (currentChampionship.isImgFirebase()) {
                hasPhoto = true;
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference httpsReference = storage.getReferenceFromUrl(currentChampionship.getPhotoUrl());

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

            }else if (((mCurrentChampionshipImageStr != null)) && (mCurrentChampionshipImageStr != "")){
                mThumbnailPreview.setImageBitmap(ImageFactory.imgStrToImage(mCurrentChampionshipImageStr));
                hasPhoto = true;
            }else {
                deletePhoto();
            }
        }
    }

    @OnClick(R.id.fab_championship_photo_delete)
    public void deletePhoto(){
        toggleFabs();

        hasPhoto = false;

        mCurrentChampionshipImageStr = "";

        mThumbnailPreview.setImageBitmap(null);
        mThumbnailPreview.setBackgroundResource(R.drawable.no_photo);
    }

    @OnClick(R.id.fab_championship_photo_camera)
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

    @OnClick(R.id.fab_championship_photo_gallery)
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
        Snackbar.make(fabMenuChampionshipPhoto,
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
                showSnackbar(fabMenuChampionshipPhoto,
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
                showSnackbar(fabMenuChampionshipPhoto,
                        getResources().getString(R.string.msg_photo_erros));
            }
        }else if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            mCurrentLatLng = place.getLatLng();
            String attributions = PlacePicker.getAttributions(data);

            if (attributions == null) {
                attributions = "";
            }

//            edtPlace.setText(name + " " + address + " " + Html.fromHtml(attributions));
            edtPlace.setText(address + " " + Html.fromHtml(attributions));
            edtPlace.setError(null);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void uploadPhotoAndSaveToDB(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Boolean isNewChampionship = false;

            if (currentChampionship == null) { //not editing
                isNewChampionship = true;
                currentChampionship = new Championship();
            }

            currentChampionship.setName(edtName.getText().toString());
            currentChampionship.setPartner(edtPartner.getText().toString());
            currentChampionship.setOwner(user.getUid());
//                currentChampionship.setInitialDate(edtInitialDate.getText().toString());
//                currentChampionship.setFinalDate(edtFinalDate.getText().toString());

            currentChampionship.setInitialDate(mInitialDate);
            currentChampionship.setFinalDate(mFinalDate);

            currentChampionship.setCategory(spinnerCategory.getSelectedItemPosition());
            currentChampionship.setPlace(edtPlace.getText().toString());
            currentChampionship.setPlayer(mPlayer);

            if (mCurrentLatLng != null){
                currentChampionship.setLat(mCurrentLatLng.latitude);
                currentChampionship.setLng(mCurrentLatLng.longitude);
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

                    String Id = "images/championships/";

                    if (isNewChampionship){
                        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();

                        String champId = firebase.child("championships").child(currentChampionship.getOwner()).push().getKey();

                        currentChampionship.setId(champId);
                    }

                    Id = Id.concat(currentChampionship.getId()).concat(".jpg");

                    StorageReference playersRef = storageRef.child(Id);

                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    //progressDialog.setTitle(getResources().getString(R.string.photo_processing));
                    progressDialog.show();

                    UploadTask uploadTask = playersRef.putBytes(bytes);
                    final boolean finalIsNewChampionship = isNewChampionship;

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

                            currentChampionship.setPhotoUrl(downloadUrl.toString());
                            currentChampionship.setImageStr(null);

                            if (finalIsNewChampionship) {
                                currentChampionship.saveDB();
                            }else{
                                currentChampionship.updateDB();
                            }

                            currentChampionship.updateResult();

                            ChampionshipInfoActivity.currentChampionship = currentChampionship;
                            ChampionshipInfoActivity.currentChampionship.setPhotoUriDownloaded(downloadUrl);
                            ChampionshipListActivity.mNeedToRefreshData = true;

                            showSnackbar(fabMenuChampionshipPhoto,
                                    getResources().getString(R.string.msg_championship_saved)
                            );
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
                    saveChampionshipWithoutPhoto(isNewChampionship);
                }
            }else {
                saveChampionshipWithoutPhoto(isNewChampionship);
            }

        }
    }

    private void saveChampionshipWithoutPhoto(Boolean isNew){
        currentChampionship.setImageStr(mCurrentChampionshipImageStr);

        if (!hasPhoto) {
            currentChampionship.setPhotoUrl(null);
            currentChampionship.setPhotoUriDownloaded(null);
            currentChampionship.setImageStr(null);
        }

        if (isNew) {
            currentChampionship.saveDB();
        }else{
            currentChampionship.updateDB();
        }

        currentChampionship.updateResult();

        ChampionshipInfoActivity.currentChampionship = currentChampionship;
        ChampionshipListActivity.mNeedToRefreshData = true;

        showSnackbar(fabMenuChampionshipPhoto,
                getResources().getString(R.string.msg_championship_saved)
        );

    }

    public void saveChampionship(){
        if (LibraryClass.isNetworkActive(this)) {
            uploadPhotoAndSaveToDB();
        }else{
            showSnackbar(fabMenuChampionshipPhoto, getResources().getString(R.string.msg_no_internet) );
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

    private void setDate(Boolean initial){
        initDate(initial);

        Calendar cDefault = Calendar.getInstance();
        cDefault.set(year, month, day);

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                this,
                cDefault.get(Calendar.YEAR),
                cDefault.get(Calendar.MONTH),
                cDefault.get(Calendar.DAY_OF_MONTH)
        );

        String tag = (initial ? "initial" : "final");

        datePickerDialog.setOnCancelListener(this);
        datePickerDialog.show( getFragmentManager(), tag );
    }

    private void initDate(Boolean initial){
        Long date = Long.valueOf(0);
        Calendar c = Calendar.getInstance();

        if (initial){
            date = mInitialDate;
        }else{
            //date = - 1 * mFinalDate;
            date = mFinalDate;
        }

        if (date != 0){ //have some date
            c.setTimeInMillis(date);
            year = c.get(Calendar.YEAR);
        }else{ //dont have date
            if (!initial){ //if is the final date
                //set the final date = to initial date
                c.setTimeInMillis(mInitialDate);
                year = c.get(Calendar.YEAR);
            }
        }

        //if( year == 0 ){

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        //}
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        //year = month = day = 0;
        //edtInitialDate.setText("");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        EditText editText;

        Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);
        c.getTimeInMillis();

        if (view.getTag() == "initial"){
            editText = edtInitialDate;
            mInitialDate = c.getTimeInMillis();
        }
        else {
            //mFinalDate = - 1 * c.getTimeInMillis();
            mFinalDate = c.getTimeInMillis();
            editText = edtFinalDate;
        }

        editText.setText( (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "/" +
                (monthOfYear + 1 < 10 ? "0" + (monthOfYear + 1) : monthOfYear + 1) + "/" +
                year);

        editText.setError(null);
    }

    public static void start(Context c,
                             Championship championship,
                             int loggedPlayerDefaultCategory,
                             Player player,
                             boolean AddingChampionship) {
        currentChampionship = championship;
        mPlayer = player;
        mLoggedPlayerDefaultCategory = loggedPlayerDefaultCategory;
        mAddingChampionship = AddingChampionship;

        c.startActivity(new Intent(c, AddChampionshipActivity.class));
    }

    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(edtName);
        }
    }

    private Boolean validateFields() {
        String name = edtName.getText().toString().trim();
        String partner = edtPartner.getText().toString().trim();
        String initalDate = edtInitialDate.getText().toString().trim();
        String finalDate = edtFinalDate.getText().toString().trim();
        String place = edtPlace.getText().toString().trim();

        return (!isEmptyFields(name,
                partner,
                initalDate,
                finalDate,
                place));
//                && hasSizeValid(set1Score1, set1Score2));
    }

    private Boolean isEmptyFields(String name,
                                  String partner,
                                  String initialDate,
                                  String finalDate,
                                  String place) {
        if (TextUtils.isEmpty(name)) {
            edtName.requestFocus();
            edtName.setError(resources.getString(R.string.msg_field_required));
            return true;
        } else if (TextUtils.isEmpty(partner)) {
            edtPartner.requestFocus();
            edtPartner.setError(resources.getString(R.string.msg_field_required));
            return true;
        } else if (TextUtils.isEmpty(initialDate)) {
            edtInitialDate.requestFocus();
            edtInitialDate.setError(resources.getString(R.string.msg_field_required));
            return true;
        } else if (TextUtils.isEmpty(finalDate)) {
            edtFinalDate.requestFocus();
            edtFinalDate.setError(resources.getString(R.string.msg_field_required));
            return true;
        } else if (TextUtils.isEmpty(place)) {
            edtPlace.requestFocus();
            edtPlace.setError(resources.getString(R.string.msg_field_required));
            return true;
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
                saveChampionship();
            }

            return true;
        }else if (id == android.R.id.home) {
            finish();

            if (mAddingChampionship){
                if (currentChampionship != null) {
                    currentChampionship.setContext(this);
                    ChampionshipInfoActivity.start(this, currentChampionship, false, mPlayer.getName());
                }
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
