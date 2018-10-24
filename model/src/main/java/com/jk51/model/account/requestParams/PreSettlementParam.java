package com.jk51.model.account.requestParams;

import com.jk51.model.order.Page;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 结算列表所需参数
 * 作者: chen_pt
 * 创建日期: 2017/7/25
 * 修改记录:
 */
public class PreSettlementParam extends Page {
    private Integer merchantId;             //商家编号
    private String merchantName;            //商家名称
    private String tradesId;                //订单号
    private Integer settlementStatus;       //商家可见状态
    private String financeNo;               //账单编号

    private String pactBillDayStartTime;    //合同出账日开始时间
    private String pactBillDayEndTime;      //合同出账日结束时间
    private String pactPayDayStartTime;     //合同结账日开始时间
    private String pactPayDayEndTime;       //合同结账日结束时间

    private String payDayStartTime;         //实际出账日开始时间
    private String payDayEndTime;           //实际出账日结束时间
    private String payDateStartTime;        //实际结账日开始时间
    private String payDateEndTime;          //实际结账日结束时间

    private Integer setType;                //合同结算周期
    private Integer setValueStart;          //合同付款日开始
    private Integer setValueEnd;            //合同付款日结束

    public String getFinanceNo() {
        return financeNo;
    }

    public void setFinanceNo(String financeNo) {
        this.financeNo = financeNo;
    }

    public Integer getSetType() {
        return setType;
    }

    public void setSetType(Integer setType) {
        this.setType = setType;
    }

    public Integer getSetValueStart() {
        return setValueStart;
    }

    public void setSetValueStart(Integer setValueStart) {
        this.setValueStart = setValueStart;
    }

    public Integer getSetValueEnd() {
        return setValueEnd;
    }

    public void setSetValueEnd(Integer setValueEnd) {
        this.setValueEnd = setValueEnd;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(Integer settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public String getPactBillDayStartTime() {
        return pactBillDayStartTime;
    }

    public void setPactBillDayStartTime(String pactBillDayStartTime) {
        this.pactBillDayStartTime = pactBillDayStartTime;
    }

    public String getPactBillDayEndTime() {
        return pactBillDayEndTime;
    }

    public void setPactBillDayEndTime(String pactBillDayEndTime) {
        this.pactBillDayEndTime = pactBillDayEndTime;
    }

    public String getPactPayDayStartTime() {
        return pactPayDayStartTime;
    }

    public void setPactPayDayStartTime(String pactPayDayStartTime) {
        this.pactPayDayStartTime = pactPayDayStartTime;
    }

    public String getPactPayDayEndTime() {
        return pactPayDayEndTime;
    }

    public void setPactPayDayEndTime(String pactPayDayEndTime) {
        this.pactPayDayEndTime = pactPayDayEndTime;
    }

    public String getPayDayStartTime() {
        return payDayStartTime;
    }

    public void setPayDayStartTime(String payDayStartTime) {
        this.payDayStartTime = payDayStartTime;
    }

    public String getPayDayEndTime() {
        return payDayEndTime;
    }

    public void setPayDayEndTime(String payDayEndTime) {
        this.payDayEndTime = payDayEndTime;
    }

    public String getPayDateStartTime() {
        return payDateStartTime;
    }

    public void setPayDateStartTime(String payDateStartTime) {
        this.payDateStartTime = payDateStartTime;
    }

    public String getPayDateEndTime() {
        return payDateEndTime;
    }

    public void setPayDateEndTime(String payDateEndTime) {
        this.payDateEndTime = payDateEndTime;
    }
}
