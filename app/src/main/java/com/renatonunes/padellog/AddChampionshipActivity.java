package com.renatonunes.padellog;

import android.Manifest;
import android.app.Activity;
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
import android.support.design.widget.Snackbar;
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
import android.widget.EditText;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.util.AlertUtils;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.LibraryClass;
import com.renatonunes.padellog.domain.util.PermissionUtils;
import com.renatonunes.padellog.domain.util.PhotoTaker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;

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

    @BindView(R.id.edt_add_partner_name)
    EditText edtPartner;

    @BindView(R.id.edt_add_initial_date)
    EditText edtInitialDate;

    @BindView(R.id.edt_add_final_date)
    EditText edtFinalDate;

    @BindView(R.id.edt_add_place)
    EditText edtPlace;

    @BindView(R.id.edt_add_category)
    EditText edtCategory;

    static final int REQUEST_PLACE_PICKER = 103;

    @BindView(R.id.fab_menu_championship_photo)
    FloatingActionMenu fabMenuChampionshipPhoto;

    private final Activity mActivity = this;
    private static Championship currentChampionship = null;
    private String mCurrentChampionshipImageStr = "";

    //to handle dates
    private int year, month, day;

    //to handle place
    private LatLng mCurrentLatLng;

    //to handle images
    @BindView(R.id.img_championship)
    ImageView mThumbnailPreview;

    private Uri mCurrentPhotoUri;
    private PhotoTaker mPhotoTaker;

    //google places api
    private GoogleApiClient mGoogleApiClient;

    private Resources resources;
    private Boolean isFabOnScreen = false;

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

            boolean ok = PermissionUtils.validate(this, 0, permissions);

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

        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentChampionship = null;
    }

    private void updateUI(){
        if (currentChampionship != null){
            edtName.setText(currentChampionship.getName());
            edtPartner.setText(currentChampionship.getPartner());
            edtCategory.setText(currentChampionship.getCategory());
            edtInitialDate.setText(currentChampionship.getInitialDate());
            edtFinalDate.setText(currentChampionship.getFinalDate());
            edtPlace.setText(currentChampionship.getPlace());

            mCurrentChampionshipImageStr = currentChampionship.getImageStr();

            if (((mCurrentChampionshipImageStr != null)) && (mCurrentChampionshipImageStr != "")){
                mThumbnailPreview.setImageBitmap(ImageFactory.imgStrToImage(mCurrentChampionshipImageStr));
            }else {
                deletePhoto();
            }
        }
    }

    @OnClick(R.id.fab_delete_championship_photo)
    public void deletePhoto(){
        fabMenuChampionshipPhoto.close(false);

        mCurrentChampionshipImageStr = "";
        mThumbnailPreview.setImageBitmap(null);
        mThumbnailPreview.setBackgroundResource(R.drawable.no_photo);
    }

    @OnClick(R.id.fab_camera_championship_photo)
    public void takePhoto(View view){
        fabMenuChampionshipPhoto.close(false);

        File placeholderFile = ImageFactory.newFile();
        mCurrentPhotoUri = Uri.fromFile(placeholderFile);

        if (!mPhotoTaker.takePhoto(placeholderFile)) {
            displayPhotoError();
        }
    }

    @OnClick(R.id.fab_gallery_championship_photo)
    public void pickPhoto(View view){
        fabMenuChampionshipPhoto.close(false);

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

        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoUri.getPath(), options);
        mThumbnailPreview.setImageBitmap(bitmap);
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
        mThumbnailPreview.setImageBitmap(BitmapFactory.decodeFile(picturePath));
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

    public void saveChampionship(){
        if (LibraryClass.isNetworkActive(this)) {
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
                currentChampionship.setImageStr(getImageStr());
                currentChampionship.setInitialDate(edtInitialDate.getText().toString());
                currentChampionship.setFinalDate(edtFinalDate.getText().toString());
                currentChampionship.setCategory(edtCategory.getText().toString());
                currentChampionship.setPlace(edtPlace.getText().toString());

                if (mCurrentLatLng != null){
                    currentChampionship.setLat(mCurrentLatLng.latitude);
                    currentChampionship.setLng(mCurrentLatLng.longitude);
                }

                if (isNewChampionship) {
                    currentChampionship.setResult(-1); //dont have matches yet
                    currentChampionship.saveDB();
                }else{
                    currentChampionship.updateDB();
                }

                //ver aqui - tratar erro

                Snackbar.make(fabMenuChampionshipPhoto,
                        getResources().getString(R.string.msg_championship_saved),
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        }else{
            showSnackbar(fabMenuChampionshipPhoto, getResources().getString(R.string.msg_no_internet) );
        }
    }

    public String getImageStr(){
        if (mCurrentPhotoUri != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            if (ImageFactory.imgIsLarge(mCurrentPhotoUri)){
                options.inSampleSize = 8; // shrink it down otherwise we will use stupid amounts of memory
            }

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoUri.getPath(), options);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] bytes = baos.toByteArray();

            String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

            return base64Image;
        }else
            return mCurrentChampionshipImageStr;
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
        initDate();

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

    private void initDate(){
        if( year == 0 ){
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        year = month = day = 0;
        //edtInitialDate.setText("");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        EditText editText;

        if (view.getTag() == "initial"){
            editText = edtInitialDate;
        }
        else
            editText = edtFinalDate;

        editText.setText( (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "/" +
                (monthOfYear + 1 < 10 ? "0" + (monthOfYear + 1) : monthOfYear + 1) + "/" +
                year);

        editText.setError(null);
    }

    public static void start(Context c, Championship championship) {
        currentChampionship = championship;

        c.startActivity(new Intent(c, AddChampionshipActivity.class));
    }

    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(edtName);
        }
    }

    private boolean validateFields() {
        String name = edtName.getText().toString().trim();
        String partner = edtPartner.getText().toString().trim();
        String category = edtCategory.getText().toString().trim();
        String initalDate = edtInitialDate.getText().toString().trim();
        String finalDate = edtFinalDate.getText().toString().trim();
        String place = edtPlace.getText().toString().trim();

        return (!isEmptyFields(name,
                partner,
                category,
                initalDate,
                finalDate,
                place));
//                && hasSizeValid(set1Score1, set1Score2));
    }

    private boolean isEmptyFields(String name,
                                  String partner,
                                  String category,
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
        } else if (TextUtils.isEmpty(category)) {
            edtCategory.requestFocus();
            edtCategory.setError(resources.getString(R.string.msg_field_required));
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 3){
//            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
//                Toast.makeText(AddChampionshipActivity.this, "Liberado a photo", Toast.LENGTH_SHORT).show();
//            }else{
//                AlertUtils.alert(this,
//                        R.string.app_name,
//                        R.string.msg_alert_permission,
//                        R.string.msg_alert_OK,
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                finish();
//                            }
//                        });
//            }
//
//        }
//    }


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

//    private void showFab(){
//        Animation show_fab_delete = AnimationUtils.loadAnimation(this, R.anim.fab_delete_show);
//        Animation show_fab_gallery = AnimationUtils.loadAnimation(this, R.anim.fab_gallery_show);
//        Animation show_fab_camera = AnimationUtils.loadAnimation(this, R.anim.fab_camera_show);
//
//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fabSaveChampionship.getLayoutParams();
////        layoutParams.rightMargin += (int) (fabSaveChampionship.getWidth());
////        layoutParams.bottomMargin += (int) (fabDelete.getHeight() * 0.25);
//        layoutParams.bottomMargin += (int) (fabSaveChampionship.getHeight() * 2);
//
//        fabDelete.setLayoutParams(layoutParams);
//        fabDelete.startAnimation(show_fab_delete);
//        fabDelete.setClickable(true);
//
//        CoordinatorLayout.LayoutParams layoutParams2 = (CoordinatorLayout.LayoutParams) fabSaveChampionship.getLayoutParams();
////        layoutParams2.rightMargin += (int) (fabGallery.getWidth() * 1.5);
////        layoutParams.rightMargin += (int) (fabSaveChampionship.getWidth());
////        layoutParams2.bottomMargin += (int) (fabGallery.getHeight() * 1.5);
//        layoutParams2.bottomMargin += (int) (fabSaveChampionship.getHeight() * 4);
//        fabGallery.setLayoutParams(layoutParams2);
//        fabGallery.startAnimation(show_fab_gallery);
//        fabGallery.setClickable(true);
////
//        CoordinatorLayout.LayoutParams layoutParams3 = (CoordinatorLayout.LayoutParams) fabSaveChampionship.getLayoutParams();
//        //layoutParams3.rightMargin += (int) (fabCamera.getWidth() * 0.25);
////        layoutParams.rightMargin += (int) (fabSaveChampionship.getWidth());
////        layoutParams3.bottomMargin += (int) (fabCamera.getHeight() * 1.7);
//        layoutParams3.bottomMargin += (int) (fabSaveChampionship.getHeight() * 6);
//        fabCamera.setLayoutParams(layoutParams3);
//        fabCamera.startAnimation(show_fab_camera);
//        fabCamera.setClickable(true);
//
//        isFabOnScreen = true;
//    };
//
//    private void hideFab(){
//        Animation hide_fab_delete = AnimationUtils.loadAnimation(this, R.anim.fab_delete_hide);
//        Animation hide_fab_gallery = AnimationUtils.loadAnimation(this, R.anim.fab_gallery_hide);
//        Animation hide_fab_camera = AnimationUtils.loadAnimation(this, R.anim.fab_camera_hide);
//
//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fabDelete.getLayoutParams();
////        layoutParams.rightMargin -= (int) (fabDelete.getWidth() * 1.7);
////        layoutParams.bottomMargin -= (int) (fabDelete.getHeight() * 0.25);
//        layoutParams.bottomMargin -= (int) (fabDelete.getHeight() * 2);
//        fabDelete.setLayoutParams(layoutParams);
//        fabDelete.startAnimation(hide_fab_delete);
//        fabDelete.setClickable(false);
//
//        CoordinatorLayout.LayoutParams layoutParams2 = (CoordinatorLayout.LayoutParams) fabGallery.getLayoutParams();
////        layoutParams2.rightMargin -= (int) (fabGallery.getWidth() * 1.5);
////        layoutParams2.bottomMargin -= (int) (fabGallery.getHeight() * 1.5);
//        layoutParams2.bottomMargin -= (int) (fabGallery.getHeight() * 4);
//        fabGallery.setLayoutParams(layoutParams2);
//        fabGallery.startAnimation(hide_fab_gallery);
//        fabGallery.setClickable(false);
////
//        CoordinatorLayout.LayoutParams layoutParams3 = (CoordinatorLayout.LayoutParams) fabCamera.getLayoutParams();
////        layoutParams3.rightMargin -= (int) (fabCamera.getWidth() * 0.25);
////        layoutParams3.bottomMargin -= (int) (fabCamera.getHeight() * 1.7);
//        layoutParams3.bottomMargin -= (int) (fabCamera.getHeight() * 6);
//        fabCamera.setLayoutParams(layoutParams3);
//        fabCamera.startAnimation(hide_fab_camera);
//        fabCamera.setClickable(false);
//
//        isFabOnScreen = false;
//    };
//
//    private void controlFab(){
//        if (isFabOnScreen){
//            hideFab();
//        }else{
//            showFab();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_activity, menu);
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
                saveChampionship();
            }

            return true;
        }else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
