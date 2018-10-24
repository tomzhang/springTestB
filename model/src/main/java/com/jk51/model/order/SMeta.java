package com.jk51.model.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-03-02
 * 修改记录:
 */
public class SMeta {
    @JsonProperty("siteId")
    private Integer siteId;
    @JsonProperty("metaId")
    private Integer metaId;
    @JsonProperty("metaType")
    private String metaType;
    @JsonProperty("metaVal")
    private String metaVal;
    @JsonProperty("metaStatus")
    private Integer metaStatus;
    @JsonProperty("metaKey")
    private String metaKey;
    @JsonProperty("metaDesc")
    private String metaDesc;
    @JsonProperty("createTime")
    private Timestamp createTime;
    @JsonProperty("updateTime")
    private Timestamp updateTime;
    @JsonProperty("themeId")
    private Integer themeId;

    public SMeta(){};

    public Integer getThemeId() {
        return themeId;
    }

    public void setThemeId(Integer themeId) {
        this.themeId = themeId;
    }

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

    public String getMetaType() {
        return metaType;
    }

    public void setMetaType(String metaType) {
        this.metaType = metaType;
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

    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
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
}
