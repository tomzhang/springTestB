package com.jk51.modules.grouppurchase.request;

/**
 * Created by mqq on 2017/11/20.
 */
public class GroupPurchaseForGoods {

    private Integer siteId;

    private Integer groupPurchaseId;

    private Integer goodsId;

    private Integer getGroupPurchaseForActivityId;

    private Integer memberId;

    private Integer userId;

    public Integer getSiteId () {
        return siteId;
    }

    public void setSiteId (Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getGetGroupPurchaseForActivityId () {
        return getGroupPurchaseForActivityId;
    }

    public void setGetGroupPurchaseForActivityId (Integer getGroupPurchaseForActivityId) {
        this.getGroupPurchaseForActivityId = getGroupPurchaseForActivityId;
    }

    public Integer getGoodsId () {
        return goodsId;
    }

    public void setGoodsId (Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGroupPurchaseId () {
        return groupPurchaseId;
    }

    public void setGroupPurchaseId (Integer groupPurchaseId) {
        this.groupPurchaseId = groupPurchaseId;
    }

    public Integer getMemberId () {
        return memberId;
    }

    public void setMemberId (Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getUserId () {
        return userId;
    }

    public void setUserId (Integer userId) {
        this.userId = userId;
    }
}
