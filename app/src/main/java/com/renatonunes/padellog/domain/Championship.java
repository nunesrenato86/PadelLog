package com.renatonunes.padellog.domain;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.clustering.ClusterItem;
import com.renatonunes.padellog.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Renato on 02/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Championship implements ClusterItem {

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
            case 0: return context.getResources().getString(R.string.round_draw);
            case 1: return context.getResources().getString(R.string.round_64);
			case 2: return context.getResources().getString(R.string.round_32);
            case 3: return context.getResources().getString(R.string.round_16);
            case 4: return context.getResources().getString(R.string.round_8);
            case 5: return context.getResources().getString(R.string.round_4);
            case 6: return context.getResources().getString(R.string.round_semi);
            case 7: return context.getResources().getString(R.string.result_name_vice);
            case 8: return context.getResources().getString(R.string.result_name_champion);
        }
        return context.getResources().getString(R.string.result_name_none);
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
        initResult();
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
            this.result = lastMatch.getResult();
        }else
            this.result = -1; //without matches

        Map<String, Object> result = new HashMap<String, Object>();

        result.put("result", this.result);

        FirebaseDatabase.getInstance().getReference().child("championships")
                .child(this.getOwner())
                .child(getId())
                .updateChildren(result);
    }


    private void initResult(){
        Map<String, Object> result = new HashMap<String, Object>();

        this.setResult(-1);
        result.put("result", -1);

        FirebaseDatabase.getInstance().getReference().child("championships")
                .child(this.getOwner())
                .child(getId())
                .updateChildren(result);
    }

    public void updateDB( DatabaseReference.CompletionListener... completionListener ){

        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("championships").child(getOwner()).child( getId() );

        Map<String, Object> map = new HashMap<>();
        setDataInMap(map);

        if( map.isEmpty() ){
            return;
        }

        if( completionListener.length > 0 ){
            firebase.updateChildren(map, completionListener[0]);
        }
        else{
            firebase.updateChildren(map);
        }
    }

    private void setDataInMap( Map<String, Object> map ) {
        if( getImageStr() != null ){
            map.put( "imageStr", getImageStr() );
        }

        if( getCategory() != null ){
            map.put( "category", getCategory() );
        }

        if( getFinalDate() != null ){
            map.put( "finalDate", getFinalDate() );
        }

        if( getInitialDate() != null ){
            map.put( "initialDate", getInitialDate() );
        }

        if( getLat() != null ){
            map.put( "lat", getLat() );
        }

        if( getLng() != null ){
            map.put( "lng", getLng() );
        }

        if( getName() != null ){
            map.put( "name", getName() );
        }

        if( getPartner() != null ){
            map.put( "partner", getPartner() );
        }

        if( getPlace() != null ){
            map.put( "place", getPlace() );
        }

//        if( getResult() != null ){
//            map.put( "result", getResult() );
//        }
    }

    @Override
    public LatLng getPosition() {

        LatLng latLng = new LatLng(this.getLat(), this.getLng());

        return latLng;
    }
}
