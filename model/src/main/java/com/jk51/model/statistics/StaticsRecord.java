package com.jk51.model.statistics;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:统计记录实体类
 * 作者: dumingliang
 * 创建日期: 2017-07-14
 * 修改记录:
 */

import java.util.Date;

public class StaticsRecord {
    private Integer siteId;
    private Integer id;
    private String staticsName;
    private String staticsValue;
    private String staticsType;
    private String staticsDesc;
    private Date createTime;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getStaticsType() {
        return staticsType;
    }

    public void setStaticsType(String staticsType) {
        this.staticsType = staticsType;
    }

    public String getStaticsDesc() {
        return staticsDesc;
    }

    public void setStaticsDesc(String staticsDesc) {
        this.staticsDesc = staticsDesc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
