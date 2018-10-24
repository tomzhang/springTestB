package com.jk51.modules.trades.domain;

import com.jk51.model.order.Trades;

import java.util.List;

/**
 * Created by Administrator on 2017/6/1.
 */
public interface TradePipe {
    void handler(List<Trades> trades);
}
