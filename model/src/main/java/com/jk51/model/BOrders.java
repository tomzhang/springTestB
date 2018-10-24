package com.jk51.model;

import java.util.Date;

public class BOrders {
    private Long orderId;

    private Integer goodsId;

    private String goodsTitle;

    private Integer goodsPrice;

    private Integer goodsNum;

    private Boolean goodsGifts;

    private String approvalNumber;

    private String specifCation;

    private Integer goodsCategory;

    private Long tradesId;

    private String goodsImgurl;

    private Integer ordersStatus;

    private String goodsCode;

    private Integer ybGoodsId;

    private String goodsBatchNo;

    private Date createTime;

    private Date updateTime;

    private Integer tradesSnapshot;
	
	private Integer id;

    private Integer siteId;

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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle == null ? null : goodsTitle.trim();
    }

    public Integer getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Integer goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public Boolean getGoodsGifts() {
        return goodsGifts;
    }

    public void setGoodsGifts(Boolean goodsGifts) {
        this.goodsGifts = goodsGifts;
    }

    public String getApprovalNumber() {
        return approvalNumber;
    }

    public void setApprovalNumber(String approvalNumber) {
        this.approvalNumber = approvalNumber == null ? null : approvalNumber.trim();
    }

    public String getSpecifCation() {
        return specifCation;
    }

    public void setSpecifCation(String specifCation) {
        this.specifCation = specifCation == null ? null : specifCation.trim();
    }

    public Integer getGoodsCategory() {
        return goodsCategory;
    }

    public void setGoodsCategory(Integer goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public Long getTradesId() {
        return tradesId;
    }

    public void setTradesId(Long tradesId) {
        this.tradesId = tradesId;
    }

    public String getGoodsImgurl() {
        return goodsImgurl;
    }

    public void setGoodsImgurl(String goodsImgurl) {
        this.goodsImgurl = goodsImgurl == null ? null : goodsImgurl.trim();
    }

    public Integer getOrdersStatus() {
        return ordersStatus;
    }

    public void setOrdersStatus(Integer ordersStatus) {
        this.ordersStatus = ordersStatus;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode == null ? null : goodsCode.trim();
    }

    public Integer getYbGoodsId() {
        return ybGoodsId;
    }

    public void setYbGoodsId(Integer ybGoodsId) {
        this.ybGoodsId = ybGoodsId;
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

    public Integer getTradesSnapshot() {
        return tradesSnapshot;
    }

    public void setTradesSnapshot(Integer tradesSnapshot) {
        this.tradesSnapshot = tradesSnapshot;
    }
}