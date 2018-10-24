package com.jk51.model;

import java.util.Date;

public class BPandianOrder {
    private Integer id;

    private Integer siteId;

    private Integer storeId;

    private Integer type;

    private String pandianNum;

    private Integer planId;

    private Date createTime;

    private Date updateTime;

    private Integer isUpSite;

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

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPandianNum() {
        return pandianNum;
    }

    public void setPandianNum(String pandianNum) {
        this.pandianNum = pandianNum == null ? null : pandianNum.trim();
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


    private BPandianPlan pandianPlan;

    public BPandianPlan getPandianPlan() {
        return pandianPlan;
    }

    public void setPandianPlan(BPandianPlan pandianPlan) {
        this.pandianPlan = pandianPlan;
    }

    public Integer getIsUpSite() {
        return isUpSite;
    }

    public void setIsUpSite(Integer isUpSite) {
        this.isUpSite = isUpSite;
    }
}
