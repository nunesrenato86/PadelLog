/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.renatonunes.padellog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.Match;

import java.util.ArrayList;

public class MatchListFragment extends Fragment {
    private RecyclerView mRootView;
    ArrayList<Match> matches = new ArrayList<Match>();
    RecyclerView.Adapter adapter;
    public static Championship mCurrentChampionship;
    public static Context mContext;
    public static String myName;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (RecyclerView) inflater.inflate(R.layout.fragment_championship_matches, container, false);

        mRootView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FloatingActionButton fab = ((FloatingActionButton) getActivity().findViewById(R.id.fab_add_match));

                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });

        return mRootView;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.refreshData();

//		initRecyclerView();
    }

//	private void initRecyclerView() {
//		mRootView.setAdapter(new MatchListAdapter(20));
//	}

    public static Fragment newInstance(Championship currentChampionship, Context context) {
        mContext = context;
        mCurrentChampionship = currentChampionship;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myName = user.getDisplayName();

        return new MatchListFragment();
    }

    //retrieve data
    private void refreshData(){
        matches.clear();
//		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//		final String userId = user.getUid();

        if (mCurrentChampionship != null) {

            FirebaseDatabase.getInstance().getReference().child("matches").child(mCurrentChampionship.getId()).addChildEventListener(new ChildEventListener() {
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

                }

                @Override
                public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void getUpdates(com.google.firebase.database.DataSnapshot dataSnapshot){

        Match match = new Match();
        match.setId(dataSnapshot.getKey());
        match.setOpponentBackdrive(dataSnapshot.getValue(Match.class).getOpponentBackdrive());
        match.setOpponentDrive(dataSnapshot.getValue(Match.class).getOpponentDrive());
        match.setOwner(dataSnapshot.getValue(Match.class).getOwner());
        match.setSet1Score1(dataSnapshot.getValue(Match.class).getSet1Score1());
        match.setSet1Score2(dataSnapshot.getValue(Match.class).getSet1Score2());
        match.setSet2Score1(dataSnapshot.getValue(Match.class).getSet2Score1());
        match.setSet2Score2(dataSnapshot.getValue(Match.class).getSet2Score2());
        match.setSet3Score1(dataSnapshot.getValue(Match.class).getSet3Score1());
        match.setSet3Score2(dataSnapshot.getValue(Match.class).getSet3Score2());
        match.setRound(dataSnapshot.getValue(Match.class).getRound());
        match.setImageStr(dataSnapshot.getValue(Match.class).getImageStr());
        match.setTeam1(myName + " / " + mCurrentChampionship.getPartner());
        match.setContext(mContext);
        matches.add(match);

        if (matches.size() > 0){
            adapter = new MatchListAdapter(mContext, matches);
            mRootView.setAdapter(adapter);
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "Sem dados", Toast.LENGTH_SHORT).show();
        }

    }

}
