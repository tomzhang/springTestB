package com.jk51.model.coupon.requestParams;

/**
 * Created by Administrator on 2018/1/4.
 */
public class StockUpParams {

    private  Integer siteId;

    private  String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSiteId() {

        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
}
