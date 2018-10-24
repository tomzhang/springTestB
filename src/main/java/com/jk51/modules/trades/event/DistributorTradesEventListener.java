package com.jk51.modules.trades.event;

import com.jk51.model.order.Trades;
import com.jk51.modules.trades.event.factory.DistributorHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 分销订单事件监听
 */
@Component
public class DistributorTradesEventListener implements ApplicationListener<TradesEvent> {
    public static final Logger logger = LoggerFactory.getLogger(DistributorTradesEventListener.class);

    @Autowired
    DistributorHandlerFactory distributorHandlerFactory;

    @Override
    public void onApplicationEvent(final TradesEvent event) {
        try {
            Trades trades = (Trades) event.getSource();
            if (trades.getDistributorId() == null || trades.getDistributorId() == 0) {
                logger.info("{} 不是分销订单,或者是分销订单但没有推荐人且用户还未成为分销商", trades.getTradesId());
                return;
            }

            // 处理
            this.distributorHandlerFactory.create(event.getType()).handle(trades);
        } catch (Exception e) {
            logger.debug("处理分销订单发生异常 {}", e.getMessage());
        }
    }
}
