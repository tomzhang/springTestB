package com.jk51.model.account.models;


import java.sql.Timestamp;

/**
 * filename :com.jk51.model.account.models.
 * author   :zw
 * date     :2017/2/14
 * Update   :第三方交易明细model
 */
public class AccountCheck {
    private Integer id;
    private Integer site_id;
    private String site_name;
    private Integer trades_id;
    private Integer sub_trades_id;
    private String pay_number;
    private String pay_style;
    private Integer real_pay;
    private Timestamp pay_time;
    private Integer real_refund_money;
    private Timestamp refund_time;
    private Integer plat_split;
    private Integer trades_split;
    private Integer trades_source;
    private Integer trades_status;
    private Integer account_checking_status;
    private Integer check_status;
    private Timestamp platform_pay_time;
    private String import_trades_status;
    private Integer account_source;
    private Integer settlement_status;
    private Timestamp settlement_day;
    private Timestamp trade_end_time;
    private Timestamp contract_pay_day;
    private Integer count_status;
    private Timestamp account_day;
    private Integer handle_status;
    private Timestamp create_time;
    private Timestamp update_time;

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

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public Integer getTrades_id() {
        return trades_id;
    }

    public void setTrades_id(Integer trades_id) {
        this.trades_id = trades_id;
    }

    public Integer getSub_trades_id() {
        return sub_trades_id;
    }

    public void setSub_trades_id(Integer sub_trades_id) {
        this.sub_trades_id = sub_trades_id;
    }

    public String getPay_number() {
        return pay_number;
    }

    public void setPay_number(String pay_number) {
        this.pay_number = pay_number;
    }

    public String getPay_style() {
        return pay_style;
    }

    public void setPay_style(String pay_style) {
        this.pay_style = pay_style;
    }

    public Integer getReal_pay() {
        return real_pay;
    }

    public void setReal_pay(Integer real_pay) {
        this.real_pay = real_pay;
    }

    public Timestamp getPay_time() {
        return pay_time;
    }

    public void setPay_time(Timestamp pay_time) {
        this.pay_time = pay_time;
    }

    public Integer getReal_refund_money() {
        return real_refund_money;
    }

    public void setReal_refund_money(Integer real_refund_money) {
        this.real_refund_money = real_refund_money;
    }

    public Timestamp getRefund_time() {
        return refund_time;
    }

    public void setRefund_time(Timestamp refund_time) {
        this.refund_time = refund_time;
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

    public Integer getTrades_source() {
        return trades_source;
    }

    public void setTrades_source(Integer trades_source) {
        this.trades_source = trades_source;
    }

    public Integer getTrades_status() {
        return trades_status;
    }

    public void setTrades_status(Integer trades_status) {
        this.trades_status = trades_status;
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

    public Timestamp getPlatform_pay_time() {
        return platform_pay_time;
    }

    public void setPlatform_pay_time(Timestamp platform_pay_time) {
        this.platform_pay_time = platform_pay_time;
    }

    public String getImport_trades_status() {
        return import_trades_status;
    }

    public void setImport_trades_status(String import_trades_status) {
        this.import_trades_status = import_trades_status;
    }

    public Integer getAccount_source() {
        return account_source;
    }

    public void setAccount_source(Integer account_source) {
        this.account_source = account_source;
    }

    public Integer getSettlement_status() {
        return settlement_status;
    }

    public void setSettlement_status(Integer settlement_status) {
        this.settlement_status = settlement_status;
    }

    public Timestamp getSettlement_day() {
        return settlement_day;
    }

    public void setSettlement_day(Timestamp settlement_day) {
        this.settlement_day = settlement_day;
    }

    public Timestamp getTrade_end_time() {
        return trade_end_time;
    }

    public void setTrade_end_time(Timestamp trade_end_time) {
        this.trade_end_time = trade_end_time;
    }

    public Timestamp getContract_pay_day() {
        return contract_pay_day;
    }

    public void setContract_pay_day(Timestamp contract_pay_day) {
        this.contract_pay_day = contract_pay_day;
    }

    public Integer getCount_status() {
        return count_status;
    }

    public void setCount_status(Integer count_status) {
        this.count_status = count_status;
    }

    public Timestamp getAccount_day() {
        return account_day;
    }

    public void setAccount_day(Timestamp account_day) {
        this.account_day = account_day;
    }

    public Integer getHandle_status() {
        return handle_status;
    }

    public void setHandle_status(Integer handle_status) {
        this.handle_status = handle_status;
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
}
