package com.jk51.interceptor;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.Trades;
import com.jk51.model.order.response.OrderResponse;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.trades.consumer.TradeMsgType;
import com.jk51.modules.trades.event.TradesEvent;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TradeListener {
    public static final Logger logger = LoggerFactory.getLogger(TradeListener.class);

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private TradesMapper tradesMapper;

    /**
     * 订单创建
     * 异步方法 不阻塞订单数据
     *
     * @param jp
     * @param flag
     */
    @AfterReturning(value = "execution(*  com.jk51.modules.order.service.OrderService.createOrders(..))",
            returning = "flag")
    @Async
    public void afterSave(JoinPoint jp, Object flag) throws InterruptedException {
        OrderResponse response=(OrderResponse)flag;
        logger.info("============fenxiao 订单信息:{} ", response);
        if(StringUtil.isEmpty(response.getTradesId())){
            logger.info("订单创建失败");
            return;
        }

        try {
            Thread.sleep(1000);
            Trades trades = tradesMapper.getTradesByTradesId(Long.parseLong(response.getTradesId()));
            // 发送订单创建事件
            TradesEvent tce = new TradesEvent(trades);
            tce.setType(TradeMsgType.TRADES_CREATE);
            applicationContext.publishEvent(tce);
        } catch (Exception e) {
            logger.info("============fenxiao 发送消息错误:{} ", e);
            e.printStackTrace();
            logger.debug(e.getMessage());
        }
    }

    /**
     * 订单退款成功
     */
    @Async
    @After("execution(* com.jk51.modules.trades.mapper.TradesMapper.updateRefundStatus(..))")
    public void afterUpdate(JoinPoint jp) {
        try {
            Trades trades = (Trades) jp.getArgs()[0];
            // 发送订单退款成功事件
            TradesEvent tce = new TradesEvent(trades);
            tce.setType(TradeMsgType.TRADES_REFUND);
            applicationContext.publishEvent(tce);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }

    }
}
