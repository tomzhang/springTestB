package com.jk51.modules.coupon.mapper;

import com.jk51.model.coupon.requestParams.ReissureActivity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;
import java.util.List;

/**
 * Created by Administrator on 2017/7/31.
 */
@Mapper
public interface CouponActivityReissureMapper {

    int addCouponActivityReissure(ReissureActivity reissureActivity);

    int updateCouponActivityReissureForSuccessNum(ReissureActivity reissureActivity);

    List<Map<String,Object>> queryAllReissureActivityList(Integer siteId,Integer activeId);
}
