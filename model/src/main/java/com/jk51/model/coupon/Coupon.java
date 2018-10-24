package com.jk51.model.coupon;

import java.util.Date;

/**
 * 该类只用来做新旧数据转换，如果要封装优惠券规则数据，使用CouponRule类
 *
 * filename :com.jk51.model.
 * author   :zw
 * date     :2017/4/19
 * Update   :
 */
@Deprecated
public class Coupon {
    private Integer site_id;
    private Integer coupon_id;
    private String coupon_name;
    private Integer coupon_type;
    private String coupon_campaign_name;
    private String coupon_amount;
    /**
     * 旧规则状态 0正常 1结束 2撤销（代码修改完毕就可以删除旧规则状态）
     * 新规则状态 0可发放 1已发完 2手动停发 3已过期 4手动作废 10待发放
     */
    private Integer coupon_status;
    private Integer coupon_limit_type;
    private String coupon_limit_vals;
    private Integer coupon_amount_min;
    private Integer coupon_amount_max;
    private Integer coupon_is_exclusive;
    private Date coupon_start;
    private Date coupon_end;
    private Integer coupon_extra_time;
    private String coupon_amount_type;
    private Integer coupon_create_num;
    private Integer coupon_available_num;
    private Date create_time;
    private Date update_time;
    private Integer coupon_dispatch_num;
    private Integer coupon_use_num;
    private Integer coupon_is_delete;
    private Integer coupon_is_use;
    private Integer coupon_trigger_id;

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(Integer coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getCoupon_name() {
        return coupon_name;
    }

    public void setCoupon_name(String coupon_name) {
        this.coupon_name = coupon_name;
    }

    public Integer getCoupon_type() {
        return coupon_type;
    }

    public void setCoupon_type(Integer coupon_type) {
        this.coupon_type = coupon_type;
    }

    public String getCoupon_campaign_name() {
        return coupon_campaign_name;
    }

    public void setCoupon_campaign_name(String coupon_campaign_name) {
        this.coupon_campaign_name = coupon_campaign_name;
    }

    public String getCoupon_amount() {
        return coupon_amount;
    }

    public void setCoupon_amount(String coupon_amount) {
        this.coupon_amount = coupon_amount;
    }

    public Integer getCoupon_status() {
        return coupon_status;
    }

    public void setCoupon_status(Integer coupon_status) {
        this.coupon_status = coupon_status;
    }

    public Integer getCoupon_limit_type() {
        return coupon_limit_type;
    }

    public void setCoupon_limit_type(Integer coupon_limit_type) {
        this.coupon_limit_type = coupon_limit_type;
    }

    public String getCoupon_limit_vals() {
        return coupon_limit_vals;
    }

    public void setCoupon_limit_vals(String coupon_limit_vals) {
        this.coupon_limit_vals = coupon_limit_vals;
    }

    public Integer getCoupon_amount_min() {
        return coupon_amount_min;
    }

    public void setCoupon_amount_min(Integer coupon_amount_min) {
        this.coupon_amount_min = coupon_amount_min;
    }

    public Integer getCoupon_amount_max() {
        return coupon_amount_max;
    }

    public void setCoupon_amount_max(Integer coupon_amount_max) {
        this.coupon_amount_max = coupon_amount_max;
    }

    public Integer getCoupon_is_exclusive() {
        return coupon_is_exclusive;
    }

    public void setCoupon_is_exclusive(Integer coupon_is_exclusive) {
        this.coupon_is_exclusive = coupon_is_exclusive;
    }

    public Date getCoupon_start() {
        return coupon_start;
    }

    public void setCoupon_start(Date coupon_start) {
        this.coupon_start = coupon_start;
    }

    public Date getCoupon_end() {
        return coupon_end;
    }

    public void setCoupon_end(Date coupon_end) {
        this.coupon_end = coupon_end;
    }

    public Integer getCoupon_extra_time() {
        return coupon_extra_time;
    }

    public void setCoupon_extra_time(Integer coupon_extra_time) {
        this.coupon_extra_time = coupon_extra_time;
    }

    public String getCoupon_amount_type() {
        return coupon_amount_type;
    }

    public void setCoupon_amount_type(String coupon_amount_type) {
        this.coupon_amount_type = coupon_amount_type;
    }

    public Integer getCoupon_create_num() {
        return coupon_create_num;
    }

    public void setCoupon_create_num(Integer coupon_create_num) {
        this.coupon_create_num = coupon_create_num;
    }

    public Integer getCoupon_available_num() {
        return coupon_available_num;
    }

    public void setCoupon_available_num(Integer coupon_available_num) {
        this.coupon_available_num = coupon_available_num;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Integer getCoupon_dispatch_num() {
        return coupon_dispatch_num;
    }

    public void setCoupon_dispatch_num(Integer coupon_dispatch_num) {
        this.coupon_dispatch_num = coupon_dispatch_num;
    }

    public Integer getCoupon_use_num() {
        return coupon_use_num;
    }

    public void setCoupon_use_num(Integer coupon_use_num) {
        this.coupon_use_num = coupon_use_num;
    }

    public Integer getCoupon_is_delete() {
        return coupon_is_delete;
    }

    public void setCoupon_is_delete(Integer coupon_is_delete) {
        this.coupon_is_delete = coupon_is_delete;
    }

    public Integer getCoupon_is_use() {
        return coupon_is_use;
    }

    public void setCoupon_is_use(Integer coupon_is_use) {
        this.coupon_is_use = coupon_is_use;
    }

    public Integer getCoupon_trigger_id() {
        return coupon_trigger_id;
    }

    public void setCoupon_trigger_id(Integer coupon_trigger_id) {
        this.coupon_trigger_id = coupon_trigger_id;
    }
}
