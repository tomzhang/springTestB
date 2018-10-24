package com.jk51.model.account.models;

import java.sql.Timestamp;

public class PayDataImport extends AccountImportData {

    private Integer id;
    private String pay_style;
    private Integer data_type;
    private String trades_id;
    private String income_amount;
    private String user_account;
    private Timestamp pay_time;
    private String business_order_sn;
    private String spending_amount;
    private String appid;
    private String trades_status;
    private String trading_scenario;
    private String remark;
    private Integer account_checking_status;
    private Integer refund_checking_status;
    private Integer check_status;
    private Timestamp create_time;
    private Timestamp update_time;
    private String account_status;
    private String platform_payment_amount;
    private String platform_service_fee;
    private String business;
    private Integer payAmountStart;
    private Integer payAmountEnd;
    private Integer refundAmountStart;
    private Integer refundAmountEnd;
    private String  account_balance;//账户余额

    private Double income_sum;
    private Double spende_sum;

    public String getAccount_balance() {
        return account_balance;
    }

    public void setAccount_balance(String account_balance) {
        this.account_balance = account_balance;
    }

    public Integer getRefund_checking_status() {
        return refund_checking_status;
    }

    public void setRefund_checking_status(Integer refund_checking_status) {
        this.refund_checking_status = refund_checking_status;
    }

    public Double getIncome_sum() {
        return income_sum;
    }

    public void setIncome_sum(Double income_sum) {
        this.income_sum = income_sum;
    }

    public Double getSpende_sum() {
        return spende_sum;
    }

    public void setSpende_sum(Double spende_sum) {
        this.spende_sum = spende_sum;
    }

    public Integer getRefundAmountStart() {
        return refundAmountStart;
    }

    public void setRefundAmountStart(Integer refundAmountStart) {
        this.refundAmountStart = refundAmountStart;
    }

    public Integer getRefundAmountEnd() {
        return refundAmountEnd;
    }

    public void setRefundAmountEnd(Integer refundAmountEnd) {
        this.refundAmountEnd = refundAmountEnd;
    }

    public Integer getPayAmountStart() {
        return payAmountStart;
    }

    public void setPayAmountStart(Integer payAmountStart) {
        this.payAmountStart = payAmountStart;
    }

    public Integer getPayAmountEnd() {
        return payAmountEnd;
    }

    public void setPayAmountEnd(Integer payAmountEnd) {
        this.payAmountEnd = payAmountEnd;
    }

    public String getFinance_no() {
        return finance_no;
    }

    public void setFinance_no(String finance_no) {
        this.finance_no = finance_no;
    }

    private String finance_no;

    public Integer getPlatform_fashionable_amount() {
        return platform_fashionable_amount;
    }

    public void setPlatform_fashionable_amount(Integer platform_fashionable_amount) {
        this.platform_fashionable_amount = platform_fashionable_amount;
    }

    private Integer platform_fashionable_amount;



    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    private String pay_time_intro;

    private String sellement_time_intro;

    private String account_checking_status_intro;
    private String refund_checking_status_intro;

    public String getRefund_checking_status_intro() {
        return refund_checking_status_intro;
    }

    public void setRefund_checking_status_intro(String refund_checking_status_intro) {
        this.refund_checking_status_intro = refund_checking_status_intro;
    }

    public String getSellement_time_intro() {
        return sellement_time_intro;
    }

    public void setSellement_time_intro(String sellement_time_intro) {
        this.sellement_time_intro = pay_time_intro;
    }

    public String getPay_time_intro() {
        return pay_time_intro;
    }

    public void setPay_time_intro(String pay_time_intro) {
        this.pay_time_intro = pay_time_intro;
    }

    public String getAccount_checking_status_intro() {
        return account_checking_status_intro;
    }

    public void setAccount_checking_status_intro(String account_checking_status_intro) {
        this.account_checking_status_intro = account_checking_status_intro;
    }


    public String getPlatform_service_fee() {
        return platform_service_fee;
    }

    public void setPlatform_service_fee(String platform_service_fee) {
        this.platform_service_fee = platform_service_fee;
    }

    public String getPlatform_payment_amount() {
        return platform_payment_amount;
    }

    public void setPlatform_payment_amount(String platform_payment_amount) {
        this.platform_payment_amount = platform_payment_amount;
    }

    public String getAccount_status() {return account_status; }

    public void setAccount_status(String account_status) {this.account_status = account_status;}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPay_style() {
        return pay_style;
    }

    public void setPay_style(String pay_style) {
        this.pay_style = pay_style;
    }

    public Integer getData_type() {
        return data_type;
    }

    public void setData_type(Integer data_type) {
        this.data_type = data_type;
    }

    public String getTrades_id() {
        return trades_id;
    }

    public void setTrades_id(String trades_id) {
        this.trades_id = trades_id;
    }

    public String getIncome_amount() {
        return income_amount;
    }

    public void setIncome_amount(String income_amount) {
        this.income_amount = income_amount;
    }

    public String getUser_account() {
        return user_account;
    }

    public void setUser_account(String user_account) {
        this.user_account = user_account;
    }

    public Timestamp getPay_time() {
        return pay_time;
    }

    public void setPay_time(Timestamp pay_time) {
        this.pay_time = pay_time;
    }

    public String getBusiness_order_sn() {
        return business_order_sn;
    }

    public void setBusiness_order_sn(String business_order_sn) {
        this.business_order_sn = business_order_sn;
    }

    public String getSpending_amount() {
        return spending_amount;
    }

    public void setSpending_amount(String spending_amount) {
        this.spending_amount = spending_amount;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getTrades_status() {
        return trades_status;
    }

    public void setTrades_status(String trades_status) {
        this.trades_status = trades_status;
    }

    public String getTrading_scenario() {
        return trading_scenario;
    }

    public void setTrading_scenario(String trading_scenario) {
        this.trading_scenario = trading_scenario;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getAccount_checking_status() {
        return account_checking_status;
    }

    public void setAccount_checking_status(Integer account_checking_status) {
        this.account_checking_status = account_checking_status;
    }

    public Integer getCheck_status() {
        return check_status;
    }

    public void setCheck_status(Integer check_status) {
        this.check_status = check_status;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public String getMapKey() {
        return trades_id + "||" + trades_status;
    }



    @Override
    public String toString() {
        return "PayDataImport{" +
                "id=" + id +
                ", pay_style='" + pay_style + '\'' +
                ", data_type=" + data_type +
                ", trades_id='" + trades_id + '\'' +
                ", income_amount='" + income_amount + '\'' +
                ", user_account='" + user_account + '\'' +
                ", pay_time=" + pay_time +
                ", business_order_sn='" + business_order_sn + '\'' +
                ", spending_amount='" + spending_amount + '\'' +
                ", appid='" + appid + '\'' +
                ", trades_status='" + trades_status + '\'' +
                ", trading_scenario='" + trading_scenario + '\'' +
                ", remark='" + remark + '\'' +
                ", account_checking_status=" + account_checking_status +
                ", check_status=" + check_status +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                '}';
    }
}
