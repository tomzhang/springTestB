package com.jk51.modules.balance.timerbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by Administrator on 2018/5/10.
 */
public class BalanceTimer {

    @Autowired
    private ApplicationContext applicationContext;
    private static final Logger log = LoggerFactory.getLogger(BalanceTimer.class);

    @Scheduled(cron = "0 0 12 * * ?")
    public void myBalanceTimer()  {
        BalanceEvent balanceEvent = new BalanceEvent(this,"balance");
        try {
            applicationContext.publishEvent(balanceEvent);
        } catch (Exception e) {
            log.info("插入失败:{}",e);
        }
    }
}
