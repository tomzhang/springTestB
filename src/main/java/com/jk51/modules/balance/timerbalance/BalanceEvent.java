package com.jk51.modules.balance.timerbalance;

import org.springframework.context.ApplicationEvent;

/**
 * Created by Administrator on 2018/5/10.
 */
public class BalanceEvent extends ApplicationEvent {

    private String timer;

    public BalanceEvent(Object source,String timer) {
        super(source);
        this.timer = timer;
    }
    public String getString() {
        return timer;
    }

}
