package com.renatonunes.padellog.domain;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by renatonunes on 30/09/17.
 */

public class AllAchievments {

    private String id;
    private String owner;
    private Integer beggining;
    //private Date beggining_d;

    private Integer expert;
    //private Date expert_d;

    private Integer elite;
    //private Date elite_d;

    private Integer stardom;
    //private Date stardom_d;

    private Integer born;
    //private Date born_d;

    private Integer legend;
    //private Date legend_d;

    private Integer victorious;
    //private Date victorious_d;

    private Integer ready;
    //private Date ready_d;

    private Integer spy;
    //private Date spy_d;

    private Integer curious;
    //private Date curious_d;

    @Exclude
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getBeggining() {
        return beggining;
    }

    public void setBeggining(Integer beggining) {
        this.beggining = beggining;
    }

//    public Date getBeggining_d() {
//        return beggining_d;
//    }
//
//    public void setBeggining_d(Date beggining_d) {
//        this.beggining_d = beggining_d;
//    }

    public Integer getExpert() {
        return expert;
    }

    public void setExpert(Integer expert) {
        this.expert = expert;
    }

//    public Date getExpert_d() {
//        return expert_d;
//    }
//
//    public void setExpert_d(Date expert_d) {
//        this.expert_d = expert_d;
//    }

    public Integer getElite() {
        return elite;
    }

    public void setElite(Integer elite) {
        this.elite = elite;
    }

//    public Date getElite_d() {
//        return elite_d;
//    }
//
//    public void setElite_d(Date elite_d) {
//        this.elite_d = elite_d;
//    }

    public Integer getStardom() {
        return stardom;
    }

    public void setStardom(Integer stardom) {
        this.stardom = stardom;
    }

//    public Date getStardom_d() {
//        return stardom_d;
//    }
//
//    public void setStardom_d(Date stardom_d) {
//        this.stardom_d = stardom_d;
//    }

    public Integer getBorn() {
        return born;
    }

    public void setBorn(Integer born) {
        this.born = born;
    }

//    public Date getBorn_d() {
//        return born_d;
//    }
//
//    public void setBorn_d(Date born_d) {
//        this.born_d = born_d;
//    }

    public Integer getLegend() {
        return legend;
    }

    public void setLegend(Integer legend) {
        this.legend = legend;
    }

//    public Date getLegend_d() {
//        return legend_d;
//    }
//
//    public void setLegend_d(Date legend_d) {
//        this.legend_d = legend_d;
//    }

    public Integer getVictorious() {
        return victorious;
    }

    public void setVictorious(Integer victorious) {
        this.victorious = victorious;
    }

//    public Date getVictorious_d() {
//        return victorious_d;
//    }
//
//    public void setVictorious_d(Date victorious_d) {
//        this.victorious_d = victorious_d;
//    }

    public Integer getReady() {
        return ready;
    }

    public void setReady(Integer ready) {
        this.ready = ready;
    }
//
//    public Date getReady_d() {
//        return ready_d;
//    }
//
//    public void setReady_d(Date ready_d) {
//        this.ready_d = ready_d;
//    }

    public Integer getSpy() {
        return spy;
    }

    public void setSpy(Integer spy) {
        this.spy = spy;
    }

//    public Date getSpy_d() {
//        return spy_d;
//    }
//
//    public void setSpy_d(Date spy_d) {
//        this.spy_d = spy_d;
//    }

    public Integer getCurious() {
        return curious;
    }

    public void setCurious(Integer curious) {
        this.curious = curious;
    }

//    public Date getCurious_d() {
//        return curious_d;
//    }
//
//    public void setCurious_d(Date curious_d) {
//        this.curious_d = curious_d;
//    }

    public AllAchievments() {}

    @Exclude
    public String getId() {
        return id;
    }

    public void saveDB(final DatabaseReference.CompletionListener... completionListener ){

        //DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("achievments");

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(getId())) { //exists
                    internalUpdateDB();
                } else { //dont exists
                    internalSaveDB();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        if (id != null) {
//            firebase = firebase.child("achievments").child(getOwner()).child( getId() );
//
//            if( completionListener.length == 0 ){
//                firebase.setValue(this);
//            }
//            else{
//                firebase.setValue(this, completionListener[0]);
//            }
//        }
    }

    public void internalSaveDB(final DatabaseReference.CompletionListener... completionListener){
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();

        if (id == null) {
            id = firebase.child("achievments").child(getOwner()).push().getKey();
        }

        firebase = firebase.child("achievments").child( getId() ); //player id

        if( completionListener.length == 0 ){
            firebase.setValue(this);
        }
        else{
            firebase.setValue(this, completionListener[0]);
        }

    }


    private void internalUpdateDB( DatabaseReference.CompletionListener... completionListener ){

        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("achievments").child( getId() );

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




//    public void saveDB(final DatabaseReference.CompletionListener... completionListener ){
//
//        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
//
//        if (id == null) {
//            id = firebase.child("achievments").child(getOwner()).push().getKey();
//        }
//
//        firebase = firebase.child("achievments").child(getOwner()).child( getId() );
//
//        if( completionListener.length == 0 ){
//            firebase.setValue(this);
//        }
//        else{
//            firebase.setValue(this, completionListener[0]);
//        }
//    }

//    public void updateDB( DatabaseReference.CompletionListener... completionListener ){
//
//        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("achievments").child(getOwner()).child( getId() );
//
//        Map<String, Object> map = new HashMap<>();
//        setDataInMap(map);
//
//        if( map.isEmpty() ){
//            return;
//        }
//
//        if( completionListener.length > 0 ){
//            firebase.updateChildren(map, completionListener[0]);
//        }
//        else{
//            firebase.updateChildren(map);
//        }
//    }


    private void setDataInMap( Map<String, Object> map ) {

        if( getBeggining() != null ){
            map.put( "beggining", getBeggining() );
        }

//        if( getBeggining_d() != null ){
//            map.put( "beggining_d", getBeggining_d() );
//        }

        //----------

        if( getExpert() != null ){
            map.put( "expert", getExpert() );
        }

//        if( getExpert_d() != null ){
//            map.put( "expert_d", getExpert_d() );
//        }

        //----------

        if( getElite() != null ){
            map.put( "elite", getElite() );
        }

//        if( getElite_d() != null ){
//            map.put( "elite_d", getElite_d() );
//        }

        //----------

        if( getStardom() != null ){
            map.put( "stardom", getStardom() );
        }

//        if( getStardom_d() != null ){
//            map.put( "stardom_d", getStardom_d() );
//        }

        //----------

        if( getBorn() != null ){
            map.put( "born", getBorn() );
        }

//        if( getBorn_d() != null ){
//            map.put( "born_d", getBorn_d() );
//        }

        //----------

        if( getLegend() != null ){
            map.put( "legend", getLegend() );
        }

//        if( getLegend_d() != null ){
//            map.put( "legend_d", getLegend_d() );
//        }

        //----------

        if( getVictorious() != null ){
            map.put( "victorious", getVictorious() );
        }

//        if( getVictorious_d() != null ){
//            map.put( "victorious_d", getVictorious_d() );
//        }

        //----------

        if( getReady() != null ){
            map.put( "ready", getReady() );
        }

//        if( getReady_d() != null ){
//            map.put( "ready_d", getReady_d() );
//        }

        //----------

        if( getSpy() != null ){
            map.put( "spy", getSpy() );
        }

//        if( getSpy_d() != null ){
//            map.put( "spy_d", getSpy_d() );
//        }

        //----------

        if( getCurious() != null ){
            map.put( "curious", getCurious() );
        }

//        if( getCurious_d() != null ){
//            map.put( "curious_d", getCurious_d() );
//        }
    }
}
