package com.jk51.mq;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RunWith(JUnit4.class)
public class SendMsgTest {
    private static final Logger logger = Logger.getLogger(SendMsgTest.class.getName());

    @Test
    public void testSend() {
        String accoutEndpoint = "http://1704256822322729.mns.cn-hangzhou.aliyuncs.com/";
        CloudAccount account = new CloudAccount("LTAIxqQBJq89MWnA", "4dITLAdbiBCr0mgXtY3Ne3Ocm9uvde", accoutEndpoint);

        String queueName = "TestQueue";
        CloudQueue queue = account.getMNSClient().getQueueRef(queueName);
        while (true) {
            List<Message> messages = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                Message message = new Message();
                String msg = RandomStringUtils.random(20);
                logger.info("发送消息:" + msg);
                message.setMessageBody(msg);
                queue.putMessage(message);
                messages.add(message);
            }
            queue.batchPutMessage(messages);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
