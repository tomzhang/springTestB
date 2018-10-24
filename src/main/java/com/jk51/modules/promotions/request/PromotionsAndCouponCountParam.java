package com.jk51.modules.promotions.request;

/**
 * Created by Administrator on 2018/5/4.
 */
public class PromotionsAndCouponCountParam {
    //活动和优惠券共有的参数
    private  Integer siteId;
    private Integer memberId;

    //活动参数
    private Integer id;

    private Integer userId;

    private Integer proActivityType;//1 b_coupon_activity 2 b_promotions_activity

    //优惠券参数
    private Integer contentType;

    private Integer ruleId;

    private Integer activityId;


    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProActivityType() {
        return proActivityType;
    }

    public void setProActivityType(Integer proActivityType) {
        this.proActivityType = proActivityType;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    @Override
    public String toString() {
        return "PromotionsAndCouponCountParam{" +
            "siteId=" + siteId +
            ", memberId=" + memberId +
            ", id=" + id +
            ", userId=" + userId +
            ", proActivityType=" + proActivityType +
            ", contentType=" + contentType +
            ", ruleId=" + ruleId +
            ", activityId=" + activityId +
            '}';
    }
}
