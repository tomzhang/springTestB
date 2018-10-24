package com.jk51.modules.offline.Timer;

import org.springframework.context.ApplicationEvent;

/**
 * Created by Administrator on 2017/7/19.
 */
public class ERPMemberEvent extends ApplicationEvent {
    private String timer;

    public ERPMemberEvent(Object source,String timer) {
        super(source);
        this.timer = timer;
    }

    public String getString() {
        return timer;
    }

}
