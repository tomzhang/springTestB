package com.jk51.model.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

/**
 * 自定义标签库
 */
public class CustomLabel {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("siteId")
    private Integer siteId;
    @JsonProperty("labelName")
    private String labelName;//标签名称
    @JsonProperty("crowdSort")
    private String crowdSort;//所在人群分组
    @JsonProperty("labelDescription")
    private String labelDescription;//标签描述
    @JsonProperty("memberCount")
    private Integer memberCount;//标签中会员数量
    @JsonProperty("memberIds")
    private String memberIds;//标签中会员ID集合
    @JsonProperty("labelSort")
    private String labelSort;//标签分类：1：会员标签
    @JsonProperty("createTime")
    private Timestamp createTime;//创建时间
    @JsonProperty("updateTime")
    private Timestamp updateTime;//修改时间
    @JsonProperty("isDel")
    private Integer isDel;//是否删除
    @JsonProperty("creater")
    private String creater;//创建人
    @JsonProperty("modifier")
    private String modifier;//修改人

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

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getCrowdSort() {
        return crowdSort;
    }

    public void setCrowdSort(String crowdSort) {
        this.crowdSort = crowdSort;
    }

    public String getLabelDescription() {
        return labelDescription;
    }

    public void setLabelDescription(String labelDescription) {
        this.labelDescription = labelDescription;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public String getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(String memberIds) {
        this.memberIds = memberIds;
    }

    public String getLabelSort() {
        return labelSort;
    }

    public void setLabelSort(String labelSort) {
        this.labelSort = labelSort;
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

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
}
