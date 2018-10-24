package com.jk51.modules.pandian.dto;

import com.jk51.modules.pandian.param.InventoryBatchNum;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/5/9
 * 修改记录:
 */
public class PandianSortRedisDto {
    private Integer siteId;
    private Integer storeId;
    private Integer currentStoreAdminId;
    private String currentGoodsCode;
    private String pandianNum;
    private List<InventoryBatchNum> batchNums ;
    private Integer enableOrder;

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

    public Integer getCurrentStoreAdminId() {
        return currentStoreAdminId;
    }

    public void setCurrentStoreAdminId(Integer currentStoreAdminId) {
        this.currentStoreAdminId = currentStoreAdminId;
    }

    public String getCurrentGoodsCode() {
        return currentGoodsCode;
    }

    public void setCurrentGoodsCode(String currentGoodsCode) {
        this.currentGoodsCode = currentGoodsCode;
    }

    public String getPandianNum() {
        return pandianNum;
    }

    public void setPandianNum(String pandianNum) {
        this.pandianNum = pandianNum;
    }

    public List<InventoryBatchNum> getBatchNums() {
        return batchNums;
    }

    public void setBatchNums(List<InventoryBatchNum> batchNums) {
        this.batchNums = batchNums;
    }

    public Integer getEnableOrder() {
        return enableOrder;
    }

    public void setEnableOrder(Integer enableOrder) {
        this.enableOrder = enableOrder;
    }

    @Override
    public String toString() {
        return "PandianSortRedisDto{" +
            "siteId=" + siteId +
            ", storeId=" + storeId +
            ", currentStoreAdminId=" + currentStoreAdminId +
            ", currentGoodsCode='" + currentGoodsCode + '\'' +
            ", pandianNum='" + pandianNum + '\'' +
            '}';
    }
}
