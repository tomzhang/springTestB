package com.jk51.modules.coupon.job;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.random.CouponCodeUtil;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.LimitRule;
import com.jk51.model.coupon.requestParams.TimeRule;
import com.jk51.model.order.Member;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponActivityProcessService;
import com.jk51.modules.coupon.service.CouponActivityService;
import com.jk51.modules.coupon.service.CouponRuleService;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chenpeng
 * 创建日期: 2017/3/7
 * 修改记录:
 */
@Component
public class SendCouponTask {

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
    @Autowired
    private CouponActivityService couponActivityService;

    @Autowired
    private CouponActivityProcessService couponActivityProcessService;

    @Autowired
    private CouponRuleService couponRuleService;

    private static final Logger logger = LoggerFactory.getLogger(SendCouponTask.class);

    public void sendMembersDayCoupons() {
        TimeRule timeRule = null;
        List<CouponRule> list = couponRuleMapper.findCouponRules();
        for (CouponRule item : list) {
            try {
                timeRule = JSON.parseObject(item.getTimeRule(), TimeRule.class);
            } catch (Exception e) {
                logger.error("timeRule转换异常");
                continue;
            }

            if (timeRule.getValidity_type() == 3) {
                TimeRule finalTimeRule = timeRule;
                couponActivityMapper.findActivityByCouponRule(item.getSiteId(), item.getRuleId()).forEach(couponActivity -> {
                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.HOUR_OF_DAY, 72);
                    String[] ids = finalTimeRule.getAssign_rule().split(",");
                    switch (finalTimeRule.getAssign_type()) {
                        case 1:// 此处判断按月份日期
                            int day = now.get(Calendar.DAY_OF_MONTH);
                            startSend(ids, day, item, couponActivity);
                            break;
                        case 2:// 此处判断按星期几
                            int dayForWeek;
                            if (now.get(Calendar.DAY_OF_WEEK) == 1) {
                                dayForWeek = 7;
                            } else {
                                dayForWeek = now.get(Calendar.DAY_OF_WEEK) - 1;
                            }
                            startSend(ids, dayForWeek, item, couponActivity);
                            break;
                    }
                });
            }
        }
    }

    private void startSend(String[] ids, int day, CouponRule item, CouponActivity couponActivity) {
        List<String> l = Arrays.asList(ids);
        List<String> res = new ArrayList<>();
        res.addAll(l);
        if (res.contains(String.valueOf(day))) {
            // 先判断一次优惠券数量是否还有，没有就不会去获取会员
            if (couponSendService.syncCouponAmount(item.getRuleId(), item.getSiteId())) {
                List<Member> members = memberMapper.findAllMember(item.getSiteId());
                for (Member member : members) {
                    if (couponSendService.syncCouponAmount(item.getRuleId(), item.getSiteId())) {
                        CouponDetail couponDetail = couponActivityProcessService.findDistanceResult(item,member.getMemberId(),
                            CouponDetail.build(
                                item.getSiteId(), String.valueOf(couponActivity.getId()),
                                item.getRuleId(),
                                couponRuleService.getCouponDownDetailNum(item.getRuleId(),item.getSiteId()),
                                member.getMemberId(), null));
                        if(couponDetail!=null)
                            couponDetailMapper.insertCouponDetail(couponDetail);
                       // couponActivityService.updateCouponCommon(item.getSiteId(), item.getRuleId(), couponActivity.getId(),
                                                                 //0, null, null);
                    }
                }

                couponActivityService.checkStatus(couponActivity.getId(), couponActivity.getSiteId());
            }
        }
    }


    private void startSend(CouponRule item, CouponActivity couponActivity) {
        // 先判断一次优惠券数量是否还有，没有就不会去获取会员
        if (couponSendService.syncCouponAmount(item.getRuleId(), item.getSiteId())) {
            List<Member> members = memberMapper.findAllMember(item.getSiteId());
            for (Member member : members) {
                if (couponSendService.syncCouponAmount(item.getRuleId(), item.getSiteId())) {
                    CouponDetail couponDetail = couponActivityProcessService.findDistanceResult(item,member.getMemberId(),CouponDetail.build(
                            item.getSiteId(), String.valueOf(couponActivity.getId()),
                            item.getRuleId(), couponRuleService.getCouponDownDetailNum(item.getRuleId(),item.getSiteId()),
                            member.getMemberId(), null));
                    if(couponDetail!=null)
                        couponDetailMapper.insertCouponDetail(couponDetail);
                    //couponActivityService.updateCouponCommon(item.getSiteId(), item.getRuleId(), couponActivity.getId(),
                                                    //         0, null, null);
                }
            }

            couponActivityService.checkStatus(couponActivity.getId(), couponActivity.getSiteId());
        }
    }

    /**
     * 自动发放优惠券会员日
     * 暂时未用到，
     */
    public void sendMembersDayCoupon() {
        LocalDate localDate = LocalDate.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        java.util.Date date = Date.from(instant);

        List<CouponActivity> activityByCouponRule = couponActivityMapper.findActivityFixedSend(date);
        for (CouponActivity couponActivity : activityByCouponRule) {
            List<CouponRule> couponRuleList = couponRuleMapper.findCouponRuleByActive(couponActivity.getSiteId(),
                                                                                      couponActivity.getId());
            for (CouponRule couponRule : couponRuleList) {
                TimeRule timeRule = null;
                try {
                    timeRule = JSON.parseObject(couponRule.getTimeRule(), TimeRule.class);
                } catch (Exception e) {
                    logger.error("timeRule转换异常");
                    continue;
                }
                Calendar now = Calendar.getInstance();
                now.add(Calendar.HOUR_OF_DAY, 72);
                if (timeRule.getValidity_type() == 3) {
                    String[] ids = timeRule.getAssign_rule().split(",");
                    switch (convertInt(timeRule.getAssign_type())) {
                        case 1:// 此处判断按月份日期
                            int day = now.get(Calendar.DAY_OF_MONTH);
                            startSend(ids, day, couponRule, couponActivity);
                            break;
                        case 2:// 此处判断按星期几
                            int dayForWeek;
                            if (now.get(Calendar.DAY_OF_WEEK) == 1) {
                                dayForWeek = 7;
                            } else {
                                dayForWeek = now.get(Calendar.DAY_OF_WEEK) - 1;
                            }
                            startSend(ids, dayForWeek, couponRule, couponActivity);
                            break;
                        default:
                            startSend(couponRule, couponActivity);
                            break;
                    }
                } else {
                    startSend(couponRule, couponActivity);
                }


            }


        }
    }

    private Integer convertInt(Integer str) {
        if (str == null) {
            return 0;
        } else {
            return str;
        }
    }

}
