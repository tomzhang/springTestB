package com.jk51.model;

import java.util.Date;

public class BPandianOrderStatus {
    private Integer id;

    private String pandianNum;

    private Integer planId;

    private String status;

    private Integer storeId;

    private Integer siteId;

    private String confirmChecker;

    private Integer confirmOperateType;

    private String auditChecker;

    private Integer auditOperateType;

    private String startChecker;

    private Integer startOperateType;

    private Date createTime;

    private Date updateTime;

    private Integer orderId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getConfirmChecker() {
        return confirmChecker;
    }

    public void setConfirmChecker(String confirmChecker) {
        this.confirmChecker = confirmChecker == null ? null : confirmChecker.trim();
    }

    public Integer getConfirmOperateType() {
        return confirmOperateType;
    }

    public void setConfirmOperateType(Integer confirmOperateType) {
        this.confirmOperateType = confirmOperateType;
    }

    public String getAuditChecker() {
        return auditChecker;
    }

    public void setAuditChecker(String auditChecker) {
        this.auditChecker = auditChecker == null ? null : auditChecker.trim();
    }

    public Integer getAuditOperateType() {
        return auditOperateType;
    }

    public void setAuditOperateType(Integer auditOperateType) {
        this.auditOperateType = auditOperateType;
    }

    public String getStartChecker() {
        return startChecker;
    }

    public void setStartChecker(String startChecker) {
        this.startChecker = startChecker == null ? null : startChecker.trim();
    }

    public Integer getStartOperateType() {
        return startOperateType;
    }

    public void setStartOperateType(Integer startOperateType) {
        this.startOperateType = startOperateType;
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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}