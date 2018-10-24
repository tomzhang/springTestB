package com.jk51.modules.merchant.Timer;

import org.springframework.context.ApplicationEvent;

/**
 * Created by Administrator on 2017/7/19.
 */
public class DataProfileEvent extends ApplicationEvent {
    private String timer;

    public DataProfileEvent(Object source,String timer) {
        super(source);
        this.timer = timer;
    }

    public String getString() {
        return timer;
    }

}
