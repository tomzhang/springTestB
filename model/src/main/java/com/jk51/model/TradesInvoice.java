package com.jk51.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jk51.commons.json.LocalDateTimeDeserializerForLongFormatter;
import com.jk51.commons.json.LocalDateTimeSerializerForLongFormatter;

import java.time.LocalDateTime;

/**
 * Created by Administrator on 2018/6/5.
 */
public class TradesInvoice {
    private Integer id;
    private Integer siteId;
    private String mobile;
    private String openId;
    private Long tradesId;
    private Integer status;
    @JsonSerialize(using = LocalDateTimeSerializerForLongFormatter.class)
    @JsonDeserialize(using = LocalDateTimeDeserializerForLongFormatter.class)
    private LocalDateTime createTime;
    @JsonSerialize(using = LocalDateTimeSerializerForLongFormatter.class)
    @JsonDeserialize(using = LocalDateTimeDeserializerForLongFormatter.class)
    private LocalDateTime updateTime;

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getTradesId() {
        return tradesId;
    }

    public void setTradesId(Long tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "TradesInvoice{" +
            "id=" + id +
            ", siteId=" + siteId +
            ", mobile='" + mobile + '\'' +
            ", openId='" + openId + '\'' +
            ", tradesId=" + tradesId +
            ", status=" + status +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            '}';
    }
}
