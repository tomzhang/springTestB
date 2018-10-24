package com.jk51.modules.promotions.request;

/**
 * Created by mqq on 2017/8/10.
 */
public class ProActivityDto {
    private  Integer siteId;

    private Integer id;

    private Integer userId;

    private Integer memberId;

    private Integer proActivityType;//1 b_coupon_activity 2 b_promotions_activity

    public Integer getProActivityType() {
        return proActivityType;
    }

    public void setProActivityType(Integer proActivityType) {
        this.proActivityType = proActivityType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
}
