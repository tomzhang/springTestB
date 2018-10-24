package com.jk51.service;

import com.jk51.Bootstrap;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.CouponFilterParams;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponFilterService;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.service.
 * author   :zw
 * date     :2017/6/28
 * Update   :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class CouponFilterServiceTest {
    @Autowired
    private CouponFilterService couponFilterService;
    @Autowired
    private PromotionsFilterService promotionsFilterService;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Test
    public void testFilter(){
        couponFilterService.getCanReceiveCoupon(100190,603+"",15232756);
    }

    @Test
    public void testFilters(){
        Map map = new HashMap();
        CouponFilterParams couponFilterParams = new CouponFilterParams();
        couponFilterParams.setSiteId(100190);
        couponFilterParams.setGoodsId(184874+"");
        couponFilterParams.setUserId(15232756);
        List<CouponActivity> canReceiveCoupon = couponFilterService.getCanReceiveCoupon( 100190, 184874+"", 15232756);
        List<PromotionsActivity> promotionsActivityList = promotionsFilterService.filterByGoodsId(couponFilterParams);
        map.put("couponList", canReceiveCoupon);
        map.put("promotionsList", promotionsActivityList);
        List<CouponRule> userCanUseCoupon = couponRuleMapper.findUserCanUseCoupon(couponFilterParams.getSiteId(),
                                                                                  couponFilterParams.getUserId());
        map.put("couponLabel",promotionsFilterService.filterCouponAndPromotions(canReceiveCoupon,promotionsActivityList,userCanUseCoupon));
        System.out.println(map);
    }

    @Test
    public void testSign(){
        CouponFilterParams couponFilterParams = new CouponFilterParams();
        couponFilterParams.setSiteId(100190);
        couponFilterParams.setGoodsId("171,173,176,209,213,220,291,361,548,");
        //couponFilterParams.setGoodsId("184874,604,605,11,171,");
        //11,171,173,176,178,205,209,213,220,291,292,301,357,361
        couponFilterParams.setUserId(15232756);
        List<PromotionsActivity> promotionsActivityList = promotionsFilterService.filterByGoodsId(couponFilterParams);
         couponFilterService.getCanUseCoupon(
                couponFilterParams.getSiteId(), couponFilterParams.getGoodsId(), couponFilterParams.getUserId(),
                promotionsActivityList);
    }
}
