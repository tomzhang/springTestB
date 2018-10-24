package com.jk51.modules.balance.timerbalance;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.modules.merchant.Timer.DataTimerSynce;
import com.jk51.mq.mns.CloudQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/11.
 */
public class BalanceInsert implements ApplicationListener<BalanceEvent> {
    private static final Logger log = LoggerFactory.getLogger(BalanceInsert.class);

    @Override
    public void onApplicationEvent(BalanceEvent event) {
        Map messageMap = new HashMap();
        messageMap.put("messageType", event.getString());
        CloudQueue queue = CloudQueueFactory.create(DataTimerSynce.DateProfile);
        Message message = new Message();
        message.setMessageBody(JSON.toJSONString(messageMap));
        try {
            queue.putMessage(message);
            log.info("成功加入队列：" + DataTimerSynce.DateProfile);
        } catch (Exception e) {
            log.info("插入失败:{}",e);
        }
    }
}
