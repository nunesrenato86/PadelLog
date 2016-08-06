package com.renatonunes.padellog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

/**
 * Created by Renato on 02/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Championship {

    private String id;
    private String name;
    private String partner;
    private String owner;
    private String imageStr;
    private Date initialDate;
    private Date finalDate;

    public Championship() {}

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

    public Date getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public Date getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }

    @Exclude
    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    @Exclude
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

}
