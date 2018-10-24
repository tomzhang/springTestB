package com.jk51.model.settle;

import java.io.Serializable;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-03-23
 * 修改记录:
 */
public class FinancialSettleDetail implements Serializable{

    private String site_id;

    private String merchant_name;

    private String trades_id;

    private String pay_number;

    private String pay_style_desc;

    private String trades_status;

    private String refund_status;

    private String order_time;

    private String pay_time;

    private String real_pay;

    private String platform_trade_id;

    private String pay_ac_status;

    private String pay_ac_time;

    private String platform_service_fee;

    private String platform_payment_amount;

    private String platform_status;

    private String refund_ac_status;

    private String platform_refund_fee;

    private String platform_refund_time;

    private String platform_refund_status;

    private String general_ac_status;

    private String settle_time;
    private String trades_ids;
    private String pay_times;
    private String pay_timess;
    private String trades_statuss;
    private String income_amounts;

    private String income_amount;
    private String business;
    private Integer account_checking_status;
    private String spending_amount;
    private String financenos;
    private String refundstatuss;
    private Double summaryStatistics;
    private Double summaryStatisticss;
    private Double realPaySum;
    private Double incomeSum;
    private Double spendeSum;

    public Double getRealPaySum() {
        return realPaySum;
    }

    public void setRealPaySum(Double realPaySum) {
        this.realPaySum = realPaySum;
    }

    public Double getSpendeSum() {
        return spendeSum;
    }

    public void setSpendeSum(Double spendeSum) {
        this.spendeSum = spendeSum;
    }

    public Double getIncomeSum() {
        return incomeSum;
    }

    public void setIncomeSum(Double incomeSum) {
        this.incomeSum = incomeSum;
    }

    public Double getSpendSum() {
        return spendSum;
    }

    public void setSpendSum(Double spendSum) {
        this.spendSum = spendSum;
    }

    private Double spendSum;

    private String refund_fee;

    public Double getSummaryStatistics() {
        return summaryStatistics;
    }

    public void setSummaryStatistics(Double summaryStatistics) {
        this.summaryStatistics = summaryStatistics;
    }

    public Double getSummaryStatisticss() {
        return summaryStatisticss;
    }

    public void setSummaryStatisticss(Double summaryStatisticss) {
        this.summaryStatisticss = summaryStatisticss;
    }



    public String getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(String refund_fee) {
        this.refund_fee = refund_fee;
    }

    public String getRefundstatuss() {
        return refundstatuss;
    }

    public void setRefundstatuss(String refundstatuss) {
        this.refundstatuss = refundstatuss;
    }

    public String getFinancenos() {
        return financenos;
    }

    public void setFinancenos(String financenos) {
        this.financenos = financenos;
    }

    public String getSpending_amount() {
        return spending_amount;
    }

    public void setSpending_amount(String spending_amount) {
        this.spending_amount = spending_amount;
    }

    public Integer getAccount_checking_status() {
        return account_checking_status;
    }

    public void setAccount_checking_status(Integer account_checking_status) {
        this.account_checking_status = account_checking_status;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getIncome_amount() {
        return income_amount;
    }

    public void setIncome_amount(String income_amount) {
        this.income_amount = income_amount;
    }

    public String getIncome_amounts() {
        return income_amounts;
    }

    public void setIncome_amounts(String income_amounts) {
        this.income_amounts = income_amounts;
    }

    public String getTrades_ids() {
        return trades_ids;
    }

    public void setTrades_ids(String trades_ids) {
        this.trades_ids = trades_ids;
    }

    public String getPay_times() {
        return pay_times;
    }

    public void setPay_times(String pay_times) {
        this.pay_times = pay_times;
    }

    public String getPay_timess() {
        return pay_timess;
    }

    public void setPay_timess(String pay_timess) {
        this.pay_timess = pay_timess;
    }

    public String getTrades_statuss() {
        return trades_statuss;
    }

    public void setTrades_statuss(String trades_statuss) {
        this.trades_statuss = trades_statuss;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public String getTrades_id() {
        return trades_id;
    }

    public void setTrades_id(String trades_id) {
        this.trades_id = trades_id;
    }

    public String getPay_number() {
        return pay_number;
    }

    public void setPay_number(String pay_number) {
        this.pay_number = pay_number;
    }

    public String getPay_style_desc() {
        return pay_style_desc;
    }

    public void setPay_style_desc(String pay_style_desc) {
        this.pay_style_desc = pay_style_desc;
    }

    public String getTrades_status() {
        return trades_status;
    }

    public void setTrades_status(String trades_status) {
        this.trades_status = trades_status;
    }

    public String getRefund_status() {
        return refund_status;
    }

    public void setRefund_status(String refund_status) {
        this.refund_status = refund_status;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getReal_pay() {
        return real_pay;
    }

    public void setReal_pay(String real_pay) {
        this.real_pay = real_pay;
    }

    public String getPlatform_trade_id() {
        return platform_trade_id;
    }

    public void setPlatform_trade_id(String platform_trade_id) {
        this.platform_trade_id = platform_trade_id;
    }

    public String getPay_ac_status() {
        return pay_ac_status;
    }

    public void setPay_ac_status(String pay_ac_status) {
        this.pay_ac_status = pay_ac_status;
    }

    public String getPay_ac_time() {
        return pay_ac_time;
    }

    public void setPay_ac_time(String pay_ac_time) {
        this.pay_ac_time = pay_ac_time;
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

    public String getPlatform_status() {
        return platform_status;
    }

    public void setPlatform_status(String platform_status) {
        this.platform_status = platform_status;
    }

    public String getRefund_ac_status() {
        return refund_ac_status;
    }

    public void setRefund_ac_status(String refund_ac_status) {
        this.refund_ac_status = refund_ac_status;
    }

    public String getPlatform_refund_fee() {
        return platform_refund_fee;
    }

    public void setPlatform_refund_fee(String platform_refund_fee) {
        this.platform_refund_fee = platform_refund_fee;
    }

    public String getPlatform_refund_time() {
        return platform_refund_time;
    }

    public void setPlatform_refund_time(String platform_refund_time) {
        this.platform_refund_time = platform_refund_time;
    }

    public String getPlatform_refund_status() {
        return platform_refund_status;
    }

    public void setPlatform_refund_status(String platform_refund_status) {
        this.platform_refund_status = platform_refund_status;
    }

    public String getGeneral_ac_status() {
        return general_ac_status;
    }

    public void setGeneral_ac_status(String general_ac_status) {
        this.general_ac_status = general_ac_status;
    }

    public String getSettle_time() {
        return settle_time;
    }

    public void setSettle_time(String settle_time) {
        this.settle_time = settle_time;
    }
}
