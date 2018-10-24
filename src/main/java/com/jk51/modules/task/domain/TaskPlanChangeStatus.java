package com.jk51.modules.task.domain;

public class TaskPlanChangeStatus {
    private String ids;
    private Integer second;
    private Byte status;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskPlanChangeStatus{" +
                "ids='" + ids + '\'' +
                ", second='" + second + '\'' +
                ", status=" + status +
                '}';
    }
}
