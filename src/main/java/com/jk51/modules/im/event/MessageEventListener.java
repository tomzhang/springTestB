package com.jk51.modules.im.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.model.order.Trades;
import com.jk51.modules.im.service.InitialMessage;
import com.jk51.modules.im.service.PushServeService;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.mq.mns.CloudQueueFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Async
@Component
public class MessageEventListener {//注解先于非注解执行，监听事件必须异步
    private static Logger logger = LoggerFactory.getLogger(MessageEventListener.class);

    @Autowired
    private PushServeService pushServeService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private DelayedMessageProduce delayedMessageProduce;


    @EventListener
    public void paySuccessListener(final PaySuccessEvent event){
        Trades trades = event.getTrades();

//        trades = tradesMapper.getTradesDetails(trades.getTradesId()); //事务注解导致这里数据不变
//        logger.info("paySuccessListener.pushMessageToList："+trades);
        if(trades.getPostStyle().intValue()!=170 && trades.getTradesStatus().intValue()==120 && trades.getAssignedStores()!=null){
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("siteId", trades.getSiteId());
            msgMap.put("assignedStoreId", trades.getAssignedStores());
            msgMap.put("tradesId", String.valueOf(trades.getTradesId()));
            msgMap.put("tradesStatus", trades.getTradesStatus());
            msgMap.put("recevierMobile", trades.getRecevierMobile());
            msgMap.put("buyerId", trades.getBuyerId());
            msgMap.put("payStyle", trades.getPayStyle());
            msgMap.put("postStyle", trades.getPostStyle());
            InitialMessage initialMessage = new InitialMessage(String.valueOf(trades.getSiteId()), String.valueOf(trades.getAssignedStores()), null);//推送消息给门店所有APP店员
            initialMessage.setMessageType(PushType.ORDER_NEWORDER.getValue());
            initialMessage.setMessageMapJSON(JSON.toJSONString(msgMap));
            try {
                pushServeService.pushMessageToList(initialMessage, null);
                logger.info("pushServeService.pushMessageToList：" + JSON.toJSONString(msgMap));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            }
        }

        /*Map messageMap = new HashMap();
        messageMap.put("messageType", PushType.ORDER_NEWORDER.getValue());
        messageMap.put("trades", JSON.toJSONString(trades));
        CloudQueue queue = CloudQueueFactory.create(PaySuccessPushServe.topicName);
        Message message = new Message();
        message.setMessageBody(JSON.toJSONString(messageMap));
        try {
            Message result = queue.putMessage(message);
            logger.info("成功加入队列：" + PaySuccessPushServe.topicName);
        }catch (Exception e){
            logger.debug("发送到消息队列失败 body:{} error:{}", message.getMessageBodyAsString(), e.getMessage());
        }*/
        if (trades.getPostStyle().intValue() == 150 && trades.getTradesStatus().intValue() == 120 && trades.getDelvTime() != null){
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("messageType", PushType.ORDER_NOTIFYSHIPMENT.getValue());
            msgMap.put("siteId", trades.getSiteId());
            msgMap.put("storeId", trades.getAssignedStores());
            msgMap.put("tradesId", trades.getTradesId());
            msgMap.put("paySuccessTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new DateTime().toDate()));

            try {
                delayedMessageProduce.delayedMessageProduce(msgMap, String.valueOf(trades.getSiteId()), PushType.ORDER_NOTIFYSHIPMENT.getValue(), null, trades.getDelvTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


            //网站后台 订单提醒 :: 指派的门店,对应 b_stores.id，当assigned_stores为0时就是说明是总部发的货, 默认 null
        //这里 原来网站后台 1分钟轮询一次
        try {
            if (trades.getAssignedStores()!=null && trades.getAssignedStores()!=0) {
                stringRedisTemplate.opsForValue().set("orderRemind_Store_" + trades.getSiteId() + trades.getAssignedStores(), String.valueOf(trades.getTradesId()));
                if (System.currentTimeMillis() - trades.getPayTime().getTime() < 60000) {//若付款超过一分钟，且已指定门店，不对商家提醒订单
                    stringRedisTemplate.opsForValue().set("orderRemind_Site_" + trades.getSiteId(), String.valueOf(trades.getTradesId()));
                }
            } else {
                stringRedisTemplate.opsForValue().set("orderRemind_Site_" + trades.getSiteId(), String.valueOf(trades.getTradesId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
