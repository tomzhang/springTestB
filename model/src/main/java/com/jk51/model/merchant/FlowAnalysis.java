package com.jk51.model.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Administrator on 2017/7/27.
 */
public class FlowAnalysis {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("siteId")
    private Integer siteId;
    @JsonProperty("staticsName")
    private String staticsName;
    @JsonProperty("staticsValue")
    private String staticsValue;
    @JsonProperty("staticsDesc")
    private String staticsDesc;
    @JsonProperty("createTime")
    private String createTime;

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

    public String getStaticsName() {
        return staticsName;
    }

    public void setStaticsName(String staticsName) {
        this.staticsName = staticsName;
    }

    public String getStaticsValue() {
        return staticsValue;
    }

    public void setStaticsValue(String staticsValue) {
        this.staticsValue = staticsValue;
    }

    public String getStaticsDesc() {
        return staticsDesc;
    }

    public void setStaticsDesc(String staticsDesc) {
        this.staticsDesc = staticsDesc;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
