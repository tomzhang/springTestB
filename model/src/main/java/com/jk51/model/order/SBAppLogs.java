package com.jk51.model.order;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-03-30
 * 修改记录:
 */
public class SBAppLogs {
    private Integer site_id;
    private Integer id;
    private Integer operator_id;
    private String operator;
    private Integer operator_type;
    private String action;
    private String remark;
    private Integer platform_type;
    private Integer store_id;
    private Timestamp create_time;
    private Timestamp update_time;

    public SBAppLogs() {
    }

    @Override
    public String toString() {
        return "BAppLogs{" +
            "Site_id=" + site_id +
            ", id=" + id +
            ", operator_id=" + operator_id +
            ", operator='" + operator + '\'' +
            ", operator_type=" + operator_type +
            ", action='" + action + '\'' +
            ", remark='" + remark + '\'' +
            ", platform_type=" + platform_type +
            ", store_id=" + store_id +
            ", create_time=" + create_time +
            ", update_time=" + update_time +
            '}';
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(Integer operator_id) {
        this.operator_id = operator_id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getOperator_type() {
        return operator_type;
    }

    public void setOperator_type(Integer operator_type) {
        this.operator_type = operator_type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getPlatform_type() {
        return platform_type;
    }

    public void setPlatform_type(Integer platform_type) {
        this.platform_type = platform_type;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }
}
