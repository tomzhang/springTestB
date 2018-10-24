package com.jk51.modules.coupon.mapper;

import com.jk51.model.coupon.Coupon;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.coupon.mapper.
 * author   :zw
 * date     :2017/4/19
 * Update   :
 */
@Mapper
public interface CouponMapper {
   List<Coupon> getOldCouponBySiteId(@Param("siteId") Integer siteId);

   List<Coupon> getOldCouponBySiteIdAndCouponId(@Param("siteId") Integer siteId,@Param("couponId") Integer couponId);

   Map getCateGoodsIds(@Param("cateId") Long cateId,@Param("siteId") Integer siteId);

   boolean insert(CouponRule couponRule);

   List<UserCoupon> getUserCoupon(@Param("siteId")int siteId, @Param("couponId")Integer couponId,
                                  @Param("pageNum") int pageNum ,@Param(value = "pageNo") int pageNo);

   int getUserCouponCount(@Param("siteId")int siteId, @Param("couponId")Integer couponId);

   boolean insertDetails(List<CouponDetail> couponDetails);

   Map getMember(@Param(value = "siteId") Integer siteId,@Param(value = "buyerId") Integer buyerId);

   Map getCouponRule(@Param(value = "siteId") Integer siteId,@Param(value = "oldCouponId") Integer oldCouponId);
}
