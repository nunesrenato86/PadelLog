/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.renatonunes.padellog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.Match;
import com.renatonunes.padellog.domain.util.ImageFactory;

import java.util.ArrayList;

public class MatchListAdapter extends RecyclerView.Adapter<MatchListViewHolder> {
	Context context;
	ArrayList<Match> matches;
    Championship mCurrentChampionship;
    boolean mIsReadOnly;

    public MatchListAdapter(Context context, ArrayList<Match> matches, Championship currentChampionship, Boolean isReadOnly) {
        this.matches = matches;
        this.context = context;
        this.mIsReadOnly = isReadOnly;
        this.mCurrentChampionship = currentChampionship;
    }

	@Override public MatchListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View v = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.card_match, parent, false);

		return new MatchListViewHolder(v);
	}

	@Override
	public void onBindViewHolder(MatchListViewHolder holder, int position) {
        holder.currentMatch = matches.get(position);
        holder.currentChampionship = mCurrentChampionship;

        holder.matchRound.setText(matches.get(position).getRoundStr());
        holder.matchScore.setText(matches.get(position).getScoreStr());

        String imgStr = matches.get(position).getImageStr();

        if (((imgStr != null)) && (imgStr != "")){
            holder.matchImage.setImageBitmap(ImageFactory.imgStrToImage(imgStr));
        }else {
            holder.matchImage.setImageBitmap(null);
            holder.matchImage.setBackgroundResource(R.drawable.no_photo);
        }

        holder.isReadOnly = mIsReadOnly;
	}

	@Override public int getItemCount() {
		return matches.size();
	}



}

