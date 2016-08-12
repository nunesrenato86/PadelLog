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
public class RecyclerAdapterChampionships extends RecyclerView.Adapter<ViewHolderChampionship> {

    Context context;
    ArrayList<Championship> championships;

    public RecyclerAdapterChampionships(Context context, ArrayList<Championship> championships) {
        this.championships = championships;
        this.context = context;
    }

    @Override
    public ViewHolderChampionship onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_championship, parent, false);

        ViewHolderChampionship viewHolder = new ViewHolderChampionship(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderChampionship holder, int position) {
        holder.currentChampionship = championships.get(position);

        holder.championshipTitle.setText(championships.get(position).getName());
        holder.championshipDetail.setText(championships.get(position).getPartner());

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
    }

    @Override
    public int getItemCount() {
        return championships.size();
    }

}
