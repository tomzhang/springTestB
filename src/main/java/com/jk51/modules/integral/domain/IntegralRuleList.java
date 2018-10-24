package com.jk51.modules.integral.domain;

/**
 * 对应积分规则列表
 *
 * @auhter zy
 * @create 2017-06-06 17:51
 */
public class IntegralRuleList {

    private String name;

    private String desc;

    private String integral;

    private String limit;

    private String status;

    private String updateTime;

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getName() {

        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getIntegral() {
        return integral;
    }

    public String getLimit() {
        return limit;
    }

    public String getStatus() {
        return status;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public IntegralRuleList(String name, String desc, String integral, String limit, String status, String updateTime) {
        this.name = name;
        this.desc = desc;
        this.integral = integral;
        this.limit = limit;
        this.status = status;
        this.updateTime = updateTime;
    }
    public IntegralRuleList() {
        super();
    }
}
