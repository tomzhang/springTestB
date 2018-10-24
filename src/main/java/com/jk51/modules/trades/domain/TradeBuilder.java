package com.jk51.modules.trades.domain;

import com.jk51.modules.goods.library.SpringContextUtil;
import com.jk51.modules.trades.consumer.TradeMsgType;
import com.jk51.modules.trades.event.factory.CreateHandler;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TradeBuilder {

    public static TradeEndPipe buildEndPipe() {
        TradeEndPipe tradeEndPipe = (TradeEndPipe) TradePipeFactory.create("end");
        // 分销
        tradeEndPipe.use((tradesWillEndList) -> {
            CreateHandler createHandler = SpringContextUtil.getApplicationContext().getBean(CreateHandler.class);
            if (CollectionUtils.isNotEmpty(tradesWillEndList)) {
                List<Long> ids = tradesWillEndList.stream().filter(trades -> trades.getDistributorId() != null && trades.getDistributorId() > 0)
                        .map(trades -> trades.getTradesId()).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(ids)) {
                    Map msg = new HashMap();
                    msg.put("tradesIds", ids);
                    msg.put("type", TradeMsgType.TRADES_FINISH);
                    createHandler.sendMqMsg(msg);
                }
            }
        });
        // 积分
//        tradeEndPipe.use((tradeList) -> {
//            IntegerRuleService IntegerRuleService = SpringContextUtil.getApplicationContext().getBean(IntegerRuleService.class);
//            tradeList.forEach(trades -> {
//                List<Integer> tadesStatusList = Arrays.asList(220,230,800);
//                if(tadesStatusList.contains(trades.getTradesStatus())){
//                    Map<String,Object> param = new HashMap<>();
//                    param.put("siteId",trades.getSiteId());
//                    param.put("buyerId",trades.getBuyerId());
//                    param.put("tradesId",trades.getTradesId());
//                    param.put("realPay",trades.getRealPay());
//
//                    String msg = IntegerRuleService.getIntegralByShopping(param);
//                }
//            });
//        });

        return tradeEndPipe;
    }
}
