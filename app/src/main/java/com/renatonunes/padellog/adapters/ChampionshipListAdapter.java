package com.renatonunes.padellog.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.renatonunes.padellog.R;
import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.util.ImageFactory;

import java.util.ArrayList;

/**
 * Created by Renato on 04/08/2016.
 */
public class ChampionshipListAdapter extends RecyclerView.Adapter<ChampionshipListViewHolder> {

    Context context;
    ArrayList<Championship> championships;

    public ChampionshipListAdapter(Context context, ArrayList<Championship> championships) {
        this.championships = championships;
        this.context = context;
    }

    @Override
    public ChampionshipListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_championship, parent, false);

        ChampionshipListViewHolder viewHolder = new ChampionshipListViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChampionshipListViewHolder holder, int position) {
        holder.currentChampionship = championships.get(position);

        holder.championshipTitle.setText(championships.get(position).getName());
        holder.championshipDetail.setText(championships.get(position).getInitialDate()
                + " até "
                + championships.get(position).getFinalDate());

//        String name = championships.get(position).getName();
//        holder.championshipTitle.setText(name);

//        String currentKey = championships.get(position).getId();
//        holder.currentKey = currentKey;

//        String partner = championships.get(position).getPartner();
//        holder.championshipDetail.setText(partner);

        String imgStr = championships.get(position).getImageStr();

        if (((imgStr != null)) && (imgStr != "")){
            holder.championshipImage.setImageBitmap(ImageFactory.imgStrToImage(imgStr));
        }else {
            holder.championshipImage.setImageBitmap(null);
            holder.championshipImage.setBackgroundResource(R.drawable.no_photo);
        }

        //holder.championshipImage.setBackgroundResource(R.drawable.fotopadel);
//        holder.championshipTrophyImage.setImageDrawable(null);

        if (championships.get(position).getResult() == 8){
            holder.championshipTrophyImage.setVisibility(View.VISIBLE);
            holder.championshipTrophyImage.setImageResource(R.drawable.trophy_gold);
        }else if (championships.get(position).getResult() == 7){
            holder.championshipTrophyImage.setVisibility(View.VISIBLE);
            holder.championshipTrophyImage.setImageResource(R.drawable.trophy_silver);
        }else
            holder.championshipTrophyImage.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return championships.size();
    }

}
