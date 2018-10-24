package com.jk51.service;

import com.jk51.Bootstrap;
import com.jk51.modules.pay.service.PayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
*
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-23
 * 修改记录:
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class PayServiceTest {
    private static final Logger log = LoggerFactory.getLogger(PayServiceTest.class);
    @Autowired
    PayService payService;

    @Test
    public void wxRefund() throws Exception {
//        payService.wxRefund(100190, 1001901524030958275l, "", 35, 35, System.currentTimeMillis());
//        payService.wxRefund(100190, 1001901524032140948l, "", 1, 1, System.currentTimeMillis());
//        payService.wxRefund(100190, 1001901524035343263l, "", 10, 10, System.currentTimeMillis());
//        payService.wxRefund(100190, 1001901524035534355l, "", 10, 10, System.currentTimeMillis());
        }

//    @Test
//    public void testGetAccessToken() throws Exception{
//        String accessToken = payService.getAccessToken(null,null,null);
//        log.info("access_token:{}", accessToken);
//    }
//
//    @Test
//    public void testGetJSAPITicketByToken() throws Exception{
//        String accessToken = payService.getAccessToken(null,null,null);
//        String ticket = payService.getJSAPITicket(null,null);
//        log.info("ticket:{}", ticket);
//    }
//
//    @Test
//    public void testGetJSAPITicket() throws Exception{
//        String ticket = payService.getJSAPITicket(null,null,null);
//        log.info("ticket:{}", ticket);
//    }

    @Test
    public void test() throws Exception{
        // Auto-generated method stub
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        dfs.setGroupingSeparator(',');
        dfs.setMonetaryDecimalSeparator('.');

        DecimalFormat df = new DecimalFormat("###,###.##", dfs);

        String aa = "3,500,000.00";
        Number num = df.parse(aa);
        System.out.println("--------"+num.doubleValue());
    }
}
