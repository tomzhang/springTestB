package com.jk51.modules.trades.domain;

import com.jk51.commons.CommonConstant;
import com.jk51.model.order.Trades;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class TradeBuilderTest {

    @Autowired
    TradesMapper tradesMapper;

    @Test
    public void buildEndPipe() throws Exception {
        TradeEndPipe tradeEndPipe = TradeBuilder.buildEndPipe();
        Map<String, Object> mapTrades = new HashMap<String, Object>();
        mapTrades.put("settlementStatus", CommonConstant.SETTLEMENT_STATUS_MAY);
        mapTrades.put("metaKeyFinish", 1);
        mapTrades.put("siteId", 100073);
        mapTrades.put("tradesEnd", -17);
        List<Trades> tradesWillEndList = tradesMapper.queryTradesEndList(mapTrades);
        tradeEndPipe.handler(tradesWillEndList);
    }

}