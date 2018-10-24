package com.jk51.modules.coupon.controller;

import com.jk51.Bootstrap;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.coupon.requestParams.CouponFilterParams;
import com.jk51.modules.promotions.job.StatusTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by ztq on 2017/12/7
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class CouponFilterControllerTest {
    @Autowired
    private CouponFilterController controller;

    /*@Test
    public void canReceiveCoupon() {
        CouponFilterParams couponFilterParams = new CouponFilterParams(100190, "4", 15234322);
        ReturnDto returnDto = controller.canReceiveCoupon(couponFilterParams);
    }*/
}
