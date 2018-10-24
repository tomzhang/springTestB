package com.jk51.modules.coupon.mapper;

import com.jk51.model.coupon.CouponLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/5/24.
 */
@Mapper
public interface CouponLogMapper {

    public void insertLog(CouponLog couponLog);

    public void updateLog(CouponLog couponLog);

    public CouponLog queryLog(CouponLog couponLog);
}
