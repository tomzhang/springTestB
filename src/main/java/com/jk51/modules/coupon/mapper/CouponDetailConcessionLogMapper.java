package com.jk51.modules.coupon.mapper;

import com.jk51.model.coupon.CouponDetailConcessionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CouponDetailConcessionLogMapper {

    /* -- insert -- */
    void insertWithoutId(CouponDetailConcessionLog log);

    /* -- query -- */
    CouponDetailConcessionLog queryByTradesId(@Param("siteId") Integer siteId, @Param("tradesId") Long tradesId);
}
