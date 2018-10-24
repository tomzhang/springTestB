package com.jk51.model.account.models;


import java.sql.Timestamp;
import java.util.Date;

/**
 * filename :com.jk51.model.account.models.
 * author   :zw
 * date     :2017/3/14
 * Update   :
 * 结算日配置表
 */
public class SettlementdayConfig {
    private Integer id;
    private Integer site_id;
    private Integer set_type;
    private String set_value;
    private Integer pay_day_value;
    private Date thelast_time;
    private Date create_time;
    private Date update_time;
    private Integer finance_type;

    private Timestamp start_date;//结算开始时间
    private Timestamp end_date;//结算结束时间

    public Integer getFinance_type() {
        return finance_type;
    }

    public void setFinance_type(Integer finance_type) {
        this.finance_type = finance_type;
    }

    public Timestamp getStart_date() {
        return start_date;
    }

    public void setStart_date(Timestamp start_date) {
        this.start_date = start_date;
    }

    public Timestamp getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Timestamp end_date) {
        this.end_date = end_date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getSet_type() {
        return set_type;
    }

    public void setSet_type(Integer set_type) {
        this.set_type = set_type;
    }

    public String getSet_value() {
        return set_value;
    }

    public void setSet_value(String set_value) {
        this.set_value = set_value;
    }

    public Integer getPay_day_value() {
        return pay_day_value;
    }

    public void setPay_day_value(Integer pay_day_value) {
        this.pay_day_value = pay_day_value;
    }

    public Date getThelast_time() {
        return thelast_time;
    }

    public void setThelast_time(Date thelast_time) {
        this.thelast_time = thelast_time;
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
