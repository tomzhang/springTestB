package com.jk51.model;

import java.sql.Timestamp;

/**
 * Created by admin on 2017/2/13.
 */
public class Target {

    /**
     *指标表主键ID
     */
    private long target_id;

    /**
     * 商家ID
     */
    private long owner;

    /**
     * 指标名称
     */
    private String target_name;

    /**
     * 二层权重值
     */
    private Double second_weigth_value;

    /**
     * 当前指标使用状态
     */
    private String use_status;

    /**
     *一层权重id
     */
    private long first_weigth_id;

    /**
     * 初始值
     */
    private int initial_value;

    /**
     * 参数区间
     */
    private String score_parameter_section;

    /**
     * 生成时间
     */
    private Timestamp create_time;

    /**
     * 参考值
     */
    private double reference_value;

    /**
     * 更新时间
     */
    private Timestamp update_time;

    public long getTarget_id() {
        return target_id;
    }

    public void setTarget_id(long target_id) {
        this.target_id = target_id;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public String getTarget_name() {
        return target_name;
    }

    public void setTarget_name(String target_name) {
        this.target_name = target_name;
    }

    public Double getSecond_weigth_value() {
        return second_weigth_value;
    }

    public void setSecond_weigth_value(Double second_weigth_value) {
        this.second_weigth_value = second_weigth_value;
    }

    public String getUse_status() {
        return use_status;
    }

    public void setUse_status(String use_status) {
        this.use_status = use_status;
    }

    public long getFirst_weigth_id() {
        return first_weigth_id;
    }

    public void setFirst_weigth_id(long first_weigth_id) {
        this.first_weigth_id = first_weigth_id;
    }

    public int getInitial_value() {
        return initial_value;
    }

    public void setInitial_value(int initial_value) {
        this.initial_value = initial_value;
    }

    public String getScore_parameter_section() {
        return score_parameter_section;
    }

    public void setScore_parameter_section(String score_parameter_section) {
        this.score_parameter_section = score_parameter_section;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public double getReference_value() {
        return reference_value;
    }

    public void setReference_value(double reference_value) {
        this.reference_value = reference_value;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }
}
