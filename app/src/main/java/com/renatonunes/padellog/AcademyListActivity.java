package com.renatonunes.padellog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.renatonunes.padellog.adapters.AcademyListAdapter;
import com.renatonunes.padellog.adapters.ChampionshipListAdapter;
import com.renatonunes.padellog.domain.Academy;
import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.Player;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AcademyListActivity extends CommonActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    ArrayList<Academy> academies = new ArrayList<Academy>();

    private static Boolean mIsModeReadOnly = false;
    private static Context mContext;
    private static Boolean mPickingAcademy;
    boolean mDidLoad = false;

    public static Boolean mNeedToRefreshData = true;

    private static ProgressDialog mProgressDialog;

    @BindView(R.id.fab_add_academy)
    FloatingActionButton fabAddAcademy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academy_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.msg_loading));
        mProgressDialog.setIndeterminate(true);

        //if (mIsModeReadOnly){
        //new Wait().execute();
        showProgressDialog();
        //}


        recyclerView = (RecyclerView) findViewById(R.id.reciclerview_academies);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0)
//                    fabAddChampionship.hide();
//                else if (dy < 0)
//                    fabAddChampionship.show();

                if (dy > 0) {
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fabAddAcademy.getLayoutParams();
                    int fab_bottomMargin = layoutParams.bottomMargin;
                    fabAddAcademy.animate().translationY(fabAddAcademy.getHeight() + fab_bottomMargin).setInterpolator(new LinearInterpolator()).start();
                } else if (dy < 0) {
                    fabAddAcademy.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
                }
            }
        });

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        fabAddAcademy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAcademyActivity.start(mContext, null ,mIsModeReadOnly);
            }
        });

        setUIPermission();
    }

    private void setUIPermission(){
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        actionbar.setTitle(getResources().getString(R.string.nav_academies));

//        if (mIsModeReadOnly){
//            fabAddAcademy.setVisibility(View.INVISIBLE);
//        }else{
//            fabAddAcademy.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsModeReadOnly) {
            if (!mDidLoad) {
                this.refreshData("1");
            }
        } else {
            if (mNeedToRefreshData) {
                this.refreshData("1");
                mNeedToRefreshData = false;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_filter) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(getResources().getString(R.string.title_dlg_select_place));
            dialogBuilder.setItems(getResources().getStringArray(R.array.filter_type), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch(which) {
                        case 0:
                            refreshData("1");
                            break;
                        default:
                            refreshData("0");
                            break;
                    }
                }

            });
            dialogBuilder.create().show();


            return true;
        }else if (id == android.R.id.home) {
            finish();
            return true;
        } else  //else if (id == R.id.action_deletar_todos){
            return super.onOptionsItemSelected(item);
    }

    public void showProgressDialog() {
        mProgressDialog.show();
    }

    public static void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!mIsModeReadOnly) {
            getMenuInflater().inflate(R.menu.menu_academy_list, menu);
        }
        return true;
    }

    //retrieve data
    private void refreshData(String value){
        mDidLoad = true;
        academies.clear();

        String filter;

        if (mIsModeReadOnly) {
            filter = "1";
        }else{
            filter = value;
        }

        //FirebaseDatabase.getInstance().getReference().child("academies").orderByChild("verified").equalTo(true).addChildEventListener(new ChildEventListener() {
        FirebaseDatabase.getInstance().getReference().child("academies").orderByChild("verified_name").startAt(filter).endAt(filter + "\uf8ff").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                getUpdates(dataSnapshot);
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                getUpdates(dataSnapshot);
            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
                getUpdates(dataSnapshot);
            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //fazer o filtro
//
//        if (mIsModeReadOnly) { //not masteruser - show only verified academies
//            //FirebaseDatabase.getInstance().getReference().child("academies").orderByChild("name").orderByChild("verified").equalTo(true).addChildEventListener(new ChildEventListener() {
//            //FirebaseDatabase.getInstance().getReference().child("academies").orderByChild("verified").equalTo(true).addChildEventListener(new ChildEventListener() {
//            FirebaseDatabase.getInstance().getReference().child("academies").orderByChild("verified_name").startAt(value).endAt(value + "\uf8ff").addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
//                    getUpdates(dataSnapshot);
//                }
//
//                @Override
//                public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
//                    getUpdates(dataSnapshot);
//                }
//
//                @Override
//                public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
//                    getUpdates(dataSnapshot);
//                }
//
//                @Override
//                public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//        }else{
//            //FirebaseDatabase.getInstance().getReference().child("academies").orderByChild("name").addChildEventListener(new ChildEventListener() {
//            FirebaseDatabase.getInstance().getReference().child("academies").orderByChild("verified_name").startAt(value).endAt(value + "\uf8ff").addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
//                    getUpdates(dataSnapshot);
//                }
//
//                @Override
//                public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
//                    getUpdates(dataSnapshot);
//                }
//
//                @Override
//                public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
//                    getUpdates(dataSnapshot);
//                }
//
//                @Override
//                public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//        }



    }

    private void getUpdates(com.google.firebase.database.DataSnapshot dataSnapshot){
        Academy academy = new Academy();
        academy.setId(dataSnapshot.getKey());
        academy.setName(dataSnapshot.getValue(Academy.class).getName());
        academy.setPlace(dataSnapshot.getValue(Academy.class).getPlace());
        academy.setLat(dataSnapshot.getValue(Academy.class).getLat());
        academy.setLng(dataSnapshot.getValue(Academy.class).getLng());
        academy.setPhone(dataSnapshot.getValue(Academy.class).getPhone());
        academy.setEmail(dataSnapshot.getValue(Academy.class).getEmail());
        academy.setPhotoUrl(dataSnapshot.getValue(Academy.class).getPhotoUrl());
        academy.setVerified(dataSnapshot.getValue(Academy.class).getVerified());

        academy.setContext(this);

        academies.add(academy);

        if (academies.size() > 0){
            adapter = new AcademyListAdapter(AcademyListActivity.this, academies, mIsModeReadOnly, mPickingAcademy);
            recyclerView.setAdapter(adapter);
        }else{
            Toast.makeText(AcademyListActivity.this, getResources().getString(R.string.msg_chart_no_data), Toast.LENGTH_SHORT).show();
        }

        hideProgressDialog();

    }

    public static void start(Context c, Boolean isReadOnly, Boolean pickingAcademy) {

        mNeedToRefreshData = true;
        mIsModeReadOnly = isReadOnly;
        mContext = c;
        mPickingAcademy = pickingAcademy;

        c.startActivity(new Intent(c, AcademyListActivity.class));
    }
}
