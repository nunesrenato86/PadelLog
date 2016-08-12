package com.renatonunes.padellog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Renato on 11/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Match {
    private String id;
    private String round;
    private String opponentDrive;
    private String opponentBackdrive;
    private String owner;
    private String imageStr;

    public Match() {
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

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getOpponentDrive() {
        return opponentDrive;
    }

    public void setOpponentDrive(String opponentDrive) {
        this.opponentDrive = opponentDrive;
    }

    public String getOpponentBackdrive() {
        return opponentBackdrive;
    }

    public void setOpponentBackdrive(String opponentBackdrive) {
        this.opponentBackdrive = opponentBackdrive;
    }

    public String getImageStr() {
        return imageStr;
    }

    public void setImageStr(String imageStr) {
        this.imageStr = imageStr;
    }

    public void saveDB(DatabaseReference.CompletionListener... completionListener ){
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();

        id = firebase.child("matches").child(getOwner()).push().getKey();

        firebase = firebase.child("matches").child(getOwner()).child( getId() );

        if( completionListener.length == 0 ){
            firebase.setValue(this);
        }
        else{
            firebase.setValue(this, completionListener[0]);
        }
    }
}
