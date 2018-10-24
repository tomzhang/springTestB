package com.jk51.modules.merchant.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * Created by Administrator on 2017/7/17.
 */
@Component
public class DateProfileTimer {

    @Autowired
    private ApplicationContext applicationContext;
    private static final Logger log = LoggerFactory.getLogger(DateProfileTimer.class);

    @Scheduled(cron = "0 0 1 * * ?")
    public void MyTimet()  {
        log.info("**********数据概况消息队列有没有执行*********");
        DataProfileEvent dataProfileEvent = new DataProfileEvent(this,"dataProfile");
        try {
            applicationContext.publishEvent(dataProfileEvent);
        } catch (Exception e) {
            log.info("插入失败:{}",e);
        }
    }





}
