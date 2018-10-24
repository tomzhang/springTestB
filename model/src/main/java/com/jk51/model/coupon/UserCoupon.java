package com.jk51.model.coupon;

import java.util.Date;

/**
 * filename :com.jk51.model.coupon.
 * author   :zw
 * date     :2017/4/19
 * Update   :
 */
public class UserCoupon {
    private Integer site_id;
    private Integer user_coupon_id;
    private Integer coupon_id;
    private String user_coupon_code;
    private Integer user_coupon_amount;
    private String user_coupon_mobile;
    private Integer buyer_id;
    private Integer user_coupon_from;
    private String user_coupon_from_desc;
    private Integer user_coupon_state;
    private String user_coupon_from_val;
    private Date user_coupon_start;
    private Date user_coupon_end;
    private Integer user_coupon_sub_amount;
    private Date user_coupon_usetime;
    private Date create_time;
    private Date update_time;

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getUser_coupon_id() {
        return user_coupon_id;
    }

    public void setUser_coupon_id(Integer user_coupon_id) {
        this.user_coupon_id = user_coupon_id;
    }

    public Integer getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(Integer coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getUser_coupon_code() {
        return user_coupon_code;
    }

    public void setUser_coupon_code(String user_coupon_code) {
        this.user_coupon_code = user_coupon_code;
    }

    public Integer getUser_coupon_amount() {
        return user_coupon_amount;
    }

    public void setUser_coupon_amount(Integer user_coupon_amount) {
        this.user_coupon_amount = user_coupon_amount;
    }

    public String getUser_coupon_mobile() {
        return user_coupon_mobile;
    }

    public void setUser_coupon_mobile(String user_coupon_mobile) {
        this.user_coupon_mobile = user_coupon_mobile;
    }

    public Integer getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(Integer buyer_id) {
        this.buyer_id = buyer_id;
    }

    public Integer getUser_coupon_from() {
        return user_coupon_from;
    }

    public void setUser_coupon_from(Integer user_coupon_from) {
        this.user_coupon_from = user_coupon_from;
    }

    public String getUser_coupon_from_desc() {
        return user_coupon_from_desc;
    }

    public void setUser_coupon_from_desc(String user_coupon_from_desc) {
        this.user_coupon_from_desc = user_coupon_from_desc;
    }

    public Integer getUser_coupon_state() {
        return user_coupon_state;
    }

    public void setUser_coupon_state(Integer user_coupon_state) {
        this.user_coupon_state = user_coupon_state;
    }

    public String getUser_coupon_from_val() {
        return user_coupon_from_val;
    }

    public void setUser_coupon_from_val(String user_coupon_from_val) {
        this.user_coupon_from_val = user_coupon_from_val;
    }

    public Date getUser_coupon_start() {
        return user_coupon_start;
    }

    public void setUser_coupon_start(Date user_coupon_start) {
        this.user_coupon_start = user_coupon_start;
    }

    public Date getUser_coupon_end() {
        return user_coupon_end;
    }

    public void setUser_coupon_end(Date user_coupon_end) {
        this.user_coupon_end = user_coupon_end;
    }

    public Integer getUser_coupon_sub_amount() {
        return user_coupon_sub_amount;
    }

    public void setUser_coupon_sub_amount(Integer user_coupon_sub_amount) {
        this.user_coupon_sub_amount = user_coupon_sub_amount;
    }

    public Date getUser_coupon_usetime() {
        return user_coupon_usetime;
    }

    public void setUser_coupon_usetime(Date user_coupon_usetime) {
        this.user_coupon_usetime = user_coupon_usetime;
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
}
