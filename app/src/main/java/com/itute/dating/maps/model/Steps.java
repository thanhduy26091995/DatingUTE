package com.itute.dating.maps.model;

/**
 * Created by buivu on 17/11/2016.
 */
public class Steps {
    private Location start_location;
    private Location end_location;
    private OverviewPolyline polyline;

    public Location getStart_location() {
        return start_location;
    }

    public void setStart_location(Location start_location) {
        this.start_location = start_location;
    }

    public Location getEnd_location() {
        return end_location;
    }

    public void setEnd_location(Location end_location) {
        this.end_location = end_location;
    }

    public OverviewPolyline getPolyline() {
        return polyline;
    }

    public void setPolyline(OverviewPolyline polyline) {
        this.polyline = polyline;
    }
}
