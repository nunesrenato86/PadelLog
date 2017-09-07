package com.renatonunes.padellog.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koushikdutta.ion.Ion;
import com.renatonunes.padellog.R;
import com.renatonunes.padellog.domain.Academy;
import com.renatonunes.padellog.domain.Player;
import com.renatonunes.padellog.domain.util.ImageFactory;

import java.util.ArrayList;

/**
 * Created by renatonunes on 05/09/17.
 */

public class RankingAdapter extends RecyclerView.Adapter<RankingViewHolder> {
    Context context;
    ArrayList<Player> players;
    Boolean mIsReadOnly;
    Boolean mIsPickingPlayer;
    String mRankingFilter;

    public RankingAdapter(Context context, ArrayList<Player> players, Boolean isReadOnly,
                          Boolean isPickingPlayer, String rankingFilter) {
        this.players = players;
        this.context = context;
        this.mIsReadOnly = isReadOnly;
        this.mIsPickingPlayer = isPickingPlayer;
        this.mRankingFilter = rankingFilter;
    }

    @Override
    public RankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_ranking, parent, false);

        RankingViewHolder viewHolder = new RankingViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RankingViewHolder holder, int position) {
        holder.currentPlayer = players.get(position);

        holder.position.setText(String.valueOf(position + 1).concat("º"));

        if (holder.currentPlayer.isImgFirebase()) {

            Ion.with(holder.playerImage)
                    .placeholder(R.drawable.no_photo)
                    .load(holder.currentPlayer.getPhotoUrl());

        } else if (holder.currentPlayer.isImgStrValid()) {
            holder.playerImage.setImageBitmap(ImageFactory.imgStrToImage(holder.currentPlayer.getImageStr()));
        }else{
            holder.playerImage.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        }

        switch(mRankingFilter) {
            case "totalChampionship":
                holder.imgTrophy.setImageResource(R.drawable.all_champs);
                holder.number.setText(String.valueOf(holder.currentPlayer.getTotalChampionship()));
                break;
            case "totalFirstPlace":
                holder.imgTrophy.setImageResource(R.drawable.trophy_gold_32);
                holder.number.setText(String.valueOf(holder.currentPlayer.getTotalFirstPlace()));
                break;
            default:
                holder.imgTrophy.setImageResource(R.drawable.trophy_silver_32);
                holder.number.setText(String.valueOf(holder.currentPlayer.getTotalSecondPlace()));
                break;
        }

        holder.playerName.setText(players.get(position).getName());

        holder.isReadOnly = mIsReadOnly;
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}
