package com.punjab.route.model;

public class City {
    private String name;
    private double lat;
    private double lng;

    public City(String name, double lat, double lng) {
        this.name = name; this.lat = lat; this.lng = lng;
    }
    public String getName() { return name; }
    public double getLat()  { return lat; }
    public double getLng()  { return lng; }
}
