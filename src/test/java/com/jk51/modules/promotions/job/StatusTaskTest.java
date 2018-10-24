package com.jk51.modules.promotions.job;

import com.jk51.Bootstrap;
import com.jk51.modules.coupon.job.CouponStausTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/28                                <br/>
 * 修改记录:                                         <br/>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class StatusTaskTest {
    @Autowired
    private StatusTask statusTask;
    @Autowired
    private CouponStausTask couponStausTask;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void checkPromotionsRuleStatus() throws Exception {
        statusTask.checkPromotionsRuleStatus();
        couponStausTask.updateStatus();
    }

    @Test
    public void groupPurchaseStatus() throws Exception {
        statusTask.groupPurchaseStatus();
    }

    @Test
    public void test1() {
        String sql = "select * from b_promotions_rule ";

    }
}
