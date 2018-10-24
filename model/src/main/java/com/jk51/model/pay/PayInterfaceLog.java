package com.jk51.model.pay;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-27
 * 修改记录:
 */
public class PayInterfaceLog {
    private Integer id;
    private Integer siteId;
    private Long tradesId;
    private Long refundNo;
    private String refundId;
    private String transactionId;
    private String payStyle;
    private String payInterface;
    private String payResult;
    private byte exeResult;
    private Date createDate;
    private Date updateDate;
    private Integer refundFee;
    private Integer tradesFee;
    private String tradesIdTime;

    public void setTradesIdTime(String tradesIdTime) {
        this.tradesIdTime = tradesIdTime;
    }

    public String getTradesIdTime() {
        return tradesIdTime;
    }

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

    public Long getTradesId() {
        return tradesId;
    }

    public void setTradesId(Long tradesId) {
        this.tradesId = tradesId;
    }

    public Long getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(Long refundNo) {
        this.refundNo = refundNo;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPayStyle() {
        return payStyle;
    }

    public void setPayStyle(String payStyle) {
        this.payStyle = payStyle;
    }

    public String getPayInterface() {
        return payInterface;
    }

    public void setPayInterface(String payInterface) {
        this.payInterface = payInterface;
    }

    public String getPayResult() {
        return payResult;
    }

    public void setPayResult(String payResult) {
        this.payResult = payResult;
    }

    public byte getExeResult() {
        return exeResult;
    }

    public void setExeResult(byte exeResult) {
        this.exeResult = exeResult;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(Integer refundFee) {
        this.refundFee = refundFee;
    }

    public Integer getTradesFee() {
        return tradesFee;
    }

    public void setTradesFee(Integer tradesFee) {
        this.tradesFee = tradesFee;
    }
}
