
package com.jk51.service;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.jk51.Bootstrap;
import com.jk51.modules.pay.service.ali.AliPayApi;
import com.jk51.modules.pay.service.ali.request.AliRequestParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class AliPayApiTest {
    @Autowired
    AliPayApi aliPayApi;
    @Test
    public void testPrecreate() throws Exception {
        AliRequestParam aliRequestParam = new AliRequestParam();
        aliRequestParam.setOut_trade_no("12345");
        aliRequestParam.setSubject("我是大老鼠");
        aliRequestParam.setTotal_amount(12.11f);
        AlipayTradePrecreateResponse response = aliPayApi.precreate(aliRequestParam);
        System.out.println("订单号为："+response.getOutTradeNo() + "\n支付地址为：" + response.getQrCode());
    }

    @Test
    public void testQuery() throws Exception {
        AliRequestParam aliRequestParam = new AliRequestParam();
        aliRequestParam.setOut_trade_no("12345");
        AlipayTradeQueryResponse response = aliPayApi.query(aliRequestParam);
        System.out.println("msg是："+ response.getMsg() +"\n订单号为："+response.getOutTradeNo());
    }

    @Test
    public void testRefund() throws Exception {
        AliRequestParam aliRequestParam = new AliRequestParam();
        aliRequestParam.setOut_trade_no("12345");
        aliRequestParam.setRefund_amount(11.11f);
        AlipayTradeQueryResponse response = aliPayApi.query(aliRequestParam);
        System.out.println("msg是："+ response.getMsg() +"\n订单号为："+response.getOutTradeNo());
    }


}

