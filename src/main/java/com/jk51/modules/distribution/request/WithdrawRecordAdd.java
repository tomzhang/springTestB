package com.jk51.modules.distribution.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/26.
 */
public class WithdrawRecordAdd {
	/**
	 * 交易ID
	 */
	private Long tradesId;

	/**
	 * 分销商id
	 */
	@NotNull(message = "distributorId不能为空")
	private Integer distributorId;

	/**
	 * 药店总部
	 */
	@NotNull(message = "owner为空")
	private Integer owner;

	/**
	 * 提现帐号
	 */
	@NotNull(message = "account不能为空")
	private String account;
	
	/**
	 * 操作类型: 1.提现奖励
	 */
	private Integer type;

	/**
	 * 提现金额 单位：分
	 */
	@NotNull(message = "amount不能为空")
	private Integer amount;

	/**
	 * 买家提现方式: 100:ali (支付宝) ，200:wx (微信)，300:银联
	 */
	@NotNull(message = "withdrawStyle不能为空")
	private String withdrawStyle;

	/**
	 * 支付平台手续费
	 */
//	@NotNull(message = "withdrawFee为空")
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
	 * distributor_money_id主键
	 */
	private Integer moneyRecordId;

	/**
	 * 支付结果
	 */
	private String payResult;
	
	@NotNull(message = "remainingMoney为空")
	private Integer remainingMoney;
	
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

	public Integer getRemainingMoney() {
		return remainingMoney;
	}

	public void setRemainingMoney(Integer remainingMoney) {
		this.remainingMoney = remainingMoney;
	}
}
