package com.jk51.model.balance;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by Administrator on 2018/4/26.
 */
public class BalanceDetail {

    @JsonProperty("siteId")
    private Integer siteId;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("payChange")
    private Integer payChange;//金额变动

    @JsonProperty("balance")
    private Integer balance;//剩余余额

    @JsonProperty("applyStatus")
    private Integer applyStatus;//操作类型 0.线下充值（+）  1.退款（+）  2.抽佣（-）  3.返佣（+）  4.短信费用（-）

    @JsonProperty("balanceStatus")
    private Integer balanceStatus;//支付类型

    @JsonProperty("payStatus")
    private Integer payStatus;//支付类型

    @JsonProperty("storeId")
    private Integer storeId;//门店ID

    @JsonProperty("storeadminId")
    private Integer storeadminId;//店员ID

    @JsonProperty("auditStatus")
    private Integer auditStatus;//审核状态

    @JsonProperty("serialNum")
    private String serialNum;//流水号

    @JsonProperty("thirdSerialNum")
    private String thirdSerialNum;//第三方流水号

    @JsonProperty("tradesId")
    private Long tradesId;//流水号

    @JsonProperty("payTime")
    private Date payTime;//支付时间

    @JsonProperty("description")
    private String description;//描述

    @JsonProperty("clearingStatus")
    private Integer clearingStatus;//结算状态

    @JsonProperty("msgStatus")
    private Integer msgStatus;//结算状态

    @JsonProperty("billNum")
    private String billNum;//流水号

    @JsonProperty("hqRemark")
    private String hqRemark;//总部后台备注

    @JsonProperty("siteRemark")
    private String siteRemark;//商户备注

    @JsonProperty("accountTime")
    private Date accountTime;//创建时间

    @JsonProperty("createTime")
    private Date createTime;//创建时间

    @JsonProperty("updateTime")
    private Date updateTime;//修改时间

    @JsonProperty("phone")
    private String phone;//手机号

    public String getThirdSerialNum() {
        return thirdSerialNum;
    }

    public void setThirdSerialNum(String thirdSerialNum) {
        this.thirdSerialNum = thirdSerialNum;
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

    public Integer getPayChange() {
        return payChange;
    }

    public void setPayChange(Integer payChange) {
        this.payChange = payChange;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Integer applyStatus) {
        this.applyStatus = applyStatus;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
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

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getStoreadminId() {
        return storeadminId;
    }

    public void setStoreadminId(Integer storeadminId) {
        this.storeadminId = storeadminId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTradesId() {
        return tradesId;
    }

    public void setTradesId(Long tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getBalanceStatus() {
        return balanceStatus;
    }

    public void setBalanceStatus(Integer balanceStatus) {
        this.balanceStatus = balanceStatus;
    }

    public Integer getClearingStatus() {
        return clearingStatus;
    }

    public void setClearingStatus(Integer clearingStatus) {
        this.clearingStatus = clearingStatus;
    }

    public String getBillNum() {
        return billNum;
    }

    public void setBillNum(String billNum) {
        this.billNum = billNum;
    }

    public String getHqRemark() {
        return hqRemark;
    }

    public void setHqRemark(String hqRemark) {
        this.hqRemark = hqRemark;
    }

    public String getSiteRemark() {
        return siteRemark;
    }

    public void setSiteRemark(String siteRemark) {
        this.siteRemark = siteRemark;
    }

    public Integer getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(Integer msgStatus) {
        this.msgStatus = msgStatus;
    }

    public Date getAccountTime() {
        return accountTime;
    }

    public void setAccountTime(Date accountTime) {
        this.accountTime = accountTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
