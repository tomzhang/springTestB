package com.jk51.model.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BTaskrewardLog implements Serializable {
    private Integer id;

    private Integer siteId;

    private Integer rewardId;

    private Byte joinType;

    private Integer joinId;

    private Integer executeId;

    private Integer taskId;

    private String taskName;

    private Byte rewardType;

    private Integer value;

    private Integer countValue;

    private String remark;

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

    public Integer getRewardId() {
        return rewardId;
    }

    public void setRewardId(Integer rewardId) {
        this.rewardId = rewardId;
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

    public Byte getRewardType() {
        return rewardType;
    }

    public void setRewardType(Byte rewardType) {
        this.rewardType = rewardType;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getCountValue() {
        return countValue;
    }

    public void setCountValue(Integer countValue) {
        this.countValue = countValue;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        BTaskrewardLog other = (BTaskrewardLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSiteId() == null ? other.getSiteId() == null : this.getSiteId().equals(other.getSiteId()))
            && (this.getRewardId() == null ? other.getRewardId() == null : this.getRewardId().equals(other.getRewardId()))
            && (this.getJoinType() == null ? other.getJoinType() == null : this.getJoinType().equals(other.getJoinType()))
            && (this.getJoinId() == null ? other.getJoinId() == null : this.getJoinId().equals(other.getJoinId()))
            && (this.getExecuteId() == null ? other.getExecuteId() == null : this.getExecuteId().equals(other.getExecuteId()))
            && (this.getTaskId() == null ? other.getTaskId() == null : this.getTaskId().equals(other.getTaskId()))
            && (this.getTaskName() == null ? other.getTaskName() == null : this.getTaskName().equals(other.getTaskName()))
            && (this.getRewardType() == null ? other.getRewardType() == null : this.getRewardType().equals(other.getRewardType()))
            && (this.getValue() == null ? other.getValue() == null : this.getValue().equals(other.getValue()))
            && (this.getCountValue() == null ? other.getCountValue() == null : this.getCountValue().equals(other.getCountValue()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSiteId() == null) ? 0 : getSiteId().hashCode());
        result = prime * result + ((getRewardId() == null) ? 0 : getRewardId().hashCode());
        result = prime * result + ((getJoinType() == null) ? 0 : getJoinType().hashCode());
        result = prime * result + ((getJoinId() == null) ? 0 : getJoinId().hashCode());
        result = prime * result + ((getExecuteId() == null) ? 0 : getExecuteId().hashCode());
        result = prime * result + ((getTaskId() == null) ? 0 : getTaskId().hashCode());
        result = prime * result + ((getTaskName() == null) ? 0 : getTaskName().hashCode());
        result = prime * result + ((getRewardType() == null) ? 0 : getRewardType().hashCode());
        result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
        result = prime * result + ((getCountValue() == null) ? 0 : getCountValue().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
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
        sb.append(", rewardId=").append(rewardId);
        sb.append(", joinType=").append(joinType);
        sb.append(", joinId=").append(joinId);
        sb.append(", executeId=").append(executeId);
        sb.append(", taskId=").append(taskId);
        sb.append(", taskName=").append(taskName);
        sb.append(", rewardType=").append(rewardType);
        sb.append(", value=").append(value);
        sb.append(", countValue=").append(countValue);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}