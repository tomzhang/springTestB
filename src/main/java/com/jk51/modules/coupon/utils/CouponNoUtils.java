package com.jk51.modules.coupon.utils;

import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.promotions.PromotionsDetail;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.promotions.mapper.PromotionsDetailMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * filename :com.jk51.modules.coupon.utils.
 * author   :zw
 * date     :2017/8/28
 * Update   :
 */

@Service
public class CouponNoUtils {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private PromotionsDetailMapper promotionsDetailMapper;

    private static final String GET_COUPON_NO = "getCouponNo";
    private static final String GET_ACTIVITY_NO = "getActivityNo";

    /**
     * 优惠券编码(用户领取以后每张优惠券对应的编码)
     *
     * @param ruleId
     * @param siteId
     * @param isCoupon 0优惠券编码，1活动编码
     * @return
     */
    public String getCouponDownDetailNum(Integer ruleId, Integer siteId, Integer isCoupon) {
        Integer integer = new Integer(0);
        synchronized (integer) {
            HashOperations<String, String, String> ops = stringRedisTemplate.opsForHash();

            String couponNo = null;

            String promotionsNo = null;

            Long num = null;

            String theNum = null;

            CouponDetail detail = null;

            PromotionsDetail promotionsDetail = null;

            if (isCoupon == 1) {
                theNum = ops.get(GET_ACTIVITY_NO, siteId.toString() + ruleId.toString());
                promotionsDetail = promotionsDetailMapper.getMAxPromotionsDetail(ruleId, siteId);
                if (StringUtils.isEmpty(theNum)) {
                    if (promotionsDetail != null && null != promotionsDetail.getPromotionsNo()) {
                        promotionsNo = promotionsDetail.getPromotionsNo();
                        if (StringUtils.equalsIgnoreCase(promotionsNo.substring(0, ruleId.toString().length()), ruleId.toString())) {
                            num = Long.parseLong(promotionsNo.substring(ruleId.toString().length(), promotionsNo.length()));
                            ops.put(GET_ACTIVITY_NO, siteId.toString() + ruleId.toString(), num.toString());
                        } else {
                            ops.put(GET_ACTIVITY_NO, siteId.toString() + ruleId.toString(), "0");
                        }
                    } else {
                        ops.put(GET_ACTIVITY_NO, siteId.toString() + ruleId.toString(), "0");
                    }
                }
                num = ops.increment(GET_ACTIVITY_NO, siteId.toString() + ruleId.toString(), 1);
            } else {
                theNum = ops.get(GET_COUPON_NO, siteId.toString() + ruleId.toString());
                detail = couponDetailMapper.getMAxCouponDetail(ruleId, siteId);
                if (StringUtils.isEmpty(theNum)) {
                    if (detail != null && null != detail.getCouponNo()) {
                        couponNo = detail.getCouponNo();
                        if (StringUtils.equalsIgnoreCase(couponNo.substring(0, ruleId.toString().length()), ruleId.toString())) {
                            num = Long.parseLong(couponNo.substring(ruleId.toString().length(), couponNo.length()));
                            ops.put(GET_COUPON_NO, siteId.toString() + ruleId.toString(), num.toString());
                        } else {
                            ops.put(GET_COUPON_NO, siteId.toString() + ruleId.toString(), "0");
                        }
                    } else {
                        ops.put(GET_COUPON_NO, siteId.toString() + ruleId.toString(), "0");
                    }
                }
                num = ops.increment(GET_COUPON_NO, siteId.toString() + ruleId.toString(), 1);
            }
            return ruleId.toString() + String.format("%08d", num);
        }
    }
}
