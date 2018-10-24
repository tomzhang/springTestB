package com.jk51.modules.trades.consumer;

/**
 * 订单消息
 */
public enum TradeMsgType {
    TRADES_CREATE,
    TRADES_UPDATE,
    TRADES_REFUND,
    TRADES_SUCCESS,
    TRADES_FINISH,
    TRADES_PAY_SUCCESS,
}
