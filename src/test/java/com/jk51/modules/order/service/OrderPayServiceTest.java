package com.jk51.modules.order.service;

import com.jk51.model.order.Trades;
import com.jk51.modules.trades.service.TradesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class OrderPayServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(OrderPayServiceTest.class);

    @Autowired
    private TradesService tradesService;

    @Test
    public void paySuccessCallback2() throws Exception {
        Trades trades = tradesService.getTradesByTradesId(1000731500004279089L);
        logger.info(trades.toString());
        tradesService.paySuccessCallback(trades);
//        orderPayService.paySuccessCallback2(trades);
        Thread.sleep(10000L);
        trades = tradesService.getTradesByTradesId(1000731500004279089L);
        assertEquals(trades.getTradesStatus().longValue(), 120L);
        logger.info("done");
        for (;;);
    }

}