package com.renatonunes.padellog.domain;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Renato on 02/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Championship {

    private Context context;
    private String id;
    private String name;
    private String partner;
    private String owner;
    private String imageStr;
    private String initialDate;
    private String finalDate;
    private String category;
    private String place;
    private Double lat;
    private Double lng;
    private Integer result;
    private Match lastMatch;

    public Championship() {}

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getImageStr() {
        return imageStr;
    }

    public void setImageStr(String imageStr) {
        this.imageStr = imageStr;
    }

    public String getInitialDate() {return initialDate;}

    public void setInitialDate(String initialDate) {this.initialDate = initialDate;}

    public String getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }

    public String getCategory() {return category;}

    public void setCategory(String category) {this.category = category;}

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Exclude
    public Match getLastMatch() {
        return lastMatch;
    }

    public void setLastMatch(Match lastMatch) {
        this.lastMatch = lastMatch;
    }

    @Exclude
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Exclude
    public String getResultStr() {
        switch(this.result) {
            case 0: return "Campeão";  // TODO: create strings.xml
            case 1: return "Campeão";
//				case 2: return MatchListFragment.newInstance();
        }
        return "";
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void updateResult(){
        //retrieve the max round
        FirebaseDatabase.getInstance().getReference().child("matches").child(this.id).orderByChild("round").limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getMatchUpdates(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getMatchUpdates(dataSnapshot);
            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void saveDB(DatabaseReference.CompletionListener... completionListener ){
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();

        id = firebase.child("championships").child(getOwner()).push().getKey();

        firebase = firebase.child("championships").child(getOwner()).child( getId() );

        if( completionListener.length == 0 ){
            firebase.setValue(this);
        }
        else{
            firebase.setValue(this, completionListener[0]);
        }
    }

    private void getMatchUpdates(com.google.firebase.database.DataSnapshot dataSnapshot){
        lastMatch = new Match();
        lastMatch.setId(dataSnapshot.getKey());
        lastMatch.setOpponentBackdrive(dataSnapshot.getValue(Match.class).getOpponentBackdrive());
        lastMatch.setOpponentDrive(dataSnapshot.getValue(Match.class).getOpponentDrive());
        lastMatch.setOwner(dataSnapshot.getValue(Match.class).getOwner());
        lastMatch.setSet1Score1(dataSnapshot.getValue(Match.class).getSet1Score1());
        lastMatch.setSet1Score2(dataSnapshot.getValue(Match.class).getSet1Score2());
        lastMatch.setSet2Score1(dataSnapshot.getValue(Match.class).getSet2Score1());
        lastMatch.setSet2Score2(dataSnapshot.getValue(Match.class).getSet2Score2());
        lastMatch.setSet3Score1(dataSnapshot.getValue(Match.class).getSet3Score1());
        lastMatch.setSet3Score2(dataSnapshot.getValue(Match.class).getSet3Score2());
        lastMatch.setRound(dataSnapshot.getValue(Match.class).getRound());
        lastMatch.setImageStr(dataSnapshot.getValue(Match.class).getImageStr());
//                match.setTeam1(myName + " / " + mCurrentChampionship.getPartner());
//                match.setContext(mContext);

        //update the championship result
        if (lastMatch != null) {
            Map<String, Object> result = new HashMap<String, Object>();

            this.result = lastMatch.getResult();

            result.put("result", this.result);

            FirebaseDatabase.getInstance().getReference().child("championships")
                    .child(this.getOwner())
                    .child(getId())
                    .updateChildren(result);
        }
    }
}
