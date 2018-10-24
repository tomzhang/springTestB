package com.jk51.model.account.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * @Author: chen
 * @Description: 商家对账数据
 * @Date: created in 2018/9/28
 * @Modified By:
 */
public class AccountCheckData {

    private String trades_id;
    private String real_pay;
    private String pay_style;
    private String pay_number;
    private String platform_payment_amount;
    private String account_checking_status;
    private Date pay_time;
    private Date create_time;

    private String refund_fee;
    private String import_refund_fee;
    private Date refund_time;

    public String getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(String refund_fee) {
        this.refund_fee = refund_fee;
    }

    public String getImport_refund_fee() {
        return import_refund_fee;
    }

    public void setImport_refund_fee(String import_refund_fee) {
        this.import_refund_fee = import_refund_fee;
    }

    public Date getRefund_time() {
        return refund_time;
    }

    public void setRefund_time(Date refund_time) {
        this.refund_time = refund_time;
    }

    public String getTrades_id() {
        return trades_id;
    }

    public void setTrades_id(String trades_id) {
        this.trades_id = trades_id;
    }

    public String getReal_pay() {
        return real_pay;
    }

    public void setReal_pay(String real_pay) {
        this.real_pay = real_pay;
    }

    public String getPay_style() {
        return pay_style;
    }

    public void setPay_style(String pay_style) {
        this.pay_style = pay_style;
    }

    public String getPay_number() {
        return pay_number;
    }

    public void setPay_number(String pay_number) {
        this.pay_number = pay_number;
    }

    public String getPlatform_payment_amount() {
        return platform_payment_amount;
    }

    public void setPlatform_payment_amount(String platform_payment_amount) {
        this.platform_payment_amount = platform_payment_amount;
    }

    public String getAccount_checking_status() {
        return account_checking_status;
    }

    public void setAccount_checking_status(String account_checking_status) {
        this.account_checking_status = account_checking_status;
    }

    public Date getPay_time() {
        return pay_time;
    }

    public void setPay_time(Date pay_time) {
        this.pay_time = pay_time;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
