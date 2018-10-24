package com.jk51.model.distribute;

import java.io.Serializable;
import java.util.Date;

/**
 * @author
 */
public class DFunds implements Serializable {
    /**
     * 资金变动表
     */
    private Integer id;

    /**
     * 所属商家
     */
    private Integer owner;

    /**
     * 分销商ID
     */
    private Integer distributorId;

    /**
     * 交易号
     */
    private String tradesId;

    /**
     * 充值账号
     */
    private Integer storeAmount;

    /**
     * 账户余额
     */
    private Integer amount;

    /**
     * 金额，单位：分
     */
    private Integer money;

    /**
     * 支付方式：1：微信 2：银联
     */
    private String payType;

    /**
     *  1-充值 2-提现 3-消费 4-退款 
     */
    private Byte type;

    /**
     * 状态：1-失败 2-成功
     */
    private int status;

    /**
     * 支付时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public Integer getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getStoreAmount() {
        return storeAmount;
    }

    public void setStoreAmount(Integer storeAmount) {
        this.storeAmount = storeAmount;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
}