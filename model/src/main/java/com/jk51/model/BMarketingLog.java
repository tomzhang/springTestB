package com.jk51.model;

import java.util.Date;

public class BMarketingLog {
    private Integer id;

    private Integer siteId;

    private Integer operatorType;

    private Integer operator;

    private String processMethod;

    private String params;

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

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public String getProcessMethod() {
        return processMethod;
    }

    public void setProcessMethod(String processMethod) {
        this.processMethod = processMethod == null ? null : processMethod.trim();
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
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

    @Override
    public String toString() {
        return "BMarketingLog{" +
            "id=" + id +
            ", siteId=" + siteId +
            ", operatorType=" + operatorType +
            ", operator=" + operator +
            ", processMethod='" + processMethod + '\'' +
            ", params='" + params + '\'' +
            ", description='" + description + '\'' +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            '}';
    }
}
