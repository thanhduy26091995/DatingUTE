package com.itute.dating.maps.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by buivu on 17/11/2016.
 */
public class Route {
    @SerializedName("overview_polyline")
    private OverviewPolyline overviewPolyline;
    private List<Legs> legs;

    public OverviewPolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    public List<Legs> getLegs() {
        return legs;
    }

    public void setLegs(List<Legs> legs) {
        this.legs = legs;
    }
}
