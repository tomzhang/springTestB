package com.jk51.model.account.models;

import java.security.PrivateKey;
import java.sql.Timestamp;

/**
 * filename :com.jk51.model.account.models.
 * author   :zw
 * date     :2017/2/17
 * Update   :
 * 连表查询 结算明细关联订单表
 */
public class SettlementDetailAndTrades {
    private long trades_id;
    private String pay_style;
    private String pay_number;
    private Integer account_checking_status;
    private Integer checking_user_type;
    private String remark;
    private String business_types;
    private String finance_no;
    private String finance_no_refund;
    private Integer refund_fee;
    private Integer refund_checking_status;
    private Integer platform_payment_amount;
    private Integer platform_fashionable_amount;
    private Integer platform_service_fee;
    private Integer platform_refund_fee;
    private Integer O2O_freight;
    private Integer post_fee;
    private Integer plat_split;
    private Integer is_refund;
    private Integer deal_finish_status;
    private Integer is_payment;
    private Integer trades_status;
    private Integer settlement_type;

    //订单
    private Integer seller_id;
    private String seller_nick;
    private Timestamp end_time; //交易成功时间
    private Integer real_pay; //实际付款金额
    private Integer trades_split; //订单佣金
    private Timestamp pay_time; //付款时间
    private Timestamp create_time; //订单创建时间
    private Integer settlement_status;

    private Timestamp start_date;//结算开始时间
    private Timestamp end_date;//结算结束时间
    private Timestamp thelast_time;//上次最后结算时间

    public Integer getIs_payment() {
        return is_payment;
    }

    public void setIs_payment(Integer is_payment) {
        this.is_payment = is_payment;
    }

    public Integer getTrades_status() {
        return trades_status;
    }

    public void setTrades_status(Integer trades_status) {
        this.trades_status = trades_status;
    }

    public Integer getSettlement_type() {
        return settlement_type;
    }

    public void setSettlement_type(Integer settlement_type) {
        this.settlement_type = settlement_type;
    }

    public Integer getDeal_finish_status() {
        return deal_finish_status;
    }

    public void setDeal_finish_status(Integer deal_finish_status) {
        this.deal_finish_status = deal_finish_status;
    }

    public Integer getIs_refund() {
        return is_refund;
    }

    public void setIs_refund(Integer is_refund) {
        this.is_refund = is_refund;
    }

    public String getFinance_no_refund() {
        return finance_no_refund;
    }

    public void setFinance_no_refund(String finance_no_refund) {
        this.finance_no_refund = finance_no_refund;
    }

    public Integer getPlat_split() {
        return plat_split;
    }

    public void setPlat_split(Integer plat_split) {
        this.plat_split = plat_split;
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

    public Timestamp getThelast_time() {
        return thelast_time;
    }

    public void setThelast_time(Timestamp thelast_time) {
        this.thelast_time = thelast_time;
    }

    public Integer getPost_fee() {
        return post_fee;
    }

    public void setPost_fee(Integer post_fee) {
        this.post_fee = post_fee;
    }

    public Integer getO2O_freight() {
        return O2O_freight;
    }

    public void setO2O_freight(Integer o2O_freight) {
        O2O_freight = o2O_freight;
    }

    public long getTrades_id() {
        return trades_id;
    }

    public void setTrades_id(long trades_id) {
        this.trades_id = trades_id;
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

    public Integer getAccount_checking_status() {
        return account_checking_status;
    }

    public void setAccount_checking_status(Integer account_checking_status) {
        this.account_checking_status = account_checking_status;
    }

    public Integer getChecking_user_type() {
        return checking_user_type;
    }

    public void setChecking_user_type(Integer checking_user_type) {
        this.checking_user_type = checking_user_type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBusiness_types() {
        return business_types;
    }

    public void setBusiness_types(String business_types) {
        this.business_types = business_types;
    }

    public String getFinance_no() {
        return finance_no;
    }

    public void setFinance_no(String finance_no) {
        this.finance_no = finance_no;
    }

    public Integer getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(Integer refund_fee) {
        this.refund_fee = refund_fee;
    }

    public Integer getRefund_checking_status() {
        return refund_checking_status;
    }

    public void setRefund_checking_status(Integer refund_checking_status) {
        this.refund_checking_status = refund_checking_status;
    }

    public Integer getPlatform_payment_amount() {
        return platform_payment_amount;
    }

    public void setPlatform_payment_amount(Integer platform_payment_amount) {
        this.platform_payment_amount = platform_payment_amount;
    }

    public Integer getPlatform_fashionable_amount() {
        return platform_fashionable_amount;
    }

    public void setPlatform_fashionable_amount(Integer platform_fashionable_amount) {
        this.platform_fashionable_amount = platform_fashionable_amount;
    }

    public Integer getPlatform_service_fee() {
        return platform_service_fee;
    }

    public void setPlatform_service_fee(Integer platform_service_fee) {
        this.platform_service_fee = platform_service_fee;
    }

    public Integer getPlatform_refund_fee() {
        return platform_refund_fee;
    }

    public void setPlatform_refund_fee(Integer platform_refund_fee) {
        this.platform_refund_fee = platform_refund_fee;
    }

    public Integer getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(Integer seller_id) {
        this.seller_id = seller_id;
    }

    public String getSeller_nick() {
        return seller_nick;
    }

    public void setSeller_nick(String seller_nick) {
        this.seller_nick = seller_nick;
    }

    public Timestamp getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Timestamp end_time) {
        this.end_time = end_time;
    }

    public Integer getReal_pay() {
        return real_pay;
    }

    public void setReal_pay(Integer real_pay) {
        this.real_pay = real_pay;
    }

    public Integer getTrades_split() {
        return trades_split;
    }

    public void setTrades_split(Integer trades_split) {
        this.trades_split = trades_split;
    }

    public Timestamp getPay_time() {
        return pay_time;
    }

    public void setPay_time(Timestamp pay_time) {
        this.pay_time = pay_time;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Integer getSettlement_status() {
        return settlement_status;
    }

    public void setSettlement_status(Integer settlement_status) {
        this.settlement_status = settlement_status;
    }
}
