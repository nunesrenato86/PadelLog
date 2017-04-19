package com.renatonunes.padellog;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.model.people.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.ChampionshipSummary;
import com.renatonunes.padellog.domain.MyMapItem;
import com.renatonunes.padellog.domain.Player;
import com.renatonunes.padellog.domain.util.AlertUtils;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.PermissionUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

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

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

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

                if (!player.getImageStr().isEmpty()) {

                    ImageView imgProfile = ((ImageView)myContentsView.findViewById(R.id.img_edit_profile_infowindow));

                    imgProfile.setImageBitmap(ImageFactory.imgStrToImage(player.getImageStr()));
                }

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

                if (!championship.getImageStr().isEmpty()) {

                    ImageView imgProfile = ((ImageView) myContentsView.findViewById(R.id.img_edit_profile_infowindow));

                    imgProfile.setImageBitmap(ImageFactory.imgStrToImage(championship.getImageStr()));

                }

                return myContentsView;
            }else{
                return null;
            }
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;

/*
            if (clickedClusterItem instanceof Player){
                //markerOptions.title(((Player)clickedClusterItem).getName());

                final Player player = ((Player)clickedClusterItem);

                TextView txtName = ((TextView)myContentsView.findViewById(R.id.lbl_display_name_infowindow));
                txtName.setText(player.getName());

                TextView txtEmail = ((TextView)myContentsView.findViewById(R.id.lbl_display_email_infowindow));
                txtEmail.setText(player.getEmail());
//
//                if (!player.getImageStr().isEmpty()){
//                    Bitmap b = ImageFactory.imgStrToImage(player.getImageStr());
//
//                    player.setMarkerBitmap(Bitmap.createScaledBitmap(b, 80, 80, false));
//                }else {
//                    player.setMarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_padellog_32));
//                }
//
//                if (((Player)item).getMarkerBitmap() != null){
//                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(((Player)item).getMarkerBitmap()));
//                }

            }else{
                final Championship championship = ((Championship)clickedClusterItem);

                TextView txtName = ((TextView)myContentsView.findViewById(R.id.lbl_display_name_infowindow));
                txtName.setText(championship.getName());

                TextView txtEmail = ((TextView)myContentsView.findViewById(R.id.lbl_display_email_infowindow));
                txtEmail.setText(championship.getCategoryStr());
            }
            */


            //return myContentsView;
        }

    }

    ArrayList<Championship> championships = new ArrayList<Championship>();
    ArrayList<Player> players = new ArrayList<Player>();

    HashMap<String, MyMapItem> mMarkerPlayerMap = new HashMap<String, MyMapItem>();

    @BindView(R.id.progressBar_maps)
    ProgressBar progressBar;

//    @BindView(R.id.fab_main)
//    FloatingActionButton fabMain;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    View headerView;
    TextView navUsername;
    TextView navEmail;
    ImageView navImage;

    @BindView(R.id.img_marker)
    ImageView imgMarker;

    public static boolean playerImageHasChanged = false;
    private CameraPosition mPreviousCameraPosition = null;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient mGoogleMapApiClient;
    private GoogleMap mMap;
    private Context mContext;
    public static Player mPlayer = null;
    private Boolean isLoading = true;
    private Boolean canZoomMap = true;

    Player mPlayerOfTheMap;

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
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        mContext = this;

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
                    EditProfileActivity.start(mContext, mPlayer);
                }
            }
        });

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

        //para poder deslogar do gogle sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
        //mClusterManager = new ClusterManager<Championship>(this, mMap);

        mClusterManager.setRenderer(new OwnIconRendered(this, mMap, mClusterManager));

        //mMap.setOnCameraChangeListener(mClusterManager);

        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(this);

        mClusterManager
                .setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyMapItem>() {
                    @Override
                    public boolean onClusterItemClick(MyMapItem item) {
                        clickedClusterItem = item;
                        return false;
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

                // to center the info window
                if (clickedClusterItem != null) {
                    Projection projection = mMap.getProjection();

                    LatLng latLng = new LatLng(clickedClusterItem.getPosition().latitude, clickedClusterItem.getPosition().longitude);
//
                    Point screenPosition = projection.toScreenLocation(latLng);

                    Point mappoint = mMap.getProjection().toScreenLocation(latLng);
                    mappoint.set(mappoint.x, mappoint.y - (screenPosition.y / 2));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(mMap.getProjection().fromScreenLocation(mappoint)));

                    clickedClusterItem = null;
                }
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

            if (!player.getImageStr().isEmpty()){
                navImage.setImageBitmap(ImageFactory.imgStrToImage(player.getImageStr()));
            }else {
                Picasso.with(this).load(R.drawable.com_facebook_profile_picture_blank_square).into(navImage);
//                if (user.getPhotoUrl() != null) {
//                    Picasso.with(this).load(user.getPhotoUrl()).into(navImage);
//                }
//                else
//                    Picasso.with(this).load(R.drawable.com_facebook_profile_picture_blank_square).into(navImage);
            }
        } else {
            navUsername.setText("Não logado");
            navEmail.setText("");
        }

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        Boolean isLoading = !navUsername.getText().equals(getResources().getString(R.string.msg_loading));

        if (!isLoading) {

            if (id == R.id.nav_show_championships) {
                canZoomMap = true;
                getChampionships();
            } else if (id == R.id.nav_my_championships) {
                callChampionshipList(mPlayer.getId(), mPlayer.getName());
            } else if (id == R.id.nav_show_players) {
                canZoomMap = true;
                getPlayers();
            } else if (id == R.id.nav_per_partner) {
                showNotDoneYet();
            } else if (id == R.id.nav_per_year) {
                Intent intent = new Intent(this, ChartActivity.class);
                startActivity(intent);
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
    public void onConnected(Bundle bundle) { //quando o client conectou com o play services

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

    class OwnIconRendered extends DefaultClusterRenderer<MyMapItem> {
//    class OwnIconRendered extends DefaultClusterRenderer<Championship> {

        public OwnIconRendered(Context context, GoogleMap map,
                               ClusterManager<MyMapItem> clusterManager) {
//                               ClusterManager<Championship> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyMapItem item, final MarkerOptions markerOptions) {
//        protected void onBeforeClusterItemRendered(Championship item, MarkerOptions markerOptions) {
//            markerOptions.icon(item.getIcon());
            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));

            if (item instanceof Championship){
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

                markerOptions.snippet(((Championship)item).getResultStr());
                markerOptions.title(((Championship)item).getName());
            }else{ //is a player
                String text = String.format(getResources().getString(R.string.lbl_see_championships),
                        String.valueOf(((Player)item).getTotalChampionship()));

                markerOptions.snippet(text);

                markerOptions.title(((Player)item).getName());

                final Player player = ((Player)item);
//
                if (!player.getImageStr().isEmpty()){
                    Bitmap b = ImageFactory.imgStrToImage(player.getImageStr());

                    player.setMarkerBitmap(Bitmap.createScaledBitmap(b, 80, 80, false));
                }else {
                    player.setMarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_padellog_32));
                }
//                    if (player.getPhotoUrl() != null) {
//                        final Target mTarget = new Target() {
//                            @Override
//                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
//                                Log.d("DEBUG", "onBitmapLoaded");
//                                Bitmap mBitmap = ((BitmapDrawable)imgMarker.getDrawable()).getBitmap();
////
//                                if (mBitmap != null){
//                                    player.setMarkerBitmap(bitmap);
//                                }
//                            }
//
//                            @Override
//                            public void onBitmapFailed(Drawable drawable) {
//                                Log.d("DEBUG", "onBitmapFailed");
//                            }
//
//                            @Override
//                            public void onPrepareLoad(Drawable drawable) {
//                                Log.d("DEBUG", "onPrepareLoad");
//                            }
//                        };
////
//                        Picasso.with(mContext).load(player.getPhotoUrl())
//                                .resize(80, 80)
//                                .into(mTarget);

                        //it doesnt work at first time loading images
//                        Picasso.with(mContext).load(player.getPhotoUrl())
//                                .resize(80, 80)
//                                .into(imgMarker, new Callback() {
//                                    @Override
//                                    public void onSuccess() {
//
////                                        Bitmap bitmap = ((BitmapDrawable)imgMarker.getDrawable()).getBitmap();
////
////                                        if (bitmap != null){
////                                            player.setMarkerBitmap(bitmap);
////                                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
////                                        }
//
//                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(((BitmapDrawable)imgMarker.getDrawable()).getBitmap()));
//                                        Log.d("RNN", "onSuccess: caiu aqui" + player.getName());
//
//                                    }
//
//                                    @Override
//                                    public void onError() {
//
//                                    }
//                                });
//
//                    } else
//                        player.setMarkerBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_padellog_32));
//                }

                if (((Player)item).getMarkerBitmap() != null){
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(((Player)item).getMarkerBitmap()));
                }

            }

            super.onBeforeClusterItemRendered(item, markerOptions);
            closeProgressBar();
        }

        @Override
        protected void onClusterItemRendered(MyMapItem clusterItem,
                                             Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);

            mMarkerPlayerMap.put(marker.getId(), clusterItem);

        }
    }

    public void getChampionships(){
//        openProgressBar();
        isShowingChampionships = true;
        championships.clear();
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

            //championship.setPlayer(mPlayer);

            championship.setContext(this);

            championships.add(championship);

            markChampionshipOnMap(championship);
        }

        verifyPlayerProfile();

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
                verifyPlayerProfile();
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
                            EditProfileActivity.start(mContext, mPlayer);
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
        clearMap();

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        final String userId = user.getUid();

        FirebaseDatabase.getInstance().getReference().child("players").addValueEventListener(new ValueEventListener() {
        //FirebaseDatabase.getInstance().getReference().child("players").orderByChild("isPublic").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getPlayersUpdates(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getPlayersUpdates(com.google.firebase.database.DataSnapshot dataSnapshot) {

        for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()) {
            Player player = new Player();
            player.setId(ds.getKey());
            player.setName(ds.getValue(Player.class).getName());

//            String owner = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            championship.setOwner(owner);
//            player.setPhotoUrl(ds.getValue(Player.class).getPhotoUrl());
            player.setPlace(ds.getValue(Player.class).getPlace());
            player.setEmail(ds.getValue(Player.class).getEmail());
            player.setImageStr(ds.getValue(Player.class).getImageStr());
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
                                        EditProfileActivity.start(mContext, mPlayer);
                                    }else{
                                        Snackbar snackbar = Snackbar
                                                .make(navigationView,
                                                        getResources().getString(R.string.msg_profile_cant_be_public),
                                                        Snackbar.LENGTH_LONG)
                                                .setAction(getResources().getString(R.string.title_activity_edit_profile), new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        EditProfileActivity.start(mContext, mPlayer);
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
        }
    }


    //TODO: custominfowindow


}
