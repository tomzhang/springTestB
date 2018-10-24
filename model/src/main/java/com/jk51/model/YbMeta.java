package com.jk51.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class YbMeta implements Serializable {
    /**
     * 元素自增ID
     */
    private Integer metaId;

    /**
     * 站点ID，999999=总站
     */
    private Integer siteId;

    /**
     * 元素键 key=>val 哈希对应key 
     */
    private String metaKey;

    /**
     * 描述
     */
    private String metaDesc;

    /**
     * 元素状态1 正常 2 未激活或未使用
     */
    private Integer metaStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 元素值 key=>val 哈希对应
     */
    private String metaVal;

    private static final long serialVersionUID = 1L;

    public Integer getMetaId() {
        return metaId;
    }

    public void setMetaId(Integer metaId) {
        this.metaId = metaId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
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

    public Integer getMetaStatus() {
        return metaStatus;
    }

    public void setMetaStatus(Integer metaStatus) {
        this.metaStatus = metaStatus;
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

    public String getMetaVal() {
        return metaVal;
    }

    public void setMetaVal(String metaVal) {
        this.metaVal = metaVal;
    }
}