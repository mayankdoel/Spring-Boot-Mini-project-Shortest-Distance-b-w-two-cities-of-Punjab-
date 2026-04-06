package com.punjab.route.model;

import java.util.List;

public class RouteResponse {
    private String       source;
    private String       destination;
    private List<String> cityPath;
    private List<RouteSegment> segments;
    private int          totalDistanceKm;
    private String       totalEstimatedTime;
    private int          numberOfStops;

    // Getters & Setters
    public String getSource()                   { return source; }
    public void   setSource(String s)           { source = s; }
    public String getDestination()              { return destination; }
    public void   setDestination(String d)      { destination = d; }
    public List<String> getCityPath()           { return cityPath; }
    public void   setCityPath(List<String> p)   { cityPath = p; }
    public List<RouteSegment> getSegments()             { return segments; }
    public void   setSegments(List<RouteSegment> s)     { segments = s; }
    public int    getTotalDistanceKm()          { return totalDistanceKm; }
    public void   setTotalDistanceKm(int d)     { totalDistanceKm = d; }
    public String getTotalEstimatedTime()       { return totalEstimatedTime; }
    public void   setTotalEstimatedTime(String t){ totalEstimatedTime = t; }
    public int    getNumberOfStops()            { return numberOfStops; }
    public void   setNumberOfStops(int n)       { numberOfStops = n; }
}