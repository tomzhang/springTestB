package com.jk51.modules.im.queue;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.mq.mns.CloudQueueFactory;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/8/27
 * 修改记录:
 */
public class ExpireProduce {

    private static final String QUEUE_NAME = "pandianExpire31";
    private static final long RETENTION_PERIOD = 6 * 60;
    private static final long DEFAULT_DELAY_SECOND = 5 * 60;

    public static final int  FOUR_MINUTE = 4 * 60;
    public static final int  FIVE_MINUTE = 5 * 60;

    public static String sendMessage(String message,Integer delaySecond){

        CloudQueue cloudQueue = CloudQueueFactory.createDelayedMessageMQ(QUEUE_NAME,RETENTION_PERIOD,DEFAULT_DELAY_SECOND);
        Message msg = new Message(message);

        if(delaySecond != null){
            msg.setDelaySeconds(delaySecond);
        }

        return cloudQueue.putMessage(msg).getReceiptHandle();

    }

    public static void deleteMsg(String receiptHandle){
        CloudQueue cloudQueue = CloudQueueFactory.createDelayedMessageMQ(QUEUE_NAME,RETENTION_PERIOD,DEFAULT_DELAY_SECOND);
        cloudQueue.deleteMessage(receiptHandle);
    }
}
