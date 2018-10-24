package com.jk51.modules.trades.consumer;

import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.order.Trades;
import com.jk51.modules.clerkvisit.service.BClerkVisitService;
import com.jk51.modules.order.service.OrderPayService;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * 支付回调的消费者
 *
 * @auhter zy
 * @create 2017-07-14 15:35
 */
@Component
@RunMsgWorker(queueName = "paySuccessQueue")
public class TradesPaySuccess implements MessageWorker {

    public static final Logger logger = LoggerFactory.getLogger(TradesPaySuccess.class);

    public static final String topicName = "paySuccessQueue";

    @Autowired
    private OrderPayService orderPayService;

    @Autowired
    DistributionTrade distributionTrade;

    private @Autowired
    TradesMapper tradesMapper;


    @Override
    public void consume(Message message) throws Exception {
        String messageBodyAsString = message.getMessageBodyAsString();
        logger.info("开始处理支付成功回调订单!, {}", message);

        logger.info("首单付款发放----pay success----------------");

        Map<String, Object> tradesMsg = JacksonUtils.json2map(messageBodyAsString);
        String type = String.valueOf(tradesMsg.get("type"));
        long tradesId=0;
        if(StringUtils.contains(type, TradeMsgType.TRADES_PAY_SUCCESS.toString())) {
//                Trades trades = JSON.parseObject(String.valueOf(tradesMsg.get("trades")), Trades.class);

            if (tradesMsg.containsKey("trades")) {
                // 2017-07-20 之前发送的消息是将整个订单信息放到消息里
                Trades oldTrades = JacksonUtils.json2pojo((String)tradesMsg.get("trades"), Trades.class);
                tradesId = oldTrades.getTradesId();
            } else {
                tradesId = NumberUtils.toLong(Objects.toString(tradesMsg.get("tradesId")));
            }
            if (tradesId == 0) {
                throw new IllegalStateException("非法的订单id");
            }
            Trades trades = tradesMapper.getTradesByTradesId(tradesId);
            if (trades!=null) {
                if (trades.getDistributorId() != null && trades.getDistributorId() != 0) {
                    // 处理分销
                    distributionTrade.handlePaySuccess(trades);
                }
                orderPayService.paySuccessCallback2(trades);
            }

        }

    }
}
