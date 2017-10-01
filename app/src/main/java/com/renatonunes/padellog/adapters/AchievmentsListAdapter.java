package com.renatonunes.padellog.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koushikdutta.ion.Ion;
import com.renatonunes.padellog.R;
import com.renatonunes.padellog.domain.Academy;
import com.renatonunes.padellog.domain.Achievment;

import java.util.ArrayList;

/**
 * Created by renatonunes on 29/09/17.
 */

public class AchievmentsListAdapter extends RecyclerView.Adapter<AchievmentsListViewHolder> {

    Context context;
    ArrayList<Achievment> achievments;

    public AchievmentsListAdapter(Context context, ArrayList<Achievment> achievments) {
        this.achievments = achievments;
        this.context = context;
    }

    @Override
    public AchievmentsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_achievment, parent, false);

        AchievmentsListViewHolder viewHolder = new AchievmentsListViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AchievmentsListViewHolder holder, int position) {
        //holder.currentAcademy = academies.get(position);
        holder.achievmentImage.setImageResource(achievments.get(position).getImgID());

        holder.achievmentName.setText(achievments.get(position).getName());

        holder.achievmentInfo.setText(achievments.get(position).getInfo());

        //myImage.setAlpha(127); //value: [0-255]. Where 0 is fully transparent and 255 is fully opaque.
        if (achievments.get(position).isUnlocked()){
            holder.achievmentImage.setAlpha(255);
        }else{
            holder.achievmentImage.setAlpha(50);
        }

    }

    @Override
    public int getItemCount() {
        return achievments.size();
    }
}
