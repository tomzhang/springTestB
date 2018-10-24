package com.jk51.modules.appInterface.util;

public class MemberInfo {
    private Integer siteId;

    private String mobile;

    private Integer buyerId;

    private boolean activated;

    private Integer orderNum;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
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

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "MemberInfo{" +
            "siteId=" + siteId +
            ", mobile='" + mobile + '\'' +
            ", buyerId=" + buyerId +
            ", activated=" + activated +
            ", orderNum=" + orderNum +
            '}';
    }
}
