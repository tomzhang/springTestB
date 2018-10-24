package com.jk51.modules.task.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jk51.modules.task.domain.RewardRule;

import java.io.Serializable;
import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-08-22
 * 修改记录:
 */

// 忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class FollowTask implements Serializable {
    private Integer siteId;

    private Integer joinId;

    private String adminName;

    private Integer storeadminId;

    private String clerkInvitationCode;

    private String storeName;

    private String rewardDetail;

    private RewardRule rewardRule;

    private Integer countValue;

    private Integer rewardType;

    private Integer reward;

    private Date startDay;

    private Date endDay;

    private String storesNumber;

    private Integer adminCount;

    private Integer targetId;

    private String planName;

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public Integer getStoreadminId() {
        return storeadminId;
    }

    public void setStoreadminId(Integer storeadminId) {
        this.storeadminId = storeadminId;
    }

    public String getClerkInvitationCode() {
        return clerkInvitationCode;
    }

    public void setClerkInvitationCode(String clerkInvitationCode) {
        this.clerkInvitationCode = clerkInvitationCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getRewardDetail() {
        return rewardDetail;
    }

    public void setRewardDetail(String rewardDetail) {
        this.rewardDetail = rewardDetail;
    }

    public RewardRule getRewardRule() {
        return rewardRule;
    }

    public void setRewardRule(RewardRule rewardRule) {
        this.rewardRule = rewardRule;
    }

    public Integer getCountValue() {
        return countValue;
    }

    public void setCountValue(Integer countValue) {
        this.countValue = countValue;
    }

    public Integer getRewardType() {
        return rewardType;
    }

    public void setRewardType(Integer rewardType) {
        this.rewardType = rewardType;
    }

    public Integer getReward() {
        return reward;
    }

    public void setReward(Integer reward) {
        this.reward = reward;
    }

    public Date getStartDay() {
        return startDay;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
    }

    public Date getEndDay() {
        return endDay;
    }

    public void setEndDay(Date endDay) {
        this.endDay = endDay;
    }

    public String getStoresNumber() {
        return storesNumber;
    }

    public void setStoresNumber(String storesNumber) {
        this.storesNumber = storesNumber;
    }

    public Integer getAdminCount() {
        return adminCount;
    }

    public void setAdminCount(Integer adminCount) {
        this.adminCount = adminCount;
    }

    public Integer getJoinId() {
        return joinId;
    }

    public void setJoinId(Integer joinId) {
        this.joinId = joinId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
}
