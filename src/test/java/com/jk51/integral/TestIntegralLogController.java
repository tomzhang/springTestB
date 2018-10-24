
package com.jk51.integral;

import com.jk51.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-03-08
 * 修改记录:
 */


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class TestIntegralLogController {
/*    @Autowired
    private IntegralLogController integralLogController;

    @Test
    public void testStoreUpdateIntegral(){
        System.out.println(integralLogController.storeUpdateIntegral(100028,7940,-100));
    }
    @Test
    public void testSelectIntegralRecord(){
        //System.out.println("testSelectIntegralRecord 返回结果："+integralLogController.selectIntegralRecord(33867));
    }*/
}

