package com.renatonunes.padellog.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.koushikdutta.ion.Ion;
import com.renatonunes.padellog.R;
import com.renatonunes.padellog.domain.Academy;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by renatonunes on 26/07/17.
 */

public class AcademyListAdapter extends RecyclerView.Adapter<AcademyListViewHolder> {

    Context context;
    ArrayList<Academy> academies;
    Boolean mIsReadOnly;
    Boolean mIsPickingAcademy;

    public AcademyListAdapter(Context context, ArrayList<Academy> academies, Boolean isReadOnly,
                              Boolean isPickingAcademy) {
        this.academies = academies;
        this.context = context;
        this.mIsReadOnly = isReadOnly;
        this.mIsPickingAcademy = isPickingAcademy;
    }

    @Override
    public AcademyListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_academy, parent, false);

        AcademyListViewHolder viewHolder = new AcademyListViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AcademyListViewHolder holder, int position) {
        holder.currentAcademy = academies.get(position);

        //Picasso.with(holder.academyName.getContext()).cancelRequest(holder.academyImage);

        //holder.academyImage.setImageDrawable(context.getResources().getDrawable(R.drawable.no_photo));

        if (holder.currentAcademy.isImgFirebase()){

            Ion.with(holder.academyImage)
                    .placeholder(R.drawable.no_photo)
                    .load(holder.currentAcademy.getPhotoUrl());

//            Picasso.with(context)
//                            .load(holder.currentAcademy.getPhotoUrl())
//                            .placeholder(R.drawable.no_photo)
//                            .into(holder.academyImage);


//            FirebaseStorage storage = FirebaseStorage.getInstance();
//
//            StorageReference httpsReference = storage.getReferenceFromUrl(holder.currentAcademy.getPhotoUrl());
//
//            httpsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    holder.currentAcademy.setPhotoUriDownloaded(uri);
//
//                    Picasso.with(context)
//                            .load(uri.toString())
//                            .placeholder(R.drawable.no_photo)
//                            .into(holder.academyImage);
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle any errors
//                }
//            });

        }else{
            //holder.academyImage.setImageResource(null);
            //holder.academyImage.setBackgroundResource(R.drawable.no_photo);
            holder.academyImage.setImageResource(R.drawable.no_photo);
        }

        holder.academyName.setText(academies.get(position).getName());

        holder.isReadOnly = mIsReadOnly;
        holder.isPickingAcademy = mIsPickingAcademy;

    }

    @Override
    public int getItemCount() {
        return academies.size();
    }
}
