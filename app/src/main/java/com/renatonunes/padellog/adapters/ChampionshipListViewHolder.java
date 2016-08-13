package com.renatonunes.padellog.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.renatonunes.padellog.ChampionshipInfoActivity;
import com.renatonunes.padellog.R;
import com.renatonunes.padellog.domain.Championship;

/**
 * Created by Renato on 04/08/2016.
 */
public class ChampionshipListViewHolder extends RecyclerView.ViewHolder {

    private final Context context;
//    public int currentItem;

    public Championship currentChampionship;

    public ImageView championshipImage;
    public TextView championshipTitle;
    public TextView championshipDetail;

    public ChampionshipListViewHolder(View itemView) {
        super(itemView);
        championshipImage = (ImageView)itemView.findViewById(R.id.item_image);
        championshipTitle = (TextView)itemView.findViewById(R.id.item_title);
        championshipDetail = (TextView)itemView.findViewById(R.id.item_detail);

        context = itemView.getContext();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
//                int position = getAdapterPosition();

//                Snackbar.make(v, "Click on item " + position + " " + currentKey,
//                        Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                ChampionshipInfoActivity.start(context, currentChampionship);

//                callMaterialUpActivity();

            }
        });
    }

//    public void callMaterialUpActivity(){
//        Intent intent = new Intent(context, ChampionshipInfoActivity.class);
//        context.startActivity(intent);
//    }
}
