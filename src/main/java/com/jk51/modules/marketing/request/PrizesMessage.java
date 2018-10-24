package com.jk51.modules.marketing.request;

import com.jk51.modules.marketing.service.MessageProduce;

public class PrizesMessage {
    public static final int MESSAGE_TYPE = MessageProduce.Constant.PRIZES_MESSAGE;

    private Integer siteId;

    private Integer planId;

    private String mobile;

    private Integer buyerId;

    private Integer prizesId;

    private Integer type;

    private Integer typeId;

    private String typeInfo;

    private Integer marketingMemberExtId;

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

    public Integer getPrizesId() {
        return prizesId;
    }

    public void setPrizesId(Integer prizesId) {
        this.prizesId = prizesId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeInfo() {
        return typeInfo;
    }

    public void setTypeInfo(String typeInfo) {
        this.typeInfo = typeInfo;
    }

    public Integer getMarketingMemberExtId() {
        return marketingMemberExtId;
    }

    public void setMarketingMemberExtId(Integer marketingMemberExtId) {
        this.marketingMemberExtId = marketingMemberExtId;
    }

    @Override
    public String toString() {
        return "PrizesMessage{" +
            "siteId=" + siteId +
            ", planId=" + planId +
            ", mobile='" + mobile + '\'' +
            ", buyerId=" + buyerId +
            ", prizesId=" + prizesId +
            ", type=" + type +
            ", typeId=" + typeId +
            ", typeInfo='" + typeInfo + '\'' +
            ", marketingMemberExtId=" + marketingMemberExtId +
            '}';
    }
}
