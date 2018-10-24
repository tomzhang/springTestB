package com.jk51.modules.trades.consumer;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.Stores;
import com.jk51.model.order.Store;
import com.jk51.model.order.Trades;
import com.jk51.modules.appInterface.mapper.BMessageSettingMapper;
import com.jk51.modules.im.service.InitialMessage;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.order.service.OrderPayService;
import com.jk51.modules.sms.service.Sms7MoorService;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.mq.mns.CloudQueueFactory;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 支付回调的消费者
 *
 * @auhter zy
 * @create 2017-07-14 15:35
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
@RunMsgWorker(queueName = "TradesWebcallNew")
public class TradesWebcall implements MessageWorker {

    public static final Logger logger = LoggerFactory.getLogger(TradesWebcall.class);

    public static final String topicName = "TradesWebcallNew";

    @Autowired
    Sms7MoorService _7moorService;

    @Autowired
    DistributionTrade distributionTrade;

    @Autowired
    TradesMapper tradesMapper;
    @Autowired
    private BMessageSettingMapper bMessageSettingMapper;
    @Autowired
    StoresMapper storesMapper;
    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void consume(Message message){
        String messageBodyAsString = message.getMessageBodyAsString();
        logger.info("支付成功开始打电话!{}",messageBodyAsString);
        try {
            Map<String, Object> tradesMsg = JacksonUtils.json2map(messageBodyAsString);
            long tradesId = NumberUtils.toLong(Objects.toString(tradesMsg.get("tradesId")));
            String isTel= tradesMsg.get("isTel")+"";
            if (tradesId == 0) {
                throw new IllegalStateException("非法的订单id");
            }
            Trades trades=tradesMapper.getTradesByTradesId(tradesId);
            logger.info("支付成功打电话tradesId:{},getTradesStatus,{},getPostStyle:{}",tradesId,trades.getTradesStatus(),trades.getPostStyle());
            if(trades.getPostStyle().equals(150)&&trades.getTradesStatus().equals(120)&&trades.getStockupStatus().equals(110)&&trades.getIsRefund().equals(0)){
                String delayStr = bMessageSettingMapper.getDelaySeconds(PushType.settingId, PushType.ORDER_Webcall.getValue());
                Integer delaySeconds =JSON.parseObject(delayStr).getInteger("delaySeconds");//秒
                Long delayNum = (new Date().getTime() - trades.getPayTime().getTime()) / 1000 / 60;
                //Long delaySecondsTime = (new DateTime().getMillis() + delaySeconds * delayNum * 1000 -trades.getPayTime().getTime() ) / 1000;//消息延时 时长 秒
                //Integer num = JSON.parseObject(delayStr).getInteger("num");//失败后间隔多久提醒
                if (delayNum >=0&&delayNum<20) {
                    logger.info("支付成功打电话assigned_stores:{}",trades.getAssignedStores());
                    Stores store=storesMapper.getStore(trades.getAssignedStores(),trades.getSiteId());
                    logger.info("支付成功打电话1tradesId:{}-----------------delayNum:{}",tradesId,delayNum);
                    Map<String, Object> str = _7moorService.landingCall(store.getTel().replace("-", ""),trades.getSiteId());
                    if ("1".equals(isTel)) {
                        orderPayService.sendMqMessage(tradesId,delaySeconds,1);
                        stringRedisTemplate.opsForValue().set(tradesId+"_7moorOrder",tradesId+"");
                    }
                }
            }
        }catch (Exception e){
            logger.info("支付成功开始打电话Exception：{}",e);
        }

    }
}
