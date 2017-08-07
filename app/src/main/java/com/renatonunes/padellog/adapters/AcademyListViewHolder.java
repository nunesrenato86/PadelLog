package com.renatonunes.padellog.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.renatonunes.padellog.AcademyInfoActivity;
import com.renatonunes.padellog.R;
import com.renatonunes.padellog.domain.Academy;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by renatonunes on 26/07/17.
 */

public class AcademyListViewHolder extends RecyclerView.ViewHolder {

    private final Context context;

    public Academy currentAcademy;

    public ImageView academyImage;
    //public ImageView championshipTrophyImage;
    public TextView academyName;
    //public TextView academyPlace;
    public Boolean isReadOnly;

    public AcademyListViewHolder(View itemView) {
        super(itemView);
        academyImage = (ImageView)itemView.findViewById(R.id.item_image);

        academyName = (TextView)itemView.findViewById(R.id.item_detail);

        //academyName = (TextView)itemView.findViewById(R.id.item_title);
        //academyPlace = (TextView)itemView.findViewById(R.id.item_detail);
        //championshipTrophyImage = (ImageView)itemView.findViewById(R.id.item_image_trophy);

        context = itemView.getContext();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

//                if ((currentAcademy.isImgFirebase()) && (currentAcademy.getPhotoUriDownloaded() == null)){
//                    FirebaseStorage storage = FirebaseStorage.getInstance();
//
//                    StorageReference httpsReference = storage.getReferenceFromUrl(currentAcademy.getPhotoUrl());
//
//                    httpsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            currentAcademy.setPhotoUriDownloaded(uri);
//                            AcademyInfoActivity.start(context, currentAcademy, isReadOnly);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            // Handle any errors
//                        }
//                    });
//                }else{
//                    AcademyInfoActivity.start(context, currentAcademy, isReadOnly);
//                }

                AcademyInfoActivity.start(context, currentAcademy, isReadOnly);
            }
        });
    }

}
