package com.jk51.modules.pandian.Response;

import java.util.Date;

public class PandianPlanInfo {
    private Integer id;
    private String pdNum;
    private String planType;
    private String checkType;
    private Date serverTime;
    private Date expireTime;
    private String storeRate;
    private String source;
    private String operatorName;
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPdNum() {
        return pdNum;
    }

    public void setPdNum(String pdNum) {
        this.pdNum = pdNum;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
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

    public String getStoreRate() {
        return storeRate;
    }

    public void setStoreRate(String storeRate) {
        this.storeRate = storeRate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "PandianPlanInfo{" +
            "id=" + id +
            ", pdNum='" + pdNum + '\'' +
            ", planType='" + planType + '\'' +
            ", checkType='" + checkType + '\'' +
            ", serverTime=" + serverTime +
            ", expireTime=" + expireTime +
            ", storeRate='" + storeRate + '\'' +
            ", source='" + source + '\'' +
            ", operatorName='" + operatorName + '\'' +
            ", createTime=" + createTime +
            '}';
    }
}
