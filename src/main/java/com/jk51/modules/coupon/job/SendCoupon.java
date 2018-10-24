package com.jk51.modules.coupon.job;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.jk51.annotation.MsgConsumer;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.coupon.event.CouponEventListener;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.goods.job.GoodsSync;
import com.jk51.mq.MsgConsumeException;
import com.jk51.mq.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.coupon.job.
 * author   :zw
 * date     :2017/5/12
 * Update   :
 */
@Component
@MsgConsumer(
        topicName = SendCoupon.MQTOPIC,
        tagName = SendCoupon.COUPON_SEND,
        consumeType = MsgConsumer.ConsumeType.Orderly,
        consumerGroup = "CouponConsumerGroup"
)
public class SendCoupon implements Consumer {
    private static Logger logger = LoggerFactory.getLogger(SendCoupon.class);
    public static final String COUPON_SEND = "coupon_send_sync";
    public static final String MQTOPIC = "CouponSendTopic2";

    @Autowired
    private CouponSendService couponSendService;
    @Override
    public void consume(List<MessageExt> msgs) throws MsgConsumeException {
        logger.info("收到新的消息--------");
        // 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
        MessageExt msg = msgs.get(0);
        // 处理消息
        String content = new String(msg.getBody());
        System.out.println(content);
        try {
          /*  Map<String, Object> autoSendCouponMap = JacksonUtils.json2map(content);
            String CouponType = autoSendCouponMap.get("type")==null?null:autoSendCouponMap.get("type").toString();
            String sendWay = autoSendCouponMap.get("sendWay")==null?null:autoSendCouponMap.get("sendWay").toString();
            Integer siteId = Integer.parseInt(autoSendCouponMap.get("site_id").toString());
            Integer activetyId = Integer.parseInt(autoSendCouponMap.get("activity_id").toString());
                logger.info("-----------------------------"+sendWay);
            if(Integer.parseInt(CouponType)==2&&Integer.parseInt(sendWay)==1){
                logger.info("自动发放优惠券至领券中心~");
                couponSendService.sendCouponDirect(siteId,activetyId);
            }else if(Integer.parseInt(sendWay)==5){
                logger.info("门店自动发放优惠券");
                couponSendService.sendClerk(siteId,activetyId);
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
        logger.info("msgs:{} {} {} {}", msg.getMsgId(), msg.getQueueOffset(), msg.getQueueId(), content);
    }
}
