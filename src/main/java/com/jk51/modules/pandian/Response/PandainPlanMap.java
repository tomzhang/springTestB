package com.jk51.modules.pandian.Response;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-03
 * 修改记录:
 */
public class PandainPlanMap {

    private String pandian_num;
    private Integer plan_id;
    private Integer type; //'0-商家设置；1-门店设置'
    private Integer plan_check_type; //'0-全盘；1-动态盘点；2-无批次盘点；3-随机盘点',
    private Integer plan_type; //'0-计划盘点；1-立即盘点',
    private Integer plan_hour;
    private Integer status;//'盘点状态: 0待上传，100待下发，200已下发待确认，300已确认待审核，400已审核，500复盘',
    private String clerks;
    private Date createTime;

    public String getPandian_num() {
        return pandian_num;
    }

    public void setPandian_num(String pandian_num) {
        this.pandian_num = pandian_num;
    }

    public Integer getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(Integer plan_id) {
        this.plan_id = plan_id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPlan_check_type() {
        return plan_check_type;
    }

    public void setPlan_check_type(Integer plan_check_type) {
        this.plan_check_type = plan_check_type;
    }

    public Integer getPlan_type() {
        return plan_type;
    }

    public void setPlan_type(Integer plan_type) {
        this.plan_type = plan_type;
    }

    public Integer getPlan_hour() {
        return plan_hour;
    }

    public void setPlan_hour(Integer plan_hour) {
        this.plan_hour = plan_hour;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getClerks() {
        return clerks;
    }

    public void setClerks(String clerks) {
        this.clerks = clerks;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
