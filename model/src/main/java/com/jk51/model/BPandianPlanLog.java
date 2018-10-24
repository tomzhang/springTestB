package com.jk51.model;

import java.util.Date;

public class BPandianPlanLog {
    private Integer id;

    private Integer siteId;

    private Integer storeId;

    private Integer planId;

    private Integer type;

    private Integer operator;

    private String oldPlan;

    private String newPlan;

    private String description;

    private Date createTime;

    private Date updateTime;

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

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public String getOldPlan() {
        return oldPlan;
    }

    public void setOldPlan(String oldPlan) {
        this.oldPlan = oldPlan == null ? null : oldPlan.trim();
    }

    public String getNewPlan() {
        return newPlan;
    }

    public void setNewPlan(String newPlan) {
        this.newPlan = newPlan == null ? null : newPlan.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
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
}
