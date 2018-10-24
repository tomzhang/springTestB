package com.jk51.modules.coupon.job;

import com.jk51.Bootstrap;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponActivityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2017/7/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class TestCouponStatusTask {


    @Autowired
    private CouponStausTask couponStausTask;
    private static final Logger logger = LoggerFactory.getLogger(CouponStausTask.class);

    @Before
    public void setUp() throws Exception {
        couponStausTask.setSiteId(100190);
    }
    @Test
    public void testUpdateStatus(){
        couponStausTask.updateStatus();
    }
    @Test
    public void testUpdateStatusByTimeRule(){
        couponStausTask.updateStatusByTimeRule();
    }
    @Test
    public void testUpdateStatusByTimeRuleForSpikeTicket(){
        couponStausTask.updateStatusByTimeRuleForSpikeTicket();
    }
}
