package com.jk51.modules.coupon.job;

import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * Created by Administrator on 2017/9/21.
 */
@Component
@RunMsgWorker(queueName = "crashCouponInfo")
public class MsnMessageSend implements MessageWorker{
    public static final Logger logger = LoggerFactory.getLogger(MsnMessageSend.class);
    public static final String topicName = "crashCouponInfo";

    @Override
    public void consume(Message message) throws Exception {
        logger.info("收到新的消息-------------");
        String messageBodyAsString = message.getMessageBodyAsString();
        Map<String, Object> tradesMsg = JacksonUtils.json2map(messageBodyAsString);
        logger.info(tradesMsg.toString());
    }
}
