package com.jk51.modules.trades.event.factory;

//import com.alibaba.rocketmq.client.producer.SendResult;
//import com.alibaba.rocketmq.client.producer.SendStatus;
//import com.alibaba.rocketmq.common.message.Message;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.order.Trades;
import com.jk51.modules.trades.consumer.DistributionTrade;
import com.jk51.modules.trades.consumer.TradeMsgType;
import com.jk51.mq.mns.CloudQueueFactory;
import com.jk51.mq.producer.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 分销订单创建
 */
@Component
public class CreateHandler implements DistributorHandler {
    @Autowired
    private DefaultProducer producer;
    @Autowired
    private CloudAccount account;

    public static final Logger logger = LoggerFactory.getLogger(CreateHandler.class);
    @Override
    public boolean handle(Trades trades) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("tradesId", trades.getTradesId());
        msg.put("type", TradeMsgType.TRADES_CREATE);
        if (!sendMqMsg(msg)) {
            logger.debug("{} 发送到消息队列失败", trades.getTradesId());
        }
        return true;
    }

    public boolean sendMqMsg(Map param){
        String queueName = DistributionTrade.topicName;
        CloudQueue queue = CloudQueueFactory.create(queueName);
        Message message = new Message();
        message.setMessageBody(JacksonUtils.mapToJson(param).getBytes());
        message.setMessageId(String.valueOf(param.get("tradesId")));
        try {
            Message result = queue.putMessage(message);
            logger.info("成功加入队列 {}", param);
            return true;
        }catch (Exception e){
            logger.debug("发送到消息队列失败 body:{} error:{}", message.getMessageBodyAsString(), e.getMessage());
        }

        return false;
    }

//    public boolean sendMqMsg(Map param) {
//        Message message = new Message();
//        message.setTopic(DistributionTrade.topicName);
//        message.setTags(DistributionTrade.tagName);
//        message.setBody(JacksonUtils.mapToJson(param).getBytes());
//        message.setKeys(String.valueOf(param.get("tradesId")));
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
//
//                return true;
//            }
//        } catch (Exception e) {
//            logger.debug("发送到消息队列失败 body:{} error:{}", new String(message.getBody()), e.getMessage());
//        }
//
//        return false;
//    }
}
