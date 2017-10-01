package com.renatonunes.padellog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.all.All;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.renatonunes.padellog.adapters.AcademyListAdapter;
import com.renatonunes.padellog.adapters.AchievmentsListAdapter;
import com.renatonunes.padellog.domain.Academy;
import com.renatonunes.padellog.domain.Achievment;
import com.renatonunes.padellog.domain.AllAchievments;
import com.renatonunes.padellog.domain.Player;

import java.util.ArrayList;

public class AchievmentsActivity extends AppCompatActivity {

    private static Context mContext;
    private static String mPlayerID;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    ArrayList<Achievment> achievments = new ArrayList<Achievment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        recyclerView = (RecyclerView) findViewById(R.id.reciclerview_achievments);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        actionbar.setTitle(getResources().getString(R.string.nav_awards));



        this.refreshData();

    }


    public static void start(Context c, String playerID) {
        mContext = c;
        mPlayerID = playerID;
        c.startActivity(new Intent(c, AchievmentsActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void refreshData(){

        FirebaseDatabase.getInstance().getReference().child("achievments").child(mPlayerID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getAchievmentsUpdates(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getAchievmentsUpdates(com.google.firebase.database.DataSnapshot ds) {


        AllAchievments allAchievments = new AllAchievments();
        allAchievments.setId(mPlayerID);

        allAchievments.setBeggining(ds.getValue(AllAchievments.class).getBeggining());
        allAchievments.setExpert(ds.getValue(AllAchievments.class).getExpert());
        allAchievments.setElite(ds.getValue(AllAchievments.class).getElite());
        allAchievments.setStardom(ds.getValue(AllAchievments.class).getStardom());
        allAchievments.setBorn(ds.getValue(AllAchievments.class).getBorn());
        allAchievments.setLegend(ds.getValue(AllAchievments.class).getLegend());
        allAchievments.setVictorious(ds.getValue(AllAchievments.class).getVictorious());
        allAchievments.setReady(ds.getValue(AllAchievments.class).getReady());
        allAchievments.setSpy(ds.getValue(AllAchievments.class).getSpy());
        allAchievments.setCurious(ds.getValue(AllAchievments.class).getCurious());


        Integer unlockCount = 0;

        //<!-- achievement It's just the beginning -->
        boolean unlocked = ((allAchievments.getBeggining() != null) && (allAchievments.getBeggining() == 1));
        Achievment achievment = new Achievment();
        achievment.setImgID(R.drawable.ac_soocomeco);
        achievment.setName(getResources().getString(R.string.str_achievement_its_just_the_beginning));
        achievment.setUnlocked(unlocked);
        achievment.setInfo(getResources().getString(R.string.str_achievement_its_just_the_beginning_info));

        if (unlocked){
            unlockCount++;
        }

        achievments.add(achievment);


    //<!-- achievement Expert in championships -->

        unlocked = ((allAchievments.getExpert() != null) && (allAchievments.getExpert() == 1));
        achievment = new Achievment();
        achievment.setImgID(R.drawable.ac_experiente);
        achievment.setName(getResources().getString(R.string.str_achievement_expert_in_championships));
        achievment.setUnlocked(unlocked);
        achievment.setInfo(getResources().getString(R.string.str_achievement_expert_in_championships_info));

        if (unlocked){
            unlockCount++;
        }

        achievments.add(achievment);

//    <!-- achievement Elite player -->

        unlocked = ((allAchievments.getElite() != null) && (allAchievments.getElite() == 1));
        achievment = new Achievment();
        achievment.setImgID(R.drawable.ac_competidorelite);
        achievment.setName(getResources().getString(R.string.str_achievement_elite_player));
        achievment.setUnlocked(unlocked);
        achievment.setInfo(getResources().getString(R.string.str_achievement_elite_player_info));

        if (unlocked){
            unlockCount++;
        }

        achievments.add(achievment);

//    <!-- achievement Rise to stardom -->

        unlocked = ((allAchievments.getStardom() != null) && (allAchievments.getStardom() == 1));
        achievment = new Achievment();
        achievment.setImgID(R.drawable.ac_rumoaoestrelato);
        achievment.setName(getResources().getString(R.string.str_achievement_rise_to_stardom));
        achievment.setUnlocked(unlocked);
        achievment.setInfo(getResources().getString(R.string.str_achievement_rise_to_stardom_info));

        if (unlocked){
            unlockCount++;
        }

        achievments.add(achievment);

//    <!-- achievement Born champion -->

        unlocked = ((allAchievments.getBorn() != null) && (allAchievments.getBorn() == 1));
        achievment = new Achievment();
        achievment.setImgID(R.drawable.ac_campeaonato);
        achievment.setName(getResources().getString(R.string.str_achievement_born_champion));
        achievment.setUnlocked(unlocked);
        achievment.setInfo(getResources().getString(R.string.str_achievement_born_champion_info));

        if (unlocked){
            unlockCount++;
        }

        achievments.add(achievment);

//    <!-- achievement You are a Paddle Legend -->

        unlocked = ((allAchievments.getLegend() != null) && (allAchievments.getLegend() == 1));
        achievment = new Achievment();
        achievment.setImgID(R.drawable.ac_lendadopadel);
        achievment.setName(getResources().getString(R.string.str_achievement_you_are_a_paddle_legend));
        achievment.setUnlocked(unlocked);
        achievment.setInfo(getResources().getString(R.string.str_achievement_you_are_a_paddle_legend_info));

        if (unlocked){
            unlockCount++;
        }

        achievments.add(achievment);

//    <!-- achievement Victorious -->

        unlocked = ((allAchievments.getVictorious() != null) && (allAchievments.getVictorious() == 1));
        achievment = new Achievment();
        achievment.setImgID(R.drawable.ac_ganhou200);
        achievment.setName(getResources().getString(R.string.str_achievement_victorious));
        achievment.setUnlocked(unlocked);
        achievment.setInfo(getResources().getString(R.string.str_achievement_victorious_info));

        if (unlocked){
            unlockCount++;
        }

        achievments.add(achievment);

//    <!-- achievement Ready to start -->

        unlocked = ((allAchievments.getReady() != null) && (allAchievments.getReady() == 1));
        achievment = new Achievment();
        achievment.setImgID(R.drawable.ac_prontoparacomecar);
        achievment.setName(getResources().getString(R.string.str_achievement_ready_to_start));
        achievment.setUnlocked(unlocked);
        achievment.setInfo(getResources().getString(R.string.str_achievement_ready_to_start_info));

        if (unlocked){
            unlockCount++;
        }

        achievments.add(achievment);

//    <!-- achievement Spy -->

        unlocked = ((allAchievments.getSpy() != null) && (allAchievments.getSpy() == 1));
        achievment = new Achievment();
        achievment.setImgID(R.drawable.ac_espiao);
        achievment.setName(getResources().getString(R.string.str_achievement_spy));
        achievment.setUnlocked(unlocked);
        achievment.setInfo(getResources().getString(R.string.str_achievement_spy_info));

        if (unlocked){
            unlockCount++;
        }

        achievments.add(achievment);

//    <!-- achievement Curious -->

        unlocked = ((allAchievments.getCurious() != null) && (allAchievments.getCurious() == 1));
        achievment = new Achievment();
        achievment.setImgID(R.drawable.ac_curioso);
        achievment.setName(getResources().getString(R.string.str_achievement_curious));
        achievment.setUnlocked(unlocked);
        achievment.setInfo(getResources().getString(R.string.str_achievement_curious_info));

        if (unlocked){
            unlockCount++;
        }

        achievments.add(achievment);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(String.valueOf(unlockCount) + " de 10 " + getResources().getString(R.string.nav_awards));

        if (achievments.size() > 0){
            adapter = new AchievmentsListAdapter(AchievmentsActivity.this, achievments);
            recyclerView.setAdapter(adapter);
        }else{
            Toast.makeText(AchievmentsActivity.this, getResources().getString(R.string.msg_chart_no_data), Toast.LENGTH_SHORT).show();
        }

        //hideProgressDialog();
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
        } else
            return super.onOptionsItemSelected(item);
    }

}
