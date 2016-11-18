package com.itute.dating.maps.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by buivu on 17/11/2016.
 */
public class DirectionResults {
    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
