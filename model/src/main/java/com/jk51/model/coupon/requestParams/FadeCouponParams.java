package com.jk51.model.coupon.requestParams;

/**
 * filename :com.jk51.model.coupon.requestParams.
 * author   :zw
 * date     :2017/4/21
 * Update   :
 * 退款退还优惠券
 */
public class FadeCouponParams {
    private String tradesId;
    private Integer siteId;
    private Integer userId;
    private Integer coupoId;

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCoupoId() {
        return coupoId;
    }

    public void setCoupoId(Integer coupoId) {
        this.coupoId = coupoId;
    }
}
