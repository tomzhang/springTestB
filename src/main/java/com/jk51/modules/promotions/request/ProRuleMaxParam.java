package com.jk51.modules.promotions.request;

/**
 * Created by mqq on 2017/8/11.
 */
public class ProRuleMaxParam {
    private String siteId;
    private String userId ;
    private String orderType ;
    private String applyChannel ;
    private String storeId;
    private String postFee ;
    private String orderFee ;
    private String goodsInfo ;
    private String areaId ;
    private String receiverProvinceCode ;
    private String receiverCityCode ;
    private String couponId;


    public String getApplyChannel() {
        return applyChannel;
    }

    public void setApplyChannel(String applyChannel) {
        this.applyChannel = applyChannel;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(String goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public String getOrderFee() {
        return orderFee;
    }

    public void setOrderFee(String orderFee) {
        this.orderFee = orderFee;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPostFee() {
        return postFee;
    }

    public void setPostFee(String postFee) {
        this.postFee = postFee;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getReceiverCityCode() {
        return receiverCityCode;
    }

    public void setReceiverCityCode(String receiverCityCode) {
        this.receiverCityCode = receiverCityCode;
    }

    public String getReceiverProvinceCode() {
        return receiverProvinceCode;
    }

    public void setReceiverProvinceCode(String receiverProvinceCode) {
        this.receiverProvinceCode = receiverProvinceCode;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }
}
