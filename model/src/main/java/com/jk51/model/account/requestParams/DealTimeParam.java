package com.jk51.model.account.requestParams;

/**
 * filename :com.jk51.model.account.requestParams.
 * author   :zw
 * date     :2017/3/17
 * Update   :
 * 交易时间设置请求参数
 */
public class DealTimeParam {
    private Integer site_id;
    private Integer trades_auto_close_time;//系统自动取消订单时间
    private Integer trades_auto_confirm_time;//系统自动确认收货时间
    private Integer trades_allow_refund_time;//交易成功后允许退款的时间

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getTrades_auto_close_time() {
        return trades_auto_close_time;
    }

    public void setTrades_auto_close_time(Integer trades_auto_close_time) {
        this.trades_auto_close_time = trades_auto_close_time;
    }

    public Integer getTrades_auto_confirm_time() {
        return trades_auto_confirm_time;
    }

    public void setTrades_auto_confirm_time(Integer trades_auto_confirm_time) {
        this.trades_auto_confirm_time = trades_auto_confirm_time;
    }

    public Integer getTrades_allow_refund_time() {
        return trades_allow_refund_time;
    }

    public void setTrades_allow_refund_time(Integer trades_allow_refund_time) {
        this.trades_allow_refund_time = trades_allow_refund_time;
    }
}
