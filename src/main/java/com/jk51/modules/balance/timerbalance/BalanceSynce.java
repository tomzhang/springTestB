package com.jk51.modules.balance.timerbalance;

import com.aliyun.mns.model.Message;
import com.jk51.modules.balance.service.BalanceService;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2018/5/11.
 */
@Component
@RunMsgWorker(queueName = "BalanceEvent")
public class BalanceSynce implements MessageWorker{
    private static Logger logger = LoggerFactory.getLogger(BalanceSynce.class);
    public static final String DateProfile= "DataProfileEvent";

    @Autowired
    private BalanceService balanceService;

    @Override
    public void consume(Message message) throws Exception {
        logger.info("===短信发送定时任务===");

        // 处理消息
        String content = message.getMessageBodyAsString();
        logger.info("{} {} {} {}",message.getReceiptHandle(),content, message.getMessageId(), message.getMessageId());

        try {
//            balanceService.sendMsgForTiming();
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }
}
