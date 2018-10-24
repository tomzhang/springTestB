package com.jk51.model.task;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class BTaskreward implements Serializable {
    private Integer id;

    /**
     * 商户id
     */
    private Integer siteId;

    /**
     * 任务执行计划id
     */
    private Integer executeId;

    /**
     * 任务id
     */
    private Integer taskId;

    /**
     * 任务名
     */
    private String taskName;

    /**
     * 计划id
     */
    private Integer planId;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 奖励对象 10门店 20 店员
     */
    private Byte joinType;

    /**
     * 奖励对象id 
     */
    private Integer joinId;

    /**
     * join_type=10 门店名称 join_type=20 店员名称
     */
    private String joinName;

    /**
     * 任务奖励 10 人民币 20 绩效
     */
    private Byte rewardType;

    /**
     * 奖励值 根据reward_type区分 如果是人名币 单位是分  奖励是正数  惩罚是负数
     */
    private Integer reward;

    /**
     * 统计值
     */
    private Integer countValue;

    /**
     * 一级目标完成时间
     */
    private Date completeTime;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getExecuteId() {
        return executeId;
    }

    public void setExecuteId(Integer executeId) {
        this.executeId = executeId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Byte getJoinType() {
        return joinType;
    }

    public void setJoinType(Byte joinType) {
        this.joinType = joinType;
    }

    public Integer getJoinId() {
        return joinId;
    }

    public void setJoinId(Integer joinId) {
        this.joinId = joinId;
    }

    public String getJoinName() {
        return joinName;
    }

    public void setJoinName(String joinName) {
        this.joinName = joinName;
    }

    public Byte getRewardType() {
        return rewardType;
    }

    public void setRewardType(Byte rewardType) {
        this.rewardType = rewardType;
    }

    public Integer getReward() {
        return reward;
    }

    public void setReward(Integer reward) {
        this.reward = reward;
    }

    public Integer getCountValue() {
        return countValue;
    }

    public void setCountValue(Integer countValue) {
        this.countValue = countValue;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
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

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
