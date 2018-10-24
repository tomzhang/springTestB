package com.jk51.modules.coupon.job;

import com.jk51.model.coupon.Coupon;
import com.jk51.model.coupon.CouponRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Transactional
@Rollback
public class OldConvertNewCouponTaskTest {
    @Autowired
    OldConvertNewCouponTask task;

    @Before
    public void setUp() throws Exception {
        task.setSiteId(100180);
    }

    @Test
    public void getCoupons() throws Exception {
        List<Coupon> coupons = task.getCoupons();
        assertTrue("没有数据", coupons.size() > 0);
    }

    @Test
    public void run() {
        task.run(100166);
    }

    @Test
    public void conv2CouponRule() throws Exception {
        List<Coupon> coupons = task.getCoupons();
        Coupon coupon = coupons.get(0);
        CouponRule couponRule = task.conv2CouponRule(coupon);

        assertEquals(couponRule.getRuleName(), coupon.getCoupon_name());
        assertEquals(couponRule.getCouponType(), 100);
        assertEquals(couponRule.getMarkedWords(), coupon.getCoupon_name());
        assertEquals(couponRule.getAmount(), Integer.parseInt(coupon.getCoupon_amount()));
        assertEquals(couponRule.getStatus(), (int)coupon.getCoupon_status());
        if (coupon.getCoupon_limit_type() == 0) {
            assertNotNull(couponRule.getOrderRule());
        } else {
            assertNotNull(couponRule.getGoodsRule());
        }
        assertNotNull(couponRule.getTimeRule());
    }

   /* @Test
    public void getUserCoupon() {
        List<UserCoupon> userCoupons = mappers.task.getUserCoupon(2);
        assertTrue("没有数据", userCoupons.size() > 0);
    }

    @Test
    public void insert2NewRule() throws Exception {
        List<Coupon> coupons = mappers.task.getCoupons();
        mappers.task.insert2NewRule(coupons.get(1));
    }

    @Test
    public void conv2CouponDetail() throws Exception {
        List<UserCoupon> userCoupons = mappers.task.getUserCoupon(2);
        UserCoupon userCoupon = userCoupons.get(0);
        int ruleId = 1290;
        CouponDetail couponDetail = mappers.task.conv2CouponDetail(userCoupon, ruleId);
        assertNotNull(couponDetail);
        assertEquals(ruleId, couponDetail.getRuleId());
        assertEquals(userCoupon.getUser_coupon_code(), couponDetail.getCouponNo());
        assertEquals((userCoupon.getUser_coupon_amount() * 100), (int)(couponDetail.getMoney() * 100));
        assertEquals(userCoupon.getBuyer_id(), couponDetail.getUserId());
    }*/
}