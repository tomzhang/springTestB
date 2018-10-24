package com.jk51.model.task;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class BExamAnswerlog implements Serializable {
    private Integer id;

    /**
     * 商家编号
     */
    private Integer siteId;

    /**
     * 计划id
     */
    private Integer planId;

    /**
     * 任务id
     */
    private Integer taskId;

    /**
     * 执行计划id
     */
    private Integer executeId;

    /**
     * 试卷id
     */
    private Integer examId;

    /**
     * 店员名称
     */
    private String name;

    /**
     * 答题正确数
     */
    private Byte num;

    /**
     * 总题数
     */
    private Byte total;

    /**
     * 店员编号 对应b_storeadmin.id
     */
    private Integer storeAdminId;

    /**
     * 门店id
     */
    private Integer storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 店员邀请码
     */
    private String clerkInvitationCode;

    /**
     * 奖励值
     */
    private Integer reward;

    /**
     * 奖励类型 和b_task一致
     */
    private Byte rewardType;

    private String remark;

    /**
     * 答题信息 不包括题目信息 只包含问题和选择的答案编号
     */
    private String snapshot;

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

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getExecuteId() {
        return executeId;
    }

    public void setExecuteId(Integer executeId) {
        this.executeId = executeId;
    }

    public Integer getExamId() {
        return examId;
    }

    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getNum() {
        return num;
    }

    public void setNum(Byte num) {
        this.num = num;
    }

    public Byte getTotal() {
        return total;
    }

    public void setTotal(Byte total) {
        this.total = total;
    }

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getClerkInvitationCode() {
        return clerkInvitationCode;
    }

    public void setClerkInvitationCode(String clerkInvitationCode) {
        this.clerkInvitationCode = clerkInvitationCode;
    }

    public Integer getReward() {
        return reward;
    }

    public void setReward(Integer reward) {
        this.reward = reward;
    }

    public Byte getRewardType() {
        return rewardType;
    }

    public void setRewardType(Byte rewardType) {
        this.rewardType = rewardType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
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
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        BExamAnswerlog other = (BExamAnswerlog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSiteId() == null ? other.getSiteId() == null : this.getSiteId().equals(other.getSiteId()))
            && (this.getPlanId() == null ? other.getPlanId() == null : this.getPlanId().equals(other.getPlanId()))
            && (this.getTaskId() == null ? other.getTaskId() == null : this.getTaskId().equals(other.getTaskId()))
            && (this.getExecuteId() == null ? other.getExecuteId() == null : this.getExecuteId().equals(other.getExecuteId()))
            && (this.getExamId() == null ? other.getExamId() == null : this.getExamId().equals(other.getExamId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getNum() == null ? other.getNum() == null : this.getNum().equals(other.getNum()))
            && (this.getTotal() == null ? other.getTotal() == null : this.getTotal().equals(other.getTotal()))
            && (this.getStoreAdminId() == null ? other.getStoreAdminId() == null : this.getStoreAdminId().equals(other.getStoreAdminId()))
            && (this.getStoreId() == null ? other.getStoreId() == null : this.getStoreId().equals(other.getStoreId()))
            && (this.getStoreName() == null ? other.getStoreName() == null : this.getStoreName().equals(other.getStoreName()))
            && (this.getClerkInvitationCode() == null ? other.getClerkInvitationCode() == null : this.getClerkInvitationCode().equals(other.getClerkInvitationCode()))
            && (this.getReward() == null ? other.getReward() == null : this.getReward().equals(other.getReward()))
            && (this.getRewardType() == null ? other.getRewardType() == null : this.getRewardType().equals(other.getRewardType()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getSnapshot() == null ? other.getSnapshot() == null : this.getSnapshot().equals(other.getSnapshot()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSiteId() == null) ? 0 : getSiteId().hashCode());
        result = prime * result + ((getPlanId() == null) ? 0 : getPlanId().hashCode());
        result = prime * result + ((getTaskId() == null) ? 0 : getTaskId().hashCode());
        result = prime * result + ((getExecuteId() == null) ? 0 : getExecuteId().hashCode());
        result = prime * result + ((getExamId() == null) ? 0 : getExamId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getNum() == null) ? 0 : getNum().hashCode());
        result = prime * result + ((getTotal() == null) ? 0 : getTotal().hashCode());
        result = prime * result + ((getStoreAdminId() == null) ? 0 : getStoreAdminId().hashCode());
        result = prime * result + ((getStoreId() == null) ? 0 : getStoreId().hashCode());
        result = prime * result + ((getStoreName() == null) ? 0 : getStoreName().hashCode());
        result = prime * result + ((getClerkInvitationCode() == null) ? 0 : getClerkInvitationCode().hashCode());
        result = prime * result + ((getReward() == null) ? 0 : getReward().hashCode());
        result = prime * result + ((getRewardType() == null) ? 0 : getRewardType().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getSnapshot() == null) ? 0 : getSnapshot().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", siteId=").append(siteId);
        sb.append(", planId=").append(planId);
        sb.append(", taskId=").append(taskId);
        sb.append(", executeId=").append(executeId);
        sb.append(", examId=").append(examId);
        sb.append(", name=").append(name);
        sb.append(", num=").append(num);
        sb.append(", total=").append(total);
        sb.append(", storeAdminId=").append(storeAdminId);
        sb.append(", storeId=").append(storeId);
        sb.append(", storeName=").append(storeName);
        sb.append(", clerkInvitationCode=").append(clerkInvitationCode);
        sb.append(", reward=").append(reward);
        sb.append(", rewardType=").append(rewardType);
        sb.append(", remark=").append(remark);
        sb.append(", snapshot=").append(snapshot);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}