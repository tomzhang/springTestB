package com.jk51.model.account.models;

import java.util.Date;

/**
 * filename :com.jk51.model.account.models.
 * author   :zw
 * date     :2017/3/15
 * Update   :
 * 平台佣金比例
 */
public class AccountCommissionRate {
    private Integer site_id;
    private double direct_purchase_rate;
    private double distributor_rate;
    private double shipping_fee_rate;
    private Date create_time;
    private Date update_time;

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public double getDirect_purchase_rate() {
        return direct_purchase_rate;
    }

    public void setDirect_purchase_rate(double direct_purchase_rate) {
        this.direct_purchase_rate = direct_purchase_rate;
    }

    public double getDistributor_rate() {
        return distributor_rate;
    }

    public void setDistributor_rate(double distributor_rate) {
        this.distributor_rate = distributor_rate;
    }

    public double getShipping_fee_rate() {
        return shipping_fee_rate;
    }

    public void setShipping_fee_rate(double shipping_fee_rate) {
        this.shipping_fee_rate = shipping_fee_rate;
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
