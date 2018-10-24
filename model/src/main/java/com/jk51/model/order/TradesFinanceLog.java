package com.jk51.model.order;

import java.util.Date;

public class TradesFinanceLog {
    private Integer id;

    private Integer siteId;

    private String tradesId;

    private Date payTime;

    private Date endTime;

    private Integer realPay;

    private Integer refundFee;

    private Integer tradesSplit;

    private Integer platSplit;

    private Integer o2oFreight;

    private String payStyle;

    private Integer settlementType;

    private Integer tradesStatus;

    private Integer isPayment;

    private Integer isRefund;

    private Integer dealFinishStatus;

    private String financeNo;

    private String financeNoRefund;

    private Integer financeType;

    private Date createTime;

    private Date updateTime;


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

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId == null ? null : tradesId.trim();
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getRealPay() {
        return realPay;
    }

    public void setRealPay(Integer realPay) {
        this.realPay = realPay;
    }

    public Integer getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(Integer refundFee) {
        this.refundFee = refundFee;
    }

    public Integer getTradesSplit() {
        return tradesSplit;
    }

    public void setTradesSplit(Integer tradesSplit) {
        this.tradesSplit = tradesSplit;
    }

    public Integer getPlatSplit() {
        return platSplit;
    }

    public void setPlatSplit(Integer platSplit) {
        this.platSplit = platSplit;
    }

    public Integer getO2oFreight() {
        return o2oFreight;
    }

    public void setO2oFreight(Integer o2oFreight) {
        this.o2oFreight = o2oFreight;
    }

    public String getPayStyle() {
        return payStyle;
    }

    public void setPayStyle(String payStyle) {
        this.payStyle = payStyle == null ? null : payStyle.trim();
    }

    public Integer getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(Integer settlementType) {
        this.settlementType = settlementType;
    }

    public Integer getTradesStatus() {
        return tradesStatus;
    }

    public void setTradesStatus(Integer tradesStatus) {
        this.tradesStatus = tradesStatus;
    }

    public Integer getIsPayment() {
        return isPayment;
    }

    public void setIsPayment(Integer isPayment) {
        this.isPayment = isPayment;
    }

    public Integer getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(Integer isRefund) {
        this.isRefund = isRefund;
    }

    public Integer getDealFinishStatus() {
        return dealFinishStatus;
    }

    public void setDealFinishStatus(Integer dealFinishStatus) {
        this.dealFinishStatus = dealFinishStatus;
    }

    public String getFinanceNo() {
        return financeNo;
    }

    public void setFinanceNo(String financeNo) {
        this.financeNo = financeNo == null ? null : financeNo.trim();
    }

    public String getFinanceNoRefund() {
        return financeNoRefund;
    }

    public void setFinanceNoRefund(String financeNoRefund) {
        this.financeNoRefund = financeNoRefund == null ? null : financeNoRefund.trim();
    }

    public Integer getFinanceType() {
        return financeType;
    }

    public void setFinanceType(Integer financeType) {
        this.financeType = financeType;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", siteId=").append(siteId);
        sb.append(", tradesId=").append(tradesId);
        sb.append(", payTime=").append(payTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", realPay=").append(realPay);
        sb.append(", refundFee=").append(refundFee);
        sb.append(", tradesSplit=").append(tradesSplit);
        sb.append(", platSplit=").append(platSplit);
        sb.append(", o2oFreight=").append(o2oFreight);
        sb.append(", payStyle=").append(payStyle);
        sb.append(", settlementType=").append(settlementType);
        sb.append(", tradesStatus=").append(tradesStatus);
        sb.append(", isPayment=").append(isPayment);
        sb.append(", isRefund=").append(isRefund);
        sb.append(", dealFinishStatus=").append(dealFinishStatus);
        sb.append(", financeNo=").append(financeNo);
        sb.append(", financeNoRefund=").append(financeNoRefund);
        sb.append(", financeType=").append(financeType);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
