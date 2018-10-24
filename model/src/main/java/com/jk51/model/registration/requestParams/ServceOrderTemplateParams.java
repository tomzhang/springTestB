package com.jk51.model.registration.requestParams;

/**
 * Created by mqq on 2017/4/14.
 */
public class ServceOrderTemplateParams {
    private Integer siteId;//商户

    private Integer storeId;//门店

    private Integer goodsId;//商品

    private String  time;//时间

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
