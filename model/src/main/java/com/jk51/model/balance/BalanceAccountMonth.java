package com.jk51.model.balance;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by Administrator on 2018/7/4.
 */
public class BalanceAccountMonth {
    @JsonProperty("siteId")
    private Integer siteId;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("lastBalance")
    private Integer lastBalance;//上期余额

    @JsonProperty("nowBalance")
    private Integer nowBalance;//本期余额

    @JsonProperty("upBalance")
    private Integer upBalance;//充值金额

    @JsonProperty("nowIncome")
    private Integer nowIncome;//本期收入

    @JsonProperty("nowExpend")
    private Integer nowExpend;//本期支出

    @JsonProperty("invoiceMoney")
    private Integer invoiceMoney;//开票金额

    @JsonProperty("invoiceStatus")
    private Integer invoiceStatus;//开票状态

    @JsonProperty("accountTime")
    private String accountTime;//结算时间

    @JsonProperty("makeInvoiceTime")
    private Date makeInvoiceTime;//开票时间

    @JsonProperty("invoiceNum")
    private String invoiceNum;//发票号码

    @JsonProperty("expressNum")
    private String expressNum;//快递单号

    @JsonProperty("expressFirm")
    private String expressFirm;//快递公司

    @JsonProperty("operator")
    private String operator;//操作人

    @JsonProperty("financeMark")
    private String financeMark;//财务备注

    @JsonProperty("siteMark")
    private String siteMark;//商家备注

    @JsonProperty("auditMark")
    private String auditMark;//审核备注

    @JsonProperty("auditStatus")
    private Integer auditStatus;//审核状态

    @JsonProperty("createTime")
    private Date createTime;//创建时间

    @JsonProperty("updateTime")
    private Date updateTime;//修改时间

    @JsonProperty("incomeReturnBrokerage")
    private Integer incomeReturnBrokerage;//收入（退佣金）

    @JsonProperty("expendBrokerage")
    private Integer expendBrokerage;//支出（佣金）

    @JsonProperty("expendMsgFee")
    private Integer expendMsgFee;//支出（短信费）

    @JsonProperty("expendPostFee")
    private Integer expendPostFee;//支出（运费）

    @JsonProperty("incomeReturnPostFee")
    private Integer incomeReturnPostFee;//收入（退运费）

    @JsonProperty("incomePresent")
    private Integer incomePresent;//收入（赠送）

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

    public Integer getLastBalance() {
        return lastBalance;
    }

    public void setLastBalance(Integer lastBalance) {
        this.lastBalance = lastBalance;
    }

    public Integer getNowBalance() {
        return nowBalance;
    }

    public void setNowBalance(Integer nowBalance) {
        this.nowBalance = nowBalance;
    }

    public Integer getUpBalance() {
        return upBalance;
    }

    public void setUpBalance(Integer upBalance) {
        this.upBalance = upBalance;
    }

    public Integer getNowIncome() {
        return nowIncome;
    }

    public void setNowIncome(Integer nowIncome) {
        this.nowIncome = nowIncome;
    }

    public Integer getNowExpend() {
        return nowExpend;
    }

    public void setNowExpend(Integer nowExpend) {
        this.nowExpend = nowExpend;
    }

    public Integer getInvoiceMoney() {
        return invoiceMoney;
    }

    public void setInvoiceMoney(Integer invoiceMoney) {
        this.invoiceMoney = invoiceMoney;
    }

    public Integer getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(Integer invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getAccountTime() {
        return accountTime;
    }

    public void setAccountTime(String accountTime) {
        this.accountTime = accountTime;
    }

    public Date getMakeInvoiceTime() {
        return makeInvoiceTime;
    }

    public void setMakeInvoiceTime(Date makeInvoiceTime) {
        this.makeInvoiceTime = makeInvoiceTime;
    }

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public String getExpressNum() {
        return expressNum;
    }

    public void setExpressNum(String expressNum) {
        this.expressNum = expressNum;
    }

    public String getExpressFirm() {
        return expressFirm;
    }

    public void setExpressFirm(String expressFirm) {
        this.expressFirm = expressFirm;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getFinanceMark() {
        return financeMark;
    }

    public void setFinanceMark(String financeMark) {
        this.financeMark = financeMark;
    }

    public String getSiteMark() {
        return siteMark;
    }

    public void setSiteMark(String siteMark) {
        this.siteMark = siteMark;
    }

    public String getAuditMark() {
        return auditMark;
    }

    public void setAuditMark(String auditMark) {
        this.auditMark = auditMark;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
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

    public Integer getIncomeReturnBrokerage() {
        return incomeReturnBrokerage;
    }

    public void setIncomeReturnBrokerage(Integer incomeReturnBrokerage) {
        this.incomeReturnBrokerage = incomeReturnBrokerage;
    }
}
