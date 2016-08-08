package com.renatonunes.padellog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.PhotoTaker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddChampionshipActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DatePickerDialog.OnDateSetListener,
        DialogInterface.OnCancelListener {

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
    private FloatingActionButton fab;
    private final Activity mActivity = this;

    //to handle dates
    private int year, month, day;

    //to handle place
    private LatLng mCurrentLatLng;

    //to handle images
    private ImageView mThumbnailPreview;
    private Uri mCurrentPhotoUri;
    private PhotoTaker mPhotoTaker;

    //google places api
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_championship);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        mPhotoTaker = new PhotoTaker(this);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab_save_championship_form);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChampionship();
            }
        });

        mThumbnailPreview = (ImageView) findViewById(R.id.thumbnail_preview);

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
        Toast.makeText(getApplicationContext(),
                "Sorry! Couldn't create a new image file", Toast.LENGTH_SHORT)
                .show();
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

            edtPlace.setText(name + " " + address + " " + Html.fromHtml(attributions));

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void saveChampionship(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            Championship championship = new Championship();
            championship.setName(edtName.getText().toString());
            championship.setPartner(edtPartner.getText().toString());
            championship.setOwner(user.getUid());
            championship.setImageStr(getImageStr());
            championship.setInitialDate(edtInitialDate.getText().toString());
            championship.setFinalDate(edtFinalDate.getText().toString());
            championship.setCategory(edtCategory.getText().toString());
            championship.setPlace(edtPlace.getText().toString());
            championship.setLat(mCurrentLatLng.latitude);
            championship.setLng(mCurrentLatLng.longitude);
            championship.saveDB();

            //ver aqui - tratar erro

            Snackbar.make(fab,
                    "Campeonato salvo com sucesso.",
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
    }
}
