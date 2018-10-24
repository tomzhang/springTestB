package com.jk51.modules.merchant.Timer;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.modules.merchant.service.TimerInsertService;
import com.jk51.mq.mns.CloudQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/19.
 */
@Async
@Component
public class DataProfileInsert implements ApplicationListener<DataProfileEvent> {

    @Autowired
    private TimerInsertService timerInsertService;
    private static final Logger log = LoggerFactory.getLogger(DateProfileTimer.class);

    @Override
    public void onApplicationEvent(DataProfileEvent event) {
        Map messageMap = new HashMap();
        messageMap.put("messageType", event.getString());
        CloudQueue queue = CloudQueueFactory.create(DataTimerSynce.DateProfile);
        Message message = new Message();
        message.setMessageBody(JSON.toJSONString(messageMap));
        try {
            Message result = queue.putMessage(message);
            log.info("成功加入队列：" + DataTimerSynce.DateProfile);
        } catch (Exception e) {
            log.info("插入失败:{}",e);
        }
    }
}
