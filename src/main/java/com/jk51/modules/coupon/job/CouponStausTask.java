package com.jk51.modules.coupon.job;

import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponRule;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponActivityService;
import com.jk51.modules.coupon.service.CouponRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者:xiapeng
 * 创建日期:2017/4/1
 */
@Component
public class CouponStausTask {
    @Autowired
    private CouponActivityMapper couponActivityMapper;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private CouponActivityService couponActivityService;
    @Autowired
    private CouponRuleService couponRuleService;

    private static final Logger logger = LoggerFactory.getLogger(CouponStausTask.class);

    private int siteId;

    public int getSiteId () {
        return siteId;
    }

    public void setSiteId (int siteId) {
        this.siteId = siteId;
    }

    /**
     * 定时任务修改活动状态， 可以被修改的活动状态包括发布中，定时发布和已发完结束
     */
    public void updateStatus () {
        couponActivityMapper.selectStatus()
                .stream()
                .forEach(this::updateByStatus);
    }

    @Async
    public void updateByStatus(CouponActivity couponActivity){
        couponActivityService.checkStatus(couponActivity.getId(),couponActivity.getSiteId());
    }

    /**
     * 活动状态发布中（未开始）-》发布中（开始）
     */
    public void updateActivityStatus () {
        couponActivityMapper.selectActivityStatus()
                .stream()
                .forEach(this::updateByActivityStatus);
    }

    @Async
    public void updateByActivityStatus(CouponActivity couponActivity){
        Timestamp startTime = couponActivity.getStartTime();
        Timestamp endTime = couponActivity.getEndTime();
        Date now = new Date();
        if (startTime != null && endTime != null && now.before(endTime) && now.after(startTime)) { //活动开始
            //修改活动状态
            couponActivityMapper.updateStatusByTime(couponActivity.getSiteId(), couponActivity.getId(), 0);
            CouponActivity couponActivity1 = couponActivityMapper.getCouponActivity(couponActivity.getSiteId(),
                couponActivity.getId());
            if (couponActivity1.getStatus() == 0 && couponActivity1.getSendType() == 2 && (couponActivity1.getSendWay() == 1 ||
                couponActivity1.getSendWay() == 5)) {
                couponActivityService.sendCoupon(couponActivity1);
            }
        }
    }


    /**
     * 定时任务修改优惠券状态
     */
    public void updateStatusByTimeRule () {
        couponRuleMapper.selectTimeRuleByValidityType()
                .parallelStream()
                .forEach(this::updateStatusByCouponRule);
    }

    public void updateStatusByTimeRuleForSpikeTicket () {
        logger.info("修改秒杀券过期。。。。。");
        couponRuleMapper.queryStatusByTimeRuleForSpikeTicket()
                .stream()
                .forEach(this::updateStatusByCouponRule);
    }

    @Async
    public void updateStatusByCouponRule(CouponRule couponRule){
        couponRuleService.checkStatus(couponRule.getSiteId(),
            couponRule.getRuleId(),
            couponRule.getTimeRule(),
            couponRule.getAmount(),
            couponRule.getStatus());
    }

}
