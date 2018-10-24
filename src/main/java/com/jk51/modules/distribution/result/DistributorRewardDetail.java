package com.jk51.modules.distribution.result;//package com.jk51.modules.distribution.result;

import java.io.Serializable;
import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-14 13:18
 * 修改记录:
 */
public class DistributorRewardDetail implements Serializable {

    private Long id ; // NOT NULL AUTO_INCREMENT,
    private String mobile;
    private Integer distributorId ; // NOT NULL COMMENT '分销商id',
    private Integer rewardId ; // NOT NULL DEFAULT '0',
    private Integer owner ; // NOT NULL,
    private Integer type ;//'操作类型  1：奖励  2：提现',
    private Integer changeMoney ; // NOT NULL COMMENT '操作的金额 单位：分',
    private Integer remainingMoney ; // NOT NULL DEFAULT '-1' COMMENT '余额 单位：分  -1表示刚插入  状态未确认',
    private Date updateTime;//奖励时间
    private Integer status;//'状态  1：待处理  2：成功  3：失败',
    private Integer withdrawFee ; // DEFAULT '0' COMMENT '提现手续费',
    private String orderId;
    private Integer level;
    private Date createTime;//下单时间
    private Long totalFee;
    private Integer fromDid;
    private String commissionLevel;
    private String orderUsername;
    private Date recordCreateTime;
    private String withdrawStyle;
    private Integer awardOne;
    private Integer awardTwo;
    private Integer awardThree;


    public DistributorRewardDetail() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public Integer getRewardId() {
        return rewardId;
    }

    public void setRewardId(Integer rewardId) {
        this.rewardId = rewardId;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getChangeMoney() {
        return changeMoney;
    }

    public void setChangeMoney(Integer changeMoney) {
        this.changeMoney = changeMoney;
    }

    public Integer getRemainingMoney() {
        return remainingMoney;
    }

    public void setRemainingMoney(Integer remainingMoney) {
        this.remainingMoney = remainingMoney;
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

    public Integer getWithdrawFee() {
        return withdrawFee;
    }

    public void setWithdrawFee(Integer withdrawFee) {
        this.withdrawFee = withdrawFee;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getFromDid() {
        return fromDid;
    }

    public void setFromDid(Integer fromDid) {
        this.fromDid = fromDid;
    }

    public String getCommissionLevel() {
        return commissionLevel;
    }

    public void setCommissionLevel(String commissionLevel) {
        this.commissionLevel = commissionLevel;
    }

    public String getOrderUsername() {
        return orderUsername;
    }

    public void setOrderUsername(String orderUsername) {
        this.orderUsername = orderUsername;
    }

    public Date getRecordCreateTime() {
        return recordCreateTime;
    }

    public void setRecordCreateTime(Date recordCreateTime) {
        this.recordCreateTime = recordCreateTime;
    }

    public String getWithdrawStyle() {
        return withdrawStyle;
    }

    public void setWithdrawStyle(String withdrawStyle) {
        this.withdrawStyle = withdrawStyle;
    }

    public Integer getAwardOne() {
        return awardOne;
    }

    public void setAwardOne(Integer awardOne) {
        this.awardOne = awardOne;
    }

    public Integer getAwardTwo() {
        return awardTwo;
    }

    public void setAwardTwo(Integer awardTwo) {
        this.awardTwo = awardTwo;
    }

    public Integer getAwardThree() {
        return awardThree;
    }

    public void setAwardThree(Integer awardThree) {
        this.awardThree = awardThree;
    }
}
