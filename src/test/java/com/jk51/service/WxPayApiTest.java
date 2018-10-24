package com.jk51.service;

import com.jk51.Bootstrap;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.pay.exception.PayException;
import com.jk51.modules.pay.service.PayfwService;
import com.jk51.modules.pay.service.wx.WxConfig;
import com.jk51.modules.pay.service.wx.WxPayApi;
import com.jk51.modules.pay.service.wx.request.WxRequestParam;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class WxPayApiTest {
    @Autowired
    WxConfig wxConfig;

    @Autowired
    WxPayApi wxPayApi;
    @Autowired
    PayfwService payService;

    /**
     * 微信配置文件测试
     */
    @Test
    public void testWxConfig() {
//        System.out.println("###############\n" + wxConfig.getAPPSECRET());
//        Assert.assertFalse(StringUtil.isEmpty(wxConfig.getAPPSECRET()));
    }

    /**
     * 微信统一下单测试
     */
    @Test
    public void testOrder() throws PayException, IOException, IllegalAccessException, NoSuchAlgorithmException {
        WxRequestParam wxRequestParam = new WxRequestParam();
        String out_trade_no = StringUtil.getRandomStr(16);
        System.out.println("订单号：" + out_trade_no);
        wxRequestParam.setOut_trade_no(out_trade_no);
        wxRequestParam.setBody("苹果");
        wxRequestParam.setTotal_fee(1);
        wxRequestParam.setTrade_type("NATIVE");
        wxRequestParam.setProduct_id("12235413214070356458059");
        String xml = wxPayApi.unifiedOrder(wxRequestParam);
        System.out.println("此处为微信统一下单返回参数\n" + xml);
        Assert.assertTrue(xml.contains("<xml><return_code><![CDATA[SUCCESS]]></return_code>"));
    }

    @Test
    public void testWxCreateNativeOrder() throws PayException, IOException {
//        payService.wxCreateNativeOrder("20170301_12341234", 1,"小米手机");
    }
    @Test
    public void sendredpack() throws PayException, IOException {
        try {
            payService.sendredpack("10019011111111111111", 200, "红包测试", "o_WI40jmxqVSw9EqrOjwaqyZOdMw");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
