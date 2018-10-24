package com.jk51.model.account.models;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by xiapeng on 2017/7/26.
 */
public class AccountCheckException {
    private Integer site_id;
    private Long trades_id;
    private String pay_style;
    private Integer error_code;
    private Integer settlement_type;
    private Integer is_payment;
    private Timestamp create_time;
    private Timestamp order_time;
    private Timestamp pay_time;
    private Timestamp end_time;
    private Timestamp consign_time;

    public Timestamp getConsign_time() {
        return consign_time;
    }

    public void setConsign_time(Timestamp consign_time) {
        this.consign_time = consign_time;
    }

    private Integer real_pay;
    private Integer refund_fee;
    private Integer plat_split;
    private Integer trades_split;
    private Integer O2O_freight;
    private Integer trades_status;
    private Integer deal_finish_status;

    @Override
    public String toString() {
        return "AccountCheckException{" +
                "site_id=" + site_id +
                ", trades_id=" + trades_id +
                ", pay_style='" + pay_style + '\'' +
                ", error_code=" + error_code +
                ", settlement_type=" + settlement_type +
                ", is_payment=" + is_payment +
                ", create_time=" + create_time +
                ", order_time=" + order_time +
                ", pay_time=" + pay_time +
                ", end_time=" + end_time +
                ", real_pay=" + real_pay +
                ", refund_fee=" + refund_fee +
                ", plat_split=" + plat_split +
                ", trades_split=" + trades_split +
                ", O2O_freight=" + O2O_freight +
                ", trades_status=" + trades_status +
                ", deal_finish_status=" + deal_finish_status +
                '}';
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Long getTrades_id() {
        return trades_id;
    }

    public void setTrades_id(Long trades_id) {
        this.trades_id = trades_id;
    }

    public String getPay_style() {
        return pay_style;
    }

    public void setPay_style(String pay_style) {
        this.pay_style = pay_style;
    }

    public Integer getError_code() {
        return error_code;
    }

    public void setError_code(Integer error_code) {
        this.error_code = error_code;
    }

    public Integer getSettlement_type() {
        return settlement_type;
    }

    public void setSettlement_type(Integer settlement_type) {
        this.settlement_type = settlement_type;
    }

    public Integer getIs_payment() {
        return is_payment;
    }

    public void setIs_payment(Integer is_payment) {
        this.is_payment = is_payment;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getOrder_time() {
        return order_time;
    }

    public void setOrder_time(Timestamp order_time) {
        this.order_time = order_time;
    }

    public Timestamp getPay_time() {
        return pay_time;
    }

    public void setPay_time(Timestamp pay_time) {
        this.pay_time = pay_time;
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

    public Integer getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(Integer refund_fee) {
        this.refund_fee = refund_fee;
    }

    public Integer getPlat_split() {
        return plat_split;
    }

    public void setPlat_split(Integer plat_split) {
        this.plat_split = plat_split;
    }

    public Integer getTrades_split() {
        return trades_split;
    }

    public void setTrades_split(Integer trades_split) {
        this.trades_split = trades_split;
    }

    public Integer getO2O_freight() {
        return O2O_freight;
    }

    public void setO2O_freight(Integer o2O_freight) {
        O2O_freight = o2O_freight;
    }

    public Integer getTrades_status() {
        return trades_status;
    }

    public void setTrades_status(Integer trades_status) {
        this.trades_status = trades_status;
    }

    public Integer getDeal_finish_status() {
        return deal_finish_status;
    }

    public void setDeal_finish_status(Integer deal_finish_status) {
        this.deal_finish_status = deal_finish_status;
    }
}
