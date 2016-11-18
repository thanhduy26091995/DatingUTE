package com.itute.dating.maps.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by buivu on 17/11/2016.
 */
public class OverviewPolyline {
    @SerializedName("points")
    private String point;

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
