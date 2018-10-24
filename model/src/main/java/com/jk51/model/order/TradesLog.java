package com.jk51.model.order;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-02-15
 * 修改记录:
 */
public class TradesLog {
    private Integer id;
    private long tradesId;
    private Integer sellerId;
    private Integer buyerId;
    private Integer newTradesStatus;
    private Integer oldTradesStatus;
    private Integer stockupStatus;  //备货状态
    private Integer shippingStatus; //发货状态
    private String sourceBusiness;   //业务来源
    private Timestamp createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getTradesId() {
        return tradesId;
    }

    public void setTradesId(long tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getNewTradesStatus() {
        return newTradesStatus;
    }

    public void setNewTradesStatus(Integer newTradesStatus) {
        this.newTradesStatus = newTradesStatus;
    }

    public Integer getOldTradesStatus() {
        return oldTradesStatus;
    }

    public void setOldTradesStatus(Integer oldTradesStatus) {
        this.oldTradesStatus = oldTradesStatus;
    }

    public Integer getStockupStatus() {
        return stockupStatus;
    }

    public void setStockupStatus(Integer stockupStatus) {
        this.stockupStatus = stockupStatus;
    }

    public Integer getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(Integer shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public String getSourceBusiness() {
        return sourceBusiness;
    }

    public void setSourceBusiness(String sourceBusiness) {
        this.sourceBusiness = sourceBusiness;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
