package com.jk51.modules.goods.event;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
//import com.alibaba.rocketmq.common.message.Message;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.goods.job.GoodsSync;
import com.jk51.mq.mns.CloudQueueFactory;
import com.jk51.mq.producer.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class GoodsEventListener implements ApplicationListener<GoodsEvent> {
    private static Logger logger = LoggerFactory.getLogger(GoodsEventListener.class);

//    @Autowired
//    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DefaultProducer producer;

    @Override
    public void onApplicationEvent(final GoodsEvent event) {
//        GoodsProviders.sendSyncQueue(event.getGoods().getGoodsId(), event.getGoods().getSiteId());
        logger.info("商品进入同步队列");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("goods_id", event.getGoods().getGoodsId());
        jsonObject.put("site_id", event.getGoods().getSiteId());
        int type = 1;//mode.equals(SyncEnum.NEW) ? 1 : 0;
        jsonObject.put("type", type);

        String queueName = GoodsSync.MQTOPIC;
        CloudQueue queue = CloudQueueFactory.create(queueName);
        Message message = new Message();
        message.setMessageBody(jsonObject.toJSONString().getBytes());
        try {
            Message result = queue.putMessage(message);
            logger.info("成功加入队列");
        }catch (Exception e){
            logger.debug("发送到消息队列失败 body:{} error:{}", message.getMessageBodyAsString(), e.getMessage());
        }
//        Message message = new Message();
//        message.setTopic(GoodsSync.MQTOPIC);
//        message.setTags(GoodsSync.YIB_GOODS_SYNC_TAG);
//        message.setBody(jsonObject.toJSONString().getBytes());
//
//        try {
//            SendResult result = producer.send(message);
//            if (result.getSendStatus() == SendStatus.SEND_OK) {
//                logger.info("成功加入队列 brokerName:{} topic:{} queueId:{} offset:{}",
//                        result.getMessageQueue().getBrokerName(),
//                        result.getMessageQueue().getTopic(),
//                        result.getMessageQueue().getQueueId(),
//                        result.getQueueOffset()
//                );
//            }
//        } catch (Exception e) {
//            logger.debug(e.getMessage());
//        }
//        ListOperations listOperations = stringRedisTemplate.opsForList();
//        listOperations.leftPush(GoodsSync.YIB_GOODS_SYNC_TAG, jsonObject.toJSONString());
    }
}
