package com.jk51.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BMarketingPlanExt {
    private Integer id;

    private Integer siteId;

    private Integer marketingPlanId;

    private Integer type;

    private Integer typeId;

    private String typeInfo;

    private Float chances;

    private Integer ceiling;

    private Integer receive;

    private Boolean isDel;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer operatorType;

    private Integer operatorId;

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

    public Integer getMarketingPlanId() {
        return marketingPlanId;
    }

    public void setMarketingPlanId(Integer marketingPlanId) {
        this.marketingPlanId = marketingPlanId;
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
        this.typeInfo = typeInfo == null ? null : typeInfo.trim();
    }

    public Float getChances() {
        return chances;
    }

    public void setChances(Float chances) {
        this.chances = chances;
    }

    public Integer getCeiling() {
        return ceiling;
    }

    public void setCeiling(Integer ceiling) {
        this.ceiling = ceiling;
    }

    public Integer getReceive() {
        return receive;
    }

    public void setReceive(Integer receive) {
        this.receive = receive;
    }

    public Boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
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

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    @Override
    public String toString() {
        return "BMarketingPlanExt{" +
            "id=" + id +
            ", siteId=" + siteId +
            ", marketingPlanId=" + marketingPlanId +
            ", type=" + type +
            ", typeId=" + typeId +
            ", typeInfo='" + typeInfo + '\'' +
            ", chances=" + chances +
            ", ceiling=" + ceiling +
            ", receive=" + receive +
            ", isDel=" + isDel +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", operatorType=" + operatorType +
            ", operatorId=" + operatorId +
            '}';
    }
}
