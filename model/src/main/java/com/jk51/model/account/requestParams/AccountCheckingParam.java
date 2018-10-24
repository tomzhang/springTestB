package com.jk51.model.account.requestParams;

/**
 * filename :com.jk51.model.account.requestParams.
 * author   :zw
 * date     :2017/2/14
 * Update   :
 * 请求对账接口，参数
 */
public class AccountCheckingParam {

private Integer trades_id;    //平台订单号

private Integer trades_source;//订单来源，第三方或者平台订单

public Integer getTrades_id() {
    return trades_id;
}

public void setTrades_id(Integer trades_id) {
    this.trades_id = trades_id;
}

public Integer getTrades_source() {
    return trades_source;
}

public void setTrades_source(Integer trades_source) {
    this.trades_source = trades_source;
}
}
