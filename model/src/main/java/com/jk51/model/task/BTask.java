package com.jk51.model.task;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class BTask implements Serializable {
    private Integer id;

    /**
     * 商户编号
     */
    private Integer siteId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务指标id
     */
    private Integer targetId;

    /**
     * 统计类型列表 用,分隔
     */
    private String typeIds;

    /**
     * 任务时间 10 自然日 20 自然周 30 自然月
     */
    private Byte timeType;

    /**
     * 任务对象 10 门店 20 店员
     */
    private Byte object;

    /**
     * 任务奖励 10 人民币 20 绩效
     */
    private Byte rewardType;

    /**
     * 奖励规则 存json
     */
    private String rewardDetail;

    /**
     * 说明
     */
    private String explain;

    /**
     * 任务状态 10 未开始 20 进行中 30 已结束 40 已删除
     */
    private Byte status;

    /**
     * 创建人类型 10 总部账号 20 门店店员账号
     */
    private Byte adminType;

    /**
     * 账号id
     */
    private Integer adminId;

    /**
     * 账号名称
     */
    private String adminName;

    /**
     * 任务奖励限额 0表示不限额
     */
    private Integer rewardLimit;

    /**
     * 已确认奖励总额
     */
    private Integer rewardTotal;

    /**
     * 惩罚最低条件 0表示无
     */
    private Integer lowTarget;

    /**
     * 惩罚值 只有当low_target大于0有效
     */
    private Integer punish;

    /**
     * 数据来源,线上，线下
     */
    private Integer taskSource;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getTypeIds() {
        return typeIds;
    }

    public void setTypeIds(String typeIds) {
        this.typeIds = typeIds;
    }

    public Byte getTimeType() {
        return timeType;
    }

    public void setTimeType(Byte timeType) {
        this.timeType = timeType;
    }

    public Byte getObject() {
        return object;
    }

    public void setObject(Byte object) {
        this.object = object;
    }

    public Byte getRewardType() {
        return rewardType;
    }

    public void setRewardType(Byte rewardType) {
        this.rewardType = rewardType;
    }

    public String getRewardDetail() {
        return rewardDetail;
    }

    public void setRewardDetail(String rewardDetail) {
        this.rewardDetail = rewardDetail;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getAdminType() {
        return adminType;
    }

    public void setAdminType(Byte adminType) {
        this.adminType = adminType;
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

    public Integer getRewardLimit() {
        return rewardLimit;
    }

    public void setRewardLimit(Integer rewardLimit) {
        this.rewardLimit = rewardLimit;
    }

    public Integer getRewardTotal() {
        return rewardTotal;
    }

    public void setRewardTotal(Integer rewardTotal) {
        this.rewardTotal = rewardTotal;
    }

    public Integer getLowTarget() {
        return lowTarget;
    }

    public void setLowTarget(Integer lowTarget) {
        this.lowTarget = lowTarget;
    }

    public Integer getPunish() {
        return punish;
    }

    public void setPunish(Integer punish) {
        this.punish = punish;
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

    public Integer getTaskSource() {
        return taskSource;
    }

    public void setTaskSource(Integer taskSource) {
        this.taskSource = taskSource;
    }
}
