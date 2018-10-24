package com.jk51.service;

import com.jk51.Bootstrap;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.order.Trades;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.modules.trades.service.TradesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * filename :com.jk51.service.
 * author   :zw
 * date     :2017/5/24
 * Update   :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class TradesServiceTest {
    @Autowired
    private TradesService tradesService;
    @Autowired
    private TradesMapper tradesMapper;

    @Test
    public void testTradesEnd(){
        tradesService.tradesEnd();
    }

    @Test
    public void updateConfirmStatusTest(){
        //门店或用户确认收货
        Trades trades = tradesMapper.getTradesByTradesId(Long.valueOf("1000731498635699052"));
        try{
            tradesService.updateConfirmStatusExtra(trades,120, "测试status");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void systemDeliveryTest(){
        //系统确认收货 1000731495453895863
        tradesService.systemDelivery();

    }

    @Test
    public void closeTrades() throws BusinessLogicException {
        Trades trades = tradesMapper.getTradesByTradesId(1001901520991864127l);
        tradesService.closeTrades(trades, trades.getTradesStatus(), "拼团时效到期，未付款");
    }
}
