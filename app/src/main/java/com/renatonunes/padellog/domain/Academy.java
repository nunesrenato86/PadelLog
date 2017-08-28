package com.renatonunes.padellog.domain;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by renatonunes on 26/07/17.
 */

public class Academy extends MyMapItem{

    private Context context;
    private String id;
    private String name;
    private String photoUrl;
    private String place;
    private String phone;
    private String email;
    private Double lat;
    private Double lng;
    private Uri photoUriDownloaded;
    private Boolean verified;
    private String verified_name;

    public Academy() {}

    public Uri getPhotoUriDownloaded() {
        return photoUriDownloaded;
    }

    @Exclude
    public void setPhotoUriDownloaded(Uri photoUriDownloaded) {
        this.photoUriDownloaded = photoUriDownloaded;
    }

    @Exclude
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getVerified_name() {
        //return verified_name;
        if (this.verified) {
            return '1' + this.name;
        } else {
            return '0' + this.name;
        }
    }

//    public void setVerified_name(String verified_name) {
//        if (this.verified) {
//            this.verified_name = '1' + this.name;
//        } else {
//            this.verified_name = '0' + this.name;
//        }
//    }

    @Exclude
    @Override
    public LatLng getPosition() {

        LatLng latLng = new LatLng(this.getLat(), this.getLng());

        return latLng;
    }

    public void saveDB(final DatabaseReference.CompletionListener... completionListener ){

        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();

        if (id == null) {
            id = firebase.child("academies").push().getKey();
        }

        firebase = firebase.child("academies").child( getId() );

        if( completionListener.length == 0 ){
            firebase.setValue(this);
        }
        else{
            firebase.setValue(this, completionListener[0]);
        }
    }

    public void updateDB( DatabaseReference.CompletionListener... completionListener ){

        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("academies").child( getId() );

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

    private void setDataInMap( Map<String, Object> map ) {

        if( getEmail() != null ){
            map.put( "email", getEmail() );
        }

        if( getVerified_name() != null ){
            map.put( "verified_name", getVerified_name() );
        }

        if( getPhone() != null ){
            map.put( "phone", getPhone() );
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

        if( getPlace() != null ){
            map.put( "place", getPlace() );
        }

        if( getVerified() != null ){
            map.put( "verified", getVerified() );
        }

        setPhotoUrlInMap(map);

    }

    @Exclude
    public boolean isImgFirebase(){
        return ((this.getPhotoUrl() != null) && (this.getPhotoUrl().contains("firebasestorage")));
    }

}
