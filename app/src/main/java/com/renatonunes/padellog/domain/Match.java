package com.renatonunes.padellog.domain;

import android.content.Context;
import android.content.res.Resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.renatonunes.padellog.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Renato on 11/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Match {
    private Context context;
    private String id;
    private Integer round;
    private String opponentDrive;
    private String opponentBackdrive;
    private String owner;
    private String imageStr;
    private String scoreStr;
    private String team1;
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
    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
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

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
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

    @Exclude
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Exclude
    public String getTeam2(){
        return getOpponentDrive() + " / " + getOpponentBackdrive();
    }

    @Exclude
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

    @Exclude
    public String getRoundStr(){
        Resources res = this.context.getResources();

        switch(this.round) {
            case 0: return res.getString(R.string.round_draw);
            case 1: return res.getString(R.string.round_64);
            case 2: return res.getString(R.string.round_32);
            case 3: return res.getString(R.string.round_16);
            case 4: return res.getString(R.string.round_8);
            case 5: return res.getString(R.string.round_4);
            case 6: return res.getString(R.string.round_semi);
            case 7: return res.getString(R.string.round_final);
//				case 2: return MatchListFragment.newInstance();
        }
        return "";
    }

    @Exclude
    public Integer getResult(){
        //if its the final, need to check if its a victory or not
        if (this.round == 7){ //its the final match
            if (this.isVictory()){
                return 8; //champion
            }else{
                return this.round;
            }
        } else{
            return this.round;
        }
    }

    @Exclude
    public Boolean isVictory(){
        Boolean has3sets = (this.getSet3Score1() != this.getSet3Score2())
                && (this.getSet3Score1() > 0 || this.getSet3Score2() > 0);

        Boolean has2sets = (this.getSet2Score1() != this.getSet2Score2())
                && (this.getSet2Score1() > 0 || this.getSet2Score2() > 0);

        if (has3sets) {
            return this.getSet3Score1() > this.getSet3Score2();
        } else if (has2sets){
            return this.getSet2Score1() > this.getSet2Score2();
        } else
            return this.getSet1Score1() > this.getSet1Score2();
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

    public void updateDB( DatabaseReference.CompletionListener... completionListener ){

        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("matches").child(getOwner()).child( getId() );

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

        if( getOpponentBackdrive() != null ){
            map.put( "opponentBackdrive", getOpponentBackdrive() );
        }

        if( getOpponentDrive() != null ){
            map.put( "opponentDrive", getOpponentDrive() );
        }

        if( getRound() != null ){
            map.put( "round", getRound() );
        }

        if( getSet1Score1() != null ){
            map.put( "set1Score1", getSet1Score1() );
        }

        if( getSet1Score2() != null ){
            map.put( "set1Score2", getSet1Score2() );
        }

        if( getSet2Score1() != null ){
            map.put( "set2Score1", getSet2Score1() );
        }

        if( getSet2Score2() != null ){
            map.put( "set2Score2", getSet2Score2() );
        }

        if( getSet3Score1() != null ){
            map.put( "set3Score1", getSet3Score1() );
        }

        if( getSet3Score2() != null ){
            map.put( "set3Score2", getSet3Score2() );
        }
    }
}
