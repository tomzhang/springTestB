package com.jk51.modules.coupon.mapper;

import com.jk51.model.coupon.BCouponDetailExtraLog;

import java.util.List;
import java.util.Map;

public interface BCouponDetailExtraLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BCouponDetailExtraLog record);

    int insertSelective(BCouponDetailExtraLog record);

    BCouponDetailExtraLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BCouponDetailExtraLog record);

    int updateByPrimaryKey(BCouponDetailExtraLog record);
    List<Map<String,Object>> findLogsList(Map<String,Object> param);
}
