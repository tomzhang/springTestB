package com.jk51.modules.task.domain;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-08-29 11:45
 * 修改记录:
 */
public class TaskPlanReportQuery {

    private Integer taskPlanId;

    private String taskPlanName;

    private Integer[] taskIds;

    public TaskPlanReportQuery() {

    }

    public Integer getTaskPlanId() {
        return taskPlanId;
    }

    public void setTaskPlanId(Integer taskPlanId) {
        this.taskPlanId = taskPlanId;
    }

    public String getTaskPlanName() {
        return taskPlanName;
    }

    public void setTaskPlanName(String taskPlanName) {
        this.taskPlanName = taskPlanName;
    }

    public Integer[] getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(Integer[] taskIds) {
        this.taskIds = taskIds;
    }
}
