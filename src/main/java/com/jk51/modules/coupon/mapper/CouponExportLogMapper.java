package com.jk51.modules.coupon.mapper;

import com.jk51.model.coupon.Coupon;
import com.jk51.model.coupon.CouponExportLog;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

/**
 * Created by Administrator on 2017/5/23.
 */

@Mapper
public interface CouponExportLogMapper {

    List<CouponExportLog> findCouponExportLog(CouponExportLog log);

    int updateCouponExportDetail(CouponExportLog log);

    int addCouponExportDetail(CouponExportLog log);


}
