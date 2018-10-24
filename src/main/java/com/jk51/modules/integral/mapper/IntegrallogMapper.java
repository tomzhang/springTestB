package com.jk51.modules.integral.mapper;

import java.util.List;
import java.util.Map;

import com.jk51.model.integral.IntegralLog;
import org.apache.ibatis.annotations.Param;

public interface IntegrallogMapper {

    Map<String, Object> getIntegralLogByTradesId(Map<String, Object> param);

    IntegralLog getIntegralLog(Map<String, Object> param);

    Map<String, Object> getIntegralByShopping(Map<String, Object> param);

    Map<String, Object> getIntegralSumRefree(Map<String, Object> param);

    Map<String, Object> getIntegralLogg(@Param("siteId") Integer siteId, @Param("type") Integer type, @Param("mark") String mark);

    Map<String, Object> getIntegralLogByMark(Map<String, Object> param);
}