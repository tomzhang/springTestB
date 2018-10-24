package com.jk51.modules.coupon.mapper;

import com.jk51.model.coupon.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * filename :com.jk51.modules.coupon.mapper.
 * author   :zw
 * date     :2017/4/19
 * Update   :
 */
@Mapper
public interface UserCouponMapper {

    List<UserCoupon> getOldUserCouponBySiteIdAndCouponId(@Param(value = "siteId") Integer siteId,
                                                         @Param(value = "couponId") Integer couponId);
}
