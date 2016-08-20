package com.renatonunes.padellog;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.renatonunes.padellog.domain.MyMapItem;
import com.renatonunes.padellog.domain.Player;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient mGoogleMapApiClient;
    private GoogleMap mMap;
    private Context mContext;

    private LocationRequest mLocationRequest;
    private Marker markerMyLocation;
    private ClusterManager mClusterManager;
    private ClusterItem mClusterItemInicio;
    private ClusterItem mClusterItemFim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.inflateHeaderView(R.layout.nav_header_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        /*leio da minha classe player, pois no login por user e password, não tenho o valor
        * de FirebaseUser user.getDisplayName()*/
        final String userId = user.getUid();
        FirebaseDatabase.getInstance().getReference().child("players").child( userId ).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Player player = dataSnapshot.getValue(Player.class);
                        updateNavUi(player.getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("RNN", "getUser:onCancelled", databaseError.toException());
                    }
                });

        //para poder deslogar do gogle sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

//        String token = FirebaseInstanceId.getInstance().getToken();
//        Log.e("TESTEMSG", "token no service: " + token);
        //dy7aCLp4u04:APA91bHeqe_pUFatAw41Ra7KU726TuFHXgC36Kn4VUxXBMWXQUAqnUMTwEYVHQIeEX94VwkEk5cbyl2JTGl0yG1D3I8k77ZC5p4i_8kOhAr-CdFO0kUuXFOBhMHSte6cTSwt0bCYoARf
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mGoogleMapApiClient == null) {
            mGoogleMapApiClient = new GoogleApiClient.Builder(mContext)
                    //.addConnectionCallbacks(this)
                    //.addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleMapApiClient.connect();
        }

        showMe();
    }

    private void updateNavUi(String name){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.textview_nav_name);
        TextView navEmail = (TextView) headerView.findViewById(R.id.textview_nav_email);
        ImageView navImage = (ImageView) headerView.findViewById(R.id.imageView);

        if (user != null) {
            if (name != ""){
                navUsername.setText(name);
            }else {
                navUsername.setText(user.getDisplayName());
            }

            navEmail.setText(user.getEmail());

            if (user.getPhotoUrl() != null) {
                Picasso.with(this).load(user.getPhotoUrl()).into(navImage);
            }
            else
                Picasso.with(this).load(R.drawable.com_facebook_profile_picture_blank_square).into(navImage);
        } else {
            navUsername.setText("Não logado");
            navEmail.setText("");
        }
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

        if (id == R.id.nav_championships) {
            Intent intent = new Intent(this, ChampionshipListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_per_partner) {

        } else if (id == R.id.nav_per_year) {

        } else if (id == R.id.nav_logout) {
            if (FirebaseAuth.getInstance() != null) {
                FirebaseAuth.getInstance().signOut();
            }

            if (LoginManager.getInstance() != null) {
                LoginManager.getInstance().logOut();
            }

            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()){
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            }

            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onConnected(Bundle bundle) { //quando o client conectou com o play services

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //esse metodo só tem a api leval 23 pra cima, por isso coloca o @targetapi ...
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);//request code
            return;
        }

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
        mClusterManager = new ClusterManager<MyMapItem>(this, mMap);

        mClusterManager.setRenderer(new OwnIconRendered(this, mMap, mClusterManager));

        /* assim eh quando tava fixo
        for (LatLng posicao : posicoes){
            MyItem offsetItem = new MyItem(posicao.latitude, posicao.longitude);
            mClusterManager.addItem(offsetItem);


        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        */

        showMe();
    }


//    public void desenharLinha(LatLng inicio, LatLng fim){
//        PolylineOptions pOpt = new PolylineOptions();
//        pOpt.add(inicio); //clica no direito do google maps e add ponto pra saber
//        pOpt.add(fim);
//        pOpt.color(Color.RED);
//        pOpt.width(3);
//        mMap.addPolyline(pOpt);
//    }

    public void showMe(){ //fakegps - simular gps
        //ultima localizacao que ele conseguiu ler, não quer dizer que é a atual
//        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//
//        updateCamera(mLastLocation);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void updateCamera(Location LastLocation) {
        //antes de usar isso, se o gps estiver desligado, teria que pedir pra ligar
        if (LastLocation != null) {
            LatLng eu = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());

            if (markerMyLocation == null){
                markerMyLocation = mMap.addMarker(new MarkerOptions().position(eu).title("Estou aqui")); //adicioana um novo marcador no mapa
            }else{
                markerMyLocation.setPosition(eu); //adiciona um novo marcador no mapa
            }

            //ver pq nao ta bombando
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(eu, 16));
        }
    };

    //resposta da permisao, sabe qual permisao e se respondeu sim ou nao
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                showMe();
            }
        }
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
        updateCamera(location);
    }

    class OwnIconRendered extends DefaultClusterRenderer<MyMapItem> {

        public OwnIconRendered(Context context, GoogleMap map,
                               ClusterManager<MyMapItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyMapItem item, MarkerOptions markerOptions) {
            //markerOptions.icon(item.getIcon());
            //markerOptions.snippet(item.getSnippet());
            //markerOptions.title(item.getTitle());
            //markerOptions.title("teste");
            markerOptions.title(item.getmTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }
}
