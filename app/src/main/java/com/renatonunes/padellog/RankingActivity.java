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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.renatonunes.padellog.adapters.AcademyListAdapter;
import com.renatonunes.padellog.adapters.RankingAdapter;
import com.renatonunes.padellog.domain.Academy;
import com.renatonunes.padellog.domain.Player;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RankingActivity extends CommonActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    ArrayList<Player> players = new ArrayList<Player>();

    private static Boolean mIsModeReadOnly = false;
    private static Context mContext;
    private Context localContext;
    private static Boolean mPickingPlayer;
    boolean mDidLoad = false;

    public static Boolean mNeedToRefreshData = true;

    private static ProgressDialog mProgressDialog;

    @BindView(R.id.fab_filter_ranking)
    FloatingActionButton fabFilterRanking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.msg_loading));
        mProgressDialog.setIndeterminate(true);

        //if (mIsModeReadOnly){
        //new Wait().execute();
        showProgressDialog();
        //}


        recyclerView = (RecyclerView) findViewById(R.id.reciclerview_ranking);
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
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fabFilterRanking.getLayoutParams();
                    int fab_bottomMargin = layoutParams.bottomMargin;
                    fabFilterRanking.animate().translationY(fabFilterRanking.getHeight() + fab_bottomMargin).setInterpolator(new LinearInterpolator()).start();
                } else if (dy < 0) {
                    fabFilterRanking.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
                }
            }
        });

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        localContext = this;

        fabFilterRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(localContext);
                dialogBuilder.setTitle(getResources().getString(R.string.title_dlg_select_place));
                dialogBuilder.setItems(getResources().getStringArray(R.array.ranking_type), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0:
                                refreshData("totalChampionship");
                                break;
                            case 1:
                                refreshData("totalFirstPlace");
                                break;
//                            case 2:
//                                refreshData("totalSecondPlace");
//                                break;
//                            case 3:
//                                refreshData("win");
//                                break;
                            default:
                                //refreshData("ratio");
                                refreshData("totalSecondPlace");
                                break;
                        }
                    }

                });
                dialogBuilder.create().show();
            }
        });

        setUIPermission();
    }

    private void setUIPermission(){
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        actionbar.setTitle(getResources().getString(R.string.nav_ranking));

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
                this.refreshData("totalChampionship");
            }
        } else {
            if (mNeedToRefreshData) {
                this.refreshData("totalChampionship");
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
//            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//            dialogBuilder.setTitle(getResources().getString(R.string.title_dlg_select_place));
//            dialogBuilder.setItems(getResources().getStringArray(R.array.filter_type), new DialogInterface.OnClickListener() {
//
//                public void onClick(DialogInterface dialog, int which) {
//                    switch(which) {
//                        case 0:
//                            refreshData("1");
//                            break;
//                        default:
//                            refreshData("0");
//                            break;
//                    }
//                }
//
//            });
//            dialogBuilder.create().show();


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
//        if (!mIsModeReadOnly) {
//            getMenuInflater().inflate(R.menu.menu_academy_list, menu);
//        }
        return true;
    }

    //retrieve data
    private void refreshData(final String value){
        mDidLoad = true;
        players.clear();

//        String filter;
//
//        if (mIsModeReadOnly) {
//            filter = "1";
//        }else{
//            filter = value;
//        }

        FirebaseDatabase.getInstance().getReference().child("players").orderByChild(value).addValueEventListener(new ValueEventListener() {
        //FirebaseDatabase.getInstance().getReference().child("players").orderByChild("isPublic").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getUpdates(dataSnapshot, value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getUpdates(com.google.firebase.database.DataSnapshot dataSnapshot, String rankingFilter) {

        for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()) {
            Player player = new Player();
            player.setId(ds.getKey());
            player.setName(ds.getValue(Player.class).getName());
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

            player.setWin(ds.getValue(Player.class).getWin());
            player.setLoss(ds.getValue(Player.class).getLoss());
            player.setRatio(ds.getValue(Player.class).getRatio());

            //player.setContext(this);

            if (player.getIsPublic()){
                players.add(player);
            }
        }

        if (players.size() > 0){
            Collections.reverse(players);

            adapter = new RankingAdapter(RankingActivity.this, players, mIsModeReadOnly, false/*mPickingAcademy*/, rankingFilter);

            recyclerView.setAdapter(adapter);
        }else{
            Toast.makeText(RankingActivity.this, getResources().getString(R.string.msg_chart_no_data), Toast.LENGTH_SHORT).show();
        }

        hideProgressDialog();

    }


    public static void start(Context c, Boolean isReadOnly, Boolean pickingAcademy) {

        mNeedToRefreshData = true;
        mIsModeReadOnly = isReadOnly;
        mContext = c;
        //mPickingAcademy = pickingAcademy;

        c.startActivity(new Intent(c, RankingActivity.class));
    }

}
