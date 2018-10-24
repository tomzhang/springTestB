package com.jk51.model.coupon.requestParams;

/**
 * Created by Administrator on 2017/7/4.
 */
public class Import {
    private Integer siteId; //门店ID
    private String goodsList; //商品编码字符串

    public String getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(String goodsList) {
        this.goodsList = goodsList;
    }

    public Integer getSiteId() {

        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
}
