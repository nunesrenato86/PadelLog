package com.renatonunes.padellog.domain;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.renatonunes.padellog.domain.util.LibraryClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Renato on 26/07/2016.
 */
public class Player {
    public static String TOKEN = "com.renatonunes.padellog.domain.Player.TOKEN";
    public static String PROVIDER = "com.renatonunes.padellog.domain.Player.PROVIDER";

    private String id;
    private String name;
    private String email;
    private String password;
    private String photoUrl;
    private String newPassword;

    private Double lat;
    private Double lng;
    private Integer category;
    private String imageStr;
    private String place;
    private Boolean isPublic;

    public Player(){}

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return this.lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getImageStr() {
        return imageStr;
    }

    public void setImageStr(String imageStr) {
        this.imageStr = imageStr;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Exclude
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void saveTokenSP(Context context, String token ){
        LibraryClass.saveSP( context, TOKEN, token );
    }
    public String getTokenSP(Context context ){
        return( LibraryClass.getSP( context, TOKEN) );
    }

    public void saveDB( DatabaseReference.CompletionListener... completionListener ){

        initDataAux();

        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("players").child( getId() );

        if( completionListener.length == 0 ){
            firebase.setValue(this);
        }
        else{
            firebase.setValue(this, completionListener[0]);
        }
    }

    public void contextDataDB( Context context ){
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("players").child( getId() );

        firebase.addListenerForSingleValueEvent( (ValueEventListener) context );
    }

    public void saveProviderSP(Context context, String token ){
        LibraryClass.saveSP( context, PROVIDER, token );
    }
    public String getProviderSP(Context context ){
        return( LibraryClass.getSP( context, PROVIDER) );
    }

    public boolean isSocialNetworkLogged( Context context ){
        String token = getProviderSP( context );
        return( token.contains("facebook") || token.contains("google") /*|| token.contains("twitter") || token.contains("github")*/ );
    }

    private void setNameInMap( Map<String, Object> map ) {
        if( getName() != null ){
            map.put( "name", getName() );
        }
    }

    private void setLatInMap( Map<String, Object> map ) {
        if( getLat() != null ){
            map.put( "lat", getLat() );
        }
    }

    private void setLngInMap( Map<String, Object> map ) {
        if( getLng() != null ){
            map.put( "lng", getLng() );
        }
    }

    private void setImageStrInMap( Map<String, Object> map ) {
        if( getImageStr() != null ){
            map.put( "imageStr", getImageStr() );
        }
    }

    private void setCategoryInMap( Map<String, Object> map ) {
        if( getCategory() != null ){
            map.put( "category", getCategory() );
        }
    }

    private void setIsPublicInMap( Map<String, Object> map ) {
        if( getIsPublic() != null ){
            map.put( "isPublic", getIsPublic() );
        }
    }

    public void setNameIfNull(String name) {
        if( this.name == null ){
            this.name = name;
        }
    }

    private void setEmailInMap( Map<String, Object> map ) {
        if( getEmail() != null ){
            map.put( "email", getEmail() );
        }
    }

    private void setPlaceInMap( Map<String, Object> map ) {
        if( getPlace() != null ){
            map.put( "place", getPlace() );
        }
    }

    public void setEmailIfNull(String email) {
        if( this.email == null ){
            this.email = email;
        }
    }

    private void setIsPublicIfNull() {
        if( this.isPublic == null ){
            this.isPublic = false;
        }
    }

    private void setCategoryIfNull() {
        if( this.category == null ){
            this.category = 26; //other
        }
    }

    private void setLatIfNull() {
        if( this.lat == null ){
            this.lat = 0.0;
        }
    }

    private void setLngIfNull() {
        if( this.lng == null ){
            this.lng = 0.0;
        }
    }

    private void setImageStrIfNull() {
        if( this.imageStr == null ){
            this.imageStr = "";
        }
    }

    private void setPlaceIfNull() {
        if( this.place == null ){
            this.place = "";
        }
    }

    private void initDataAux(){
        setCategoryIfNull();
        setLatIfNull();
        setLngIfNull();
        setImageStrIfNull();
        setIsPublicIfNull();
        setPlaceIfNull();
    }

    public void updateDB( DatabaseReference.CompletionListener... completionListener ){

        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("players").child( getId() );

        Map<String, Object> map = new HashMap<>();
        setNameInMap(map);
        setEmailInMap(map);
        setLatInMap(map);
        setLngInMap(map);
        setImageStrInMap(map);
        setCategoryInMap(map);
        setIsPublicInMap(map);
        setPlaceInMap(map);

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
}
