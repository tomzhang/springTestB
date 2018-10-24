package com.jk51.model.account.models;

import java.util.Date;

/**
 * filename :com.jk51.model.account.models.
 * author   :zw
 * date     :2017/3/16
 * Update   :
 *
 */
public class PayPlatform {
    private Integer id;
    private Integer site_id;
    private String pay_type;
    private String payment_name;
    private String payment_desc;
    private String my_account;
    private Date create_time;
    private Date update_time;
    private String code;
    private Double procedure_fee;

    public Double getProcedure_fee() {
        return procedure_fee;
    }

    public void setProcedure_fee(Double procedure_fee) {
        this.procedure_fee = procedure_fee;
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

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getPayment_name() {
        return payment_name;
    }

    public void setPayment_name(String payment_name) {
        this.payment_name = payment_name;
    }

    public String getPayment_desc() {
        return payment_desc;
    }

    public void setPayment_desc(String payment_desc) {
        this.payment_desc = payment_desc;
    }

    public String getMy_account() {
        return my_account;
    }

    public void setMy_account(String my_account) {
        this.my_account = my_account;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
