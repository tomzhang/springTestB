package com.jk51.model;

import java.util.Date;

public class GoodsExtd {
    private Integer siteId;
    private Integer goodsextdId;

    private Integer goodsId;

    private Integer browseNumber;

    private Integer transMumber;

    private Integer shoppingNumber;

    private Date productDate;

    private String goodsBatchNo;

    private String netWt;

    private Date createTime;

    private Date updateTime;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getGoodsextdId() {
        return goodsextdId;
    }

    public void setGoodsextdId(Integer goodsextdId) {
        this.goodsextdId = goodsextdId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getBrowseNumber() {
        return browseNumber;
    }

    public void setBrowseNumber(Integer browseNumber) {
        this.browseNumber = browseNumber;
    }

    public Integer getTransMumber() {
        return transMumber;
    }

    public void setTransMumber(Integer transMumber) {
        this.transMumber = transMumber;
    }

    public Integer getShoppingNumber() {
        return shoppingNumber;
    }

    public void setShoppingNumber(Integer shoppingNumber) {
        this.shoppingNumber = shoppingNumber;
    }

    public Date getProductDate() {
        return productDate;
    }

    public void setProductDate(Date productDate) {
        this.productDate = productDate;
    }

    public String getGoodsBatchNo() {
        return goodsBatchNo;
    }

    public void setGoodsBatchNo(String goodsBatchNo) {
        this.goodsBatchNo = goodsBatchNo == null ? null : goodsBatchNo.trim();
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

    public String getNetWt() {
        return netWt;
    }

    public void setNetWt(String netWt) {
        this.netWt = netWt;
    }
}