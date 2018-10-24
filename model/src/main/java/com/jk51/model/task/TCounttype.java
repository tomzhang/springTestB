package com.jk51.model.task;

import java.io.Serializable;

/**
 * @author 
 */
public class TCounttype implements Serializable {
    private Integer id;

    /**
     * 任务指标组id
     */
    private Integer groupId;

    /**
     * 展示名称
     */
    private String name;

    /**
     * 类型 
     */
    private String tblName;

    /**
     * 过滤条件
     */
    private String filterCondition;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTblName() {
        return tblName;
    }

    public void setTblName(String tblName) {
        this.tblName = tblName;
    }

    public String getFilterCondition() {
        return filterCondition;
    }

    public void setFilterCondition(String filterCondition) {
        this.filterCondition = filterCondition;
    }

    @Override
    public String toString() {
        return "TCounttype{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", name='" + name + '\'' +
                ", tblName='" + tblName + '\'' +
                ", filterCondition='" + filterCondition + '\'' +
                '}';
    }
}