package com.renatonunes.padellog.adapters;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.renatonunes.padellog.R;

/**
 * Created by Renato on 04/08/2016.
 */
public class ViewHolderChampionship extends RecyclerView.ViewHolder {

    public int currentItem;
    public ImageView championshipImage;
    public TextView championshipTitle;
    public TextView championshipDetail;

    /*
    * private String id;
    private String name;
    private String partner;
    private String owner;*/

    public ViewHolderChampionship(View itemView) {
        super(itemView);
        championshipImage = (ImageView)itemView.findViewById(R.id.item_image);
        championshipTitle = (TextView)itemView.findViewById(R.id.item_title);
        championshipDetail = (TextView)itemView.findViewById(R.id.item_detail);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = getAdapterPosition();

                Snackbar.make(v, "Click detected on item " + position,
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
