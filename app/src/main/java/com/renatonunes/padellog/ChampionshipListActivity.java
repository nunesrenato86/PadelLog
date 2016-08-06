package com.renatonunes.padellog;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.renatonunes.padellog.adapters.RecyclerAdapterChampionships;
import com.renatonunes.padellog.domain.Championship;

import java.util.ArrayList;

public class ChampionshipListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    ArrayList<Championship> championships = new ArrayList<Championship>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_championship_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.reciclerview_championships);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        this.refreshData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_championship);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAddChampionshipActivity();
            }
        });
    }

    public void callAddChampionshipActivity(){
        Intent intent = new Intent(this, AddChampionshipActivity.class);
        startActivity(intent);
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();

        //ver aqui um modo de filtar melhor
//        FirebaseDatabase.getInstance().getReference().child("championships").addChildEventListener(new ChildEventListener() {
        //FirebaseDatabase.getInstance().getReference().child("championships").addChildEventListener(new ChildEventListener() {
        FirebaseDatabase.getInstance().getReference().child("championships").child(userId).addChildEventListener(new ChildEventListener() {
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
        championship.setName(dataSnapshot.getValue(Championship.class).getName());
        championship.setOwner(dataSnapshot.getValue(Championship.class).getOwner());
        championship.setPartner(dataSnapshot.getValue(Championship.class).getPartner());
        championship.setImageStr(dataSnapshot.getValue(Championship.class).getImageStr());

        championships.add(championship);
//        }

        if (championships.size() > 0){
            adapter = new RecyclerAdapterChampionships(ChampionshipListActivity.this, championships);
            recyclerView.setAdapter(adapter);
        }else{
            Toast.makeText(ChampionshipListActivity.this, "Sem dados", Toast.LENGTH_SHORT).show();
        }

    }


}
