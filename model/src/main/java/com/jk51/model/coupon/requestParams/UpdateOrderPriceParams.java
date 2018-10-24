package com.jk51.model.coupon.requestParams;

/**
 * Created by xiapeng on 2017/9/7.
 */
public class UpdateOrderPriceParams {
    private String tradesId;
    private String price;
    private String siteId;
    private  String userName;
    private  String userId;
    private  String platform;
    private  String remark;
    private  String storeId;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
