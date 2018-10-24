package com.jk51.modules.distribution.result;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-14 13:18
 * 修改记录:
 */
public class DistributorReward implements Serializable {

    private Integer distributorId ; // NOT NULL COMMENT  分销商id ,
    private Integer owner ; // NOT NULL,
    private String mobile;

    private Integer rewardCount;
    private Integer expenditureCount;
    private Integer remainingCount;

    private List<Integer> changeMoneyList;

    public DistributorReward() {

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<Integer> getChangeMoneyList() {
        return changeMoneyList;
    }

    public void setChangeMoneyList(List<Integer> changeMoneyList) {
        this.changeMoneyList = changeMoneyList;
    }

    public Integer getRewardCount() {
        return rewardCount;
    }

    public void setRewardCount(Integer rewardCount) {
        this.rewardCount = rewardCount;
    }

    public Integer getExpenditureCount() {
        return expenditureCount;
    }

    public void setExpenditureCount(Integer expenditureCount) {
        this.expenditureCount = expenditureCount;
    }

    public Integer getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(Integer remainingCount) {
        this.remainingCount = remainingCount;
    }
}
