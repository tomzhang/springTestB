package com.jk51.model.coupon.requestParams;

/**
 * filename :com.jk51.model.coupon.requestParams.
 * author   :zw
 * date     :2017/7/5
 * Update   :
 */
public class CouponFilterParams {
    private  Integer siteId;
    private  String goodsId;
    private  Integer userId;
    private Integer memberId;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getUserId() {
        return this.memberId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMemberId () {
        return memberId;
    }

    public void setMemberId (Integer memberId) {
        this.memberId = memberId;
    }

    public CouponFilterParams() {
        super();
    }

    public CouponFilterParams(Integer siteId,String goodsId,Integer userId){
        this.siteId = siteId;
        this.goodsId = goodsId;
        this.userId = userId;
    }

}
