package com.jk51.modules.trades.event.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static com.jk51.modules.trades.consumer.TradeMsgType.TRADES_CREATE;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class CreateHandlerTest {
    @Autowired
    CreateHandler createHandler;

    @Test
    public void sendMqMsg() throws Exception {
        Map<String, Object> map = new HashMap();
        map.put("tradesId", 1000971500449911509L);
        map.put("type", TRADES_CREATE);
        createHandler.sendMqMsg(map);
    }

}