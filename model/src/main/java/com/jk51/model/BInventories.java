package com.jk51.model;

import java.util.Date;

public class BInventories {
    private Integer id;

    private Integer planId;

    private String pandianNum;

    private Integer storeId;

    private String goodsCode;

    private String drugName;

    private String specifCation;

    private String goodsCompany;

    private Double inventoryAccounting;

    private Double actualStore;

    private Date createTime;

    private Date updateTime;

    private Integer siteId;

    private Integer inventoryChecker;

    private String batchNumber;

    private String quality;

    private String storeNum;

    private Integer inventoryConfirm;

    private Integer isDel;

    private Integer orderId;

    private Integer erpDataSeq;

    private Integer esId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getPandianNum() {
        return pandianNum;
    }

    public void setPandianNum(String pandianNum) {
        this.pandianNum = pandianNum == null ? null : pandianNum.trim();
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode == null ? null : goodsCode.trim();
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName == null ? null : drugName.trim();
    }

    public String getSpecifCation() {
        return specifCation;
    }

    public void setSpecifCation(String specifCation) {
        this.specifCation = specifCation == null ? null : specifCation.trim();
    }

    public String getGoodsCompany() {
        return goodsCompany;
    }

    public void setGoodsCompany(String goodsCompany) {
        this.goodsCompany = goodsCompany == null ? null : goodsCompany.trim();
    }

    public Double getInventoryAccounting() {
        return inventoryAccounting;
    }

    public void setInventoryAccounting(Double inventoryAccounting) {
        this.inventoryAccounting = inventoryAccounting;
    }

    public Double getActualStore() {
        return actualStore;
    }

    public void setActualStore(Double actualStore) {
        this.actualStore = actualStore;
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

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getInventoryChecker() {
        return inventoryChecker;
    }

    public void setInventoryChecker(Integer inventoryChecker) {
        this.inventoryChecker = inventoryChecker;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber == null ? null : batchNumber.trim();
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality == null ? null : quality.trim();
    }

    public String getStoreNum() {
        return storeNum;
    }

    public void setStoreNum(String storeNum) {
        this.storeNum = storeNum == null ? null : storeNum.trim();
    }

    public Integer getInventoryConfirm() {
        return inventoryConfirm;
    }

    public void setInventoryConfirm(Integer inventoryConfirm) {
        this.inventoryConfirm = inventoryConfirm;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getErpDataSeq() {
        return erpDataSeq;
    }

    public void setErpDataSeq(Integer erpDataSeq) {
        this.erpDataSeq = erpDataSeq;
    }

    public Integer getEsId() {
        return esId;
    }

    public void setEsId(Integer esId) {
        this.esId = esId;
    }
}
