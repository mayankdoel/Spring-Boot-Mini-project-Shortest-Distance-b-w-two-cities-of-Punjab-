package com.punjab.route.model;

public class RouteSegment {
    private String from;
    private String to;
    private int    distanceKm;
    private String highway;
    private String estimatedTime;

    public RouteSegment(String from, String to, int distanceKm,
                        String highway, String estimatedTime) {
        this.from = from; this.to = to; this.distanceKm = distanceKm;
        this.highway = highway; this.estimatedTime = estimatedTime;
    }
    public String getFrom()          { return from; }
    public String getTo()            { return to; }
    public int    getDistanceKm()    { return distanceKm; }
    public String getHighway()       { return highway; }
    public String getEstimatedTime() { return estimatedTime; }
}