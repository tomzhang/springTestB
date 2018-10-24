package com.jk51.modules.coupon.event;

import com.jk51.model.coupon.mqParams.SendCouponMq;
import org.springframework.context.ApplicationEvent;

/**
 * filename :com.jk51.modules.coupon.event.
 * author   :zw
 * date     :2017/5/12
 * Update   :
 */
public class CouponEvent extends ApplicationEvent {
    private SendCouponMq sendCouponMq;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public CouponEvent(Object source) {
        super(source);
    }
    public CouponEvent(Object source,SendCouponMq sendCouponMq){
        super(source);
        this.sendCouponMq = sendCouponMq;
    }
    public SendCouponMq getSendCouponMq(){
        return sendCouponMq;
    }
}
