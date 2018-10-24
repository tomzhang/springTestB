package com.jk51.model.promotions.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/10                                <br/>
 * 修改记录:                                         <br/>
 */
public class TimeRuleForPromotionsRule {
    public static final int VALIDITY_TYPE_ABSOLUTE_TIME = 1;
    public static final int VALIDITY_TYPE_MONTH_SEPARATE = 2;
    public static final int VALIDITY_TYPE_WEEKLY_SEPARATE = 3;

    /**
     * 1绝对时间，2按月份分隔, 3按星期分隔, 4秒杀时间
     */
    private Integer validity_type;

    /**
     * 开始时间
     */
    @JsonProperty("startTime")
    private String startTime;

    /**
     * 结束时间
     */
    @JsonProperty("endTime")
    private String endTime;

    /**
     * 按逗号隔开1,5,15
     */
    private String assign_rule;

    /**
     * 是否以倒计时模式显示
     * 1 代表 以倒计时模式显示
     * 0 代表 不以倒计时模式显示
     */
    private Integer showTimeCountDown;

    /**
     * 每月的最后几天计算 默认为0表示不设置最后几天
     */
    private Integer lastDayWork;

    public Integer getValidity_type() {
        return validity_type;
    }

    public void setValidity_type(Integer validity_type) {
        this.validity_type = validity_type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAssign_rule() {
        return assign_rule;
    }

    public void setAssign_rule(String assign_rule) {
        this.assign_rule = assign_rule;
    }

    public Integer getShowTimeCountDown() {
        return showTimeCountDown;
    }

    public void setShowTimeCountDown(Integer showTimeCountDown) {
        this.showTimeCountDown = showTimeCountDown;
    }

    public Integer getLastDayWork() {
        return lastDayWork;
    }

    public void setLastDayWork(Integer lastDayWork) {
        this.lastDayWork = lastDayWork;
    }

    @Override
    public String toString() {
        return "TimeRuleForPromotionsRule{" +
                "validity_type=" + validity_type +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", assign_rule='" + assign_rule + '\'' +
                ", showTimeCountDown=" + showTimeCountDown +
                ", lastDayWork=" + lastDayWork +
                '}';
    }
}
