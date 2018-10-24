package com.jk51.sms;

import com.jk51.Bootstrap;
import com.jk51.modules.sms.service.Sms7MoorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-02-22
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class _7MoorServiceTest {

    @Autowired
    private Sms7MoorService _7moorSmsService;

    @Test
    public void testGetTemplate(){
        System.out.println(_7moorSmsService._7moorgetSmsTemplate());
    }


    @Test
    public void testWEbCal(){
        _7moorSmsService._7MoorWebCallNew("18302196165","0019","100190");
    }

    @Test
    public void testCall(){
        _7moorSmsService.webcall("02165798989",0);
    }
    @Test
    public void landingCall(){
        _7moorSmsService.landingCall("18302196165",100190);
    }
}
