package com.jk51.model;

import java.util.Date;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-06-07
 * 修改记录:
 */
public class BInventoryLog {

    private Integer id;
    private Integer siteId;
    private Integer storeId;
    private Integer storeAdminId;
    private String pandianNum;
    private String goodsCode;
    private String batchNums;
    private Integer enableOrder;
    private Date createTime;
    private Date updateTime;
    private String score;

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

    public String getPandianNum() {
        return pandianNum;
    }

    public void setPandianNum(String pandianNum) {
        this.pandianNum = pandianNum;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getBatchNums() {
        return batchNums;
    }

    public void setBatchNums(String batchNums) {
        this.batchNums = batchNums;
    }

    public Integer getEnableOrder() {
        return enableOrder;
    }

    public void setEnableOrder(Integer enableOrder) {
        this.enableOrder = enableOrder;
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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
