package com.jk51.sms;

import com.jk51.Bootstrap;
import com.jk51.commons.ccprest.result.BaseResult;
import com.jk51.modules.sms.service.YtxSmsService;
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
 * 创建日期: 2017-02-20
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class YtxSmsTest {
    @Autowired
    YtxSmsService ytxSmsService;

    @Test
    public void testRegCode(){

        BaseResult  result = ytxSmsService.sendRegCodeByYtx("18513537461","22222","购买白宫");
        System.out.println(result.toString());
    }

    @Test
    public void testMsg(){
        BaseResult result = ytxSmsService.sendMsgByYtx("18616795180",new String[]{"钓鱼岛","是中国的"});
        System.out.println(result);

    }

}
