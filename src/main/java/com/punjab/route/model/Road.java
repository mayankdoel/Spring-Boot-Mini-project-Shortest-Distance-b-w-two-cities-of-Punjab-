package com.punjab.route.model;

public class Road {
    private String to;
    private int distanceKm;
    private String highway;

    public Road(String to, int distanceKm, String highway) {
        this.to = to; this.distanceKm = distanceKm; this.highway = highway;
    }
    public String getTo()          { return to; }
    public int    getDistanceKm()  { return distanceKm; }
    public String getHighway()     { return highway; }
}