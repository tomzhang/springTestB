package com.jk51.modules.coupon.mapper;

import com.jk51.model.coupon.CouponClerk;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.coupon.mapper.
 * author   :zw
 * date     :2017/3/26
 * Update   :
 */
@Mapper
public interface CouponClerkMapper {

    int addCouponClerk(CouponClerk couponClerk);

    int addCouponClerkList(List<CouponClerk> couponClerkList);

    List<Map<String,Object>> findManagerCoupon(Integer siteId,String managerId);

    Map<String,Object> findClerkIsExist(Integer siteId,String mobile);

    List<CouponClerk> getByActivityIdAndSiteId(Integer siteId, Integer activityId);

    List<Map<String,Object>> getClerkUsableCoupons(Integer siteId,String managerId);
}
