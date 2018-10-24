package com.jk51.modules.coupon.event;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.jk51.modules.coupon.job.SendCoupon;
import com.jk51.modules.goods.event.GoodsEventListener;
import com.jk51.modules.goods.job.GoodsSync;
import com.jk51.mq.producer.DefaultProducer;
import groovy.util.logging.Commons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * filename :com.jk51.modules.coupon.event.
 * author   :zw
 * date     :2017/5/12
 * Update   :
 */
@Component
public class CouponEventListener implements ApplicationListener<CouponEvent> {
    private static Logger logger = LoggerFactory.getLogger(CouponEventListener.class);

    @Autowired
    private DefaultProducer producer;

    @Override
    public void onApplicationEvent(CouponEvent event) {
        logger.info("进入优惠券发放队列");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("activity_id", event.getSendCouponMq().getActivityId());
        jsonObject.put("site_id", event.getSendCouponMq().getSiteId());
        jsonObject.put("type", event.getSendCouponMq().getType());
        jsonObject.put("sendWay", event.getSendCouponMq().getSendWay());

        Message message = new Message();
        message.setTopic(SendCoupon.MQTOPIC);
        message.setTags(SendCoupon.COUPON_SEND);
        message.setBody(jsonObject.toJSONString().getBytes());

        try {
            SendResult result = producer.send(message);
            if (result.getSendStatus() == SendStatus.SEND_OK) {
                logger.info("优惠券成功加入队列 brokerName:{} topic:{} queueId:{} offset:{}",
                            result.getMessageQueue().getBrokerName(),
                            result.getMessageQueue().getTopic(),
                            result.getMessageQueue().getQueueId(),
                            result.getQueueOffset()
                );
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }
}
