package com.jk51.model.map;

/**
 * Created by chen on 2017/2/16.
 */
public class Coordinate {
    //经度
    private double lng;
    //纬度
    private double lat;
    private String adcode;

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getAdcode() {
        return adcode;
    }

    public Coordinate(){}
    public Coordinate(double lng,double lat){
        this.lng=lng;
        this.lat=lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return lng+","+lat;
    }
}
