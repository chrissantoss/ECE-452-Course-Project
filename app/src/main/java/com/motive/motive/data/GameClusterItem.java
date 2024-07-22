package com.motive.motive.data;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class GameClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final String mId;
    private final String mSnippet;

    public GameClusterItem(double lat, double lng, String title, String id) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mId = id;
        mSnippet = null;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }
    @Override
    public String getSnippet() {
        return null;
    }

    public String getID() {
        return mId;
    }
}
