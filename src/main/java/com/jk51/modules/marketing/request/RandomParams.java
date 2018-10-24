package com.jk51.modules.marketing.request;

public class RandomParams {
    private Integer siteId;

    private Integer planId;

    private String mobile;

    private Integer buyerId;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    @Override
    public String toString() {
        return "RandomParams{" +
            "siteId=" + siteId +
            ", planId=" + planId +
            ", mobile=" + mobile +
            ", buyerId=" + buyerId +
            '}';
    }
}
