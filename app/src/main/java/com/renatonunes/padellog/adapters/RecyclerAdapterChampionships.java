package com.renatonunes.padellog.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.renatonunes.padellog.R;
import com.renatonunes.padellog.domain.Championship;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);

        ViewHolderChampionship viewHolder = new ViewHolderChampionship(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderChampionship holder, int position) {
//        holder.championshipTitle.setText(titles[position]);
//        holder.championshipDetail.setText(details[position]);
//        holder.championshipImage.setImageResource(images[position]);

        String name = championships.get(position).getName();
        holder.championshipTitle.setText(name);

        String partner = championships.get(position).getPartner();
        holder.championshipDetail.setText(partner);

        String imgStr = championships.get(position).getImageStr();

        if (((imgStr != null)) && (imgStr != "")){
            holder.championshipImage.setImageBitmap(imgStrToImage(imgStr));
        }else
            holder.championshipImage.setBackgroundResource(R.drawable.no_photo);

        //holder.championshipImage.setBackgroundResource(R.drawable.fotopadel);
    }

    @Override
    public int getItemCount() {
        return championships.size();
    }

    private Bitmap imgStrToImage(String imgStr){
        byte[] imageAsBytes = Base64.decode(imgStr.getBytes(), Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
