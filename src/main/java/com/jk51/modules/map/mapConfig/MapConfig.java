package com.jk51.modules.map.mapConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by chen on 2017/2/16.
 */
@Component
@ConfigurationProperties(prefix = "mapConfig")
public class MapConfig {

    private String GEO_KEY;
    private String BD_KEY;
    private String geoDistance;
    private String geoCoordinate;
    private String bdDistance;
    private String bdCoordinate;


    public String getGEO_KEY() {
        return GEO_KEY;
    }

    public void setGEO_KEY(String GEO_KEY) {
        this.GEO_KEY = GEO_KEY;
    }

    public String getBD_KEY() {
        return BD_KEY;
    }

    public void setBD_KEY(String BD_KEY) {
        this.BD_KEY = BD_KEY;
    }

    public String getGeoDistance() {
        return geoDistance;
    }

    public void setGeoDistance(String geoDistance) {
        this.geoDistance = geoDistance;
    }

    public String getGeoCoordinate() {
        return geoCoordinate;
    }

    public void setGeoCoordinate(String geoCoordinate) {
        this.geoCoordinate = geoCoordinate;
    }

    public String getBdDistance() {
        return bdDistance;
    }

    public void setBdDistance(String bdDistance) {
        this.bdDistance = bdDistance;
    }

    public String getBdCoordinate() {
        return bdCoordinate;
    }

    public void setBdCoordinate(String bdCoordinate) {

        this.bdCoordinate = bdCoordinate;
    }
}
