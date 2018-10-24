package com.jk51.model.task;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class BTaskcount implements Serializable {
    private Integer id;

    /**
     * 商户id
     */
    private Integer siteId;

    /**
     * 任务id
     */
    private Integer taskId;

    /**
     * 计划id
     */
    private Integer planId;

    /**
     * 任务执行计划id
     */
    private Integer executeId;

    /**
     * 对象类型 10 门店 20 店员 和b_taskplan一致
     */
    private Byte joinType;

    /**
     * 对象id join_type是10为门店id 20店员id
     */
    private Integer joinId;

    /**
     * 统计值  比如订单量存10  如果是金额值单位为分 100元存10000
     */
    private Integer countValue;

    /**
     * 统计开始时间
     */
    private Date countStart;

    /**
     * 统计结束时间
     */
    private Date countEnd;

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

    public Integer getExecuteId() {
        return executeId;
    }

    public void setExecuteId(Integer executeId) {
        this.executeId = executeId;
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

    public Integer getCountValue() {
        return countValue;
    }

    public void setCountValue(Integer countValue) {
        this.countValue = countValue;
    }

    public Date getCountStart() {
        return countStart;
    }

    public void setCountStart(Date countStart) {
        this.countStart = countStart;
    }

    public Date getCountEnd() {
        return countEnd;
    }

    public void setCountEnd(Date countEnd) {
        this.countEnd = countEnd;
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