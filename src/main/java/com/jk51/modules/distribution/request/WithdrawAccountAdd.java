package com.jk51.modules.distribution.request;


import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/24.
 */
public class WithdrawAccountAdd {
	/**
	  * 分销商id
     */
	@NotNull(message = "distributorId不能为空")
	private Integer distributorId;

	/**
	 * 药店总部
	 */
	@NotNull(message = "owner不能为空")
	private Integer owner;

	/**
	 * 开户人姓名
	 */
	@NotNull(message = "name不能为空")
	private String name;

	/**
	 * 提现帐号
	 */
	@NotNull(message = "account不能为空")
	private String account;

	/**
	 * 账号类型: 100:ali (支付宝) ，200:wx (微信)，300:银联
	 */
	@NotNull(message = "type不能为空")
	private String type;

	/**
	 * 开户行名称
	 */
	private String bandName;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBandName() {
		return bandName;
	}

	public void setBandName(String bandName) {
		this.bandName = bandName;
	}
	
	
}
