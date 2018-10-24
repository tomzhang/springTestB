package com.jk51.modules.integral.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by guosheng on 2017/6/2.
 */
@Mapper
public interface OffIntegralLogMapper {

    int insertSelect(@Param("siteId") Integer siteId, @Param("mobile") String mobile, @Param("tradesId") String tradesId,
                     @Param("total_consum_integral") String total_consum_integral, @Param("offline_consum_integral") String offline_consum_integral,
                     @Param("online_consum_integral") String online_consum_integral, @Param("offline_total_integral") String offline_total_integral,
                     @Param("online_total_integral") String online_total_integral, @Param("create_time") String create_time, @Param("consum_desc") String consum_desc);

    List<Map<String, Object>> select(@Param("siteId") Integer siteId, @Param("mobile") String mobile);

    Map<String, Object> selectTradeBytradeId(@Param("siteId") Integer siteId, @Param("tradesId") Long tradesId);

    int insertOffIntegral( Map<String,Object> paramLogs);

}
