package com.jk51.modules.trades.event.factory;

import com.jk51.modules.trades.consumer.TradeMsgType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DistributorHandlerFactory {
    @Autowired
    CreateHandler createHandler;
    @Autowired
    RefundHandler refundHandler;

    public DistributorHandler create(final TradeMsgType type) {
        switch (type) {
            case TRADES_CREATE:
                return createHandler;
            case TRADES_REFUND:
                return refundHandler;
            default:
                return trades -> true;
        }
    }
}
