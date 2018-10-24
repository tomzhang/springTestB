package com.jk51.modules.trades.controller;

import com.jk51.Bootstrap;
import com.jk51.commons.http.HttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/11/24                                <br/>
 * 修改记录:                                         <br/>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class TradesControllerTest {


    @Test
    public void merchantApplyRefund() throws Exception {
        String url = "/trades/merchantApplyRefund";
        Map<String, Object> map = new HashMap<>();
        map.put("tradeId", 111);
        map.put("realRefundMoney", 222);
        HttpClient.doHttpPost(url, map);
    }

}
