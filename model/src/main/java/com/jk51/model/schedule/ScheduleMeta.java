package com.jk51.model.schedule;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 任务定义元数据
 * 作者: wangzhengfei
 * 创建日期: 2017-02-21
 * 修改记录:
 */
public class ScheduleMeta {

    private Integer id;

    private String name;

    private String beanName;

    private String method;

    private String serverAddr;

    private String cronExp;

    private String paramJSON;

    private Integer status;

    private Integer enabled;

    private String desc;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getCronExp() {
        return cronExp;
    }

    public void setCronExp(String cronExp) {
        this.cronExp = cronExp;
    }

    public String getParamJSON() {
        return paramJSON;
    }

    public void setParamJSON(String paramJSON) {
        this.paramJSON = paramJSON;
    }

    public Integer geStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ScheduleMeta{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", beanName='").append(beanName).append('\'');
        sb.append(", method='").append(method).append('\'');
        sb.append(", serverAddr='").append(serverAddr).append('\'');
        sb.append(", cronExp='").append(cronExp).append('\'');
        sb.append(", paramJSON='").append(paramJSON).append('\'');
        sb.append(", status=").append(status);
        sb.append(", enabled=").append(enabled);
        sb.append(", desc='").append(desc).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }
}
