package com.jk51.sms;

import com.jk51.Bootstrap;
import com.jk51.modules.offline.service.ErpToolsService;
import com.jk51.modules.sms.service.ZtSmsService;
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
 * 创建日期: 2017-02-21
 * 修改记录:正通短信平台测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class ZtSmsTest {
    @Autowired
    ZtSmsService ztService;
    @Autowired
    private ErpToolsService erpToolsService;

    /*
    测试短信数据是否可以使用
     */

    /*
       测试语音短信数据是否可以使用
       未完成
        */
    @Test
    public void TestYYtpl() {
        System.out.println(ztService.SendSoundMessage("18616795180"));
    }

   /* @Test
    public void testEmails() {
        try {
            erpToolsService.sendMail("472239254@163.com", "错误邮件信息");
        } catch (Exception e) {
            System.out.println("邮件发送时失败，e：" + e.getMessage());
        }

    }*/
}
