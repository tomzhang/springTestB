package com.jk51.model.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/6/9.
 */
public class MemberLabel {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("siteId")
    private Integer siteId;
    @JsonProperty("storeIds")
    private String storeIds;        //人群归属：指定商家下的门店
    @JsonProperty("crowdName")
    private String crowdName;       //人群名称
    @JsonProperty("crowdSort")
    private Integer crowdSort;      //人群分类：自定义人群、推荐人群等
    @JsonProperty("description")
    private String description;     //人群用途说明
    @JsonProperty("crowdType")
    private Integer crowdType;      //人群类型
    @JsonProperty("labelCount")
    private Integer labelCount;          //人群数量
    @JsonProperty("labelSort")
    private Integer labelSort;      //标签分类：基础标签，交易标签，健康标签;
    @JsonProperty("createtime")
    private Date createtime;        //创建时间
    @JsonProperty("updatetime")
    private Date updatetime;        //修改时间
    @JsonProperty("labelGroup")
    private String labelGroup;      //标签组
    @JsonProperty("scene")
    private String scene;           //综合IDs
    @JsonProperty("isDel")
    private Integer isDel;          //是否删除
    @JsonProperty("customIds")
    private String customIds;          //自定义标签IDs
    @JsonProperty("crowdIds")
    private String crowdIds;          //基础标签IDs+交易标签IDs

    public String getCustomIds() {
        return customIds;
    }

    public void setCustomIds(String customIds) {
        this.customIds = customIds;
    }

    public String getCrowdIds() {
        return crowdIds;
    }

    public void setCrowdIds(String crowdIds) {
        this.crowdIds = crowdIds;
    }

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

    public String getStoreIds() {
        return storeIds;
    }

    public void setStoreIds(String storeIds) {
        this.storeIds = storeIds;
    }

    public String getCrowdName() {
        return crowdName;
    }

    public void setCrowdName(String crowdName) {
        this.crowdName = crowdName;
    }

    public Integer getCrowdSort() {
        return crowdSort;
    }

    public void setCrowdSort(Integer crowdSort) {
        this.crowdSort = crowdSort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getLabelGroup() {
        return labelGroup;
    }

    public void setLabelGroup(String labelGroup) {
        this.labelGroup = labelGroup;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Integer getCrowdType() {
        return crowdType;
    }

    public void setCrowdType(Integer crowdType) {
        this.crowdType = crowdType;
    }

    public Integer getLabelCount() {
        return labelCount;
    }

    public void setLabelCount(Integer labelCount) {
        this.labelCount = labelCount;
    }

    public Integer getLabelSort() {
        return labelSort;
    }

    public void setLabelSort(Integer labelSort) {
        this.labelSort = labelSort;
    }
}
