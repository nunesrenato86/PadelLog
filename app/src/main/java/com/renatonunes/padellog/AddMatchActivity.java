package com.renatonunes.padellog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.Match;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.PhotoTaker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddMatchActivity extends AppCompatActivity {

    //fields
    @BindView(R.id.edt_add_match_opponent_drive)
    EditText edtOpponentDrive;

    @BindView(R.id.edt_add_match_opponent_backdrive)
    EditText edtOpponentBackdrive;

    private final Activity mActivity = this;

    //to handle images
    @BindView(R.id.img_match)
    ImageView mThumbnailPreview;

    @BindView(R.id.fab_save_match)
    FloatingActionButton fabSaveMatch;

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

    private Uri mCurrentPhotoUri;
    private static Championship currentChampionship = null;
    private PhotoTaker mPhotoTaker;
    private ArrayAdapter<String> dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_match);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        mPhotoTaker = new PhotoTaker(this);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        fabSaveMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMatch();
            }
        });

        // Spinner element
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add(getResources().getString(R.string.round_draw));
        categories.add(getResources().getString(R.string.round_64));
        categories.add(getResources().getString(R.string.round_32));
        categories.add(getResources().getString(R.string.round_16));
        categories.add(getResources().getString(R.string.round_8));
        categories.add(getResources().getString(R.string.round_4));
        categories.add(getResources().getString(R.string.round_semi));
        categories.add(getResources().getString(R.string.round_final));

        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);

        // attaching data adapter to spinner
        spinnerRound.setAdapter(dataAdapter);

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

    public void TakePhoto(View view){
        //tem que verificar a permissao pro android 6

        File placeholderFile = ImageFactory.newFile();
        mCurrentPhotoUri = Uri.fromFile(placeholderFile);

        if (!mPhotoTaker.takePhoto(placeholderFile)) {
            displayPhotoError();
        }
    }

    public void PickPhoto(View view){
        //tem que verificar a permissao pro android 6
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
        Snackbar.make(fabSaveMatch,
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
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }else if (requestCode == PhotoTaker.REQUEST_PICK_PHOTO){
            if (resultCode == RESULT_OK) {
                previewPickedImage(data);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void saveMatch(){
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (currentChampionship != null) {
            Match match = new Match();
            match.setOpponentBackdrive(edtOpponentBackdrive.getText().toString());
            match.setOpponentDrive(edtOpponentDrive.getText().toString());
            match.setImageStr(getImageStr());
            match.setOwner(currentChampionship.getId());

            int value;

            //score 1
            if (edtSet1Score1.getText().toString().isEmpty()){
                value = 0;
            }else {
                value = Integer.valueOf(edtSet1Score1.getText().toString());
            }
            match.setSet1Score1(value);

            if (edtSet1Score2.getText().toString().isEmpty()){
                value = 0;
            }else {
                value = Integer.valueOf(edtSet1Score2.getText().toString());
            }
            match.setSet1Score2(value);

            //score 2
            if (edtSet2Score1.getText().toString().isEmpty()){
                value = 0;
            }else {
                value = Integer.valueOf(edtSet2Score1.getText().toString());
            }
            match.setSet2Score1(value);

            if (edtSet2Score2.getText().toString().isEmpty()){
                value = 0;
            }else {
                value = Integer.valueOf(edtSet2Score2.getText().toString());
            }
            match.setSet2Score2(value);

            //score 3
            if (edtSet3Score1.getText().toString().isEmpty()){
                value = 0;
            }else {
                value = Integer.valueOf(edtSet3Score1.getText().toString());
            }
            match.setSet3Score1(value);

            if (edtSet3Score2.getText().toString().isEmpty()){
                value = 0;
            }else {
                value = Integer.valueOf(edtSet3Score2.getText().toString());
            }
            match.setSet3Score2(value);

            //match.setRound(edtRound.getText().toString());
            match.setRound(dataAdapter.getItem(spinnerRound.getSelectedItemPosition()).toString());
            match.saveDB();

            //ver aqui - tratar erro
            Snackbar.make(fabSaveMatch,
                    "Partida salva.",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else  //else if (id == R.id.action_deletar_todos){
            return super.onOptionsItemSelected(item);
    }

    public String getImageStr(){
        if (mCurrentPhotoUri != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inSampleSize = 8; // shrink it down otherwise we will use stupid amounts of memory

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoUri.getPath(), options);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] bytes = baos.toByteArray();

            String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

            return base64Image;
        }else
            return "";
    }

    public static void start(Context c, Championship championship) {
        currentChampionship = championship;

        c.startActivity(new Intent(c, AddMatchActivity.class));
    }

}
