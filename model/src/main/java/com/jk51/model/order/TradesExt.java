package com.jk51.model.order;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:  交易拓展表
 * 作者: hulan
 * 创建日期: 2017-02-20
 * 修改记录:
 */
public class TradesExt {
    private Integer siteId;
    private Integer tradesExtId;
    private long tradesId;
    private String originalGoodsData;
    private Integer integralUsed;
    private Integer integralPreAward;
    private Integer integralFinalAward;
    private Integer isFirstOrder;
    private Integer integralPrice;
    private Integer reduceReductionAmount;
    private Integer bjDiscountAmount;
    private Integer userCouponId;
    private Integer userCouponAmount;
    private Timestamp tradesRankTime;
    private Integer tradesRank;
    private Integer cashPaymentPay;
    private Integer medicalInsuranceCardPay;
    private Integer lineBreaksPay;
    private String cashReceiptNote;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Integer distance;

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getTradesExtId() {
        return tradesExtId;
    }

    public void setTradesExtId(Integer tradesExtId) {
        this.tradesExtId = tradesExtId;
    }

    public long getTradesId() {
        return tradesId;
    }

    public void setTradesId(long tradesId) {
        this.tradesId = tradesId;
    }

    public String getOriginalGoodsData() {
        return originalGoodsData;
    }

    public void setOriginalGoodsData(String originalGoodsData) {
        this.originalGoodsData = originalGoodsData;
    }

    public Integer getIntegralUsed() {
        return integralUsed;
    }

    public void setIntegralUsed(Integer integralUsed) {
        this.integralUsed = integralUsed;
    }

    public Integer getIntegralPreAward() {
        return integralPreAward;
    }

    public void setIntegralPreAward(Integer integralPreAward) {
        this.integralPreAward = integralPreAward;
    }

    public Integer getIntegralFinalAward() {
        return integralFinalAward;
    }

    public void setIntegralFinalAward(Integer integralFinalAward) {
        this.integralFinalAward = integralFinalAward;
    }

    public Integer getIsFirstOrder() {
        return isFirstOrder;
    }

    public void setIsFirstOrder(Integer isFirstOrder) {
        this.isFirstOrder = isFirstOrder;
    }

    public Integer getIntegralPrice() {
        return integralPrice;
    }

    public void setIntegralPrice(Integer integralPrice) {
        this.integralPrice = integralPrice;
    }

    public Integer getReduceReductionAmount() {
        return reduceReductionAmount;
    }

    public void setReduceReductionAmount(Integer reduceReductionAmount) {
        this.reduceReductionAmount = reduceReductionAmount;
    }

    public Integer getBjDiscountAmount() {
        return bjDiscountAmount;
    }

    public void setBjDiscountAmount(Integer bjDiscountAmount) {
        this.bjDiscountAmount = bjDiscountAmount;
    }

    public Integer getUserCouponId() {
        return userCouponId;
    }

    public void setUserCouponId(Integer userCouponId) {
        this.userCouponId = userCouponId;
    }

    public Integer getUserCouponAmount() {
        return userCouponAmount;
    }

    public void setUserCouponAmount(Integer userCouponAmount) {
        this.userCouponAmount = userCouponAmount;
    }

    public Timestamp getTradesRankTime() {
        return tradesRankTime;
    }

    public void setTradesRankTime(Timestamp tradesRankTime) {
        this.tradesRankTime = tradesRankTime;
    }

    public Integer getTradesRank() {
        return tradesRank;
    }

    public void setTradesRank(Integer tradesRank) {
        this.tradesRank = tradesRank;
    }

    public Integer getCashPaymentPay() {
        return cashPaymentPay;
    }

    public void setCashPaymentPay(Integer cashPaymentPay) {
        this.cashPaymentPay = cashPaymentPay;
    }

    public Integer getMedicalInsuranceCardPay() {
        return medicalInsuranceCardPay;
    }

    public void setMedicalInsuranceCardPay(Integer medicalInsuranceCardPay) {
        this.medicalInsuranceCardPay = medicalInsuranceCardPay;
    }

    public Integer getLineBreaksPay() {
        return lineBreaksPay;
    }

    public void setLineBreaksPay(Integer lineBreaksPay) {
        this.lineBreaksPay = lineBreaksPay;
    }

    public String getCashReceiptNote() {
        return cashReceiptNote;
    }

    public void setCashReceiptNote(String cashReceiptNote) {
        this.cashReceiptNote = cashReceiptNote;
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

    @Override
    public String toString() {
        return "TradesExt{" +
                "siteId=" + siteId +
                ", tradesExtId=" + tradesExtId +
                ", tradesId=" + tradesId +
                ", originalGoodsData='" + originalGoodsData + '\'' +
                ", integralUsed=" + integralUsed +
                ", integralPreAward=" + integralPreAward +
                ", integralFinalAward=" + integralFinalAward +
                ", isFirstOrder=" + isFirstOrder +
                ", integralPrice=" + integralPrice +
                ", reduceReductionAmount=" + reduceReductionAmount +
                ", bjDiscountAmount=" + bjDiscountAmount +
                ", userCouponId=" + userCouponId +
                ", userCouponAmount=" + userCouponAmount +
                ", tradesRankTime=" + tradesRankTime +
                ", tradesRank=" + tradesRank +
                ", cashPaymentPay=" + cashPaymentPay +
                ", medicalInsuranceCardPay=" + medicalInsuranceCardPay +
                ", lineBreaksPay=" + lineBreaksPay +
                ", cashReceiptNote='" + cashReceiptNote + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
