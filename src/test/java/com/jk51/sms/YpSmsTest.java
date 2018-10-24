//package com.jk51.sms.ytx;
//
//import com.jk51.Bootstrap;
//import com.jk51.modules.sms.service.CommonService;
//import com.jk51.modules.sms.service.YpSmsService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
///**
// * 版权所有(C) 2017 上海伍壹健康科技有限公司
// * 描述:
// * 作者: dumingliang
// * 创建日期: 2017-02-20
// * 修改记录:
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = Bootstrap.class)
//@ActiveProfiles("test")
//public class YpSmsTest {
//    @Autowired
//    YpSmsService yunPianService;
//    @Autowired
//    CommonService commonService;
//
//    /*
//    测试短信数据是或否可以选择
//    输出0，短信发送成功，其他为失败
//     */
//    @Test
//    public void sendSMS() {
//        commonService.ladingCode2(100190, "17698311514", "51jiankang", "上海大药房",
//            "上海市虹口区宝山区", "47414785477", "4747", "www.baidu.com");
//    }
//
//
//}
