package com.jk51.sms;

import com.jk51.Bootstrap;
import com.jk51.modules.sms.service.CommonService;
import com.jk51.modules.sms.service.DjSmsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-07-23
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class DianJiTest {
    @Autowired
    private DjSmsService djSmsService;

    @Autowired
    private CommonService commonService;

    @Test
    public void dianjiSmsTest() {
        String result = djSmsService.SendMessage(100190, "您的验证码：2018，有效时间1分钟。", "17698311514", 1, 1);
        System.out.println("结果集" + result);
    }

    //验证码
    //验证码【code】,【merchanName】提示：如非本人操作，请忽略本短信。
  /*  @Test
    public void valiCodeTest() {
        Integer result = commonService.sendValiCode(100190, "17698311514", "1234", 1, 1);
        System.out.println("结果集" + result);
    }

    //订单短信
    //您已成功下单。订单号：【order】；下单时间：【Date】；详情请咨询门店：【storemobile】。
    @Test
    public void sendOrderSMSNewTest() {
        String result = commonService.sendOrderSMSNew(100190, "17698311514", "1234", "17698311514", 1);
        System.out.println("结果集" + result);
    }

    //医生预约
    //恭喜您【phone】已成功预约【doctor】，到店后请出示本通知短信进行签到，请注意医生的排班信息以免延误。
    @Test
    public void sendDoctorTest() {
        Integer result = commonService.sendDoctor("17698311514", "小杜", 100190, 1, 1);
        System.out.println("结果集" + result);
    }

    //回访短信
    //【51健康】尊敬的用户【mName】微商城【time】【title】会员福利，点击查看详情:【url】。回T退订。
    @Test
    public void sendActivitySMSTest() {
        String result = commonService.activitySMS(100190, "17698311514", "五一健康", "2018-05-01", "大酬宾", "www.baidu.com", 1);
        System.out.println("结果集" + result);
    }

    *//*三级分销短信
    【51健康】我悄悄告诉你一个验证码【code】，你就可以加入【name】了，戳下载【URL】。*//*
    @Test
    public void sendValiSMSTest() {
        String result = commonService.sendValiSMS(100190, "17698311514", "1234", "五一健康", "www.baidu.com", 1);
        System.out.println("结果集" + result);
    }

    //商户订单短信
    //     String word = "您有新的" + ordertype + "订单" + order + "，请" + storename + "尽快处理。";
    @Test
    public void sendOrderSMSTEst() {
        String result = commonService.sendOrderSMS(100190, "17698311514", "自提订单", "五一健康", "www.baidu.com", 1);
        System.out.println("结果集" + result);
    }

    //APP会员付款短信
    //【51健康】【mName】【sName】【sadminName】店员发来了一个待付款订单，需付金额：【money】元。点击链接完成付款:【url】。
    @Test
    public void sendOrderAddressTest() {
        String result = commonService.sendOrderAddress(100190, "17698311514", "51健康", "虹口门店", "小明", "10", "www.baidu.com", 1);
        System.out.println("结果集" + result);
    }

    //提货码新版短信
    //【head】提醒您到【storeName】提货；地址：【address】；电话：【storephone】；提货码：【code】点击查看条形码【URL】;
    @Test
    public void ladingCode2Test() {
        String result = commonService.ladingCode2(100190, "17698311514", "天博药房",
            "天博大药房学院路店", "世纪路与学院路交叉口东北角北数6-9间法院家属院西侧",
            "0370-2229609", "9809125469", "http://100277.shop-run.51jk.com/3QjER3", 1);
        System.out.println("结果集" + result);
    }
*/
    //环境异常短信
    @Test
    public void errorMessageTest() {
        String word = "灰度环境日志文件过大异常短信,ip地址: + ipAddress;158.47.15.57";
        String result = djSmsService.SendMessage(0, word, "17698311514", 1, 1);
        System.out.println("结果集" + result);
    }

    //服务商短信
    @Test
    public void serviceMerchantSMSTest() {
//     充值：
//        提醒商户：【51健康】提醒：您已成功充值【money】 元
//        提醒51后台：【51健康】商户：【merchantName】【siteId】已成功充值 【money】 元
//        String word_merchant = "【51健康】提醒：您已成功充值500元";
//        String word_51jk = "【51健康】商户：51健康100190已成功充值500元";
//        String result1 = commonService.serviceSMS_charge(0, word_merchant, "17698311514", 1);
//        String result2 = commonService.serviceSMS_charge(0, word_51jk, "17698311514", 1);

//        信用用完提醒（停止商家服务）：
//        提醒商户：【51健康】提醒：您的信用值已用完，并且已停止用户下单，请及时处理
//        提醒51后台：【51健康】商户：【merchantName】【siteId】信用值已用完，并且已停止用户下单，请及时处理
//        String word_merchant = "【51健康】提醒：您的信用值已用完，并且已停止用户下单，请及时处理";
//        String word_51jk = "【51健康】商户：51健康100190信用值已用完，并且已停止用户下单，请及时处理";
//        String result1 = commonService.serviceSMS_closeService(0, word_merchant, "17698311514", 1);
//        String result2 = commonService.serviceSMS_closeService(0, word_51jk, "17698311514", 1);

//        低于预警值：
//        提醒商户：【51健康】提醒：您的余额已低于预警值，请及时充值，以免影响业务
//        String word_merchant = "【51健康】提醒：您的余额已低于预警值，请及时充值，以免影响业务";
//        String result1 = commonService.serviceSMS_belowValue(0, word_merchant, "17698311514", 1);

//        余额不足预警的N%
//        提醒商户：【51健康】提醒：您的余额已不足【money】 元，请及时充值，以免影响业务
//        提醒51后台：【51健康】商户：【merchantName】【siteId】的余额已不足【money】元，请提醒充值
//        String word_merchant = "【51健康】提醒：您的余额已不足100元，请及时充值，以免影响业务";
//        String word_51jk = "【51健康】商户：51健康100190的余额已不足100元，请提醒充值";
//        String result1 = commonService.serviceSMS_lackbalance(0, word_merchant, "17698311514", 1, 1);
//        String result2 = commonService.serviceSMS_lackbalance(0, word_51jk, "17698311514", 1, 1);
//        System.out.println("结果集1=" + result1);
//        System.out.println("结果集2=" + result2);
    }

  /*  @Test
    public void logisticsSMSTest() {
        String word = "发货失败，订单号：11011，收货人：小杜的订单发货失败，请及时处理。";
        String result1 = commonService.logisticsSMS(100, word, "17698311514", null, 800);
        System.out.println("结果集1=" + result1);
    }*/
}
