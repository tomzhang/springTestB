package com.jk51.modules.im.event;

import com.jk51.model.order.Trades;
import org.springframework.context.ApplicationEvent;

public class PaySuccessEvent extends ApplicationEvent {
    private Trades trades;

    public PaySuccessEvent(Object source, Trades trades) {
        super(source);
        this.trades = trades;
    }

    public Trades getTrades() {
        return trades;
    }

    public void setTrades(Trades trades) {
        this.trades = trades;
    }
}
