package com.jk51.mq;

import com.aliyun.mns.model.Message;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunMsgWorker(queueName = "TestQueue", num = 10)
public class DemoMessageWorker implements MessageWorker {
    private static final Logger logger = LoggerFactory.getLogger(DemoMessageWorker.class);

    @Override
    public void consume(Message message) throws Exception {
        logger.info(message.getMessageBody());

    }
}
