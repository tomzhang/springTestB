package com.jk51.integral;

import com.jk51.Bootstrap;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.modules.integral.service.IntegerRuleService;
import com.jk51.modules.userScenarios.service.QrcodeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-03-08
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class TestIntegralLogService {
    /*@Autowired
    private IntegralLogService integralLogService;

    @Test
    public void testSelectIntegralLog(){
       System.out.println(integralLogService.selectIntegralLog(33867));
    }*/

    @Autowired
    private IntegerRuleService integerRuleService;





    @Test
    public void test(){
        BigInteger bi = new BigInteger("439");
        BigInteger bi2 = new BigInteger("2");
        System.out.println(bi.compareTo(bi2)+"+===="+(bi.intValue()==439));

    }

    @Test
    public void testCheckinIntegral(){
        Map<String, Object> param = new HashMap();
        param.put("siteId", 100030);
        param.put("buyerId", 225353);

        ReturnDto a = integerRuleService.integralAddForChicken(param);
        System.out.println(a.toString());
    }


}
