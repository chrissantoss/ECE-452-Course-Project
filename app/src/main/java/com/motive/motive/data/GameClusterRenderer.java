package com.motive.motive.data;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class GameClusterRenderer extends DefaultClusterRenderer<GameClusterItem> {

    public GameClusterRenderer(Context context, GoogleMap map, ClusterManager<GameClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster<GameClusterItem> cluster) {
        // Change the threshold to the desired number
        return cluster.getSize() > 1; // Change this value to 2, 3, or any other number as needed
    }
}
