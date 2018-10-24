package com.jk51.modules.trades.consumer;


import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.mq.MsgConsumeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DistributionTradeTest {
    @Autowired
    DistributionTrade distributionTrade;

    @Test
    public void testConsume() throws MsgConsumeException {
        Message message = new Message();

//        List<MessageExt> msgs = new ArrayList<>();
//        MessageExt msg = new MessageExt();
        Map<String, Object> msgBody = new HashMap<>();
        msgBody.put("tradesId", Long.valueOf("1000731498634248434"));
        msgBody.put("type", TradeMsgType.TRADES_PAY_SUCCESS);
        message.setMessageBody(JacksonUtils.mapToJson(msgBody).getBytes());

       // distributionTrade.consume(message);
//        msg.setBody(JacksonUtils.mapToJson(msgBody).getBytes());
//        msgs.add(msg);
//        distributionTrade.consume(msgs);
    }
}