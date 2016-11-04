package com.renatonunes.padellog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.renatonunes.padellog.adapters.ChampionshipListAdapter;
import com.renatonunes.padellog.domain.Championship;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChampionshipListActivity extends CommonActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    ArrayList<Championship> championships = new ArrayList<Championship>();

    private static int mLoggedPlayerDefaultCategory;
    private static String mUserIdToList;
    private static String mFirstName;

    @BindView(R.id.fab_add_championship)
    FloatingActionButton fabAddChampionship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_championship_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerView = (RecyclerView) findViewById(R.id.reciclerview_championships);
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
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fabAddChampionship.getLayoutParams();
                    int fab_bottomMargin = layoutParams.bottomMargin;
                    fabAddChampionship.animate().translationY(fabAddChampionship.getHeight() + fab_bottomMargin).setInterpolator(new LinearInterpolator()).start();
                } else if (dy < 0) {
                    fabAddChampionship.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
                }
            }
        });

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        fabAddChampionship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAddChampionshipActivity();
            }
        });

        //only for test
        //mUserIdToList = "Pj4RBjzaiNNcKVOo7FrZipZ93hK2";

        setUIPermission();
    }

    private void setUIPermission(){
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        if (isModeReadOnly()){
            fabAddChampionship.setVisibility(View.INVISIBLE);
            actionbar.setTitle(getResources().getString(R.string.nav_championships));
        }else{
            fabAddChampionship.setVisibility(View.VISIBLE);
            actionbar.setTitle(getResources().getString(R.string.nav_my_championships));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.refreshData();
    }

    public void callAddChampionshipActivity(){
        //Intent intent = new Intent(this, AddChampionshipActivity.class);
        //startActivity(intent);
        AddChampionshipActivity.start(this, null, mLoggedPlayerDefaultCategory);

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

    //retrieve data
    private void refreshData(){
        championships.clear();
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //final String userId = user.getUid();

        //ver aqui um modo de filtar melhor
//        FirebaseDatabase.getInstance().getReference().child("championships").addChildEventListener(new ChildEventListener() {
        //FirebaseDatabase.getInstance().getReference().child("championships").addChildEventListener(new ChildEventListener() {
        FirebaseDatabase.getInstance().getReference().child("championships").child(mUserIdToList).orderByChild("dateSort").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                //if (dataSnapshot.getKey().equals(userId)) { //ver aqui um modo de filtar melhor
                    getUpdates(dataSnapshot);
                //}
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
//                if (dataSnapshot.getKey().equals(userId)) {
                    getUpdates(dataSnapshot);
//                }
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

    }

    private void getUpdates(com.google.firebase.database.DataSnapshot dataSnapshot){
        //championships.clear();

        //for (com.google.firebase.database.DataSnapshot ds: dataSnapshot.getChildren()){
//        for (com.google.firebase.database.DataSnapshot ds: dataSnapshot.getChildren()){

//            com.google.firebase.database.DataSnapshot ds: dataSnapshot.getChildren()
        Championship championship = new Championship();
        championship.setId(dataSnapshot.getKey());
        championship.setName(dataSnapshot.getValue(Championship.class).getName());

        String owner = FirebaseAuth.getInstance().getCurrentUser().getUid();
        championship.setOwner(owner);
//        championship.setOwner(dataSnapshot.getValue(Championship.class).getOwner());

        championship.setPartner(dataSnapshot.getValue(Championship.class).getPartner());
        championship.setPlace(dataSnapshot.getValue(Championship.class).getPlace());
        championship.setResult(dataSnapshot.getValue(Championship.class).getResult());
        championship.setImageStr(dataSnapshot.getValue(Championship.class).getImageStr());
        championship.setLat(dataSnapshot.getValue(Championship.class).getLat());
        championship.setLng(dataSnapshot.getValue(Championship.class).getLng());
        championship.setInitialDate(dataSnapshot.getValue(Championship.class).getInitialDate());
        championship.setFinalDate(dataSnapshot.getValue(Championship.class).getFinalDate());
        championship.setCategory(dataSnapshot.getValue(Championship.class).getCategory());
        championship.setContext(this);

        championships.add(championship);//        }

        if (championships.size() > 0){
            adapter = new ChampionshipListAdapter(ChampionshipListActivity.this, championships, isModeReadOnly(), mFirstName);
            recyclerView.setAdapter(adapter);
        }else{
            Toast.makeText(ChampionshipListActivity.this, getResources().getString(R.string.msg_chart_no_data), Toast.LENGTH_SHORT).show();
        }

    }

    public static void start(Context c, int loggedPlayerDefaultCategory, String userIdToList, String playerName) {
        mLoggedPlayerDefaultCategory = loggedPlayerDefaultCategory;
        mUserIdToList = userIdToList;
        //mFirstName = playerName.substring(0, playerName.indexOf(' '));

        mFirstName = playerName;

        c.startActivity(new Intent(c, ChampionshipListActivity.class));
    }


    private boolean isModeReadOnly(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String loggedUserId = user.getUid();

        return !mUserIdToList.equals(loggedUserId);
    }
}
