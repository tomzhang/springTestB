package com.jk51.modules.erpprice.domain;

import com.jk51.modules.common.gaode.DirectionWalkingWebApi;
import com.jk51.modules.common.pojo.Point;

/**
 * @author 门店距离
 */
public class StoreDirection extends DirectionWalkingWebApi {
    private final Integer storeId;
    private Integer direction;

    private static double EARTH_RADIUS = 6371.393;

    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }

    /**
     * 计算两个经纬度之间的距离
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double calcDistance(double lat1, double lng1, double lat2, double lng2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
            Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 1000);
        return s;
    }

    public StoreDirection(Point origin, StorePoint destination) {
        super(origin, destination);
        storeId = destination.getStoreId();

        direction = (int)calcDistance(origin.getLat(), origin.getLng(), destination.getLat(), destination.getLng());
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Integer getDirection() {
        return direction;
    }

    public Integer getStoreId() {
        return storeId;
    }
}
