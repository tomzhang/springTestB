package com.jk51.model.task;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class BTaskExecute implements Serializable {
    private Integer id;

    /**
     * 任务id
     */
    private Integer taskId;

    /**
     * 计划id
     */
    private Integer planId;

    /**
     * 统计值
     */
    private Integer countValue;

    /**
     * 完成
     */
    private Boolean complete;

    /**
     * 开始日期
     */
    private Date startDay;

    /**
     * 结束日期
     */
    private Date endDay;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getCountValue() {
        return countValue;
    }

    public void setCountValue(Integer countValue) {
        this.countValue = countValue;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
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
        BTaskExecute other = (BTaskExecute) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTaskId() == null ? other.getTaskId() == null : this.getTaskId().equals(other.getTaskId()))
            && (this.getPlanId() == null ? other.getPlanId() == null : this.getPlanId().equals(other.getPlanId()))
            && (this.getCountValue() == null ? other.getCountValue() == null : this.getCountValue().equals(other.getCountValue()))
            && (this.getComplete() == null ? other.getComplete() == null : this.getComplete().equals(other.getComplete()))
            && (this.getStartDay() == null ? other.getStartDay() == null : this.getStartDay().equals(other.getStartDay()))
            && (this.getEndDay() == null ? other.getEndDay() == null : this.getEndDay().equals(other.getEndDay()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTaskId() == null) ? 0 : getTaskId().hashCode());
        result = prime * result + ((getPlanId() == null) ? 0 : getPlanId().hashCode());
        result = prime * result + ((getCountValue() == null) ? 0 : getCountValue().hashCode());
        result = prime * result + ((getComplete() == null) ? 0 : getComplete().hashCode());
        result = prime * result + ((getStartDay() == null) ? 0 : getStartDay().hashCode());
        result = prime * result + ((getEndDay() == null) ? 0 : getEndDay().hashCode());
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
        sb.append(", taskId=").append(taskId);
        sb.append(", planId=").append(planId);
        sb.append(", countValue=").append(countValue);
        sb.append(", complete=").append(complete);
        sb.append(", startDay=").append(startDay);
        sb.append(", endDay=").append(endDay);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}