package com.jk51.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BPandianPlan {
    private Integer id;

    private Integer siteId;

    private Integer storeId;

    private Integer type;

    private Integer planType;

    private Integer planDay;

    private Integer planHour;

    private String planExecutor;

    private Integer planStockShow;

    private Integer planCheck;

    private Integer planCheckType;

    private Integer planSignature;

    private Integer planOperator;

    private Integer planStop;

    private Integer planDelete;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date planStopTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date planDeleteTime;

    private String operatorName;

    private Date serverTime;

    private Date expireTime;

    private Integer nowMonth;

    private Integer uploadType;
    private Integer againType;

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

    public Integer getPlanType() {
        return planType;
    }

    public void setPlanType(Integer planType) {
        this.planType = planType;
    }

    public Integer getPlanDay() {
        return planDay;
    }

    public void setPlanDay(Integer planDay) {
        this.planDay = planDay;
    }

    public Integer getPlanHour() {
        return planHour;
    }

    public void setPlanHour(Integer planHour) {
        this.planHour = planHour;
    }

    public String getPlanExecutor() {
        return planExecutor;
    }

    public void setPlanExecutor(String planExecutor) {
        this.planExecutor = planExecutor;
    }

    public Integer getPlanStockShow() {
        return planStockShow;
    }

    public void setPlanStockShow(Integer planStockShow) {
        this.planStockShow = planStockShow;
    }

    public Integer getPlanCheck() {
        return planCheck;
    }

    public void setPlanCheck(Integer planCheck) {
        this.planCheck = planCheck;
    }

    public Integer getPlanCheckType() {
        return planCheckType;
    }

    public void setPlanCheckType(Integer planCheckType) {
        this.planCheckType = planCheckType;
    }

    public Integer getPlanSignature() {
        return planSignature;
    }

    public void setPlanSignature(Integer planSignature) {
        this.planSignature = planSignature;
    }

    public Integer getPlanOperator() {
        return planOperator;
    }

    public void setPlanOperator(Integer planOperator) {
        this.planOperator = planOperator;
    }

    public Integer getPlanStop() {
        return planStop;
    }

    public void setPlanStop(Integer planStop) {
        this.planStop = planStop;
    }

    public Integer getPlanDelete() {
        return planDelete;
    }

    public void setPlanDelete(Integer planDelete) {
        this.planDelete = planDelete;
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

    public Date getPlanStopTime() {
        return planStopTime;
    }

    public void setPlanStopTime(Date planStopTime) {
        this.planStopTime = planStopTime;
    }

    public Date getPlanDeleteTime() {
        return planDeleteTime;
    }

    public void setPlanDeleteTime(Date planDeleteTime) {
        this.planDeleteTime = planDeleteTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Date getServerTime() {
        return serverTime;
    }

    public void setServerTime(Date serverTime) {
        this.serverTime = serverTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getNowMonth() {
        return nowMonth;
    }

    public void setNowMonth(Integer nowMonth) {
        this.nowMonth = nowMonth;
    }

    public Integer getUploadType() {
        return uploadType;
    }

    public void setUploadType(Integer uploadType) {
        this.uploadType = uploadType;
    }

    public Integer getAgainType() {
        return againType;
    }

    public void setAgainType(Integer againType) {
        this.againType = againType;
    }

    @Override
    public String toString() {
        return "BPandianPlan{" +
            "id=" + id +
            ", siteId=" + siteId +
            ", storeId=" + storeId +
            ", type=" + type +
            ", planType=" + planType +
            ", planDay=" + planDay +
            ", planHour=" + planHour +
            ", planExecutor='" + planExecutor + '\'' +
            ", planStockShow=" + planStockShow +
            ", planCheck=" + planCheck +
            ", planCheckType=" + planCheckType +
            ", planSignature=" + planSignature +
            ", planOperator=" + planOperator +
            ", planStop=" + planStop +
            ", planDelete=" + planDelete +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", planStopTime=" + planStopTime +
            ", planDeleteTime=" + planDeleteTime +
            ", operatorName='" + operatorName + '\'' +
            ", serverTime=" + serverTime +
            ", expireTime=" + expireTime +
            ", nowMonth=" + nowMonth +
            ", uploadType=" + uploadType +
            ", againType=" + againType +
            '}';
    }
}
