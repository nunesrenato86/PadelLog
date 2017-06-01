package com.renatonunes.padellog.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.renatonunes.padellog.MainActivity;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.LibraryClass;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.renatonunes.padellog.MainActivity.playerImageHasChanged;

/**
 * Created by Renato on 26/07/2016.
 */
public class Player extends MyMapItem{ //implements ClusterItem{
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

    //private ChampionshipSummary championshipSummary;
    private long totalChampionship;
    private long totalFirstPlace;
    private long totalSecondPlace;

    private Bitmap markerBitmap;

    public Player(){}

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    public long getTotalChampionship(){return this.totalChampionship;}

    public void setTotalChampionship(long totalChampionship) {
        this.totalChampionship = totalChampionship;
    }

    //@Exclude
    public long getTotalFirstPlace(){return this.totalFirstPlace;}

    @Exclude
    public void setTotalFirstPlace(long totalFirstPlace) {
        this.totalFirstPlace = totalFirstPlace;
    }

    //@Exclude
    public long getTotalSecondPlace(){return this.totalSecondPlace;}

    @Exclude
    public void setTotalSecondPlace(long totalSecondPlace) {
        this.totalSecondPlace = totalSecondPlace;
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

    @Exclude
    public Bitmap getMarkerBitmap() {
        return markerBitmap;
    }

    public void setMarkerBitmap(Bitmap markerBitmap) {
        this.markerBitmap = markerBitmap;
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
    @Override
    public LatLng getPosition() {

        LatLng latLng = new LatLng(this.getLat(), this.getLng());

        return latLng;
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

    public void saveDB(final DatabaseReference.CompletionListener... completionListener ){

        initDataAux();

        final DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("players").child( getId() );

        if (completionListener.length == 0) {
            firebase.setValue(this);
        } else {
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

    public Boolean isSocialNetworkLogged( Context context ){
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

    private void setPhotoUrlInMap( Map<String, Object> map ) {
        //if( getPhotoUrl() != null ){
            map.put( "photoUrl", getPhotoUrl() );
        //}
    }

    public void setEmailIfNull(String email) {
        if( this.email == null ){
            this.email = email;
        }
    }

    public boolean isImgStrValid(){
        return (this.getImageStr() != null) && (!this.getImageStr().isEmpty());
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

    /*
    public void updateDB(final DatabaseReference.CompletionListener... completionListener ){

        final DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("players").child( getId() );

        final Map<String, Object> map = new HashMap<>();
        setNameInMap(map);
        setEmailInMap(map);
        setLatInMap(map);
        setLngInMap(map);
        setImageStrInMap(map);
        setCategoryInMap(map);
        setIsPublicInMap(map);
        setPlaceInMap(map);
        //setPhotoUrlInMap(map);

        if (this.getImageStr() != null){ //have profile img as base64 string
            Bitmap bitmap = ImageFactory.imgStrToImage(this.getImageStr());

            if (bitmap != null) { //when user cancel the action and click in save

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                byte[] bytes = baos.toByteArray();

                FirebaseStorage storage = FirebaseStorage.getInstance();

                // Create a storage reference from our app
                StorageReference storageRef = storage.getReferenceFromUrl("gs://padellog-b49b1.appspot.com");

                String Id = "images/players/";

                Id = Id.concat(this.getId()).concat(".jpg");

                StorageReference playersRef = storageRef.child(Id);

                UploadTask uploadTask = playersRef.putBytes(bytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        setPhotoUrl(downloadUrl.toString());
                        setPhotoUrlInMap(map);

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
                });
            } else {
                setPhotoUrlInMap(map);

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
        }else{
            setPhotoUrlInMap(map);

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
    */


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
        setPhotoUrlInMap(map);

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


//    public void updateChampionshipsCount(Boolean isInsertingMatch, int result){
//
//
//        //fazer isso funcionar
//
//        //8 champion
//        //7 vice
//        int valor = 0;
//
//        if (isInsertingMatch){
//            valor = 1;
//        }else{
//            valor = -1;
//        }
//
//        if (valor != 0) {
//            Map<String, Object> mResult = new HashMap<String, Object>();
//
//            if (result == 8) {
//                mResult.put("totalFirstPlace", this.getTotalFirstPlace() + valor);
//            } else if (result == 7) {
//                mResult.put("totalSecondPlace", this.getTotalSecondPlace() + valor);
//            }
//
//            FirebaseDatabase.getInstance().getReference().child("players")
//                    .child(getId())
//                    .updateChildren(mResult);
//        }
//
//    }

    public void incTotalChampionship(){
        this.totalChampionship = this.totalChampionship + 1;

        updateTotalChampionships();
    }

    public void decTotalChampionship(){
        this.totalChampionship = this.totalChampionship - 1;

        if (this.totalChampionship < 0){
            this.totalChampionship = 0;
        }

        updateTotalChampionships();
    }

    private void updateTotalChampionships(){
        Map<String, Object> mResult = new HashMap<String, Object>();

        mResult.put("totalChampionship", this.totalChampionship);

        FirebaseDatabase.getInstance().getReference().child("players")
                .child(getId())
                .updateChildren(mResult);
    }


//    public void countChamps(){
//
//
//        FirebaseDatabase.getInstance().getReference().child("championships").child(this.id).addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.e("RNN", "Total: " + dataSnapshot.getChildrenCount());
//
//                        //count[0] = dataSnapshot.getChildrenCount();
//                        getPlayersUpdates(dataSnapshot);
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w("RNN", "getUser:onCancelled", databaseError.toException());
//                    }
//                });
//    }
//
    public void countFirstPlace(){

// tornar isso privado e usar no metodo updateplayercount, nas properties com exclude, e tirar o que foi
        //feito pra gravar


        FirebaseDatabase.getInstance().getReference().child("championships").child(this.id)
                .orderByChild("result").startAt(8).endAt(8) //firstplace
                .addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("RNN", "Campeao: " + dataSnapshot.getChildrenCount());
                        //count[0] = dataSnapshot.getChildrenCount();
                        updateFirstPlace(dataSnapshot.getChildrenCount());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("RNN", "getUser:onCancelled", databaseError.toException());
                    }
                });

    }
//
    public void countSecondPlace(){

// tornar isso privado e usar no metodo updateplayercount, nas properties com exclude, e tirar o que foi
        //feito pra gravar

        FirebaseDatabase.getInstance().getReference().child("championships").child(this.id)
                .orderByChild("result").startAt(7).endAt(7) //secondplace
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.e("RNN", "Vice: " + dataSnapshot.getChildrenCount());
                                updateSecondPlace(dataSnapshot.getChildrenCount());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("RNN", "getUser:onCancelled", databaseError.toException());
                            }
                        });

    }

    public void updateChampionshipsCount(){

        //this.countChamps();
        this.countFirstPlace();
        this.countSecondPlace();
    }


//    private void getPlayersUpdates(com.google.firebase.database.DataSnapshot dataSnapshot) {
//        this.setTotalChampionship(dataSnapshot.getChildrenCount());
//    }

    private void updateFirstPlace(long count){
        Map<String, Object> mResult = new HashMap<String, Object>();

        mResult.put("totalFirstPlace", count);

        MainActivity.mPlayer.setTotalFirstPlace(count);

        FirebaseDatabase.getInstance().getReference().child("players")
                .child(getId())
                .updateChildren(mResult);
    }

    private void updateSecondPlace(long count){
        Map<String, Object> mResult = new HashMap<String, Object>();

        mResult.put("totalSecondPlace", count);

        MainActivity.mPlayer.setTotalSecondPlace(count);

        FirebaseDatabase.getInstance().getReference().child("players")
                .child(getId())
                .updateChildren(mResult);
    }

    public void makePublic(){
        Map<String, Object> mResult = new HashMap<String, Object>();

        mResult.put("isPublic", true);

        FirebaseDatabase.getInstance().getReference().child("players")
                .child(getId())
                .updateChildren(mResult);
    }

    public boolean havePlace(){
        return ((this.getLng() != 0) || (this.getLat() != 0));
    }

    public boolean canBePublic(){
        return (this.havePlace() && (!this.imageStr.equals("")));
    }

    public boolean isImgFirebase(){
        return ((this.getPhotoUrl() != null) && (this.getPhotoUrl().contains("firebasestorage")));
    }




}
