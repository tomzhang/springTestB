package com.jk51.model.order;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:  自提编码
 * 作者: hulan
 * 创建日期: 2017-03-23
 * 修改记录:
 */
public class Stockup {
    private Integer siteId;
    private Integer id;
    private String stockupId;
    private long tradesId;
    private Integer storeId;
    private Integer clerkId;
    private Integer stockupStatus;
    private Timestamp stockupTime;
    private Integer shippingStatus;
    private Timestamp shippingTime;
    private Timestamp createTime;
    private Timestamp updateTime;

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

    public String getStockupId() {
        return stockupId;
    }

    public void setStockupId(String stockupId) {
        this.stockupId = stockupId;
    }

    public long getTradesId() {
        return tradesId;
    }

    public void setTradesId(long tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getClerkId() {
        return clerkId;
    }

    public void setClerkId(Integer clerkId) {
        this.clerkId = clerkId;
    }

    public Integer getStockupStatus() {
        return stockupStatus;
    }

    public void setStockupStatus(Integer stockupStatus) {
        this.stockupStatus = stockupStatus;
    }

    public Timestamp getStockupTime() {
        return stockupTime;
    }

    public void setStockupTime(Timestamp stockupTime) {
        this.stockupTime = stockupTime;
    }

    public Integer getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(Integer shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public Timestamp getShippingTime() {
        return shippingTime;
    }

    public void setShippingTime(Timestamp shippingTime) {
        this.shippingTime = shippingTime;
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
}
