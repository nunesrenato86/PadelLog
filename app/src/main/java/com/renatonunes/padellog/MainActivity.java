package com.renatonunes.padellog;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.AddressConstants;
import com.google.android.gms.identity.intents.UserAddressRequest;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.renatonunes.padellog.adapters.RankingAdapter;
import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.ChampionshipSummary;
import com.renatonunes.padellog.domain.Match;
import com.renatonunes.padellog.domain.MyMapItem;
import com.renatonunes.padellog.domain.Player;
import com.renatonunes.padellog.domain.util.AlertUtils;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.LibraryClass;
import com.renatonunes.padellog.domain.util.PermissionUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends CommonActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnInfoWindowClickListener{

    /*
    class MarkerCallback implements Callback {
        Marker marker = null;
        String URL;
        ImageView userPhoto;

        MarkerCallback(Marker marker, String URL, ImageView userPhoto) {
            this.marker = marker;
            this.URL = URL;
            this.userPhoto = userPhoto;
        }

        @Override
        public void onError() {
            //Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
        }

        @Override
        public void onSuccess() {
            Boolean isShown = false;

            if (marker != null){
                isShown = marker.isInfoWindowShown();
            }

            //if (marker != null && marker.isInfoWindowShown()) {
            //if (isShown){
            if (mMustRefreshInfoWindow){
                marker.hideInfoWindow();

                Picasso.with(mContext)
                        .load(URL)
                        .into(userPhoto);

                marker.showInfoWindow(); //essa bosta no campeonato retorna a info normal
                mMustRefreshInfoWindow = false;
            }
        }
    }
    */

    class MarkerCallback implements Callback {
        Marker marker = null;
        String URL;

        MarkerCallback(Marker marker) {
            this.marker = marker;
        }

        @Override
        public void onError() {
            //Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
        }

        @Override
        public void onSuccess() {
            //if (marker != null && marker.isInfoWindowShown()) {
            if ((marker != null) && (mMustRefreshInfoWindow)){
                //marker.hideInfoWindow();
                marker.showInfoWindow();
                mMustRefreshInfoWindow = false;
            }
        }
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView txtName = ((TextView)myContentsView.findViewById(R.id.lbl_display_name_infowindow));
            TextView txtEmail = ((TextView)myContentsView.findViewById(R.id.lbl_display_email_infowindow));
            TextView lblAllChamps = ((TextView)myContentsView.findViewById(R.id.lbl_all_championships_infowindow));
            TextView lblChampion = ((TextView)myContentsView.findViewById(R.id.lbl_champions_infowindow));
            TextView lblVice = ((TextView)myContentsView.findViewById(R.id.lbl_vices_infowindow));

            ImageView imgAllChamps = ((ImageView)myContentsView.findViewById(R.id.img_allchampionships_count_infowindow));
            ImageView imgChampion = ((ImageView)myContentsView.findViewById(R.id.img_champion_count_infowindow));
            ImageView imgVice = ((ImageView)myContentsView.findViewById(R.id.img_vice_count_infowindow));

            final ImageView imgProfile = ((ImageView)myContentsView.findViewById(R.id.img_edit_profile_infowindow));

            if (clickedClusterItem instanceof Player){
                //markerOptions.title(((Player)clickedClusterItem).getName());

                final Player player = ((Player)clickedClusterItem);

                txtName.setText(player.getName());

                txtEmail.setText(getResources().getString(R.string.title_activity_chart));

                lblAllChamps.setVisibility(View.VISIBLE);
                lblAllChamps.setText(String.valueOf(player.getTotalChampionship()));
                imgAllChamps.setVisibility(View.VISIBLE);

                lblChampion.setVisibility(View.VISIBLE);
                lblChampion.setText(String.valueOf(player.getTotalFirstPlace()));
                imgChampion.setVisibility(View.VISIBLE);

                lblVice.setVisibility(View.VISIBLE);
                lblVice.setText(String.valueOf(player.getTotalSecondPlace()));
                imgVice.setVisibility(View.VISIBLE);

                if (player.isImgFirebase()) {
                    Picasso.with(mContext)
                            .load(player.getPhotoUrl())
                            .error(R.drawable.com_facebook_profile_picture_blank_square)
                            .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                            .into(imgProfile);
                } else if (player.isImgStrValid()) {
                    imgProfile.setImageBitmap(ImageFactory.imgStrToImage(player.getImageStr()));
                }else{
                    imgProfile.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
                    //Picasso.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_square).into(imgProfile);
                }

                clickedClusterItem = null;
                return myContentsView;

            }else if (clickedClusterItem instanceof Championship) {
                final Championship championship = ((Championship) clickedClusterItem);

                lblAllChamps.setVisibility(View.GONE);
                imgAllChamps.setVisibility(View.GONE);

                lblChampion.setVisibility(View.GONE);
                imgChampion.setVisibility(View.GONE);

                lblVice.setVisibility(View.GONE);
                imgVice.setVisibility(View.GONE);

                txtName.setText(championship.getName());

                txtEmail.setText(championship.getResultStr());

                if (championship.isImgFirebase()) {
                    Picasso.with(mContext)
                            .load(championship.getPhotoUrl())
                            .error(R.drawable.no_photo)
                            .placeholder(R.drawable.no_photo)
                            .into(imgProfile);
                } else if (championship.isImgStrValid()) {
                    imgProfile.setImageBitmap(ImageFactory.imgStrToImage(championship.getImageStr()));
                }else{
                    imgProfile.setImageResource(R.drawable.no_photo);
                }

                clickedClusterItem = null;
                return myContentsView;

            }else{
                clickedClusterItem = null;
                return null;
            }
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

    }

    private static final int REQUEST_CODE = 7860;

    ArrayList<Championship> championships = new ArrayList<Championship>();
    ArrayList<Player> players = new ArrayList<Player>();

    ArrayList<Championship> myChampionships = new ArrayList<Championship>();
    ArrayList<Match> myMatches = new ArrayList<Match>();

    private Boolean alreadyCounted = false;
    private Boolean alreadyCalledProfile = false;

    HashMap<String, MyMapItem> mMarkerPlayerMap = new HashMap<String, MyMapItem>();

    @BindView(R.id.progressBar_maps)
    ProgressBar progressBar;

    @BindView(R.id.fab_main)
    FloatingActionButton fabMain;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    View headerView;
    TextView navUsername;
    TextView navEmail;
    ImageView navImage;

    @BindView(R.id.img_marker)
    ImageView imgMarker;


    private ProgressDialog mProgressDialog;
    private IconGenerator mIconGenerator;
    private IconGenerator mClusterIconGenerator;
    private ImageView mImageView;
    //private ImageView mClusterImageView;
    private int mDimension;

    private Boolean mMustRefreshInfoWindow = false;
    private String mInfoWindowBase64Image;

    public static boolean playerImageHasChanged = false;
    public static boolean playerPlaceHasChanged = false;
    private CameraPosition mPreviousCameraPosition = null;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient mGoogleMapApiClient;
    private GoogleMap mMap;
    private Context mContext;
    public static Player mPlayer = null;
    private Boolean isLoading = true;
    private Boolean canZoomMap = true;
    private DefaultClusterRenderer mRenderer = null;

    //Player mPlayerOfTheMap;

    private Boolean isShowingChampionships = true;

//    private LocationRequest mLocationRequest;
//    private Marker markerMyLocation;
    private ClusterManager mClusterManager;

    private MyMapItem clickedClusterItem;

    protected void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );
    }

    protected void closeProgressBar(){
        progressBar.setVisibility( View.INVISIBLE );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setBackgroundColor(Color.TRANSPARENT);

        setSupportActionBar(toolbar);

//        Window window = this.getWindow();
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//        {
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//        }

        String value = LibraryClass.getSP(this, "isShowingChampionships");

        isShowingChampionships = value.equals("1") || value.equals("");

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        mContext = this;

        mProgressDialog = new ProgressDialog(mContext);

        mIconGenerator = new IconGenerator(mContext);
        mClusterIconGenerator = new IconGenerator(mContext);

        mImageView = new ImageView(mContext);
        mDimension = (int) getResources().getDimension(R.dimen.marker_size);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
        int padding = (int) getResources().getDimension(R.dimen.marker_padding);
        mImageView.setPadding(padding, padding, padding, padding);
        mIconGenerator.setContentView(mImageView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permissions[] = new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.READ_CONTACTS
            };

            boolean ok = PermissionUtils.validate(this, 3, permissions);

            if (ok) {
                Log.i("RNN", "Permissions OK");
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.inflateHeaderView(R.layout.nav_header_main);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);

        navUsername = (TextView) headerView.findViewById(R.id.textview_nav_name);
        navEmail = (TextView) headerView.findViewById(R.id.textview_nav_email);
        navImage = (ImageView) headerView.findViewById(R.id.img_nav_player);

        navImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoading) {
                    EditProfileActivity.start(mContext, mPlayer, false);
                }
            }
        });

        //to get user address and logout
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();

        com.google.android.gms.identity.intents.Address.AddressOptions options =
                new com.google.android.gms.identity.intents.Address.AddressOptions(AddressConstants.Themes.THEME_LIGHT);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(com.google.android.gms.identity.intents.Address.API, options)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

//to enable google logout
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

        if (mPlayer != null) {
            updateNavUi(mPlayer);
        }else{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            final String userId = user.getUid();
            FirebaseDatabase.getInstance().getReference().child("players").child(userId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mPlayer = dataSnapshot.getValue(Player.class);
                            mPlayer.setId(userId);
                            updateNavUi(mPlayer);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("RNN", "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }


//        String token = FirebaseInstanceId.getInstance().getToken();
//        Log.e("TESTEMSG", "token no service: " + token);
        //dy7aCLp4u04:APA91bHeqe_pUFatAw41Ra7KU726TuFHXgC36Kn4VUxXBMWXQUAqnUMTwEYVHQIeEX94VwkEk5cbyl2JTGl0yG1D3I8k77ZC5p4i_8kOhAr-CdFO0kUuXFOBhMHSte6cTSwt0bCYoARf

        new Wait().execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());


        mClusterManager = new ClusterManager<MyMapItem>(this, mMap);

        mRenderer = new OwnIconRendered(this, mMap, mClusterManager);

        mClusterManager.setRenderer(mRenderer);

        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(this);

        mClusterManager
                .setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyMapItem>() {
                    @Override
                    public boolean onClusterItemClick(MyMapItem item) {

                        clickedClusterItem = item;

                        //PopUpActivity.start(mContext);

                        //return false;

                        int yMatrix = 350, xMatrix = 40;

                        DisplayMetrics metrics1 = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics1);
                        switch(metrics1.densityDpi)
                        {
                            case DisplayMetrics.DENSITY_LOW:
                                yMatrix = 80;
                                xMatrix = 20;
                                break;
                            case DisplayMetrics.DENSITY_MEDIUM:
                                yMatrix = 100;
                                xMatrix = 25;
                                break;
                            case DisplayMetrics.DENSITY_HIGH:
                                yMatrix = 150;
                                xMatrix = 30;
                                break;
                            case DisplayMetrics.DENSITY_XHIGH:
                                yMatrix = 200;
                                xMatrix = 40;
                                break;
                            case DisplayMetrics.DENSITY_XXHIGH:
                                yMatrix = 200;
                                xMatrix = 50;
                                break;
                        }

                        Projection projection = mMap.getProjection();
                        LatLng latLng = item.getPosition();
                        Point point = projection.toScreenLocation(latLng);
                        Point point2 = new Point(point.x+xMatrix,point.y-yMatrix);

                        LatLng point3 = projection.fromScreenLocation(point2);
                        CameraUpdate zoom1 = CameraUpdateFactory.newLatLng(point3);
                        mMap.animateCamera(zoom1);
//
//                        if ((item instanceof Player) || (item instanceof Championship)) {
//                            Marker marker = mRenderer.getMarker(item);
//                            if (marker != null) {
//                                marker.showInfoWindow();
//                                mMustRefreshInfoWindow = true;
//                            }
//                        }
//
//                        return true;



                        if (item instanceof Player) {
                            Marker marker = mRenderer.getMarker(item);

                            if (marker != null) {
                                if (((Player)item).isImgFirebase()) {
                                    mMustRefreshInfoWindow = true;

                                    Picasso.with(mContext)
                                            .load(((Player) item).getPhotoUrl())
                                            .error(R.drawable.com_facebook_profile_picture_blank_square)
                                            .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                                            .into(mImageView, new MarkerCallback(marker));
                                } else{
                                    marker.showInfoWindow();

                                    mMustRefreshInfoWindow = false;
                                }
                            }
                        } else if (item instanceof Championship){
                            Marker marker = mRenderer.getMarker(item);

                            if (marker != null) {
                                if (((Championship)item).isImgFirebase()) {
                                    mMustRefreshInfoWindow = true;

                                    Picasso.with(mContext)
                                            .load(((Championship) item).getPhotoUrl())
                                            .error(R.drawable.no_photo)
                                            .placeholder(R.drawable.no_photo)
                                            .into(mImageView, new MarkerCallback(marker));
                                }else{
                                    marker.showInfoWindow();

                                    mMustRefreshInfoWindow = false;
                                }
                            }
                        }

                        return true;
                    }
                });

        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(
                new MyInfoWindowAdapter());

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                CameraPosition position = mMap.getCameraPosition();
                if (mPreviousCameraPosition == null || mPreviousCameraPosition.zoom != position.zoom) {
                    mPreviousCameraPosition = mMap.getCameraPosition();
                    mClusterManager.cluster();
                }
//
//                // to center the info window
//                if (clickedClusterItem != null) {
//                    Projection projection = mMap.getProjection();
//
//                    LatLng latLng = new LatLng(clickedClusterItem.getPosition().latitude, clickedClusterItem.getPosition().longitude);
////
//                    Point screenPosition = projection.toScreenLocation(latLng);
//
//                    Point mappoint = mMap.getProjection().toScreenLocation(latLng);
//                    mappoint.set(mappoint.x, mappoint.y - (screenPosition.y / 2));
//                    mMap.animateCamera(CameraUpdateFactory.newLatLng(mMap.getProjection().fromScreenLocation(mappoint)));
//
//                    clickedClusterItem = null;
//                }
            }
        });

        if (mGoogleMapApiClient == null) {
            mGoogleMapApiClient = new GoogleApiClient.Builder(mContext)
                    //.addConnectionCallbacks(this)
                    //.addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleMapApiClient.connect();
        }
    }

    private void updateNavUi(Player player){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if (!player.getName().isEmpty()){
                navUsername.setText(player.getName());
            }else {
                navUsername.setText(user.getDisplayName());
            }

            navEmail.setText(user.getEmail());

            if (mPlayer.getPhotoUriDownloaded() != null) {
                Picasso.with(getApplicationContext()).load(mPlayer.getPhotoUriDownloaded().toString()).into(navImage);
            } else if (player.isImgFirebase()) {
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference httpsReference = storage.getReferenceFromUrl(player.getPhotoUrl());

                httpsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mPlayer.setPhotoUriDownloaded(uri);

                        Picasso.with(mContext).load(uri.toString()).into(navImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            }else if (player.isImgStrValid()){
                navImage.setImageBitmap(ImageFactory.imgStrToImage(player.getImageStr()));

                convertPhoto();
            }else {
                navImage.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
            }
        } else {
            navUsername.setText("Não logado");
            navEmail.setText("");
        }

        updatePlayerPlace();

        countMatches();// testar isso aqui e testar no user do google

        //convertPhoto();

        isLoading = false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (!isLoading) {
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_championships) {
                showChampionships();

                return true;
            } else if (id == R.id.action_players) {
                showPlayers();

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showChampionships(){
        LibraryClass.saveSP(this, "isShowingChampionships", "1");
        canZoomMap = true;
        getChampionships();
    }

    private void showPlayers(){
        LibraryClass.saveSP(this, "isShowingChampionships", "0");
        canZoomMap = true;
        getPlayers();
    }

    private void callAcademyList(){
        AcademyListActivity.start(this, !isMasterUser(), false);
    }

    private void callRanking(){
        RankingActivity.start(this, !isMasterUser(), false);
    }

    private boolean isMasterUser(){
        if (mPlayer == null){
            return false;
        }else{
            return mPlayer.getId().equals(getResources().getString(R.string.control_key));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        Boolean isLoading = !navUsername.getText().equals(getResources().getString(R.string.msg_loading));

        if (!isLoading) {

            if (id == R.id.nav_show_championships) {
                showChampionships();
            } else if (id == R.id.nav_my_championships) {
                callChampionshipList(mPlayer.getId(), mPlayer.getName());
            } else if (id == R.id.nav_academies) {
                callAcademyList();
            } else if (id == R.id.nav_show_players) {
                showPlayers();
            } else if (id == R.id.nav_ranking) {
                callRanking();
            } else if (id == R.id.nav_per_year) {
                Intent intent = new Intent(this, ChartActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_policy){
                PrivacyPolicyActivity.start(this);
            } else if (id == R.id.nav_logout) {
                if (FirebaseAuth.getInstance() != null) {
                    FirebaseAuth.getInstance().signOut();
                }

                if (LoginManager.getInstance() != null) {
                    LoginManager.getInstance().logOut();
                }

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                }

                finish();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onConnected(Bundle bundle) {

//        String permissions[] = new String[]{
//                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                android.Manifest.permission.ACCESS_NETWORK_STATE
//        };
//
//        boolean ok = PermissionUtils.validate(this , 0, permissions);
//
//        if (ok){
//            Log.i("RNN", "Permissions OK");
//        }

//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            //esse metodo só tem a api leval 23 pra cima, por isso coloca o @targetapi ...
//            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);//request code
//            return;
//        }

        /*
        PolylineOptions pOpt = new PolylineOptions();
        pOpt.add(new LatLng(-30.032654, -51.227706)); //clica no direito do google maps e add ponto pra saber
        pOpt.add(new LatLng(-30.035556, -51.227968));
        pOpt.color(Color.BLUE);
        pOpt.width(3);
        mMap.addPolyline(pOpt);

        CircleOptions circle = new CircleOptions();
        circle.center(new LatLng(-29.973658, -51.194998));
        circle.fillColor(Color.BLUE);
        circle.strokeColor(Color.BLACK);
        circle.radius(200);
        mMap.addCircle(circle);
*/
        //começa a ouvir quando o cara se meche, etc
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient,
//                mLocationRequest,
//                this);
        /*
            for (LatLng posicao : posicoes){
                MarkerOptions mOpt = new MarkerOptions();
                mOpt.position(posicao);
                mMap.addMarker(mOpt);
            }
            */
//        mClusterManager = new ClusterManager<MyMapItem>(this, mMap);
//
//        mClusterManager.setRenderer(new OwnIconRendered(this, mMap, mClusterManager));

        /* assim eh quando tava fixo
        for (LatLng posicao : posicoes){
            MyItem offsetItem = new MyItem(posicao.latitude, posicao.longitude);
            mClusterManager.addItem(offsetItem);


        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        */
    }


//    public void desenharLinha(LatLng inicio, LatLng fim){
//        PolylineOptions pOpt = new PolylineOptions();
//        pOpt.add(inicio); //clica no direito do google maps e add ponto pra saber
//        pOpt.add(fim);
//        pOpt.color(Color.RED);
//        pOpt.width(3);
//        mMap.addPolyline(pOpt);
//    }

//    public void showMe(){ //fakegps - simular gps
        //ultima localizacao que ele conseguiu ler, não quer dizer que é a atual
//        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//
//        updateCamera(mLastLocation);

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }

//    private void updateCamera(Location LastLocation) {
//        //antes de usar isso, se o gps estiver desligado, teria que pedir pra ligar
//        if (LastLocation != null) {
//            LatLng eu = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
//
//            if (markerMyLocation == null){
//                markerMyLocation = mMap.addMarker(new MarkerOptions().position(eu).title("Estou aqui")); //adicioana um novo marcador no mapa
//            }else{
//                markerMyLocation.setPosition(eu); //adiciona um novo marcador no mapa
//            }
//
//            //ver pq nao ta bombando
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(eu, 16));
//        }
//    };

    //resposta da permisao, sabe qual permisao e se respondeu sim ou nao
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 3){
//            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
//                getChampionships();
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
        //OK can use maps
    }

    @Override
    public void onConnectionSuspended(int i) {
        //quando a conexao com o service foi suspensa -mostrar algo pro usuario, sem rede, sem mapa, sl
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //quando tem alguma falha com a conexao com o mapa
    }

    @Override
    public void onLocationChanged(Location location) {
        //updateCamera(location);
    }

    private void GlideLoadImgToMarker(String url, final Marker marker){
        Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.1f)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mImageView.setImageDrawable(resource);
                        Bitmap icon = mIconGenerator.makeIcon();
                        if (marker.getTag() != null) {
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                        }
                    }
                });

    }

    private void GlideLoadImgToMarker(final Marker marker){
        Glide.with(mContext)
                .load(R.drawable.ic_padellog_48)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.1f)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mImageView.setImageDrawable(resource);
                        Bitmap icon = mIconGenerator.makeIcon();

                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(icon);

                        if (marker.getTag() != null) {
                            marker.setIcon(bitmapDescriptor);
                        }
                    }
                });

    }

    private void GlideLoadImgToMarker(byte[] base64img, final Marker marker){
        Glide.with(mContext)
                .load(base64img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.1f)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mImageView.setImageDrawable(resource);
                        Bitmap icon = mIconGenerator.makeIcon();

                        if (marker.getTag() != null) {
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                        }
                    }
                });

    }


    class OwnIconRendered extends DefaultClusterRenderer<MyMapItem> {

        public OwnIconRendered(Context context, GoogleMap map,
                               ClusterManager<MyMapItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyMapItem item, final MarkerOptions markerOptions) {

            if (item instanceof Championship){

                markerOptions.snippet(((Championship)item).getResultStr());
                markerOptions.title(((Championship)item).getName());

                switch(((Championship)item).getResult()) {
                    case 8: //champions
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.trophy_gold_48));

                        break;
                    case 7: //vice
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.trophy_silver_48));

                        break;
                    default:
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_padellog_48));
                        break;
                }
            }else if (item instanceof Player){
                String text = String.format(getResources().getString(R.string.lbl_see_championships),
                        String.valueOf(((Player)item).getTotalChampionship()));

                markerOptions.snippet(text);

                markerOptions.title(((Player)item).getName());

                final Player player = ((Player)item);

                if (player.isImgStrValid()){
                    Bitmap b = ImageFactory.imgStrToImage(player.getImageStr());

                    player.setMarkerBitmap(Bitmap.createScaledBitmap(b, 80, 80, false));
                }else {
                    player.setMarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_padellog_32));
                }

                if (((Player)item).getMarkerBitmap() != null){
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(((Player)item).getMarkerBitmap()));
                }

            }

            super.onBeforeClusterItemRendered(item, markerOptions);
            closeProgressBar();
        }

        @Override
        protected void onClusterItemRendered(MyMapItem clusterItem,
                                             final Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);

            marker.setTag("tag"); //to check after when rendering

            mMarkerPlayerMap.put(marker.getId(), clusterItem);

            if (clusterItem instanceof Player) {
                if (((Player) clusterItem).isImgFirebase()) {
                    GlideLoadImgToMarker(((Player) clusterItem).getPhotoUrl(), marker);
                }
                else if (((Player) clusterItem).isImgStrValid()) {
                    GlideLoadImgToMarker(Base64.decode(((Player) clusterItem).getImageStr(), Base64.DEFAULT), marker);
                } else {
                    GlideLoadImgToMarker(marker);
                }
            }

//            else if (clusterItem instanceof Championship) {
//                if (((Championship) clusterItem).isImgFirebase()) {
//                    GlideLoadImgToMarker(((Championship) clusterItem).getPhotoUrl(), marker);
//                }
//                else if (((Championship) clusterItem).isImgStrValid()) {
//                    GlideLoadImgToMarker(Base64.decode(((Championship) clusterItem).getImageStr(), Base64.DEFAULT), marker);
//                } else {
//                    GlideLoadImgToMarker(marker);
//                }
//            }
        }
    }

    public void getChampionships(){
//        openProgressBar();
        isShowingChampionships = true;
        championships.clear();
        mMarkerPlayerMap.clear();
        clearMap();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            final String userId = user.getUid();

            FirebaseDatabase.getInstance().getReference().child("championships").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getUpdates(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

//        FirebaseDatabase.getInstance().getReference().child("championships").child(userId).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
//                getUpdates(dataSnapshot);
////                closeProgressBar();
//            }
//
//            @Override
//            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
//                getUpdates(dataSnapshot);
////                closeProgressBar();
//            }
//
//            @Override
//            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        }

    }

    private void getUpdates(com.google.firebase.database.DataSnapshot dataSnapshot){

        for (com.google.firebase.database.DataSnapshot ds: dataSnapshot.getChildren()) {
            Championship championship = new Championship();
            championship.setId(ds.getKey());
            championship.setName(ds.getValue(Championship.class).getName());

            String owner = FirebaseAuth.getInstance().getCurrentUser().getUid();
            championship.setOwner(owner);
            championship.setPartner(ds.getValue(Championship.class).getPartner());
            championship.setPlace(ds.getValue(Championship.class).getPlace());
            championship.setResult(ds.getValue(Championship.class).getResult());
            championship.setImageStr(ds.getValue(Championship.class).getImageStr());
            championship.setLat(ds.getValue(Championship.class).getLat());
            championship.setLng(ds.getValue(Championship.class).getLng());
            championship.setInitialDate(ds.getValue(Championship.class).getInitialDate());
            championship.setFinalDate(ds.getValue(Championship.class).getFinalDate());
            championship.setCategory(ds.getValue(Championship.class).getCategory());
            championship.setPhotoUrl(ds.getValue(Championship.class).getPhotoUrl());
            championship.setTrophyUrl(ds.getValue(Championship.class).getTrophyUrl());

            championship.setWin(ds.getValue(Championship.class).getWin());
            championship.setLoss(ds.getValue(Championship.class).getLoss());
            championship.setRatio(ds.getValue(Championship.class).getRatio());

            //championship.setPlayer(mPlayer);

            championship.setContext(this);

            championships.add(championship);

            markChampionshipOnMap(championship);
        }

        //verifyPlayerProfile();

//        if (championships.size() > 0){
//            adapter = new ChampionshipListAdapter(ChampionshipListActivity.this, championships);
//            recyclerView.setAdapter(adapter);
//        }else{
//            Toast.makeText(ChampionshipListActivity.this, "Sem dados", Toast.LENGTH_SHORT).show();

//        Championship championship = new Championship();
//        championship.setId(dataSnapshot.getKey());
//        championship.setName(dataSnapshot.getValue(Championship.class).getName());
//
//        String owner = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        championship.setOwner(owner);
//        championship.setPartner(dataSnapshot.getValue(Championship.class).getPartner());
//        championship.setPlace(dataSnapshot.getValue(Championship.class).getPlace());
//        championship.setResult(dataSnapshot.getValue(Championship.class).getResult());
//        championship.setImageStr(dataSnapshot.getValue(Championship.class).getImageStr());
//        championship.setLat(dataSnapshot.getValue(Championship.class).getLat());
//        championship.setLng(dataSnapshot.getValue(Championship.class).getLng());
//        championship.setInitialDate(dataSnapshot.getValue(Championship.class).getInitialDate());
//        championship.setFinalDate(dataSnapshot.getValue(Championship.class).getFinalDate());
//        championship.setCategory(dataSnapshot.getValue(Championship.class).getCategory());
//        championship.setContext(this);
//
//        championships.add(championship);//        }
//
//        markChampionshipOnMap(championship);
//
////        if (championships.size() > 0){
////            adapter = new ChampionshipListAdapter(ChampionshipListActivity.this, championships);
////            recyclerView.setAdapter(adapter);
////        }else{
////            Toast.makeText(ChampionshipListActivity.this, "Sem dados", Toast.LENGTH_SHORT).show();
    }

    private void clearMap(){
        if (mMap != null) {
            mMap.clear();
            mClusterManager.clearItems();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (playerImageHasChanged) {
            updateNavUi(mPlayer);
        }

        if (isShowingChampionships){
            getChampionships();
        }else{
            getPlayers();
        }
    }

    private class Wait extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            openProgressBar();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            }
            catch (InterruptedException ie) {
                Log.d("RNN2", ie.toString());
            }
            return(championships.size() == 0);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                closeProgressBar();
                //verifyPlayerProfile();
            }
        }
    }

    public void verifyPlayerProfile(){
        if ( (mPlayer != null) && (!mPlayer.havePlace()) ){
            AlertDialog dialogo = new AlertDialog.Builder(mContext)
                    .setTitle(getResources().getString(R.string.title_dlg_warning))
                    .setMessage(getResources().getString(R.string.msg_profile_incomplete))
                    .setPositiveButton(getResources().getString(R.string.btn_complete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditProfileActivity.start(mContext, mPlayer, false);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.btn_later), null)
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
        }

    }

    private void showNotDoneYet(){

//        mPlayer.countChamps();
//        mPlayer.countFirstPlace();
//        mPlayer.countSecondPlacePlace();

        Snackbar.make(navigationView,
                getResources().getString(R.string.msg_not_done_yet),
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    public void getPlayers(){
        openProgressBar();
        isShowingChampionships = false;
        players.clear();
        mMarkerPlayerMap.clear();
        clearMap();

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        final String userId = user.getUid();

        if (isMasterUser()) {
            FirebaseDatabase.getInstance().getReference().child("players").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getPlayersUpdates(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            FirebaseDatabase.getInstance().getReference().child("players").orderByChild("isPublic").equalTo(true).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getPlayersUpdates(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void getPlayersUpdates(com.google.firebase.database.DataSnapshot dataSnapshot) {

        for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()) {
            Player player = new Player();
            player.setId(ds.getKey());
            player.setName(ds.getValue(Player.class).getName());

//            String owner = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            championship.setOwner(owner);
            player.setPlace(ds.getValue(Player.class).getPlace());
            player.setEmail(ds.getValue(Player.class).getEmail());
            player.setImageStr(ds.getValue(Player.class).getImageStr());
            player.setPhotoUrl(ds.getValue(Player.class).getPhotoUrl());
            player.setLat(ds.getValue(Player.class).getLat());
            player.setLng(ds.getValue(Player.class).getLng());
            player.setIsPublic(ds.getValue(Player.class).getIsPublic());
            player.setCategory(ds.getValue(Player.class).getCategory());

            player.setTotalChampionship(ds.getValue(Player.class).getTotalChampionship());
            player.setTotalFirstPlace(ds.getValue(Player.class).getTotalFirstPlace());
            player.setTotalSecondPlace(ds.getValue(Player.class).getTotalSecondPlace());


            //player.updateChampionshipsCount();

            //mCheckInforInServer(player, player.getId());

            players.add(player);

            if (player.havePlace()){
                markPlayerOnMap(player);
            }else{
                if (isMasterUser()) {
                    double longitude = Math.random() * Math.PI * 2;
                    double latitude = Math.acos(Math.random() * 2 - 1);

                    player.setLat(latitude);
                    player.setLng(longitude);

                    markPlayerOnMap(player);
                }
            }

        }


//        closeProgressBar();
    }


//    private void getChampionshipSummaryUpdates(Player player, com.google.firebase.database.DataSnapshot dataSnapshot){
//        player.setTotalChampionship(dataSnapshot.getChildrenCount());
//
//        players.add(player);
//
//        markPlayerOnMap(player);
//    }
//
//    public interface OnGetDataListener {
//
//        public void onStart();
//        public void onSuccess(DataSnapshot data);
//        public void onFailed(DatabaseError databaseError);
//    }
//
//    public void mReadDataOnce(String child, final OnGetDataListener listener) {
//        listener.onStart();
//        FirebaseDatabase.getInstance().getReference().child("championships").child(child).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                listener.onSuccess(dataSnapshot);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                listener.onFailed(databaseError);
//            }
//        });
//    }
//
//    private void mCheckInforInServer(final Player player, String child) {
//        mReadDataOnce(child, new OnGetDataListener() {
//            @Override
//            public void onStart() {
//                //DO SOME THING WHEN START GET DATA HERE
//            }
//
//            @Override
//            public void onSuccess(DataSnapshot data) {
//                //DO SOME THING WHEN GET DATA SUCCESS HERE
//                getChampionshipSummaryUpdates(player, data);
//
//            }
//
//            @Override
//            public void onFailed(DatabaseError databaseError) {
//                //DO SOME THING WHEN GET DATA FAILED HERE
//            }
//        });
//
//    }

    private void markChampionshipOnMap(Championship championship){
        if (mMap != null) {
//            LatLng marker = null;

//            marker = new LatLng(championship.getLat(), championship.getLng());

//            mMap.addMarker(new MarkerOptions().position(marker).title(championship.getName()));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));

//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 5));

//            MyMapItem myMapItem = new MyMapItem(championship.getLat(), championship.getLng(), championship.getName());
//            mClusterManager.addItem(offsetItem)
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(offsetItem.getPosition(), 5));
            mClusterManager.addItem(championship);

            if (canZoomMap) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(championship.getPosition(), 5));
                canZoomMap = false;
            }

            mClusterManager.cluster();
        }

        closeProgressBar();
    }


    private void markPlayerOnMap(Player player){
        if (mMap != null) {
            mClusterManager.addItem(player);

            if (canZoomMap) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mPlayer.getPosition(), 10));

                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(player.getPosition(), 5));
                canZoomMap = false;
            }

            mClusterManager.cluster();
        }

        //Log.e("RNN", "adicionar aqui " + player.getName());

        closeProgressBar();
    }

//    private void markPlayersOnMap(){
//        if (mMap != null) {
//
//            mMap.clear();
//
//            for (Player player: players) {
//
//                mClusterManager.addItem(player);
//
//                if (canZoomMap) {
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(player.getPosition(), 5));
//                    canZoomMap = false;
//                }
//            }
//
//            mClusterManager.cluster();
//        }
//
//        //Log.e("RNN", "adicionar aqui " + player.getName());
//
//        closeProgressBar();
//    }

    //@OnClick(R.id.fab_main)
    public void callChampionshipList(String userToList, String playerName){
        ChampionshipListActivity.start(this, mPlayer, userToList, playerName);
    }

    @OnClick(R.id.fab_main)
    public void callAddChampionshipActivity(){
        if (mPlayer != null) {
            AddChampionshipActivity.start(this,
                    null,
                    mPlayer.getCategory(),
                    mPlayer,
                    true);

            //countMatches();
        }
    }

    public static void start(Context c, Player player) {
        mPlayer = player;

        c.startActivity(new Intent(c, MainActivity.class));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (!isShowingChampionships){

            final Player thePlayerOnThisMarker = (Player)mMarkerPlayerMap.get(marker.getId());

            if (thePlayerOnThisMarker.getTotalChampionship() > 0 ) {
                if (mPlayer.getIsPublic()){
                    callChampionshipList(thePlayerOnThisMarker.getId(), thePlayerOnThisMarker.getName());
                }else{
                    Snackbar snackbar = Snackbar
                            .make(navigationView,
                                    getResources().getString(R.string.msg_player_must_be_public),
                                    Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(R.string.lbl_make_public), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (mPlayer.canBePublic()) {
                                        //mPlayer.makePublic();
                                        //mPlayer.setIsPublic(true);
                                        EditProfileActivity.start(mContext, mPlayer, false);
                                    }else{
                                        Snackbar snackbar = Snackbar
                                                .make(navigationView,
                                                        getResources().getString(R.string.msg_profile_cant_be_public),
                                                        Snackbar.LENGTH_LONG)
                                                .setAction(getResources().getString(R.string.title_activity_edit_profile), new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        EditProfileActivity.start(mContext, mPlayer, false);
                                                    }
                                                });

                                        snackbar.show();
                                    }

                                }
                            });

                    snackbar.show();
                }
            }else{
                showSnackbar(navigationView, getResources().getString(R.string.msg_player_without_chapionships));
            }
        } else { //championship - show current championship
            final Championship championship = (Championship)mMarkerPlayerMap.get(marker.getId());

            championship.setPlayer(mPlayer);

            ChampionshipInfoActivity.start(mContext,
                    championship,
                    false,
                    mPlayer.getName());
        }
    }


    private void updatePlayerPlace(){

        if ((mPlayer != null) && (!mPlayer.havePlace())){

            AccessToken accessToken = AccessToken.getCurrentAccessToken();

            if (accessToken != null) {
                mProgressDialog.setMessage(getResources().getString(R.string.msg_update_place));

                mProgressDialog.show();

                Log.e("RNN2", accessToken.getToken());

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
                                            mProgressDialog.dismiss();
                                            verifyPlayerProfile();
                                        } else {
                                            getLatLng(city);
                                        }

                                    } catch (Throwable t) {
                                        Log.e("RNN2", "Could not parse malformed JSON: \"" + rawJson + "\"");
                                    }
                                } catch (JSONException e) {
                                    mProgressDialog.dismiss();
                                    e.printStackTrace();
                                    verifyPlayerProfile();
                                }
                            }
                        }
                ).executeAsync();
            } else { //Google
                UserAddressRequest request = UserAddressRequest.newBuilder().build();

                com.google.android.gms.identity.intents.Address.requestUserAddress(mGoogleApiClient,
                        request,
                        REQUEST_CODE);
            }
        }
    }

    //https://stackoverflow.com/questions/10008108/how-to-get-the-latitude-and-longitude-from-city-name-in-android
    private void getLatLng(String location){
        try {
            Geocoder gc = new Geocoder(this);
            List<Address> addresses = gc.getFromLocationName(location, 1); // get the found Address Objects

            //List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available

            if ((addresses == null) || (addresses.isEmpty())){
                mProgressDialog.dismiss();
                verifyPlayerProfile();
            } else {
                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {

                        //Log.d("RNN", new LatLng(a.getLatitude(), a.getLongitude()).toString());

                        mPlayer.setPlace(location);
                        mPlayer.setLat(a.getLatitude());
                        mPlayer.setLng(a.getLongitude());

                        //Log.d("RNN", mPlayer.getPlace() + "/" + mPlayer.getLat() + "/" + mPlayer.getLng());

                        mPlayer.updateDB(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                mProgressDialog.dismiss();

                                if (!isShowingChampionships) {
                                    getPlayers();
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mPlayer.getPosition(), 10));
                                }
                            }
                        });
                        //ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    } else {
                        mProgressDialog.dismiss();
                        verifyPlayerProfile();
                    }
                }
            }
        } catch (IOException e) {
            mProgressDialog.dismiss();
            // handle the exception
        }
    }

    private void convertPhoto(){
        if ((mPlayer != null) && (mPlayer.isImgStrValid()) && (!mPlayer.isImgFirebase())){
            Bitmap bitmap = ImageFactory.imgStrToImage(mPlayer.getImageStr());

            if (bitmap != null) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);

                byte[] bytes = baos.toByteArray();

                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageRef = storage.getReferenceFromUrl("gs://padellog-b49b1.appspot.com");

                String Id = "images/players/";

                Id = Id.concat(mPlayer.getId()).concat(".jpg");

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

                        mPlayer.setPhotoUrl(downloadUrl.toString());
                        mPlayer.setImageStr(null);

                        mPlayer.updateDB(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (!isShowingChampionships){
                                    getPlayers();
                                }

                                showSnackbar(navigationView,
                                        getResources().getString(R.string.msg_championship_converted)
                                );
                            }
                        });


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE){
            switch (resultCode) {
                case Activity.RESULT_OK:
                    UserAddress userAddress = UserAddress.fromIntent(data);

                    //b = (a > 0) ? 1 : 2;

                    //String teste = (!userAddress.getAddress1().equals("") ? "," : "");

                    String place = userAddress.getAddress1() //address
                            + (!userAddress.getAddress1().equals("") ? "," : "")
                            + userAddress.getLocality() //city
                            + (!userAddress.getLocality().equals("") ? "," : "")
                            + userAddress.getAdministrativeArea(); //UF

                    Log.i("RNN", place);

                    if (!place.equals("")) {
                        getLatLng(place);
                    } else {
                        verifyPlayerProfile();
                    }

                    break;
                case Activity.RESULT_CANCELED:
                    Log.i("RNN", "cancel");
                    verifyPlayerProfile();
                    break;
                default:
                    //NO ADDRESS
                    Log.i("RNN", "no address");
                    verifyPlayerProfile();
                    break;
            }

        }
    }

    private void countMatches(){
        if ((mPlayer.getWin() == null) && (mPlayer.getTotalChampionship() > 0)) {

            mProgressDialog.setMessage(getResources().getString(R.string.msg_update_count));

            mProgressDialog.show();

            myChampionships.clear();
            myMatches.clear();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            final String userId = user.getUid();

            FirebaseDatabase.getInstance().getReference().child("championships").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!alreadyCounted) {
                        getMyChampionshipsUpdates(dataSnapshot);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void getMyChampionshipsUpdates(com.google.firebase.database.DataSnapshot dataSnapshot) {

        for (com.google.firebase.database.DataSnapshot ds: dataSnapshot.getChildren()) {
            Championship championship = new Championship();
            championship.setId(ds.getKey());
            championship.setName(ds.getValue(Championship.class).getName());

            String owner = FirebaseAuth.getInstance().getCurrentUser().getUid();
            championship.setOwner(owner);
            championship.setPartner(ds.getValue(Championship.class).getPartner());
            championship.setPlace(ds.getValue(Championship.class).getPlace());
            championship.setResult(ds.getValue(Championship.class).getResult());
            championship.setImageStr(ds.getValue(Championship.class).getImageStr());
            championship.setLat(ds.getValue(Championship.class).getLat());
            championship.setLng(ds.getValue(Championship.class).getLng());
            championship.setInitialDate(ds.getValue(Championship.class).getInitialDate());
            championship.setFinalDate(ds.getValue(Championship.class).getFinalDate());
            championship.setCategory(ds.getValue(Championship.class).getCategory());
            championship.setPhotoUrl(ds.getValue(Championship.class).getPhotoUrl());
            championship.setTrophyUrl(ds.getValue(Championship.class).getTrophyUrl());

            championship.setWin(ds.getValue(Championship.class).getWin());
            championship.setLoss(ds.getValue(Championship.class).getLoss());
            championship.setRatio(ds.getValue(Championship.class).getRatio());

            championship.setPlayer(mPlayer);

            championship.setContext(this);

            myChampionships.add(championship);
        }

        for (final Championship mChampionship: myChampionships){
            //Log.d("RNN", key);

            FirebaseDatabase.getInstance().getReference().child("matches").child(mChampionship.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getMyMatchesUpdates(dataSnapshot, mChampionship);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void getMyMatchesUpdates(com.google.firebase.database.DataSnapshot dataSnapshot, Championship championship) {

        //aqui ta chegando a chave Kq5kovhzz_wVmMW8t3A q eh a do torneio
        //        tem q chegar a Kqm__FuHq-aPPlNL_ik q eh a da partida
        //Integer win = 0;
        //Integer loss = 0;

        for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()) {
            Match match = new Match();

            String key = ds.getKey();

            match.setId(key);
            match.setOpponentBackdrive(ds.getValue(Match.class).getOpponentBackdrive());
            match.setOpponentDrive(ds.getValue(Match.class).getOpponentDrive());
            match.setOwner(ds.getValue(Match.class).getOwner());
            match.setSet1Score1(ds.getValue(Match.class).getSet1Score1());
            match.setSet1Score2(ds.getValue(Match.class).getSet1Score2());
            match.setSet2Score1(ds.getValue(Match.class).getSet2Score1());
            match.setSet2Score2(ds.getValue(Match.class).getSet2Score2());
            match.setSet3Score1(ds.getValue(Match.class).getSet3Score1());
            match.setSet3Score2(ds.getValue(Match.class).getSet3Score2());
            match.setRound(ds.getValue(Match.class).getRound());
            match.setImageStr(ds.getValue(Match.class).getImageStr());
            match.setPhotoUrl(ds.getValue(Match.class).getPhotoUrl());
            //match.setTeam1(myName + " / " + mCurrentChampionship.getPartner());
            match.setContext(mContext);
            match.setChampionship(championship);

            //if (player.getIsPublic()){
            //    myMatches.add(match);
            //}

//            if (match.isVictory()){
//                win = win++;
//            }else{
//                loss = loss++;
//            }

            if (match.getChampionship() != null){
                if (match.isVictory()) {
                    //this.getChampionship().getPlayer().incWin();
                    match.getChampionship().incWin();
                }else{
                    //this.getChampionship().getPlayer().incLoss();
                    match.getChampionship().incLoss();
                }
            }
        }

        alreadyCounted = true;
        mProgressDialog.dismiss();

        if (!alreadyCalledProfile) {
            EditProfileActivity.start(mContext, mPlayer, false);
            alreadyCalledProfile = true;
        }


//        if (isShowingChampionships){
//            getChampionships();
//        }else{
//            getPlayers();
//        }

//        for(Match m: myMatches){
//            if (m.isVictory()){
//                win = win + 1;
//            }else{
//                loss = loss + 1;
//            }
//
//
//        }


//        Toast.makeText(mContext,
//                "W: " + String.valueOf(win) + "/ L: " + String.valueOf(loss)
//                , Toast.LENGTH_SHORT).show();


        //hideProgressDialog();

    }
}


