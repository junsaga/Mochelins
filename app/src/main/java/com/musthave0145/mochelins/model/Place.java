package com.musthave0145.mochelins.model;

import java.io.Serializable;

public class Place implements Serializable  {
    public String name;
    public String Vicinity;
    private String business_status;

    private String icon;

    public String rating;

    public Geometry geometry;
    public String getIcon() {
        return icon;
    }
    public String getBusinessStatus() {
        return business_status;
    }
    public class Geometry implements Serializable {

        public Location location;

        public class Location implements Serializable {
            public double lat;
            public double lng;
        }
    }
}
