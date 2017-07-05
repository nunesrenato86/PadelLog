package com.renatonunes.padellog.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.renatonunes.padellog.R;
import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Renato on 04/08/2016.
 */
public class ChampionshipListAdapter extends RecyclerView.Adapter<ChampionshipListViewHolder> {

    Context context;
    ArrayList<Championship> championships;
    Boolean mIsReadOnly;
    String mFirstName;

    public ChampionshipListAdapter(Context context, ArrayList<Championship> championships, Boolean isReadOnly,
                                   String firstName) {
        this.championships = championships;
        this.context = context;
        this.mIsReadOnly = isReadOnly;
        this.mFirstName = firstName;
    }

    @Override
    public ChampionshipListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_championship, parent, false);

        ChampionshipListViewHolder viewHolder = new ChampionshipListViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ChampionshipListViewHolder holder, int position) {
        holder.currentChampionship = championships.get(position);

        holder.championshipTitle.setText(championships.get(position).getName());
        holder.championshipDetail.setText(championships.get(position).getInitialDateStr()
                + " até "
                + championships.get(position).getFinalDateStr());

        if (championships.get(position).getResult() == 8){
            holder.championshipTrophyImage.setVisibility(View.VISIBLE);
            holder.championshipTrophyImage.setImageResource(R.drawable.trophy_gold);
        }else if (championships.get(position).getResult() == 7){
            holder.championshipTrophyImage.setVisibility(View.VISIBLE);
            holder.championshipTrophyImage.setImageResource(R.drawable.trophy_silver);
        }else
            holder.championshipTrophyImage.setVisibility(View.INVISIBLE);

        holder.isReadOnly = mIsReadOnly;
        holder.playerToListFirstName = mFirstName;

        Picasso.with(holder.championshipImage.getContext()).cancelRequest(holder.championshipImage);

        holder.championshipImage.setBackgroundResource(R.drawable.no_photo);
        holder.championshipImage.setImageDrawable(context.getResources().getDrawable(R.drawable.no_photo));

        if (holder.currentChampionship.isImgFirebase()){

            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference httpsReference = storage.getReferenceFromUrl(holder.currentChampionship.getPhotoUrl());

            httpsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    holder.currentChampionship.setPhotoUriDownloaded(uri);

                    Picasso.with(context)
                            .load(uri.toString())
                            .placeholder(R.drawable.no_photo)
                            .into(holder.championshipImage);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

        }else if (holder.currentChampionship.isImgStrValid()){
            holder.championshipImage.setImageBitmap(ImageFactory.imgStrToImage(holder.currentChampionship.getImageStr()));

        }else{
            holder.championshipImage.setImageBitmap(null);
            //holder.championshipImage.setBackgroundResource(R.drawable.no_photo);
            holder.championshipImage.setImageDrawable(context.getResources().getDrawable(R.drawable.no_photo));
        }

    }

    @Override
    public int getItemCount() {
        return championships.size();
    }

}
