package com.jk51.modules.coupon.service;

import com.jk51.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/7/17                                 <br/>
 * 修改记录:                                         <br/>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class CouponActivityServiceTest {
    @Autowired
    private CouponActivityService couponActivityService;

    @Test
    public void checkStatus() throws Exception {
        System.out.println(couponActivityService.checkStatus(481, 100073));
    }

}
