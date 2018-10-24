package com.jk51.model.coupon.requestParams;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * filename :com.jk51.model.coupon.requestParams.
 * author   :zw
 * date     :2017/3/6
 * Update   :
 */
public class TimeRule {
    /* -- 字段 validity_type 的几个常量 -- */
    public static final int VALIDITY_TYPE_ABSOLUTE_TIME = 1;
    public static final int VALIDITY_TYPE_RELATIVE_TIME = 2;
    public static final int VALIDITY_TYPE_SCHEDULE_TIME = 3;
    public static final int VALIDITY_TYPE_MONTH_SEPARATE = 4;
    public static final int VALIDITY_TYPE_WEEKLY_SEPARATE = 5;

    private Integer validity_type; //1绝对时间，2相对时间，3指定时间, 4秒杀时间, 5按星期分隔
    @JsonProperty("startTime")
    private String startTime;//开始时间
    @JsonProperty("endTime")
    private String endTime;//结束时间
    private Integer draw_day; //领取几天后
    private Integer how_day; //几天内使用
    private Integer assign_type;//指定时间的类型 1按月份日期，2按星期
    private String assign_rule;//按逗号隔开1,5,15

    public Integer getValidity_type() {
        return validity_type;
    }

    public void setValidity_type(Integer validity_type) {
        this.validity_type = validity_type;
    }

    public Integer getDraw_day() {
        return draw_day;
    }

    public void setDraw_day(Integer draw_day) {
        this.draw_day = draw_day;
    }

    public Integer getHow_day() {
        return how_day;
    }

    public void setHow_day(Integer how_day) {
        this.how_day = how_day;
    }

    public Integer getAssign_type() {
        return assign_type;
    }

    public void setAssign_type(Integer assign_type) {
        this.assign_type = assign_type;
    }

    public String getAssign_rule() {
        return assign_rule;
    }

    public void setAssign_rule(String assign_rule) {
        this.assign_rule = assign_rule;
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
}
