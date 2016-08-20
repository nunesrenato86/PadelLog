package com.renatonunes.padellog.domain;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Renato on 19/08/2016.
 */
public class MyMapItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;

    public MyMapItem(double lat, double lng, String title) {

        mPosition = new LatLng(lat, lng);
        mTitle = title;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getmTitle() {
        return mTitle;
    }
}
