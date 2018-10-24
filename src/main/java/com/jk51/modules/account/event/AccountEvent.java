package com.jk51.modules.account.event;

import com.jk51.model.account.mqRarams.AccountCouponMq;
import com.jk51.model.coupon.mqParams.SendCouponMq;
import org.springframework.context.ApplicationEvent;

/**
 * filename :com.jk51.modules.coupon.event.
 * author   :zw
 * date     :2017/5/12
 * Update   :
 */
public class AccountEvent extends ApplicationEvent {
    private AccountCouponMq accountCouponMq;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AccountEvent(Object source) {
        super(source);
    }
    public AccountEvent(Object source, AccountCouponMq accountCouponMq){
        super(source);
        this.accountCouponMq = accountCouponMq;
    }
    public AccountCouponMq getSendCouponMq(){
        return accountCouponMq;
    }
}
