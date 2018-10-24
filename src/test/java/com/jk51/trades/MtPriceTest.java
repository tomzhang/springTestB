package com.jk51.trades;

import com.jk51.Bootstrap;
import com.jk51.modules.trades.service.TradesDeliveryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: chenpengtao
 * @Description:
 * @Date: created in 2018/8/14
 * @Modified By:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class MtPriceTest {

    @Autowired
    private TradesDeliveryService tradesDeliveryService;

    @Test
    public void test1(){
        int res = tradesDeliveryService.calcMtPrice("上海",4.1);
        System.out.println("执行结果--"+res);
    }


}
