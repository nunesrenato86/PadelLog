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
    private String scoreStr;
    private Integer set1Score1;
    private Integer set1Score2;
    private Integer set2Score1;
    private Integer set2Score2;
    private Integer set3Score1;
    private Integer set3Score2;

    public Match() {
    }

    public Integer getSet1Score1() {
        return set1Score1;
    }

    public void setSet1Score1(Integer set1Score1) {
        this.set1Score1 = set1Score1;
    }

    public Integer getSet1Score2() {
        return set1Score2;
    }

    public void setSet1Score2(Integer set1Score2) {
        this.set1Score2 = set1Score2;
    }

    public Integer getSet2Score1() {
        return set2Score1;
    }

    public void setSet2Score1(Integer set2Score1) {
        this.set2Score1 = set2Score1;
    }

    public Integer getSet2Score2() {
        return set2Score2;
    }

    public void setSet2Score2(Integer set2Score2) {
        this.set2Score2 = set2Score2;
    }

    public Integer getSet3Score1() {
        return set3Score1;
    }

    public void setSet3Score1(Integer set3Score1) {
        this.set3Score1 = set3Score1;
    }

    public Integer getSet3Score2() {
        return set3Score2;
    }

    public void setSet3Score2(Integer set3Score2) {
        this.set3Score2 = set3Score2;
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

    public String getScoreStr() {
        //0 X 0 3 X 2 11 X 10
        String set1 = getSet1Score1().toString()
                + " X "
                + getSet1Score2().toString();

        String set2 = getSet2Score1().toString()
                + " X "
                + getSet2Score2().toString();

        String set3 = getSet3Score1().toString()
                + " X "
                + getSet3Score2().toString();

        String result = set1;

        if (!set2.contentEquals("0 X 0")){
            result = result + " " + set2;
        }

        if (!set3.contentEquals("0 X 0")){
            result = result + " " + set3;
        }

        return result;
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
