package com.jk51.modules.grouppurchase.request;

import java.time.LocalDateTime;

/**
 * Created by mqq on 2017/11/17.
 */
public class GroupPurchaseParam {
    private Integer siteId;

    private Integer id;

    private Integer parentId;
    private Integer proActivityId;

    private String tradesId;

    private String openId;

    private Integer memberId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;

    private Integer goodsId;



    public Integer getSiteId () {
        return siteId;
    }

    public void setSiteId (Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getId () {
        return id;
    }

    public void setId (Integer id) {
        this.id = id;
    }

    public Integer getMemberId () {
        return memberId;
    }

    public void setMemberId (Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getParentId () {
        return parentId;
    }

    public void setParentId (Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getProActivityId () {
        return proActivityId;
    }

    public void setProActivityId (Integer proActivityId) {
        this.proActivityId = proActivityId;
    }

    public LocalDateTime getCreateTime () {
        return createTime;
    }

    public void setCreateTime (LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime () {
        return updateTime;
    }

    public void setUpdateTime (LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getOpenId () {
        return openId;
    }

    public void setOpenId (String openId) {
        this.openId = openId;
    }

    public String getTradesId () {
        return tradesId;
    }

    public void setTradesId (String tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getStatus () {
        return status;
    }

    public void setStatus (Integer status) {
        this.status = status;
    }

    public Integer getGoodsId () {
        return goodsId;
    }

    public void setGoodsId (Integer goodsId) {
        this.goodsId = goodsId;
    }
}
