package com.jk51.model.account.requestParams;

import java.sql.Date;

/**
 * filename :com.jk51.model.account.requestParams.
 * author   :zw
 * date     :2017/3/14
 * Update   :
 */
public class SettingSettlementDayParam {
    private Integer id;
    private Integer site_id;
    private Integer set_type; //0按日结算 1按周结算 2按月结算
    private String set_value;  //0表示按日结算
    private Integer pay_day_value; //预留 默认是3天
    private Date thelast_time;
    private int finance_type; //设置结算方式 0以结束状态结算  1以付款状态结算

    public int getFinance_type() {
        return finance_type;
    }

    public void setFinance_type(int finance_type) {
        this.finance_type = finance_type;
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
}
