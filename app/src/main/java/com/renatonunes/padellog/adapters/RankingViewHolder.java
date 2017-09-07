package com.renatonunes.padellog.adapters;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.renatonunes.padellog.AcademyInfoActivity;
import com.renatonunes.padellog.AcademyListActivity;
import com.renatonunes.padellog.AddChampionshipActivity;
import com.renatonunes.padellog.EditProfileActivity;
import com.renatonunes.padellog.R;
import com.renatonunes.padellog.domain.Academy;
import com.renatonunes.padellog.domain.Player;

/**
 * Created by renatonunes on 05/09/17.
 */

public class RankingViewHolder extends RecyclerView.ViewHolder {

    private final Context context;

    public Player currentPlayer;
    public Boolean isPickingPlayer;

    public TextView position;
    public TextView number;
    public ImageView playerImage;
    public ImageView imgTrophy;
    public TextView playerName;
    //public TextView academyPlace;
    public Boolean isReadOnly;

    public RankingViewHolder(View itemView) {
        super(itemView);
        playerImage = (ImageView)itemView.findViewById(R.id.item_image);

        playerName = (TextView)itemView.findViewById(R.id.item_detail);

        position = (TextView)itemView.findViewById(R.id.item_position);
        number = (TextView)itemView.findViewById(R.id.item_number);
        imgTrophy = (ImageView)itemView.findViewById(R.id.item_trophy_ranking);

        //academyName = (TextView)itemView.findViewById(R.id.item_title);
        //academyPlace = (TextView)itemView.findViewById(R.id.item_detail);
        //championshipTrophyImage = (ImageView)itemView.findViewById(R.id.item_image_trophy);

        context = itemView.getContext();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                //if (isPickingPlayer){
                    //AddChampionshipActivity.selectedAcademy = currentAcademy;

                    //((AcademyListActivity)context).finish();
                //}else{
                    EditProfileActivity.start(context, currentPlayer, true);
                            //, isReadOnly);
                //}

            }
        });
    }

}
