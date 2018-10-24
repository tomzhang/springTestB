package com.jk51.model.order;

import java.sql.Timestamp;

/**
 * Created by Administrator on 2017/9/7.
 */
public class TradesUpdatePriceLog {
    private Integer siteId;
    private Integer id;
    private String tradesNo;
    private Integer originalPrice;
    private Integer changePriceBefore;
    private Integer changePriceAfter;
    private Integer operationPlatform;
    private String storeName;
    private Integer storeId;
    private String operationAccount;
    private String operationUser;
    private Integer operationId;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String remark;


    public Integer getOperationId() {
        return operationId;
    }

    public void setOperationId(Integer operationId) {
        this.operationId = operationId;
    }

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

    public String getTradesNo() {
        return tradesNo;
    }

    public void setTradesNo(String tradesNo) {
        this.tradesNo = tradesNo;
    }

    public Integer getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Integer originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getChangePriceBefore() {
        return changePriceBefore;
    }

    public void setChangePriceBefore(Integer changePriceBefore) {
        this.changePriceBefore = changePriceBefore;
    }

    public Integer getChangePriceAfter() {
        return changePriceAfter;
    }

    public void setChangePriceAfter(Integer changePriceAfter) {
        this.changePriceAfter = changePriceAfter;
    }

    public Integer getOperationPlatform() {
        return operationPlatform;
    }

    public void setOperationPlatform(Integer operationPlatform) {
        this.operationPlatform = operationPlatform;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getOperationAccount() {
        return operationAccount;
    }

    public void setOperationAccount(String operationAccount) {
        this.operationAccount = operationAccount;
    }

    public String getOperationUser() {
        return operationUser;
    }

    public void setOperationUser(String operationUser) {
        this.operationUser = operationUser;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
