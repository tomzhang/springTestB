package com.jk51.model.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

/**
 * Created by Administrator on 2017/9/19.
 */
public class relationLlabel {
    @JsonProperty("id")
    private Integer id; //会员-标签-店员 关系表
    @JsonProperty("siteId")
    private Integer siteId;
    @JsonProperty("buyerId")
    private Integer buyerId;   //会员ID
    @JsonProperty("labelName")
    private String labelName;   //标签名称
    @JsonProperty("storeadminIds")
    private String storeadminIds;   //店员ID集合
    @JsonProperty("storeadminCount")
    private Integer storeadminCount;    //店员数量
    @JsonProperty("labelType")
    private Integer labelType;    //店员数量
    @JsonProperty("createTime")
    private Timestamp createTime;//创建时间
    @JsonProperty("updateTime")
    private Timestamp updateTime;//修改时间

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

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getStoreadminIds() {
        return storeadminIds;
    }

    public void setStoreadminIds(String storeadminIds) {
        this.storeadminIds = storeadminIds;
    }

    public Integer getStoreadminCount() {
        return storeadminCount;
    }

    public void setStoreadminCount(Integer storeadminCount) {
        this.storeadminCount = storeadminCount;
    }

    public Integer getLabelType() {
        return labelType;
    }

    public void setLabelType(Integer labelType) {
        this.labelType = labelType;
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
