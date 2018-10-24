package com.jk51.modules.pandian.Response;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-02
 * 修改记录:
 */
public class PandianOrderResponse {

    private String pandian_num;
    private String storeName;
    private Date createTime;
    private String pandianType;   //盘点类型
    private Double inventoryTotal;  //账目库存
    private Double actualStoreTotal; //实际库存
    private Double ProfitAndLossNum;  //盈亏数量
    private String ProfitAndLossStatus; //盈亏状态
    private String statu;               //盘点状态
    private Integer statusNum;
    private Integer plan_id;
    private Integer storeId;
    private Integer pandianOrderId;
    private Integer isUpSite;
    private String storesNumber;
    private String billid;
    private long countInventoryed;
    private long countNotInventoryed;
    private long checkerNum;
    private Long planCheckerNum;

    public String getPandian_num() {
        return pandian_num;
    }

    public void setPandian_num(String pandian_num) {
        this.pandian_num = pandian_num;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getPandianType() {
        return pandianType;
    }

    public void setPandianType(String pandianType) {
        this.pandianType = pandianType;
    }

    public Double getInventoryTotal() {
        return inventoryTotal;
    }

    public void setInventoryTotal(Double inventoryTotal) {
        this.inventoryTotal = inventoryTotal;
    }

    public Double getActualStoreTotal() {
        return actualStoreTotal;
    }

    public void setActualStoreTotal(Double actualStoreTotal) {
        this.actualStoreTotal = actualStoreTotal;
    }

    public Double getProfitAndLossNum() {
        return ProfitAndLossNum;
    }

    public void setProfitAndLossNum(Double profitAndLossNum) {
        ProfitAndLossNum = profitAndLossNum;
    }

    public String getProfitAndLossStatus() {
        return ProfitAndLossStatus;
    }

    public void setProfitAndLossStatus(String profitAndLossStatus) {
        ProfitAndLossStatus = profitAndLossStatus;
    }

    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    public Integer getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(Integer plan_id) {
        this.plan_id = plan_id;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getPandianOrderId() {
        return pandianOrderId;
    }

    public void setPandianOrderId(Integer pandianOrderId) {
        this.pandianOrderId = pandianOrderId;
    }

    public Integer getIsUpSite() {
        return isUpSite;
    }

    public void setIsUpSite(Integer isUpSite) {
        this.isUpSite = isUpSite;
    }

    public String getStoresNumber() {
        return storesNumber;
    }

    public void setStoresNumber(String storesNumber) {
        this.storesNumber = storesNumber;
    }

    public String getBillid() {
        return billid;
    }

    public void setBillid(String billid) {
        this.billid = billid;
    }

    public long getCountInventoryed() {
        return countInventoryed;
    }

    public void setCountInventoryed(long countInventoryed) {
        this.countInventoryed = countInventoryed;
    }

    public long getCountNotInventoryed() {
        return countNotInventoryed;
    }

    public void setCountNotInventoryed(long countNotInventoryed) {
        this.countNotInventoryed = countNotInventoryed;
    }

    public long getCheckerNum() {
        return checkerNum;
    }

    public void setCheckerNum(long checkerNum) {
        this.checkerNum = checkerNum;
    }

    public Long getPlanCheckerNum() {
        return planCheckerNum;
    }

    public void setPlanCheckerNum(Long planCheckerNum) {
        this.planCheckerNum = planCheckerNum;
    }

    public Integer getStatusNum() {
        return statusNum;
    }

    public void setStatusNum(Integer statusNum) {
        this.statusNum = statusNum;
    }
}
