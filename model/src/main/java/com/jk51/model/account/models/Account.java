package com.jk51.model.account.models;

import java.util.Date;

/**
 * filename :com.jk51.model.account.models.
 * author   :zw
 * date     :2017/3/15
 * Update   :
 *
 */
public class Account {
    private Integer account_id;
    private Integer seller_id;
    private String account_name;
    private Integer payplatform_id;
    private String account;
    private double proportion;
    private Date create_time;
    private Date update_time;

    public Integer getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Integer account_id) {
        this.account_id = account_id;
    }

    public Integer getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(Integer seller_id) {
        this.seller_id = seller_id;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public Integer getPayplatform_id() {
        return payplatform_id;
    }

    public void setPayplatform_id(Integer payplatform_id) {
        this.payplatform_id = payplatform_id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public double getProportion() {
        return proportion;
    }

    public void setProportion(double proportion) {
        this.proportion = proportion;
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
