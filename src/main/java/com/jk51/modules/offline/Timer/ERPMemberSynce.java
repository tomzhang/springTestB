package com.jk51.modules.offline.Timer;

import com.aliyun.mns.model.Message;
import com.jk51.modules.offline.service.GuangJiOfflineService;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/7/19.
 */
@Component
@RunMsgWorker(queueName = "ERPMemberEvent")
public class ERPMemberSynce implements MessageWorker {

    @Autowired
    private GuangJiOfflineService guangJiOfflineService;
    private static Logger logger = LoggerFactory.getLogger(ERPMemberSynce.class);
    public static final String ERPMember = "ERPMemberEvent";

    @Override
    public void consume(Message message) {
        logger.info("===ERP会员同步定时任务===");
        // 处理消息
        String content = message.getMessageBodyAsString();
        logger.info("{} {} {} {}", message.getReceiptHandle(), content, message.getMessageId(), message.getMessageId());
        try {
            guangJiOfflineService.memberByTimer();
        } catch (Exception e) {
            logger.debug(e.getMessage()); // 抛出一个异常更新失败
        }
    }


}
