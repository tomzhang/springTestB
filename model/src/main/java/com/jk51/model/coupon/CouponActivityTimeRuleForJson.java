package com.jk51.model.coupon;

/**
 * 优惠券发券对应的时间规则
 * Created by Administrator on 2018/10/9.
 */
public class CouponActivityTimeRuleForJson {
    public static final int VALIDITY_TYPE_MONTH_SEPARATE = 2;
    public static final int VALIDITY_TYPE_WEEKLY_SEPARATE = 3;

    /**
     * 2按月份分隔, 3按星期分隔
     */
    private Integer validity_type;

    /**
     * 按逗号隔开1,5,15
     */
    private String assign_rule;

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

    public String getAssign_rule() {
        return assign_rule;
    }

    public void setAssign_rule(String assign_rule) {
        this.assign_rule = assign_rule;
    }

    public Integer getLastDayWork() {
        return lastDayWork;
    }

    public void setLastDayWork(Integer lastDayWork) {
        this.lastDayWork = lastDayWork;
    }
}
