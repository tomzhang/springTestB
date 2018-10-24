package com.jk51.modules.common.pojo;

import java.util.Objects;

/**
 * @author
 */
public class Point {
    private Float lat;
    private Float lng;

    public Point(Float lat, Float lng) {
        Objects.requireNonNull(lat);
        Objects.requireNonNull(lng);

        this.lat = lat;
        this.lng = lng;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return lng + "," + lat;
    }
}
