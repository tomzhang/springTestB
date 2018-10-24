package com.jk51.mq;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.coupon.job.MsnMessageSend;
import com.jk51.modules.trades.consumer.TradesWebcall;
import com.jk51.mq.mns.CloudQueueFactory;
import com.jk51.mq.mns.MessageReceiver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MnsTest {
    private static final Logger logger = Logger.getLogger(MnsTest.class.getName());

    @Autowired
    CloudAccount account;

    CloudQueue queue;

    @Autowired
    public void initCloudQueue(CloudAccount account) {
        String queueName = "TestQueue";
        queue = CloudQueueFactory.create(queueName);
    }

    @Test
    public void abc() {
        MNSClient client = account.getMNSClient();
        String queueName = "TestQueue";

        try {
            CloudQueue queue = CloudQueueFactory.create(queueName);
//            MessageReceiver receiver = new MessageReceiver(workerId, sMNSClient, "TestQueue");
            ExecutorService executorService = Executors.newCachedThreadPool();
            AtomicInteger ai = new AtomicInteger();
            for (int i = 0; i < 10; i++) {
                Thread thread2 = new Thread(() -> {
                    int id = ai.incrementAndGet();
                    MessageReceiver receiver = new MessageReceiver(id, client, "TestQueue");

                    while (true) {
                        logger.info("123");
                        Message message = receiver.receiveMessage();
                        queue.deleteMessage(message.getReceiptHandle());
                    }
                });
                executorService.execute(thread2);
            }

        } catch (ClientException ce) {
            System.out.println("Something wrong with the network connection between client and MNS service."
                    + "Please check your network and DNS availablity.");
            ce.printStackTrace();
        }

    }

    @Test
    public void loop() {
        while (true);
    }

    @Test
    public void test1() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Map<String, String>> maps = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, String> temp = new HashMap();
            temp.put("name", "name" + i);
            temp.put("value", "value" + i);

            maps.add(temp);
        }

        for (Map<String, String> i : maps) {
            executorService.execute(() -> {
                while (true) {
                    logger.info(i.get("name"));
                }
            });
        }
        while (true);
    }

    @Test
    public void testBatch() {
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 17; i++) {
            Message message = new Message();
            message.setMessageBody("sdfsdf" + i);
            messages.add(message);
        }
        queue.batchPutMessage(messages);
    }


    @Test
    public void testCouponMsn() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", 1);
        map.put("couponNo", 123456);
        map.put("mobile", "18520187056");
        map.put("startTime","2017-9-21");
        map.put("endTime", "2017-9-23");
        String queueName = MsnMessageSend.topicName;
        CloudQueue queue = CloudQueueFactory.create(queueName);
        Message message = new Message();
        message.setMessageBody(JacksonUtils.mapToJson(map).getBytes());
        try {
            Message result = queue.putMessage(message);
            logger.info("tradesId{} 加入消息队列成功! queueName:{} messageBody:{},messageId:{}");
        } catch (Exception e) {
            logger.info("发送到消息队列失败 body:{} error:{}");
        }
    }
}
