package com.renatonunes.padellog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.koushikdutta.ion.Ion;
import com.renatonunes.padellog.domain.Academy;
import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.Match;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.LibraryClass;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AcademyInfoActivity extends CommonActivity
        implements AppBarLayout.OnOffsetChangedListener,
        OnMapReadyCallback{

    private GoogleApiClient mGoogleMapApiClient;
    private GoogleMap mMap;
    private int mMaxScrollSize;
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    //private static final int PERCENTAGE_TO_ANIMATE_TITLES = 100;
    private static Context context;
    private boolean mIsAvatarShown = true;
    //private boolean mIsTitlesShown = true;

    public static Academy mCurrentAcademy;
    public static Context mContext;
    private static Boolean mIsReadOnly = false;

    @BindView(R.id.academy_collapsing)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.fab_edit_academy)
    FloatingActionButton fabNavigate;

    @BindView(R.id.img_top_academy)
    ImageView TopImage;

    @BindView(R.id.text_academy_name)
    TextView textName;

    @BindView(R.id.text_academy_place)
    TextView textPlace;

    @BindView(R.id.text_academy_phone)
    TextView textPhone;

    @BindView(R.id.text_academy_email)
    TextView textEmail;

    @Override
    public void onMapReady(GoogleMap googleMap) {


//        MarkerOptions mOpt = new MarkerOptions();
//        mOpt.position(posicao);
//        mMap.addMarker(mOpt);

        mMap = googleMap;

        if (mGoogleMapApiClient == null) {
            mGoogleMapApiClient = new GoogleApiClient.Builder(mContext)
                    //.addConnectionCallbacks(this)
                    //.addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleMapApiClient.connect();
        }

        LatLng marker = mCurrentAcademy.getPosition();

        mMap.addMarker(new MarkerOptions().position(marker).title(mCurrentAcademy.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academy_info);

        //TabLayout tabLayout = (TabLayout) findViewById(R.id.academy_tabs);
        ViewPager viewPager = (ViewPager) findViewById(R.id.academy_viewpager);
        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.academy_appbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.academy_info_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("");

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        collapsingToolbarLayout.setTitle("");

        fabNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //editAcademy();

//                String uri = "http://maps.google.com/maps?q=loc:"+mCurrentAcademy.getLat()+","+mCurrentAcademy.getLng()+" ("+mCurrentAcademy.getPlace()+")";
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
//                intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
//                intent.setData(Uri.parse(uri));
//                startActivity(intent);

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + mCurrentAcademy.getLat() + "," + mCurrentAcademy.getLng() + "");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }

            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.academy_map);
        mapFragment.getMapAsync(this);


        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();

        //setUIPermission();

    }

//    private void setUIPermission(){
//        if (mIsReadOnly){
//            fabNavigate.setVisibility(View.INVISIBLE);
//        }else{
//            fabNavigate.setVisibility(View.VISIBLE);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!mIsReadOnly) {
            getMenuInflater().inflate(R.menu.menu_match_info, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_match_edit) {
            editAcademy();

            return true;
        }else if (id == R.id.action_match_delete){
            //deleteMatch();
            askToDeleteAcademy();

            return true;
        }else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    public static void start(Context c, Academy currentAcademy, Boolean isReadOnly) {
        mContext = c;
        mCurrentAcademy = currentAcademy;
        mIsReadOnly = isReadOnly;
        c.startActivity(new Intent(c, AcademyInfoActivity.class));
    }

    private void editAcademy(){
        AddAcademyActivity.start(mContext, mCurrentAcademy, mIsReadOnly);
    }

    private void deleteAcademy(){
        FirebaseDatabase.getInstance().getReference()
                .child("academies")
                .child(mCurrentAcademy.getId()).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null); // This removes the node.
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                Toast.makeText(AcademyInfoActivity.this,
                        getResources().getString(R.string.msg_action_deleted),
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }

    private void updateUI(){
        //setting top image
        if (mCurrentAcademy.getPhotoUriDownloaded() != null){
            Picasso.with(mContext).load(mCurrentAcademy.getPhotoUriDownloaded().toString()).into(TopImage);
        } else if (mCurrentAcademy.isImgFirebase()){

            Ion.with(TopImage)
                    .placeholder(R.drawable.no_photo)
                    .load(mCurrentAcademy.getPhotoUrl());

//            FirebaseStorage storage = FirebaseStorage.getInstance();
//
//            StorageReference httpsReference = storage.getReferenceFromUrl(mCurrentAcademy.getPhotoUrl());
//
//            httpsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    Picasso.with(getApplicationContext()).load(uri.toString()).into(TopImage);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle any errors
//                }
//            });

        }else{
            TopImage.setImageResource(R.drawable.no_photo);
        }

        collapsingToolbarLayout.setTitle("");

        textName.setText(mCurrentAcademy.getName());
        textPlace.setText(mCurrentAcademy.getPlace());
        textPhone.setText(mCurrentAcademy.getPhone());
        textEmail.setText(mCurrentAcademy.getEmail());
    }

    private void askToDeleteAcademy(){

        if (LibraryClass.isNetworkActive(this)) {

            AlertDialog dialogo = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.title_dlg_confirm_delete))
                    .setMessage(getResources().getString(R.string.msg_academy_delete))
                    .setPositiveButton(getResources().getString(R.string.btn_delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteAcademy();

                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.btn_cancel), null)
                    .create();

            dialogo.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = ((AlertDialog) dialog)
                            .getButton(AlertDialog.BUTTON_POSITIVE);

                    positiveButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));


                    Button negativeButton = ((AlertDialog) dialog)
                            .getButton(AlertDialog.BUTTON_NEGATIVE);

                    negativeButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            });

            dialogo.show();

        }else{
            showSnackbar(fabNavigate, getResources().getString(R.string.msg_no_internet) );
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            TopImage.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            TopImage.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }

//        if (percentage >= PERCENTAGE_TO_ANIMATE_TITLES && mIsTitlesShown) {
//            mIsTitlesShown = false;
//            //textName.animate().scaleY(0).scaleX(0).setDuration(200).start();
//            //textSubtitle.animate().scaleY(0).scaleX(0).setDuration(200).start();
//        }
//
//        if (percentage <= PERCENTAGE_TO_ANIMATE_TITLES && !mIsTitlesShown) {
//            mIsTitlesShown = true;
//
////            textName.animate()
////                    .scaleY(1).scaleX(1)
////                    .start();
//
////            textSubtitle.animate()
////                    .scaleY(1).scaleX(1)
////                    .start();
//        }
    }
}
