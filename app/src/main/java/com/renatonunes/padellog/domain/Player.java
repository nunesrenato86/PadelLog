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

    public Player(){}

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    //backup do meu
//    public void saveDB(){
//        //Firebase firebase = LibraryClass.getFirebase();
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//
//        DatabaseReference myRef = firebaseDatabase.getReference();
//
//        String ID = this.getId();
//        this.setPassword(null);
//        setId(null);
//
//        //firebase.setValue(this);
//        myRef.child("players").child(ID).setValue(this);
//    }

    public void saveDB( DatabaseReference.CompletionListener... completionListener ){
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

    public void setEmailIfNull(String email) {
        if( this.email == null ){
            this.email = email;
        }

    }

    public void updateDB( DatabaseReference.CompletionListener... completionListener ){

        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("players").child( getId() );

        Map<String, Object> map = new HashMap<>();
        setNameInMap(map);
        setEmailInMap(map);

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
