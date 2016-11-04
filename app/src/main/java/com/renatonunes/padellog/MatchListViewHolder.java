/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.renatonunes.padellog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.Match;

public class MatchListViewHolder extends RecyclerView.ViewHolder {

    private final Context context;
//    public int currentItem;

    public Match currentMatch;
    public Championship currentChampionship;

//    public String currentKey;
    public ImageView matchImage;
    public TextView matchRound;
    public TextView matchScore;
    public Boolean isReadOnly;

    public MatchListViewHolder(View itemView) {
        super(itemView);

        matchImage = (ImageView)itemView.findViewById(R.id.item_match_image);
        matchRound = (TextView)itemView.findViewById(R.id.item_match_round);
        matchScore = (TextView)itemView.findViewById(R.id.item_match_score);

        context = itemView.getContext();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
//                int position = getAdapterPosition();
//
//                Snackbar.make(v, "Click on item " + position,
//                        Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                MatchInfoActivity.start(context, currentMatch, currentChampionship, isReadOnly);

            }
        });

    }



}
