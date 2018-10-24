package com.jk51.modules.trades.event.factory;

import com.jk51.model.order.Trades;
import com.jk51.modules.trades.consumer.TradeMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yeah
 * 创建日期: 2017-04-19
 * 修改记录:
 */
@Component
public class RefundHandler implements DistributorHandler{
    public static final Logger logger = LoggerFactory.getLogger(RefundHandler.class);

    @Autowired
    CreateHandler createHandler;

    @Override
    public boolean handle(Trades trades) {
        logger.info("{} 发送订单退款成功消息", trades.getTradesId());
        Map<String, Object> msg = new HashMap<>();
        msg.put("tradesId", trades.getTradesId());
        msg.put("type", TradeMsgType.TRADES_REFUND);
        if (!this.createHandler.sendMqMsg(msg)) {
            logger.debug("{} 发送到消息队列失败", trades.getTradesId());
        }
        return true;
    }
}
