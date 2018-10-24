package com.jk51.model.distribute;

import java.io.Serializable;
import java.util.Date;

/**
 * @author
 */
public class DWithdrawRecord{
    /**
     * 提现记录
     */
    private Integer id;

    /**
     * 交易ID
     */
    private Long  tradesId;


    /**
     * 分销商id
     */
    private Integer distributorId;

    /**
     * 药店总部
     */
    private Integer owner;

    /**
     * 提现帐号
     */
    private String account;

    /**
     * 操作类型: 1.提现奖励
     */
    private Integer type;

    /**
     * 提现金额 单位：分
     */
    private Integer amount;

    /**
     * 买家提现方式: 100:ali (支付宝) ，200:wx (微信)，300:银联
     */
    private String withdrawStyle;

    /**
     * 支付平台手续费
     */
    private Integer withdrawFee;

    /**
     * 交易费用 单位：分
     */
    private Integer totalFee;

    /**
     * 状态  0：失败  1：成功  
     */
    private Integer payStatus;

    /**
     * 对账状态 0=未对账（默认） 1=已对账
     */
    private Integer checkingStatus;

    /**
     * 资金可结算状态 100=不结算 150=待结算 200=可结算
     */
    private Short settlementStatus;

    /**
     * 订单结算时间
     */
    private Date tradesTime;

    /**
     * 申请日期
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * distributor_money_id主键
     */
    private Integer moneyRecordId;

    /**
     * 支付结果
     */
    private String payResult;

    private static final long serialVersionUID = 1L;

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

    public Integer getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getWithdrawStyle() {
        return withdrawStyle;
    }

    public void setWithdrawStyle(String withdrawStyle) {
        this.withdrawStyle = withdrawStyle;
    }

    public Integer getWithdrawFee() {
        return withdrawFee;
    }

    public void setWithdrawFee(Integer withdrawFee) {
        this.withdrawFee = withdrawFee;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getCheckingStatus() {
        return checkingStatus;
    }

    public void setCheckingStatus(Integer checkingStatus) {
        this.checkingStatus = checkingStatus;
    }

    public Short getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(Short settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public Date getTradesTime() {
        return tradesTime;
    }

    public void setTradesTime(Date tradesTime) {
        this.tradesTime = tradesTime;
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

    public Integer getMoneyRecordId() {
        return moneyRecordId;
    }

    public void setMoneyRecordId(Integer moneyRecordId) {
        this.moneyRecordId = moneyRecordId;
    }

    public String getPayResult() {
        return payResult;
    }

    public void setPayResult(String payResult) {
        this.payResult = payResult;
    }
}