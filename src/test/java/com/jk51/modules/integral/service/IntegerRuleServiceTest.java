package com.jk51.modules.integral.service;

import com.jk51.Bootstrap;
import com.jk51.model.order.Trades;
import com.jk51.modules.integral.service.IntegerRuleService;
import com.jk51.modules.trades.service.TradesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/6/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class IntegerRuleServiceTest {

    @Autowired
    private IntegerRuleService service;
    @Autowired
    private TradesService tradesService;

    @Test
    public void integralByOrderPaySuccessTest(){
        String trades_id = "1000731500280640258";
        Trades trades = tradesService.getTradesByTradesId(Long.valueOf(trades_id));

        service.integralByOrderMulti(trades);
    }

    @Test
    public void getIntegralByConsultTest(){
        service.getIntegralByConsult(100073,54299);
    }

}