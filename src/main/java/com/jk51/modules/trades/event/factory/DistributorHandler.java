package com.jk51.modules.trades.event.factory;

import com.jk51.model.order.Trades;

public interface DistributorHandler {
    boolean handle(Trades trades);
}
