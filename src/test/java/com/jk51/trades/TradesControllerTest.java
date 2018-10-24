package com.jk51.trades;

import com.alibaba.fastjson.JSON;
import com.jk51.Bootstrap;
import com.jk51.commons.random.CouponCodeUtil;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.TimeRule;
import com.jk51.model.order.Member;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.trades.controller.TradesController;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-02-24
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class TradesControllerTest {
    @Autowired
    private TradesController tradesController;

    @Test
    public void testDalDeliveryProcess() {  //送货上门
        //System.out.println(tradesController.dealDeliveryProcess(1000421486552961119L,120,110,110)); //跟新为已付款
        //System.out.println(tradesController.dealDeliveryProcess(1000421486552961119L,120,120,110)); //备货完成
        //System.out.println(tradesController.dealDeliveryProcess(1000421486552961119L,130,120,120)); //发货
//        System.out.println(tradesController.dealDeliveryProcess(1000421486552961119L,220,110,9999)); //确认收货
    }

    @Test
    public void testDirect_purchase() {  //直购
        // System.out.println(tradesController.direct_purchase(1000151464435602635L,150,170));
    }

    @Test
    public void testCloseTrades() {  //未付款 取消订单
        System.out.println(tradesController.closeTrades(1000791460093041268L, 1, 140));
    }


    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private CouponSendService couponSendService;
    @Autowired
    private CouponActivityMapper couponActivityMapper;


    @Test
    public void test11() {
        List<CouponRule> list = couponRuleMapper.findCouponRules();
        //list.stream().filter(item -> JSON.parseObject(item.getTimeRule(), TimeRule.class).getValidity_type() == 3).collect(Collectors.toList());

        for (CouponRule item : list) {
            TimeRule timeRule = JSON.parseObject(item.getTimeRule(), TimeRule.class);
            if (timeRule.getValidity_type() == 3) {
            couponActivityMapper.findActivityByCouponRule(item.getSiteId(), item.getRuleId()).forEach(couponActivity -> {
                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.HOUR_OF_DAY, 72);
                    String[] ids = timeRule.getAssign_rule().split(",");
                    switch (timeRule.getAssign_type()) {
                        case 1:// 此处判断按月份日期
                            int day = now.get(Calendar.DAY_OF_MONTH);
                            if (Arrays.asList(ids).contains(String.valueOf(day))) {
                                List<Member> members = memberMapper.findAllMember(item.getSiteId());
                                for (Member member : members) {
                                    if (couponSendService.syncCouponAmount(item.getRuleId(), item.getSiteId())) {
                                        CouponDetail couponDetail = CouponDetail.build(item.getSiteId(), String.valueOf(couponActivity.getId()),
                                                item.getRuleId(), CouponCodeUtil.getCouponCode(String.valueOf(item.getRuleId())),
                                                member.getMemberId(), null);
                                        couponDetailMapper.insertCouponDetail(couponDetail);
                                    } else {
                                        return;
                                    }
                                }
                            }
                            break;
                        case 2:// 此处判断按星期几
                            int dayForWeek;
                            if (now.get(Calendar.DAY_OF_WEEK) == 1) {
                                dayForWeek = 7;
                            } else {
                                dayForWeek = now.get(Calendar.DAY_OF_WEEK) - 1;
                            }
                            if (Arrays.asList(ids).contains(String.valueOf(dayForWeek))) {
                                List<Member> members = memberMapper.findAllMember(item.getSiteId());
                                for (Member member : members) {
                                    if (couponSendService.syncCouponAmount(item.getRuleId(), item.getSiteId())) {
                                        CouponDetail couponDetail = CouponDetail.build(item.getSiteId(), String.valueOf(couponActivity.getId()),
                                                item.getRuleId(), CouponCodeUtil.getCouponCode(String.valueOf(item.getRuleId())), member.getMemberId(), null);
                                        couponDetailMapper.insertCouponDetail(couponDetail);
                                    } else {
                                        return;
                                    }
                                }
                            }
                            break;
                    }

            });
            }

        }

    }
}

