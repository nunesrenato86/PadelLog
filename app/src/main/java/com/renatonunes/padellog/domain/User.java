package com.renatonunes.padellog.domain;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.renatonunes.padellog.domain.util.LibraryClass;

/**
 * Created by Renato on 26/07/2016.
 */
public class User {
    public static String TOKEN = "com.renatonunes.padellog.domain.User.TOKEN";

    private String id;
    private String name;
    private String email;
    private String password;
    private String photoUrl;
    private String newPassword;

    public User(){}

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void saveTokenSP(Context context, String token ){
        LibraryClass.saveSP( context, TOKEN, token );
    }
    public String getTokenSP(Context context ){
        return( LibraryClass.getSP( context, TOKEN) );
    }

    public void saveDB(){
        //Firebase firebase = LibraryClass.getFirebase();
        FirebaseDatabase firebaseDatabase = LibraryClass.getFirebaseDatabase();

        DatabaseReference myRef = firebaseDatabase.getReference();

        //firebaseDatabase = firebaseDatabase.child("users").child(getId());

        String ID = this.getId();
        //setPassword(null);
        setId(null);
        //firebase.setValue(this);
        myRef.child("users").child(ID).setValue(this);
    }
}
