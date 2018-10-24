package com.jk51.model.treat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by wangcheng on 2017/3/6.
 */
public class O2OMeta {
    @JsonProperty("siteId")
    private Integer siteId;//站点ID，999999=总站
    @JsonProperty("metaId")
    private Integer metaId;//元素自增ID
    @JsonProperty("metaKey")
    private String metaKey;//元素键 key=>val 哈希对应key
    @JsonProperty("metaVal")
    private String metaVal;//元素值 key=>val 哈希对应
    @JsonProperty("metaStatus")
    private Integer metaStatus;//元素状态1 正常 2 未激活或未使用
    @JsonProperty("metaDesc")
    private String metaDesc;//'描述'
    @JsonProperty("createTime")
    private Timestamp createTime;//'创建时间'
    @JsonProperty("updateTime")
    private Timestamp updateTime;//'更新时间'
    @JsonProperty("time_val")
    private String time_val;
    @JsonProperty("metaIdforTimeGap")
    private Integer metaIdforTimeGap;
    private Date orderTime;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getMetaId() {
        return metaId;
    }

    public void setMetaId(Integer metaId) {
        this.metaId = metaId;
    }

    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }

    public String getMetaVal() {
        return metaVal;
    }

    public void setMetaVal(String metaVal) {
        this.metaVal = metaVal;
    }

    public Integer getMetaStatus() {
        return metaStatus;
    }

    public void setMetaStatus(Integer metaStatus) {
        this.metaStatus = metaStatus;
    }

    public String getMetaDesc() {
        return metaDesc;
    }

    public void setMetaDesc(String metaDesc) {
        this.metaDesc = metaDesc;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getTime_val() {
        return time_val;
    }

    public void setTime_val(String time_val) {
        this.time_val = time_val;
    }

    public Integer getMetaIdforTimeGap() {
        return metaIdforTimeGap;
    }

    public void setMetaIdforTimeGap(Integer metaIdforTimeGap) {
        this.metaIdforTimeGap = metaIdforTimeGap;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }
}
