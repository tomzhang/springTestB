package com.jk51.modules.task.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 订单销售数据
 */
@JsonIgnoreProperties
public class TradesSale {
    /**
     * 商户id
     */
    private Integer siteId;

    /**
     * 订单id
     */
    private Long tradesId;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 推荐店员
     */
    private Integer recommendUserId;

    private Integer storeUserId;

    /**
     * 商品价格
     */
    private Integer goodsPrice;

    /**
     * 总价包含运费
     */
    private Integer totalFee;

    /**
     * 运费
     */
    private Integer postFee;

    /**
     * 实付款
     */
    private Integer realPay;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Long getTradesId() {
        return tradesId;
    }

    public void setTradesId(Long tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public Integer getRecommendUserId() {
        return recommendUserId;
    }

    public void setRecommendUserId(Integer recommendUserId) {
        this.recommendUserId = recommendUserId;
    }

    public Integer getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Integer goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getPostFee() {
        return postFee;
    }

    public void setPostFee(Integer postFee) {
        this.postFee = postFee;
    }

    public Integer getRealPay() {
        return realPay;
    }

    public void setRealPay(Integer realPay) {
        this.realPay = realPay;
    }

    public Integer getStoreUserId() {
        return storeUserId;
    }

    public void setStoreUserId(Integer storeUserId) {
        this.storeUserId = storeUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradesSale that = (TradesSale) o;

        return tradesId.equals(that.tradesId);
    }

    @Override
    public int hashCode() {
        return tradesId.hashCode();
    }

    @Override
    public String toString() {
        return "TradesSale{" +
                "siteId=" + siteId +
                ", tradesId=" + tradesId +
                ", goodsId=" + goodsId +
                ", goodsNum=" + goodsNum +
                ", recommendUserId=" + recommendUserId +
                ", storeUserId=" + storeUserId +
                ", goodsPrice=" + goodsPrice +
                ", totalFee=" + totalFee +
                ", postFee=" + postFee +
                ", realPay=" + realPay +
                '}';
    }
}
