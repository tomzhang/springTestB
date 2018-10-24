package com.jk51.service;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.jk51.Bootstrap;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.CouponRuleActivity;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.mapper.CouponRuleActivityMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.service.GoodsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class GoodsInfoServiceTest {

//    @Autowired
//    private GoodsService goodsService;
//
//    @Autowired
//    private GoodsMapper mapper;

//    @Test
//    public void updateTest(){
//        String goodsname=mapper.get("50").getDrugName();
//        try {
//            goodsService.updateGoods("50", "111");
//        }catch (RuntimeException r) {
//            System.out.println(r);
//        }
//        Assert.assertEquals(goodsname,mapper.get("50").getDrugName());
//
//    }


    @Test
    public void updateStatusByTimeRule() {
        List<CouponRule> list = couponRuleMapper.selectTimeRuleByValidityType();
        for (CouponRule couponRule : list) {
            Map maps = (Map) JSON.parse(couponRule.getTimeRule());
            if (maps.get("startTime") != null && maps.get("endTime") != null) {
                String time1 = maps.get("startTime").toString();
                time1 = time1 + " 23:59:59";
                Long t1 = Timestamp.valueOf(time1).getTime();
                String time2 = maps.get("endTime").toString();
                time2 = time2 + " 23:59:59";
                Long t2 = Timestamp.valueOf(time2).getTime();
                Long t3 = System.currentTimeMillis();
                if (t2 < t3) {
                    couponRuleMapper.updateStatusByTime(couponRule.getSiteId(), couponRule.getRuleId(), 1);
                } else if (t1 < t3 && t3 < t2) {
                    couponRuleMapper.updateStatusByTime(couponRule.getSiteId(), couponRule.getRuleId(), 0);
                } else if (t3 < t1) {
                    couponRuleMapper.updateStatusByTime(couponRule.getSiteId(), couponRule.getRuleId(), 2);
                }
            }
        }
    }

    @Autowired
    private CouponActivityMapper mapper;
    @Autowired
    private CouponRuleActivityMapper couponRuleActivityMapper;
    @Autowired
    private CouponActivityMapper couponActivityMapper;
    @Autowired
    private CouponRuleMapper couponRuleMapper;

    @Test
    public void updateTimeByStatus() {
        Integer ruleId = 1111;
        List<CouponRuleActivity> list1 = couponRuleActivityMapper.selectActiveIdByRuleId(ruleId);
        for (CouponRuleActivity couponRuleActivity : list1) {
            if (couponRuleActivityMapper.selectActiveIdByRuleId(couponRuleActivity.getActiveId()).size() <= 1) {
                for (CouponRuleActivity cra : list1) {
                    if (couponRuleActivityMapper.getRuleByActive(cra.getSiteId(), cra.getActiveId()).size() > 1) {
                        if (couponRuleActivityMapper.selectActiveId(couponRuleActivity.getRuleId()).size() > 1) {
//                            couponActivityMapper.updateStatus(couponRuleActivity.getSiteId(), couponRuleActivity.getActiveId());
                        }
                    } else if (couponRuleActivityMapper.getRuleByActive(cra.getSiteId(), cra.getActiveId()).size() <= 1) {
                        couponActivityMapper.updateStatus(cra.getSiteId(), couponRuleActivity.getActiveId());
                    }
                }
            }
            break;
        }
    }

    @Test
    public void update123123(){
        Integer ruleId =1111;
        List<CouponRuleActivity> list1 = couponRuleActivityMapper.selectActiveIdByRuleId(ruleId);
        for (CouponRuleActivity couponRuleActivity:list1){
            if (couponRuleActivityMapper.selectActiveIdByRuleId(couponRuleActivity.getActiveId()).size() <= 1){
                if (couponRuleActivityMapper.getRuleByActive(couponRuleActivity.getSiteId(),couponRuleActivity.getRuleId()).size()<=1){
                    couponActivityMapper.updateStatus(couponRuleActivity.getSiteId(),couponRuleActivity.getActiveId());
                }
            }
        }
    }
}