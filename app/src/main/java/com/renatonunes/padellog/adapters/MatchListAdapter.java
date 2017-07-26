/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
import com.renatonunes.padellog.R;
import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.Match;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MatchListAdapter extends RecyclerView.Adapter<MatchListViewHolder> {
	Context context;
	ArrayList<Match> matches;
    Championship mCurrentChampionship;
    boolean mIsReadOnly;
    public String mFirstName;

    public MatchListAdapter(Context context, ArrayList<Match> matches, Championship currentChampionship,
                            Boolean isReadOnly, String firstName) {
        this.matches = matches;
        this.context = context;
        this.mIsReadOnly = isReadOnly;
        this.mFirstName = firstName;
        this.mCurrentChampionship = currentChampionship;
    }

	@Override public MatchListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View v = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.card_match, parent, false);

		return new MatchListViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final MatchListViewHolder holder, int position) {
        holder.currentMatch = matches.get(position);
        holder.currentChampionship = mCurrentChampionship;

        holder.matchRound.setText(holder.currentMatch.getRoundStr());
        holder.matchOpponent.setText(holder.currentMatch.getTeam2());
        holder.matchScore.setText(holder.currentMatch.getScoreStr());

        //Ver aqui

        if (holder.currentMatch.isImgFirebase()){

            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference httpsReference = storage.getReferenceFromUrl(holder.currentMatch.getPhotoUrl());

            httpsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    holder.currentMatch.setPhotoUriDownloaded(uri);

                    Picasso.with(context).load(uri.toString()).into(holder.matchImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

        }else if (holder.currentMatch.isImgStrValid()){
            holder.matchImage.setImageBitmap(ImageFactory.imgStrToImage(holder.currentMatch.getImageStr()));
        }else{
            holder.matchImage.setImageBitmap(null);
            holder.matchImage.setBackgroundResource(R.drawable.no_photo);
        }

        holder.isReadOnly = mIsReadOnly;
        holder.firstName = mFirstName;
	}

	@Override public int getItemCount() {
		return matches.size();
	}



}

