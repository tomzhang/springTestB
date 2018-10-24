package com.jk51.service;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.jk51.Bootstrap;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.mqParams.SendCouponMq;
import com.jk51.modules.coupon.event.CouponEvent;
import com.jk51.modules.coupon.job.SendCouponTask;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.service.CouponActivityService;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.mq.ProducerMsgTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * filename :com.jk51.service.
 * author   :zw
 * date     :2017/5/3
 * Update   :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class CouponActivityServiceTest {

    private static Logger logger = LoggerFactory.getLogger(CouponActivityServiceTest.class);
    @Autowired
    private CouponActivityService couponActivityService;

    @Test
    public void testCouponCenter() {

        Integer siteId = 100030;
        Integer userId = 1523965;
        String type = 2 + "";
       // List<CouponActivity> couponActivities = couponActivityService.couponCenter(siteId, userId, 1, type);
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testMq() {
        SendCouponMq sendCouponMq = new SendCouponMq();
        sendCouponMq.setActivityId(1);
        sendCouponMq.setSiteId(100030);
        sendCouponMq.setType(1);
        CouponEvent couponEvent = new CouponEvent(this, sendCouponMq);
        applicationContext.publishEvent(couponEvent);
    }

    @Test
    public void getMsg() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("PushConsumer");
        consumer.setNamesrvAddr("172.20.10.164:9876");
        try {
            consumer.subscribe("CouponSendTopic", "coupon_send_sync");
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.registerMessageListener(
                    new MessageListenerConcurrently() {
                        @Override
                        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                            Message msg = msgs.get(0);
                            logger.info(msg.toString() + "----------------------------");
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                    }
            );
            consumer.start();
        } catch (Exception e) {
            logger.error("mq消费消息异常", e);
        }
    }

    @Test
    public void conviceTest() {
        String str = "[{\"goodsId\":\"4872\",\"num\":\"2\",\"goodsPrice\":1989.9999999999998}]";
        try {
            List<LinkedHashMap<String, Object>> list = JacksonUtils.json2listMap(str);
            Iterator<LinkedHashMap<String, Object>> it = list.iterator();
            List<Map<String, Integer>> goodsList = new ArrayList<>();
            while (it.hasNext()) {
                Map<String, Integer> goodsMap = new HashMap<String, Integer>();
                LinkedHashMap<String, Object> map = it.next();
                Iterator<Map.Entry<String, Object>> itMap = map.entrySet().iterator();
                while (itMap.hasNext()) {
                    Map.Entry<String, Object> entry = itMap.next();
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Integer)
                        goodsMap.put(key, (Integer) value);
                    else
                        goodsMap.put(key, (int) Math.rint(Float.parseFloat(value.toString())));
                }
                goodsList.add(goodsMap);
            }
        } catch (Exception e) {
            System.out.println("goodsInfo数据格式错误");

        }
    }

    @Test
    public void testConvince() {
        String str = "12.23";
        String.valueOf((int) Math.rint(Float.parseFloat(str) * 100));
    }

    @Autowired
    private CouponSendService couponSendService;
    @Autowired
    private CouponActivityMapper couponActivityMapper;

    @Test
    public void testSendCleck() {
        CouponActivity couponActivity = couponActivityMapper.getCouponActivity(100073, 482);
       // couponActivityService.sendCoupon(couponActivity);
        couponSendService.sendClerk(couponActivity);
        while (true) {
        }
    }
}
