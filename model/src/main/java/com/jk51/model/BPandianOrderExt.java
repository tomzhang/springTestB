package com.jk51.model;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 盘点单扩展
 * 作者: gaojie
 * 创建日期: 2017-10-18
 * 修改记录:
 */
public class BPandianOrderExt extends BPandianOrder {

    private Long inventoryTotal;  //账目库存数
    private Long actualStoreTotal; //实际库存
    private Long ProfitAndLossNum;   //盈亏数
    private String profitAndLossStatus; //盈亏状态
    private String storeName;
    private String storesNumber;
    private Integer status;  //盘点状态
    private Integer planCheckType;



    public Long getInventoryTotal() {
        return inventoryTotal;
    }

    public void setInventoryTotal(Long inventoryTotal) {
        this.inventoryTotal = inventoryTotal;
    }

    public Long getActualStoreTotal() {
        return actualStoreTotal;
    }

    public void setActualStoreTotal(Long actualStoreTotal) {
        this.actualStoreTotal = actualStoreTotal;
    }

    public Long getProfitAndLossNum() {
        return ProfitAndLossNum;
    }

    public void setProfitAndLossNum(Long profitAndLossNum) {
        ProfitAndLossNum = profitAndLossNum;
    }

    public String getProfitAndLossStatus() {
        return profitAndLossStatus;
    }

    public void setProfitAndLossStatus(String profitAndLossStatus) {
        this.profitAndLossStatus = profitAndLossStatus;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoresNumber() {
        return storesNumber;
    }

    public void setStoresNumber(String storesNumber) {
        this.storesNumber = storesNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPlanCheckType() {
        return planCheckType;
    }

    public void setPlanCheckType(Integer planCheckType) {
        this.planCheckType = planCheckType;
    }
}
