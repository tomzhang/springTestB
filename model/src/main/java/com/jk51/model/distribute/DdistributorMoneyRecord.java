package com.jk51.model.distribute;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-14 13:18
 * 修改记录:
 */
public class DdistributorMoneyRecord {

    private Long id ; // NOT NULL AUTO_INCREMENT,
    private Long distributorId ; // NOT NULL COMMENT '分销商id',
    private Long rewardId ; // NOT NULL DEFAULT '0',
    private Long  fromDid ; // NOT NULL COMMENT '该奖励来自哪个分销商',
    private Long owner ; // NOT NULL,
    private Integer type ;//'操作类型  1：奖励  2：提现',
    private Long changeMoney ; // NOT NULL COMMENT '操作的金额 单位：分',
    private Long remainingMoney ; // NOT NULL DEFAULT '-1' COMMENT '余额 单位：分  -1表示刚插入  状态未确认',
    private Date createTime ;// '创建时间',
    private Date updateTime;
    private Integer status;//'状态  1：待处理  2：成功  3：失败',
    private Integer orderStatus ;// NOT NULL DEFAULT '1',
    private Long tradeId;//NOT NULL DEFAULT '0',
    private Integer withdrawFee ; // DEFAULT '0' COMMENT '提现手续费',
    private String momentRewardPattern;// DEFAULT NULL COMMENT '分佣时采用的模板',
    private String remark;//DEFAULT NULL COMMENT '拒绝提现的备注',

    public DdistributorMoneyRecord() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRewardId() {
        return rewardId;
    }

    public void setRewardId(Long rewardId) {
        this.rewardId = rewardId;
    }

    public void setDistributorId(Long distributorId) {
        this.distributorId = distributorId;
    }

    public void setFromDid(Long fromDid) {
        this.fromDid = fromDid;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getChangeMoney() {
        return changeMoney;
    }

    public void setChangeMoney(Long changeMoney) {
        this.changeMoney = changeMoney;
    }

    public Long getDistributorId() {
        return distributorId;
    }

    public Long getFromDid() {
        return fromDid;
    }

    public Long getRemainingMoney() {
        return remainingMoney;
    }

    public void setRemainingMoney(Long remainingMoney) {
        this.remainingMoney = remainingMoney;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public Integer getWithdrawFee() {
        return withdrawFee;
    }

    public void setWithdrawFee(Integer withdrawFee) {
        this.withdrawFee = withdrawFee;
    }

    public String getMomentRewardPattern() {
        return momentRewardPattern;
    }

    public void setMomentRewardPattern(String momentRewardPattern) {
        this.momentRewardPattern = momentRewardPattern;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
