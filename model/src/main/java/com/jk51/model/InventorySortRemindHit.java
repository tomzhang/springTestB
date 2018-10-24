package com.jk51.model;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/7/2
 * 修改记录:
 */
public class InventorySortRemindHit {

    private Integer id;
    private String pandianNum;
    private Integer siteId;
    private Integer storeId;
    private Integer storeAdminId;
    private String currentGoodsCode;
    private String remindGoodsCode;
    private Integer type;
    private Date createTime;
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPandianNum() {
        return pandianNum;
    }

    public void setPandianNum(String pandianNum) {
        this.pandianNum = pandianNum;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public String getCurrentGoodsCode() {
        return currentGoodsCode;
    }

    public void setCurrentGoodsCode(String currentGoodsCode) {
        this.currentGoodsCode = currentGoodsCode;
    }

    public String getRemindGoodsCode() {
        return remindGoodsCode;
    }

    public void setRemindGoodsCode(String remindGoodsCode) {
        this.remindGoodsCode = remindGoodsCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
}
