package com.jk51.modules.task.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jk51.model.task.BTaskBlob;
import com.jk51.modules.task.domain.*;
import com.jk51.modules.task.domain.validation.AllowValue;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

public class BTaskDTO {
    @NotNull(groups = {UpdateGroup.class}, message = "任务记录id不能为空")
    private Integer id;

    @NotNull(groups = {AddGroup.class}, message = "商户编号不能为空")
    private Integer siteId;

    @NotBlank(groups = {AddGroup.class}, message = "任务名称不能为空")
    @Length(max = 32)
    private String name;

    @NotNull(groups = {AddGroup.class}, message = "请选择任务")
    private Integer targetId;

    @NotNull(groups = {AddGroup.class}, message = "任务指标不能为空")
    @JsonSerialize(using = ArrayToStringSerialize.class)
    private Integer[] typeIds;

    @NotNull(groups = {AddGroup.class}, message = "请选择时间类型")
    private Byte timeType;

    @NotNull(groups = {AddGroup.class}, message = "请选择任务对象")
    private Byte object;

//    @NotEmpty(groups = {AddGroup.class}, message = "请选择任务奖励类型")
    private Byte rewardType;

//    @NotEmpty(groups = {AddGroup.class}, message = "奖励类型规则不能为空")
    @Valid
    @JsonSerialize(using = JsonStringSerialize.class)
    private RewardRule rewardDetail;

    @Length(max = 255)
    private String explain;

    @NotNull(groups = {AddGroup.class}, message = "任务状态不能为空")
    @AllowValue(value = {10, 20, 30}, groups = {AddGroup.class, UpdateGroup.class}, message = "任务状态只允许为未开始、进行中、已结束")
    private Byte status;

    @NotNull(groups = {AddGroup.class}, message = "操作人类型不能为空")
    private Byte adminType;

    @NotNull(groups = {AddGroup.class}, message = "操作人编号不能为空")
    private Integer adminId;

    @NotBlank(groups = {AddGroup.class}, message = "操作人名字不能为空")
    private String adminName;

    private Integer taskSource;

    private Integer rewardLimit;

    private Integer lowTarget;

    private Integer punish;

    private BTaskBlob taskBlob;

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

    public Integer[] getTypeIds() {
        return typeIds;
    }

    public void setTypeIds(Integer[] typeIds) {
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

    public RewardRule getRewardDetail() {
        return rewardDetail;
    }

    public void setRewardDetail(RewardRule rewardDetail) {
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

    public BTaskBlob getTaskBlob() {
        return taskBlob;
    }

    public void setTaskBlob(BTaskBlob taskBlob) {
        this.taskBlob = taskBlob;
    }

    public Integer getTaskSource() {
        return taskSource;
    }

    public void setTaskSource(Integer taskSource) {
        this.taskSource = taskSource;
    }

    @Override
    public String toString() {
        return "BTaskDTO{" +
                "id=" + id +
                ", siteId=" + siteId +
                ", name='" + name + '\'' +
                ", targetId=" + targetId +
                ", typeIds=" + Arrays.toString(typeIds) +
                ", timeType=" + timeType +
                ", object=" + object +
                ", rewardType=" + rewardType +
                ", rewardDetail=" + rewardDetail +
                ", explain='" + explain + '\'' +
                ", status=" + status +
                ", adminType=" + adminType +
                ", adminId=" + adminId +
                ", adminName='" + adminName + '\'' +
                ", rewardLimit=" + rewardLimit +
                ", lowTarget=" + lowTarget +
                ", punish=" + punish +
                ", taskBlob=" + taskBlob +
                '}';
    }
}
