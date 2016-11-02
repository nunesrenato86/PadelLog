package com.renatonunes.padellog;

import android.app.Activity;
import android.content.Context;
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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.Match;
import com.renatonunes.padellog.domain.util.AlertUtils;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.LibraryClass;
import com.renatonunes.padellog.domain.util.PermissionUtils;
import com.renatonunes.padellog.domain.util.PhotoTaker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddMatchActivity extends CommonActivity {

    Animation fabRotateClockwise;
    Animation fabRotateAntiClockwise;

    //fields
    private final Activity mActivity = this;

    @BindView(R.id.edt_add_match_opponent_drive)
    EditText edtOpponentDrive;

    @BindView(R.id.edt_add_match_opponent_backdrive)
    EditText edtOpponentBackdrive;

    //to handle images
    @BindView(R.id.img_match)
    ImageView mThumbnailPreview;

    @BindView(R.id.fab_match_photo_camera)
    FloatingActionButton fabMatchPhoto;

    @BindView(R.id.fab_match_photo_gallery)
    FloatingActionButton fabMatchPhotoGallery;

    @BindView(R.id.fab_match_photo_delete)
    FloatingActionButton fabMatchPhotoDelete;

    @BindView(R.id.fab_match_photo_add)
    FloatingActionButton fabMatchPhotoAdd;

    private Boolean isVisible = false;

    @BindView(R.id.spinner_match_round)
    Spinner spinnerRound;

    @BindView(R.id.edt_add_match_score_set1_1)
    EditText edtSet1Score1;

    @BindView(R.id.edt_add_match_score_set1_2)
    EditText edtSet1Score2;

    @BindView(R.id.edt_add_match_score_set2_1)
    EditText edtSet2Score1;

    @BindView(R.id.edt_add_match_score_set2_2)
    EditText edtSet2Score2;

    @BindView(R.id.edt_add_match_score_set3_1)
    EditText edtSet3Score1;

    @BindView(R.id.edt_add_match_score_set3_2)
    EditText edtSet3Score2;

    private String mCurrentMatchImageStr = "";
    private static Match mCurrentMatch;
    private Uri mCurrentPhotoUri;
    private static Championship currentChampionship = null;
    private PhotoTaker mPhotoTaker;

    private ArrayAdapter<String> dataAdapter;
    private Resources resources;
//    private NetworkChangeReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_match);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        //mNetworkReceiver = new NetworkChangeReceiver(this);

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

        resources = getResources();

        mPhotoTaker = new PhotoTaker(this);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        initSpinner();

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

//        spinnerRound.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                // On selecting a spinner item
////                String round = adapterView.getItemAtPosition(i).toString();
//                // Showing selected spinner item
////                Toast.makeText(getApplicationContext(),
////                        "Selected round : " + round, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
    }

    private void initSpinner(){
        // Spinner element
        // Spinner Drop down elements
        List<String> rounds = new ArrayList<String>();
        rounds.add(resources.getString(R.string.round_draw));
        rounds.add(resources.getString(R.string.round_64));
        rounds.add(resources.getString(R.string.round_32));
        rounds.add(resources.getString(R.string.round_16));
        rounds.add(resources.getString(R.string.round_8));
        rounds.add(resources.getString(R.string.round_4));
        rounds.add(resources.getString(R.string.round_semi));
        rounds.add(resources.getString(R.string.round_final));

        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rounds);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);

        // attaching data adapter to spinner
        spinnerRound.setAdapter(dataAdapter);
    }

    @OnClick(R.id.fab_match_photo_add)
    public void toggleFabs(){
        if (isVisible){
            fabMatchPhotoDelete.animate().scaleY(0).scaleX(0).setDuration(200).start();
            fabMatchPhotoGallery.animate().scaleY(0).scaleX(0).setDuration(200).start();
            fabMatchPhoto.animate().scaleY(0).scaleX(0).setDuration(200).start();

            fabMatchPhotoAdd.startAnimation(fabRotateAntiClockwise);
        }else{
            fabMatchPhotoDelete.animate().scaleY(1).scaleX(1).setDuration(200).start();
            fabMatchPhotoGallery.animate().scaleY(1).scaleX(1).setDuration(200).start();
            fabMatchPhoto.animate().scaleY(1).scaleX(1).setDuration(200).start();

            fabMatchPhotoAdd.startAnimation(fabRotateClockwise);
        }
        isVisible = !isVisible;
    }

    private void updateUI(){
        fabMatchPhotoDelete.animate().scaleY(0).scaleX(0).setDuration(0).start();
        fabMatchPhotoGallery.animate().scaleY(0).scaleX(0).setDuration(0).start();
        fabMatchPhoto.animate().scaleY(0).scaleX(0).setDuration(0).start();

//        fabMatchPhotoDelete.setVisibility(View.VISIBLE);
//        fabMatchPhotoGallery.setVisibility(View.VISIBLE);

        if (mCurrentMatch != null){
            edtOpponentDrive.setText(mCurrentMatch.getOpponentDrive());

            edtOpponentBackdrive.setText(mCurrentMatch.getOpponentBackdrive());

            mCurrentMatchImageStr = mCurrentMatch.getImageStr();

            if (((mCurrentMatchImageStr != null)) && (mCurrentMatchImageStr != "")){
                mThumbnailPreview.setImageBitmap(ImageFactory.imgStrToImage(mCurrentMatchImageStr));
            }else {
                deletePhoto();
            }

            spinnerRound.setSelection(mCurrentMatch.getRound());

            edtSet1Score1.setText(mCurrentMatch.getSet1Score1().toString());
            edtSet1Score2.setText(mCurrentMatch.getSet1Score2().toString());

            edtSet2Score1.setText(mCurrentMatch.getSet2Score1().toString());
            edtSet2Score2.setText(mCurrentMatch.getSet2Score2().toString());

            edtSet3Score1.setText(mCurrentMatch.getSet3Score1().toString());
            edtSet3Score2.setText(mCurrentMatch.getSet3Score2().toString());
        }
    }

    @OnClick(R.id.fab_match_photo_delete)
    public void deletePhoto(){
        toggleFabs();

        mCurrentMatchImageStr = "";
        mThumbnailPreview.setImageBitmap(null);
        mThumbnailPreview.setBackgroundResource(R.drawable.no_photo);
    }

    @OnClick(R.id.fab_match_photo_camera)
    public void takePhoto(){
        if (isVisible) {

            File placeholderFile = ImageFactory.newFile();
            mCurrentPhotoUri = Uri.fromFile(placeholderFile);

            if (!mPhotoTaker.takePhoto(placeholderFile)) {
                displayPhotoError();
            }
        }

        toggleFabs();
    }

    @OnClick(R.id.fab_match_photo_gallery)
    public void PickPhoto(){
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

        BitmapFactory.Options options = new BitmapFactory.Options();

        if (ImageFactory.imgIsLarge(mCurrentPhotoUri)) {
            //options.inSampleSize = 8;

            ImageFactory.mContext = this;
            picturePath = ImageFactory.compressImage(picturePath);
            mCurrentPhotoUri = Uri.parse(picturePath);
        }

        mThumbnailPreview.setImageBitmap(BitmapFactory.decodeFile(picturePath, options));
    }

    private void displayPhotoError() {
        Snackbar.make(fabMatchPhoto,
                getResources().getString(R.string.msg_error_img_file),
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
                showSnackbar(fabMatchPhoto,
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
                showSnackbar(fabMatchPhoto,
                        getResources().getString(R.string.msg_photo_erros));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void saveMatch(){
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (LibraryClass.isNetworkActive(this)) {
            if (currentChampionship != null) {
                Boolean isNewMatch = false;

                if (mCurrentMatch == null) { //not editing
                    isNewMatch = true;
                    mCurrentMatch = new Match();
                }
                mCurrentMatch.setOpponentBackdrive(edtOpponentBackdrive.getText().toString());
                mCurrentMatch.setOpponentDrive(edtOpponentDrive.getText().toString());
                mCurrentMatch.setImageStr(getImageStr());
                mCurrentMatch.setOwner(currentChampionship.getId());

                int value;

                //score 1
                if (edtSet1Score1.getText().toString().isEmpty()){
                    value = 0;
                }else {
                    value = Integer.valueOf(edtSet1Score1.getText().toString());
                }
                mCurrentMatch.setSet1Score1(value);

                if (edtSet1Score2.getText().toString().isEmpty()){
                    value = 0;
                }else {
                    value = Integer.valueOf(edtSet1Score2.getText().toString());
                }
                mCurrentMatch.setSet1Score2(value);

                //score 2
                if (edtSet2Score1.getText().toString().isEmpty()){
                    value = 0;
                }else {
                    value = Integer.valueOf(edtSet2Score1.getText().toString());
                }
                mCurrentMatch.setSet2Score1(value);

                if (edtSet2Score2.getText().toString().isEmpty()){
                    value = 0;
                }else {
                    value = Integer.valueOf(edtSet2Score2.getText().toString());
                }
                mCurrentMatch.setSet2Score2(value);

                //score 3
                if (edtSet3Score1.getText().toString().isEmpty()){
                    value = 0;
                }else {
                    value = Integer.valueOf(edtSet3Score1.getText().toString());
                }
                mCurrentMatch.setSet3Score1(value);

                if (edtSet3Score2.getText().toString().isEmpty()){
                    value = 0;
                }else {
                    value = Integer.valueOf(edtSet3Score2.getText().toString());
                }
                mCurrentMatch.setSet3Score2(value);

                mCurrentMatch.setRound(spinnerRound.getSelectedItemPosition());
                mCurrentMatch.setContext(this);

                if (isNewMatch) {
                    mCurrentMatch.saveDB();
                }else{
                    mCurrentMatch.updateDB();
                }

                currentChampionship.updateResult();
                ChampionshipInfoActivity.currentChampionship = currentChampionship;

                showSnackbar(fabMatchPhoto,
                        getResources().getString(R.string.msg_match_saved)
                        );
            }
        }else{
            showSnackbar(fabMatchPhoto, getResources().getString(R.string.msg_no_internet));
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        currentChampionship = null;
        mCurrentMatch = null;
    }

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
                saveMatch();
            }

            return true;
        }else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getImageStr(){
        if (mCurrentPhotoUri != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            //if (ImageFactory.imgIsLarge(mCurrentPhotoUri)) {
            //    options.inSampleSize = 8; // shrink it down otherwise we will use stupid amounts of memory
            //}

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoUri.getPath(), options);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] bytes = baos.toByteArray();

            String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

            return base64Image;
        }else
            return mCurrentMatchImageStr;
    }

    public static void start(Context c, Championship championship, Match currentMatch) {
        currentChampionship = championship;
        mCurrentMatch = currentMatch;

        c.startActivity(new Intent(c, AddMatchActivity.class));
    }

    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(edtOpponentDrive);
        }
    }

    private boolean validateFields() {
        String opponentDrive = edtOpponentDrive.getText().toString().trim();
        String opponentBackDrive = edtOpponentBackdrive.getText().toString().trim();
        String set1Score1 = edtSet1Score1.getText().toString().trim();
        String set1Score2 = edtSet1Score2.getText().toString().trim();
        return (!isEmptyFields(opponentDrive, opponentBackDrive, set1Score1, set1Score2));
//                && hasSizeValid(set1Score1, set1Score2));
    }

    private boolean isEmptyFields(String opponentDrive, String opponentBackDrive,
                                  String set1Score1, String set1Score2) {
        if (TextUtils.isEmpty(opponentDrive)) {
            edtOpponentDrive.requestFocus();
            edtOpponentDrive.setError(resources.getString(R.string.msg_field_required));
            return true;
        } else if (TextUtils.isEmpty(opponentBackDrive)) {
            edtOpponentBackdrive.requestFocus();
            edtOpponentBackdrive.setError(resources.getString(R.string.msg_field_required));
            return true;
        } else if (TextUtils.isEmpty(set1Score1)) {
            edtSet1Score1.requestFocus();
            edtSet1Score1.setError(resources.getString(R.string.msg_field_required));
            return true;
        } else if (TextUtils.isEmpty(set1Score2)) {
            edtSet1Score2.requestFocus();
            edtSet1Score2.setError(resources.getString(R.string.msg_field_required));
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
    protected void onResume() {
        super.onResume();
//        registerReceiver(mNetworkReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }
}
