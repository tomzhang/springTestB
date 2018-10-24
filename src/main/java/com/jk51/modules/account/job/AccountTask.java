package com.jk51.modules.account.job;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import com.jk51.annotation.MsgConsumer;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.account.service.ChargeOffService;
import com.jk51.modules.account.service.SettlementDetailService;
import com.jk51.mq.MsgConsumeException;
import com.jk51.mq.consumer.Consumer;
import com.jk51.mq.mns.CloudQueueFactory;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jk51.model.account.mqRarams.AccountCouponMq;

import java.util.List;

/**
 * filename :com.jk51.modules.coupon.job.
 * author   :zw
 * date     :2017/5/12
 * Update   :
 */
@Component
@RunMsgWorker(queueName = "CouponSendTopic")
public class AccountTask implements MessageWorker{
    @Autowired
    private SettlementDetailService settlementDetailService;
    @Autowired
    private ChargeOffService chargeOffService;

    private static final Logger logger = LoggerFactory.getLogger(AccountTask.class);
    public static final String MQTOPIC = "CouponSendTopic";

    @Override
    public void consume(Message message) throws Exception {
        logger.info("收到新的消息----account----");

        if(message != null){
            // 处理消息
            String content = message.getMessageBodyAsString();
            AccountCouponMq accountCouponMq = null;

            try {
                accountCouponMq = JacksonUtils.json2pojo(content, AccountCouponMq.class);
                logger.info("message:{} {} {} {}",message.getReceiptHandle(),message.getMessageBodyAsString(),message.getMessageId(),message.getDequeueCount());

                switch (accountCouponMq.getType()) {
                    case 1:
                        try {
                            settlementDetailService.batchAccountChecking(null);
                            logger.info("run auto account result:start");
                        } catch (Exception e) {
                            logger.error("Run Billing Job Error:", e);
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            chargeOffService.classifiedAccount(accountCouponMq.getSiteId(),"system");
                            logger.info("run auto account result:start");
                        } catch (Exception e) {
                            logger.error("Run Billing Job Error:", e);
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
//@MsgConsumer(
//        topicName = AccountTask.MQTOPIC,
//        tagName = AccountTask.ACCOUNT_SYNC,
//        consumeType = MsgConsumer.ConsumeType.Orderly,
//        consumerGroup = "ConsumerGroup"
//)
//public class AccountTask implements Consumer {
//    @Autowired
//    private SettlementDetailService settlementDetailService;
//    @Autowired
//    private ChargeOffService chargeOffService;
//
//    private static Logger logger = LoggerFactory.getLogger(AccountTask.class);
//    public static final String ACCOUNT_SYNC = "account_sync";
//    public static final String MQTOPIC = "CouponSendTopic";
//
//    @Override
//    public void consume(List<MessageExt> msgs) throws MsgConsumeException {
//        logger.info("收到新的消息----account----");
//        // 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
//        MessageExt msg = msgs.get(0);
//        // 处理消息
//        String content = new String(msg.getBody());
//        AccountCouponMq accountCouponMq = null;
//        try {
//            accountCouponMq = JacksonUtils.json2pojo(content, AccountCouponMq.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        logger.info("msgs:{} {} {} {}", msg.getMsgId(), msg.getQueueOffset(), msg.getQueueId(), content);
//        switch (accountCouponMq.getType()) {
//            case 1:
//                try {
//                    settlementDetailService.batchAccountChecking();
//                    logger.info("run auto account result:start");
//                } catch (Exception e) {
//                    logger.error("Run Billing Job Error:", e);
//                    e.printStackTrace();
//                }
//                break;
//            case 2:
//                try {
//                    chargeOffService.classifiedAccount(accountCouponMq.getSiteId());
//                    logger.info("run auto account result:start");
//                } catch (Exception e) {
//                    logger.error("Run Billing Job Error:", e);
//                    e.printStackTrace();
//                }
//                break;
//            default:
//                break;
//        }
//    }
//}


