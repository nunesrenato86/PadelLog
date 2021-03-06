package com.renatonunes.padellog.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.renatonunes.padellog.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Renato on 02/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Championship extends MyMapItem{//implements ClusterItem {

    private Context context;
    private String id;
    private String name;
    private String partner;
    private String owner;
    private String imageStr;
    private Long initialDate;
    private String photoUrl;
    private Long finalDate;
    private Long dateSort;
    private Integer category;
    private String place;
    private Double lat;
    private Double lng;
    private Integer result;
    private Match lastMatch;
    private Player player;
    private String trophyUrl;

    private Integer win;
    private Integer loss;
    private Double ratio;

    private Bitmap markerBitmap;

    private Uri photoUriDownloaded;

    public Uri getPhotoUriDownloaded() {
        return photoUriDownloaded;
    }

    @Exclude
    public void setPhotoUriDownloaded(Uri photoUriDownloaded) {
        this.photoUriDownloaded = photoUriDownloaded;
    }

    @Exclude
    public Bitmap getMarkerBitmap() {
        return markerBitmap;
    }

    public void setMarkerBitmap(Bitmap markerBitmap) {
        this.markerBitmap = markerBitmap;
    }

    public Championship() {}

    public Integer getWin() {
        setWinIfNull();

        return win;
    }

    public void setWin(Integer win) {
        this.win = win;
    }

    public Integer getLoss() {
        setLossIfNull();

        return loss;
    }

    public void setLoss(Integer loss) {
        this.loss = loss;
    }

    public Double getRatio() {
        setRatioIfNull();

        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getTrophyUrl() {
        return trophyUrl;
    }

    public void setTrophyUrl(String trophyUrl) {
        this.trophyUrl = trophyUrl;
    }

    public Integer getResult() {
        if (this.result == null){
            this.result = -1;
        }

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

    //@Exclude
    public String getImageStr() {
        return imageStr;
    }

    //@Exclude
    public void setImageStr(String imageStr) {
        //if (imageStr == null) { //so seto se for null, pois troquei pro storage
            this.imageStr = imageStr;
        //}
    }

    public Long getInitialDate() {return initialDate;}

    public void setInitialDate(Long initialDate) {this.initialDate = initialDate;}

    public Long getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Long finalDate) {

        this.finalDate = finalDate;
        setDateSort(finalDate);

    }

    public Integer getCategory() {return category;}

    public void setCategory(Integer category) {this.category = category;}

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

    public Long getDateSort() {
        return dateSort;
    }

    public void setDateSort(Long dateSort) {
        this.dateSort = - 1 * dateSort;
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
        if (this.result == null){
            this.result = -1;
        }

        switch (this.result) {
            case 0:
                return context.getResources().getString(R.string.round_draw);
            case 1:
                return context.getResources().getString(R.string.round_64);
            case 2:
                return context.getResources().getString(R.string.round_32);
            case 3:
                return context.getResources().getString(R.string.round_16);
            case 4:
                return context.getResources().getString(R.string.round_8);
            case 5:
                return context.getResources().getString(R.string.round_4);
            case 6:
                return context.getResources().getString(R.string.round_semi);
            case 7:
                return context.getResources().getString(R.string.result_name_vice);
            case 8:
                return context.getResources().getString(R.string.result_name_champion);
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
    public String getInitialDateStr(){
        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(initialDate);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return  (day < 10 ? "0" + day : day) + "/" +
                (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "/" +
                year;
    }

    @Exclude
    public String getFinalDateStr(){
        Calendar c = Calendar.getInstance();

        //c.setTimeInMillis(-1 * finalDate);
        c.setTimeInMillis(finalDate);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return  (day < 10 ? "0" + day : day) + "/" +
                (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "/" +
                year;
    }

    @Exclude
    public Player getPlayer() {
        return player;
    }

    @Exclude
    public void setPlayer(Player player) {
        this.player = player;
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

        if (id == null) {
            id = firebase.child("championships").child(getOwner()).push().getKey();
        }

        firebase = firebase.child("championships").child(getOwner()).child( getId() );

        if( completionListener.length == 0 ){
            firebase.setValue(this);
        }
        else{
            firebase.setValue(this, completionListener[0]);
        }

        this.getPlayer().incTotalChampionship();
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

        //if (this.result > 5) { //8 champ, 7 vice, 5 semi to dec one champ or vice
            this.getPlayer().updateChampionshipsCount();
        //}
        //this.getPlayer().updateChampionshipsCount(true, this.result); //ver aqui quando deleto uma partida aqui nao ta inserindo
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

    private void setPhotoUrlInMap( Map<String, Object> map ) {
        //if( getPhotoUrl() != null ){
        map.put( "photoUrl", getPhotoUrl() );
        //}
    }

    private void setTrophyUrlInMap( Map<String, Object> map ) {
        //if( getPhotoUrl() != null ){
        map.put( "trophyUrl", getTrophyUrl() );
        //}
    }

    private void setDataInMap( Map<String, Object> map ) {
        //if( getImageStr() != null ){
            map.put( "imageStr", getImageStr() );
        //}

        if( getCategory() != null ){
            map.put( "category", getCategory() );
        }

        if( getFinalDate() != null ){
            map.put( "finalDate", getFinalDate() );
        }

        if( getDateSort() != null ){
            map.put( "dateSort", getDateSort() );
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

        if( getWin() != null ){
            map.put( "win", getWin());
        }

        if( getLoss() != null ){
            map.put( "loss", getLoss());
        }

        if( getRatio() != null ){
            map.put( "ratio", getRatio());
        }

        setPhotoUrlInMap(map);

        setTrophyUrlInMap(map);

//        if( getResult() != null ){
//            map.put( "result", getResult() );
//        }
    }

    private void setWinIfNull() {
        if( this.win == null ){
            this.win = 0;
        }
    }

    private void setLossIfNull() {
        if( this.loss == null ){
            this.loss = 0;
        }
    }

    private void setRatioIfNull() {
        if( this.ratio == null ){
            this.ratio = 0.0;
        }
    }

    @Exclude
    @Override
    public LatLng getPosition() {

        LatLng latLng = new LatLng(this.getLat(), this.getLng());

        return latLng;
    }

    @Exclude
    public String getCategoryStr() {
        switch(this.category) {
            case 0: return context.getResources().getString(R.string.category_pro);
            case 1: return context.getResources().getString(R.string.category_open);
            case 2: return context.getResources().getString(R.string.category_2nd);
            case 3: return context.getResources().getString(R.string.category_3th);
            case 4: return context.getResources().getString(R.string.category_4th);
            case 5: return context.getResources().getString(R.string.category_5th);
            case 6: return context.getResources().getString(R.string.category_6th);
            case 7: return context.getResources().getString(R.string.category_7th);

            case 8: return context.getResources().getString(R.string.category_3035a);
            case 9: return context.getResources().getString(R.string.category_3035b);
            case 10: return context.getResources().getString(R.string.category_3035c);

            case 11: return context.getResources().getString(R.string.category_4045a);
            case 12: return context.getResources().getString(R.string.category_4045b);
            case 13: return context.getResources().getString(R.string.category_4045c);

            case 14: return context.getResources().getString(R.string.category_5055a);
            case 15: return context.getResources().getString(R.string.category_5055b);
            case 16: return context.getResources().getString(R.string.category_5055c);

            case 17: return context.getResources().getString(R.string.category_mixedA);
            case 18: return context.getResources().getString(R.string.category_mixedB);
            case 19: return context.getResources().getString(R.string.category_mixedC);
            case 20: return context.getResources().getString(R.string.category_mixedD);

            case 21: return context.getResources().getString(R.string.category_sub12);
            case 22: return context.getResources().getString(R.string.category_sub14);
            case 23: return context.getResources().getString(R.string.category_sub16);
            case 24: return context.getResources().getString(R.string.category_sub18);
            case 25: return context.getResources().getString(R.string.category_sub20);

        }
        return context.getResources().getString(R.string.category_other);
    }

    @Exclude
    public boolean isImgFirebase(){
        return ((this.getPhotoUrl() != null) && (this.getPhotoUrl().contains("firebasestorage")));
    }

    @Exclude
    public boolean haveTrophy(){
        return (this.getTrophyUrl() != null);
    }

    @Exclude
    public boolean isImgStrValid(){
        return (this.getImageStr() != null) && (!this.getImageStr().isEmpty());
    }

    public void incWin(){
        setWinIfNull();

        this.win = this.win + 1;

        updateTotalWin();

        this.getPlayer().incWin();
    }

    public void incLoss(){
        setLossIfNull();

        this.loss = this.loss + 1;

        updateTotalLoss();

        this.getPlayer().incLoss();
    }

    public void decWin(Integer count){
        setWinIfNull();

        this.win = this.win - count;

        if (this.win < 0){
            this.win = 0;
        }

        updateTotalWin();

        this.getPlayer().decWin(count);
    }

    public void decLoss(Integer count){
        setLossIfNull();

        this.loss = this.loss - count;

        if (this.loss < 0){
            this.loss = 0;
        }

        updateTotalLoss();

        this.getPlayer().decLoss(count);
    }

    private void updateTotalWin(){
        Map<String, Object> mResult = new HashMap<String, Object>();

        mResult.put("win", this.win);

        calcRatio();

        mResult.put("ratio", this.ratio);

        FirebaseDatabase.getInstance().getReference().child("championships")
                .child(this.getOwner())
                .child(getId())
                .updateChildren(mResult);
    }

    private void calcRatio(){
        setWinIfNull();
        setLossIfNull();

        Integer total = this.win + this.loss;

        if (total > 0){
            this.ratio = ((double)this.win / ((double)total)) * 100.00;
        }else{
            this.ratio = 0.00;
        }
    }

    private void updateTotalLoss(){
        Map<String, Object> mResult = new HashMap<String, Object>();

        mResult.put("loss", this.loss);

        calcRatio();

        mResult.put("ratio", this.ratio);

        FirebaseDatabase.getInstance().getReference().child("championships")
                .child(this.getOwner())
                .child(getId())
                .updateChildren(mResult);
    }
}
