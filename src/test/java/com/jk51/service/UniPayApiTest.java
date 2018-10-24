package com.jk51.service;

import com.jk51.Bootstrap;
import com.jk51.commons.date.DateUtils;
import com.jk51.modules.pay.service.uni.UniPayApi;
import org.junit.Assert;
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
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-22
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class UniPayApiTest {
    private static final Logger log = LoggerFactory.getLogger(UniPayApiTest.class);
    @Autowired
    UniPayApi uniPayApi;

    @Test
    public void testConsumeUndo() throws Exception{
        Map<String,String> map = uniPayApi.consumeUndo(DateUtils.formatDate("yyyyMMddHHmmss"),"201702221505472059218","1");
        Assert.assertNotNull(map);
    }
    @Test
    public void testQuery() throws Exception{
        String txnTime = DateUtils.formatDate("yyyyMMddHHmmss");
        Map<String,String> map = uniPayApi.query("201702240001",txnTime);
        Assert.assertNotNull(map);
    }

    @Test
    public void testRefund() throws Exception{
        Map<String,String> map = uniPayApi.refund("201702221505472059218","1");
        Assert.assertNotNull(map);
    }
}
