package com.jk51.model.coupon.mqParams;

/**
 * filename :com.jk51.model.coupon.mqParams.
 * author   :zw
 * date     :2017/5/12
 * Update   :
 */
public class SendCouponMq {
    private Integer siteId;
    private Integer activityId;
    private Integer type;//发放类型，
    private Integer sendWay;

    public Integer getSendWay() {
        return sendWay;
    }

    public void setSendWay(Integer sendWay) {
        this.sendWay = sendWay;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
