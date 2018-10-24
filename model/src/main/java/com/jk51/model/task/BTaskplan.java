package com.jk51.model.task;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class BTaskplan implements Serializable {
    private Integer id;

    /**
     * 商户编号
     */
    private Integer siteId;

    /**
     * 任务计划名称
     */
    private String name;

    /**
     * 任务id列表用,分隔
     */
    private String taskIds;

    /**
     * 发送对象 10 门店 20 店员
     */
    private Byte joinType;

    private Date startTime;

    private Date endTime;

    /**
     * 任务有效日类型 10 每天 20 每周 30 每月
     */
    private Byte activeType;

    /**
     * 每天 这个字段为空 每周 1,2,3,4,5,6,7 每月1,2,....31
     */
    private String dayNum;

    /**
     * 发送者 10 总部 20 门店
     */
    private Byte sourceType;

    /**
     * 账号id
     */
    private Integer adminId;

    /**
     * 账号名称
     */
    private String adminName;

    /**
     * app显示位置 10 任务列表页
     */
    private Byte appPosition;

    /**
     * 说明
     */
    private String explan;

    /**
     * 计划状态 10 未开始 20 进行中 30 已结束
     */
    private Byte status;

    private Date createTime;

    private Date updateTime;

    /**
     * 发送对象id列表 用,分割
     */
    private String joinIds;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(String taskIds) {
        this.taskIds = taskIds;
    }

    public Byte getJoinType() {
        return joinType;
    }

    public void setJoinType(Byte joinType) {
        this.joinType = joinType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Byte getActiveType() {
        return activeType;
    }

    public void setActiveType(Byte activeType) {
        this.activeType = activeType;
    }

    public String getDayNum() {
        return dayNum;
    }

    public void setDayNum(String dayNum) {
        this.dayNum = dayNum;
    }

    public Byte getSourceType() {
        return sourceType;
    }

    public void setSourceType(Byte sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public Byte getAppPosition() {
        return appPosition;
    }

    public void setAppPosition(Byte appPosition) {
        this.appPosition = appPosition;
    }

    public String getExplan() {
        return explan;
    }

    public void setExplan(String explan) {
        this.explan = explan;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
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

    public String getJoinIds() {
        return joinIds;
    }

    public void setJoinIds(String joinIds) {
        this.joinIds = joinIds;
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
        BTaskplan other = (BTaskplan) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSiteId() == null ? other.getSiteId() == null : this.getSiteId().equals(other.getSiteId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getTaskIds() == null ? other.getTaskIds() == null : this.getTaskIds().equals(other.getTaskIds()))
            && (this.getJoinType() == null ? other.getJoinType() == null : this.getJoinType().equals(other.getJoinType()))
            && (this.getStartTime() == null ? other.getStartTime() == null : this.getStartTime().equals(other.getStartTime()))
            && (this.getEndTime() == null ? other.getEndTime() == null : this.getEndTime().equals(other.getEndTime()))
            && (this.getActiveType() == null ? other.getActiveType() == null : this.getActiveType().equals(other.getActiveType()))
            && (this.getDayNum() == null ? other.getDayNum() == null : this.getDayNum().equals(other.getDayNum()))
            && (this.getSourceType() == null ? other.getSourceType() == null : this.getSourceType().equals(other.getSourceType()))
            && (this.getAdminId() == null ? other.getAdminId() == null : this.getAdminId().equals(other.getAdminId()))
            && (this.getAdminName() == null ? other.getAdminName() == null : this.getAdminName().equals(other.getAdminName()))
            && (this.getAppPosition() == null ? other.getAppPosition() == null : this.getAppPosition().equals(other.getAppPosition()))
            && (this.getExplan() == null ? other.getExplan() == null : this.getExplan().equals(other.getExplan()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getJoinIds() == null ? other.getJoinIds() == null : this.getJoinIds().equals(other.getJoinIds()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSiteId() == null) ? 0 : getSiteId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getTaskIds() == null) ? 0 : getTaskIds().hashCode());
        result = prime * result + ((getJoinType() == null) ? 0 : getJoinType().hashCode());
        result = prime * result + ((getStartTime() == null) ? 0 : getStartTime().hashCode());
        result = prime * result + ((getEndTime() == null) ? 0 : getEndTime().hashCode());
        result = prime * result + ((getActiveType() == null) ? 0 : getActiveType().hashCode());
        result = prime * result + ((getDayNum() == null) ? 0 : getDayNum().hashCode());
        result = prime * result + ((getSourceType() == null) ? 0 : getSourceType().hashCode());
        result = prime * result + ((getAdminId() == null) ? 0 : getAdminId().hashCode());
        result = prime * result + ((getAdminName() == null) ? 0 : getAdminName().hashCode());
        result = prime * result + ((getAppPosition() == null) ? 0 : getAppPosition().hashCode());
        result = prime * result + ((getExplan() == null) ? 0 : getExplan().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getJoinIds() == null) ? 0 : getJoinIds().hashCode());
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
        sb.append(", name=").append(name);
        sb.append(", taskIds=").append(taskIds);
        sb.append(", joinType=").append(joinType);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", activeType=").append(activeType);
        sb.append(", dayNum=").append(dayNum);
        sb.append(", sourceType=").append(sourceType);
        sb.append(", adminId=").append(adminId);
        sb.append(", adminName=").append(adminName);
        sb.append(", appPosition=").append(appPosition);
        sb.append(", explan=").append(explan);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", joinIds=").append(joinIds);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public enum ActiveType {
        // 每天
        EVERY_DAY((byte)10),
        // 每周
        EVERY_WEEK((byte)20),
        // 每月
        EVERY_MONTH((byte)30);

        private Byte value;

        ActiveType(Byte value) {
            this.value = value;
        }

        public Byte getValue() {
            return value;
        }
    }
}
