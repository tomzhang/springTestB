package com.jk51.modules.promotions.consumer;

import com.alibaba.fastjson.JSONObject;
import com.jk51.Bootstrap;
import com.jk51.model.order.Trades;
import com.jk51.modules.trades.service.TradesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by ztq on 2017/12/6
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class GroupBookingFailOperationTest {
    @Autowired
    private GroupBookingFailOperation operation;
    @Autowired
    private TradesService tradesService;

    @Test
    public void sendFailToGroupMsgToWeChatCustomer() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openId", "o4ayHwemgvRa0yJXisZgssQY1krY");
        jsonObject.put("goodsId", 1103);
        Trades trades = tradesService.getTradesByTradesId(1001901512628293258l);
    }
}
