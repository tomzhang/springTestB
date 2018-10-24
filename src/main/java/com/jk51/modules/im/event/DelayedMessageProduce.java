package com.jk51.modules.im.event;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.model.YbMeta;
import com.jk51.model.order.Trades;
import com.jk51.modules.appInterface.mapper.BMessageSenderMapper;
import com.jk51.modules.appInterface.mapper.BMessageSettingMapper;
import com.jk51.modules.im.service.InitialMessage;
import com.jk51.modules.im.service.PushServeService;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.merchant.mapper.YbMetaMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.mq.mns.CloudQueueFactory;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RunMsgWorker(queueName = "DelayedMessageMQ2-pre")//监听 DelayedMessageMQ：用于接收延时消息的普通队列,普通队列，非及时消息都丢这里
public class DelayedMessageProduce implements MessageWorker {
    public static final Logger logger = LoggerFactory.getLogger(DelayedMessageProduce.class);
    public static final String topicName = "DelayedMessageMQ2-pre";

    @Autowired
    private BMessageSettingMapper bMessageSettingMapper;
    @Autowired
    private BMessageSenderMapper bMessageSenderMapper;
    @Autowired
    private PushServeService pushServeService;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    @Override
    public void consume(Message message) throws Exception {
        logger.info("开始处理消息：" + message.getMessageBodyAsString());
        try {
            String messageStr = message.getMessageBodyAsString();
            Map<String, Object> messageMap = JSON.parseObject(messageStr, HashMap.class);

            String siteId = String.valueOf(messageMap.get("siteId"));
            String messageType = String.valueOf(messageMap.get("messageType"));

            String delayStr = bMessageSettingMapper.getDelaySeconds(PushType.settingId, messageType);
            Integer delaySeconds = null;
            Integer num = null;
            if(!StringUtil.isEmpty(delayStr)){//解析延时配置
                try {
                    delaySeconds = JSON.parseObject(delayStr).getInteger("delaySeconds");//秒 延时
                    num = JSON.parseObject(delayStr).getInteger("num");//提醒次数
                } catch (Exception e) {
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            messageMap.put("siteId", siteId);
            messageMap.put("messageType", messageType);
            messageMap.put("currentTimeConsume", sdf.format(new DateTime().toDate()));

            if(delaySeconds!=null && num!=null && (messageMap.get("startTime") != null || messageMap.get("endTime") != null)){//延时消息 处理
                if(Integer.parseInt(String.valueOf(messageMap.get("executeNum"))) <= num){//已提醒次数从 1 开始
                    if(PushType.ORDER_NOTIFYSHIPMENT.getValue().equals((String) messageMap.get("messageType"))){//提醒发货消息
                        processNotifyShipment(messageMap, delaySeconds, num);//提醒发货消息处理
                    } else {
                        Date startTime = null;
                        Date endTime = null;
                        if(!StringUtil.isEmpty(messageMap.get("startTime"))){
                            startTime = sdf.parse(String.valueOf(messageMap.get("startTime")));
                        }
                        if(!StringUtil.isEmpty(messageMap.get("endTime"))){
                            endTime = sdf.parse(String.valueOf(messageMap.get("endTime")));
                        }
                        Integer executeNum = Integer.parseInt(String.valueOf(messageMap.get("executeNum")));
                        processShipmentMessage(messageMap, delaySeconds, num, startTime, endTime, executeNum);
                    }
                }
            }else {//非延时
                if(Arrays.asList(
                    PushType.NOTIFY_CHANGE_STORES.getValue(),
                    PushType.NOTIFY_CHANGE_PASSWORD.getValue(),
                    PushType.CONSTRAINT_OUT.getValue()
                ).contains((String) messageMap.get("messageType"))){//门店调配提醒
                    processNotifyAppQuit(messageMap);
                }else if(Arrays.asList(
                    PushType.NOTIFY_SEND_COUPON.getValue(),
                    PushType.TASK_NEWTASK.getValue(),
                    PushType.TASK_PUNISHMENTTASK.getValue(),
                    PushType.TASK_FINISH.getValue(),
                    PushType.TASK_NEWORDERTASK.getValue(),
                    PushType.TASK_NEWREGISTERTASK.getValue(),
                    PushType.TASK_NEWEXAM.getValue(),
                    PushType.TASK_VISIT.getValue()
                ).contains((String) messageMap.get("messageType"))){
                    notifyMessage(messageMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
//            throw e;
        }
    }

    /**
     *直接APP消息
     */
    public void notifyMessage(Map<String, Object> messageMap){
        try {
            String siteId = !StringUtil.isEmpty(messageMap.get("siteId")) ? String.valueOf(messageMap.get("siteId")) : null;
            String storeId = !StringUtil.isEmpty(messageMap.get("storeId")) ? String.valueOf(messageMap.get("storeId")) : null;
            String storeAdminId = !StringUtil.isEmpty(messageMap.get("storeAdminId")) ? String.valueOf(messageMap.get("storeAdminId")) : null;
            String messageType = String.valueOf(messageMap.get("messageType"));

            InitialMessage initialMessage = new InitialMessage(siteId, storeId, storeAdminId);//推送消息给门店所有APP店员
            initialMessage.setMessageType(messageType);
            initialMessage.setMessageMapJSON(JSON.toJSONString(messageMap));
            pushServeService.pushMessageToList(initialMessage, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *通知APP退出登录
     */
    public void processNotifyAppQuit(Map<String, Object> messageMap){
        try {
            String siteId = (String) messageMap.get("siteId");
            String storeId = (String) messageMap.get("storeId");
            String storeAdminId = (String) messageMap.get("storeAdminId");

            String clientId = bMessageSenderMapper.getPushClientId(siteId, storeAdminId);//获取推送ID
            if(StringUtil.isEmpty(clientId)){
                return;
            }

            //String localOnLine = stringRedisTemplate.opsForValue().get("notifyId_" + siteId + "_" + storeAdminId + "_" + clientId);//查询账号是否登录APP
            /*String cid = stringRedisTemplate.opsForValue().get("notifyId_" + siteId + "_" + storeAdminId);//最后一次登录cid
            if(!StringUtil.isEmpty(cid)){*/
                InitialMessage initialMessage = new InitialMessage(siteId, storeId, storeAdminId);
                initialMessage.setMessageType((String) messageMap.get("messageType"));
                initialMessage.setMessageMapJSON(JSON.toJSONString(messageMap));
                pushServeService.pushMessageToSingle(initialMessage);
            /*}*/
        } catch (Exception e) {
           logger.error("通知APP退出登录,报错信息：{}",ExceptionUtil.exceptionDetail(e));
        }
    }


    /**
     *提醒发货消息处理
     */
    private void processNotifyShipment(Map<String, Object> messageMap, Integer delaySeconds, Integer num) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Trades trades = tradesMapper.getTradesDetails(Long.parseLong(String.valueOf(messageMap.get("tradesId"))));
        if(trades.getPostStyle().intValue()==150 && trades.getTradesStatus().intValue()==120){//订单还未发货
            messageMap.put("messageType", PushType.ORDER_NOTIFYSHIPMENT.getValue());//待发货提醒
            messageMap.put("siteId", trades.getSiteId());
            messageMap.put("storeId", trades.getAssignedStores());

            messageMap.put("tradesId", String.valueOf(trades.getTradesId()));
            messageMap.put("postStyle", trades.getPostStyle());
            messageMap.put("stockupId", !StringUtil.isEmpty(trades.getStockupId())?trades.getStockupId():"无");//取货号
            messageMap.put("delvTime", sdf.format(trades.getDelvTime()));
            messageMap.put("assignedStoreId", trades.getAssignedStores());
            messageMap.put("tradesStatus", trades.getTradesStatus());
            messageMap.put("recevierMobile", trades.getRecevierMobile());
            messageMap.put("paySuccessTime", String.valueOf(messageMap.get("paySuccessTime")));
            messageMap.put("executeNum", String.valueOf(messageMap.get("executeNum")));

            processShipmentMessage(messageMap, delaySeconds, num, null, trades.getDelvTime(), Integer.parseInt(String.valueOf(messageMap.get("executeNum"))));
        }
    }


    /**
     * APP消息
     * @param shipmentMessageMap
     * @param delaySeconds
     * @param num
     * @param startTime
     * @param endTime
     * @param executeNum
     * @throws Exception
     */
    public void processShipmentMessage(Map<String, Object> shipmentMessageMap, Integer delaySeconds, Integer num, Date startTime, Date endTime, Integer executeNum) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new DateTime().toDate();

        Long delaySecondsTime = null;
        if(delaySeconds!=null && num!=null){//延时消息 处理
            Long endMillis = null;
            if(startTime != null){
                shipmentMessageMap.put("startTime", sdf.format(startTime));
                endMillis = startTime.getTime() + num * delaySeconds * 1000;
            }else if(endTime != null){
                shipmentMessageMap.put("endTime", sdf.format(endTime));
                endMillis = endTime.getTime();
            }

            if(endMillis != null){//null作为即时消息
                Long delayNum = (endMillis - currentDate.getTime()) / 1000 / delaySeconds;//延时次数
                if (delayNum > 0) {
                    //delayNum = delayNum >= num ? num : delayNum;
                    //delaySecondsTime = (endMillis - delaySeconds * delayNum * 1000 - currentDate.getTime()) / 1000;//消息延时 时长 秒
                    delaySecondsTime = delaySeconds.longValue();
                }

                if(delayNum >= 0){
                    shipmentMessageMap.put("executeNum", executeNum);
                    shipmentMessageMap.put("remainingTime", (endMillis - currentDate.getTime()) / (60 * 1000));//距离最后一次可提醒时间 分钟

                    if(!StringUtil.isEmpty(shipmentMessageMap.get("queueName"))){
                        CloudQueue queue = CloudQueueFactory.createDelayedMessageMQ(String.valueOf(shipmentMessageMap.get("queueName")), Long.valueOf(8 * 24 * 3600), null);//NotifyShipment 通知待发货的 延时消息普通队列，DelayedMessageMQ：用于接收延时消息的普通队列
                        Message shipmentMessage = new Message();
                        shipmentMessage.setMessageBody(JSON.toJSONString(shipmentMessageMap));

                        try {
                            Message result = queue.putMessage(shipmentMessage);
                            logger.info("加入消息队列成功：" + shipmentMessageMap.get("queueName"));
                            shipmentMessageMap.put("result", result.toString());
                        } catch (Exception e) {
                            logger.debug("发送到消息队列失败 body:{} error:{}", shipmentMessage.getMessageBodyAsString(), e.getMessage());
                        }
                    } else {//APP推送
                        String siteId = !StringUtil.isEmpty(shipmentMessageMap.get("siteId")) ? String.valueOf(shipmentMessageMap.get("siteId")) : null;
                        String storeId = !StringUtil.isEmpty(shipmentMessageMap.get("storeId")) ? String.valueOf(shipmentMessageMap.get("storeId")) : null;
                        String storeAdminId = !StringUtil.isEmpty(shipmentMessageMap.get("storeAdminId")) ? String.valueOf(shipmentMessageMap.get("storeAdminId")) : null;
                        String messageType = String.valueOf(shipmentMessageMap.get("messageType"));

                        InitialMessage initialMessage = new InitialMessage(siteId, storeId, storeAdminId);//推送消息给门店所有APP店员
                        initialMessage.setMessageType(messageType);
                        initialMessage.setMessageMapJSON(JSON.toJSONString(shipmentMessageMap));
                        try {
                            pushServeService.pushMessageToList(initialMessage, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }

        if(delaySecondsTime != null){
            shipmentMessageMap.put("executeNum", executeNum + 1);
            shipmentMessageMap.put("delaySeconds", delaySecondsTime);
            shipmentMessageMap.put("currentTimeProduce", sdf.format(currentDate));

            CloudQueue queue = CloudQueueFactory.createDelayedMessageMQ(DelayedMessageProduce.topicName, Long.valueOf(8 * 24 * 3600), null);//NotifyShipment 通知待发货的 延时消息普通队列，DelayedMessageMQ：用于接收延时消息的普通队列
            Message shipmentMessage = new Message();
            shipmentMessage.setMessageBody(JSON.toJSONString(shipmentMessageMap));
            shipmentMessage.setDelaySeconds(Integer.parseInt(String.valueOf(delaySecondsTime)));//设置消息延时，单位是秒

            try {
                Message result = queue.putMessage(shipmentMessage);
                logger.info("加入消息队列成功：" + DelayedMessageProduce.topicName);
            } catch (Exception e) {
                logger.debug("发送到消息队列失败 body:{} error:{}", shipmentMessage.getMessageBodyAsString(), e.getMessage());
            }
        }
    }



    /**
     * 发送消息
     * @param messageMap    ：通知内容
     * @param siteId        ：商家ID
     * @param messageType  ：消息类型
     * @param startTime    ：通知起始时间
     * @param endTime      ：通知终止时间
     * @throws Exception
     */
    public void delayedMessageProduce(Map<String, Object> messageMap, String siteId, String messageType, Date startTime, Date endTime) throws Exception{
        if(messageMap == null){
            messageMap = new HashMap<>();
        }

        String delayStr = bMessageSettingMapper.getDelaySeconds(PushType.settingId, messageType);
        Integer delaySeconds = null;
        Integer num = null;
        if(!StringUtil.isEmpty(delayStr)){//解析延时配置
            try {
                delaySeconds = JSON.parseObject(delayStr).getInteger("delaySeconds");//秒 延时
                num = JSON.parseObject(delayStr).getInteger("num");//提醒次数
            } catch (Exception e) {
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new DateTime().toDate();

        messageMap.put("siteId", siteId);
        messageMap.put("messageType", messageType);
        messageMap.put("currentTimeProduce", sdf.format(currentDate));

        Long delaySecondsTime = null;
        if(delaySeconds!=null && num!=null){//延时消息 处理
            Long endMillis = null;
            if(startTime != null){
                messageMap.put("startTime", sdf.format(startTime));
                endMillis = startTime.getTime() + num * delaySeconds * 1000;
            }else if(endTime != null){
                messageMap.put("endTime", sdf.format(endTime));
                endMillis = endTime.getTime();
            }

            if(endMillis != null){//null作为即时消息
                Long delayNum = (endMillis - currentDate.getTime()) / 1000 / delaySeconds;//延时次数
                if (delayNum > 0) {
                    delayNum = delayNum >= num ? num : delayNum;
                    delaySecondsTime = (endMillis - delaySeconds * delayNum * 1000 - currentDate.getTime()) / 1000;//消息延时 时长 秒
                } else if (delayNum == 0) {
                    //作为即时消息
                }else {
                    return;
                }
                messageMap.put("executeNum", 1);//第一次提醒
                messageMap.put("delaySeconds", delaySecondsTime);//下次提醒时间
            }
        }

        CloudQueue queue = CloudQueueFactory.createDelayedMessageMQ(DelayedMessageProduce.topicName, Long.valueOf(8 * 24 * 3600), null);//NotifyShipment 通知待发货的 延时消息普通队列，DelayedMessageMQ：用于接收延时消息的普通队列
        Message shipmentMessage = new Message();
        shipmentMessage.setMessageBody(JSON.toJSONString(messageMap));
        if(delaySecondsTime != null){//未配置 或 未指定start&end-Time 为即时消息
            shipmentMessage.setDelaySeconds(Integer.parseInt(String.valueOf(delaySecondsTime)));//设置消息延时，单位是秒
        }

        try {
            Message result = queue.putMessage(shipmentMessage);
            logger.info("加入消息队列成功：" + DelayedMessageProduce.topicName);
        } catch (Exception e) {
            logger.debug("发送到消息队列失败 body:{} error:{}", shipmentMessage.getMessageBodyAsString(), e.getMessage());
        }
    }

}
