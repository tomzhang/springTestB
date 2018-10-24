package com.jk51.modules.erpprice.domain;

import com.jk51.modules.common.pojo.Point;

/**
 * @author
 * 门店经纬度坐标点
 */
public class StorePoint extends Point {
    private final Integer storeId;

    public StorePoint(Integer storeId, Float lat, Float lng) {
        super(lat, lng);
        this.storeId = storeId;
    }

    public Integer getStoreId() {
        return storeId;
    }
}
