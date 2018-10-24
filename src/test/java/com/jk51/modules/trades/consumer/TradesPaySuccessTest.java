package com.jk51.modules.trades.consumer;

import com.aliyun.mns.model.Message;
import com.jk51.Bootstrap;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.uitls.TaskTest;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/3                                <br/>
 * 修改记录:                                         <br/>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class TradesPaySuccessTest {
    private static final Logger log = LoggerFactory.getLogger(TaskTest.class);

    @Autowired
    private TradesPaySuccess tradesPaySuccess;

    @Test
    public void testConsume() throws Exception {
        Message message = new Message();
        Map<String, Object> map = new HashedMap();
        map.put("tradesId", "1001901501752034253");
        map.put("type", TradeMsgType.TRADES_PAY_SUCCESS);
        message.setMessageBody(JacksonUtils.mapToJson(map).getBytes());

        tradesPaySuccess.consume(message);
    }
}
