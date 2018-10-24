package com.jk51.modules.offline.Timer;

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
public class ERPTimer {

    @Autowired
    private ApplicationContext applicationContext;
    private static final Logger log = LoggerFactory.getLogger(ERPTimer.class);

    @Scheduled(cron = "0 0 22 4 * ?")
    public void MyTimet()  {
        log.info("**********erp会员更新消息队列有没有执行*********");
        ERPMemberEvent  erpMemberEvent = new ERPMemberEvent(this,"ERPMember");
        try {
            applicationContext.publishEvent(erpMemberEvent);
        } catch (Exception e) {
            log.info("插入失败:{}",e);
        }
    }

}
