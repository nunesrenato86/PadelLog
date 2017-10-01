package com.renatonunes.padellog.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.renatonunes.padellog.AcademyInfoActivity;
import com.renatonunes.padellog.AcademyListActivity;
import com.renatonunes.padellog.AddChampionshipActivity;
import com.renatonunes.padellog.R;
import com.renatonunes.padellog.domain.Academy;

/**
 * Created by renatonunes on 29/09/17.
 */

public class AchievmentsListViewHolder extends RecyclerView.ViewHolder {

    private final Context context;

    //public Academy currentAcademy;

    public ImageView achievmentImage;

    public TextView achievmentName;
    public TextView achievmentInfo;
    public TextView achievmentDate;

    public AchievmentsListViewHolder(View itemView) {
        super(itemView);
        achievmentImage = (ImageView)itemView.findViewById(R.id.item_image);

        achievmentName = (TextView)itemView.findViewById(R.id.item_detail);
        achievmentInfo = (TextView)itemView.findViewById(R.id.item_info);

        //academyName = (TextView)itemView.findViewById(R.id.item_title);
        //academyPlace = (TextView)itemView.findViewById(R.id.item_detail);
        //championshipTrophyImage = (ImageView)itemView.findViewById(R.id.item_image_trophy);

        context = itemView.getContext();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                //AcademyInfoActivity.start(context, currentAcademy, isReadOnly);

            }
        });
    }

}
