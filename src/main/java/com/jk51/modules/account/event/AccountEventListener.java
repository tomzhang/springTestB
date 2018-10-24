package com.jk51.modules.account.event;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
//import com.alibaba.rocketmq.common.message.Message;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.model.account.mqRarams.AccountCouponMq;
import com.jk51.modules.account.job.AccountTask;
import com.jk51.modules.coupon.job.SendCoupon;
import com.jk51.mq.mns.CloudQueueFactory;
import com.jk51.mq.producer.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * filename :com.jk51.modules.coupon.event.
 * author   :zw
 * date     :2017/5/12
 * Update   :
 */
@Component
public class AccountEventListener implements ApplicationListener<AccountEvent> {
    private static Logger logger = LoggerFactory.getLogger(AccountEventListener.class);

    @Autowired
    private DefaultProducer producer;

    @Override
    public void onApplicationEvent(AccountEvent event) {
        logger.info("进入自动对账队列");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("siteId", event.getSendCouponMq().getSiteId());
        jsonObject.put("type", event.getSendCouponMq().getType());

        Message message = new Message();
        String queueName = AccountTask.MQTOPIC;
        CloudQueue queue = CloudQueueFactory.create(queueName);
        message.setMessageBody(jsonObject.toJSONString().getBytes());
        try {
            Message result = queue.putMessage(message);
            logger.info("进入自动对账队列");
//            SendResult result = producer.send(message);
//            if (result.getSendStatus() == SendStatus.SEND_OK) {
//                logger.info("进入自动对账队列 brokerName:{} topic:{} queueId:{} offset:{}",
//                            result.getMessageQueue().getBrokerName(),
//                            result.getMessageQueue().getTopic(),
//                            result.getMessageQueue().getQueueId()
//                );
//            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }
}
