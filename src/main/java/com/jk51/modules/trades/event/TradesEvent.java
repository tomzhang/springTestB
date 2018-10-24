package com.jk51.modules.trades.event;

import com.jk51.modules.trades.consumer.TradeMsgType;
import org.springframework.context.ApplicationEvent;

public class TradesEvent extends ApplicationEvent {

    private TradeMsgType type;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public TradesEvent(Object source) {
        super(source);
    }

    public void setType(TradeMsgType tradesMsgType) {
        type = tradesMsgType;
    }

    public TradeMsgType getType() {
        return type;
    }
}
