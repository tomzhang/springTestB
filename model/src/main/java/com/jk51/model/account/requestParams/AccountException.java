package com.jk51.model.account.requestParams;

import com.jk51.model.order.Page;

import java.sql.Timestamp;

/**
 * Created by xiapeng on 2017/7/25.
 */
public class AccountException extends Page{
    private String trades_id;
    private String paystyle;
    private String settlementtype;
    private String ispayment;
    private String creat_time;
    private String order_time;
    private String pay_time;
    private String end_time;
    private String consign_time;
    private String realpay;

    public String getConsign_time() {
        return consign_time;
    }

    public void setConsign_time(String consign_time) {
        this.consign_time = consign_time;
    }

    private String refundfee;
    private String platsplit;
    private String tradessplit;
    private String O2Ofreight;
    private  String tradesstatus;
     private String  dealfinishstatus;
     private  String  error_code;

    public String getTrades_id() {
        return trades_id;
    }

    public void setTrades_id(String trades_id) {
        this.trades_id = trades_id;
    }

    public String getPaystyle() {
        return paystyle;
    }

    public void setPaystyle(String paystyle) {
        this.paystyle = paystyle;
    }

    public String getSettlementtype() {
        return settlementtype;
    }

    public void setSettlementtype(String settlementtype) {
        this.settlementtype = settlementtype;
    }

    public String getIspayment() {
        return ispayment;
    }

    public void setIspayment(String ispayment) {
        this.ispayment = ispayment;
    }

    public String getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(String creat_time) {
        this.creat_time = creat_time;
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

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getRealpay() {
        return realpay;
    }

    public void setRealpay(String realpay) {
        this.realpay = realpay;
    }

    public String getRefundfee() {
        return refundfee;
    }

    public void setRefundfee(String refundfee) {
        this.refundfee = refundfee;
    }

    public String getPlatsplit() {
        return platsplit;
    }

    public void setPlatsplit(String platsplit) {
        this.platsplit = platsplit;
    }

    public String getTradessplit() {
        return tradessplit;
    }

    public void setTradessplit(String tradessplit) {
        this.tradessplit = tradessplit;
    }

    public String getO2Ofreight() {
        return O2Ofreight;
    }

    public void setO2Ofreight(String o2Ofreight) {
        O2Ofreight = o2Ofreight;
    }

    public String getTradesstatus() {
        return tradesstatus;
    }

    public void setTradesstatus(String tradesstatus) {
        this.tradesstatus = tradesstatus;
    }

    public String getDealfinishstatus() {
        return dealfinishstatus;
    }

    public void setDealfinishstatus(String dealfinishstatus) {
        this.dealfinishstatus = dealfinishstatus;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }
}
