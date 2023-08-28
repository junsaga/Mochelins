package com.musthave0145.mochelins.model;

import com.google.android.gms.maps.model.LatLng;

public class MarkerItem {
    private String title;
    private LatLng location;
    double lat;
    double lon;
    int price;


    String Vincity;

    public MarkerItem(String name, LatLng location) {
        this.title = title;
        this.location = location;
    }


    public LatLng getLocation() {
        return location;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVincity() {
        return Vincity;
    }

    public void setVincity(String vincity) {
        Vincity = vincity;
    }

    public MarkerItem(double lat, double lon, int price) {
        this.lat = lat;
        this.lon = lon;
        this.price = price;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
