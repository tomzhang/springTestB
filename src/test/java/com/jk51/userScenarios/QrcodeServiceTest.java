package com.jk51.userScenarios;

import com.jk51.Bootstrap;
import com.jk51.modules.userScenarios.service.QrcodeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-06-13
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class QrcodeServiceTest {

    @Autowired
    private QrcodeService qrcodeService;

    @Value("${sms.sdk.appid}")
    private String appid;

    @Value("${sms.sdk.secret}")
    private String secret;

    @Test
    public void testGetAccessToken(){
        System.out.println(qrcodeService.getAccessToken(100073,appid, secret));
    }

    @Test
    public void testMilliSeconds(){
//        Long millis = qrcodeService.milliSeconds();
//        System.out.println(millis);
    }

    @Test
    public void testScanCodeConcern(){
        Map<String, Object> param = new HashMap();
        Map<String, Object> result = new HashMap();

        //测试普通关注
//        param.put("siteId", 100030);
//        param.put("ToUserName", "csyd");
//        param.put("FromUserName", "o6_bmjrPTlm6_2sgVt7hMZOPfL2M");
//        param.put("CreateTime", "1294890876859");
//        param.put("MsgType", "event");
//        param.put("Event", "subscribe");

        //带参数关注测试
//        param.put("siteId", 100030);
//        param.put("ToUserName", "csyd");
//        param.put("FromUserName", "o6_bmjrPTlm6_2sgVt7hMZOPfL2M");
//        param.put("CreateTime", "123456789");
//        param.put("MsgType", "event");
//        param.put("Event", "subscribe");
//        param.put("EventKey","qrscene_admin_10_10001");
//        param.put("Ticket","gQEY8TwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyWWk5c3MySUdleDExMDAwMDAwN0cAAgSrTj9ZAwQAAAAA");

        //已关注带参数测试
        param.put("siteId", 100030);
        param.put("ToUserName", "csyd");
        param.put("FromUserName", "o6_bmjrPTlm6_2sgVt7hMZOPfL2M");
        param.put("CreateTime", "123456789");
        param.put("MsgType", "event");
        param.put("Event", "SCAN");
        param.put("EventKey","qrscene_admin_10_10001");
        param.put("Ticket","gQEY8TwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyWWk5c3MySUdleDExMDAwMDAwN0cAAgSrTj9ZAwQAAAAA");

        result = qrcodeService.scanCodeConcern(param);

        String show = result.toString();

        System.out.println(show);


    }
}
