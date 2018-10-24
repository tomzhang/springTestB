package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SBAppLogs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-03-30
 * 修改记录:
 */
@Mapper
public interface BAppLogsMapper {

    int insertSelective(SBAppLogs record);

    List<Map> selectStoreLog(@Param("site_id") Integer site_id, @Param("store_id") Integer store_id, @Param("operator") String operator,
                             @Param("action") String action,@Param("startTime") String startTime,@Param("endTime") String endTime,
                             @Param("num") Integer pageNum, @Param("size") Integer pageSize,@Param("content") String content);

    Integer selectStoreLogCount(@Param("site_id") Integer siteId, @Param("store_id") Integer store_id, @Param("operator") String operatorName,
                                @Param("action") String action,@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("content") String content);

    List<Map> selectMerchantLog(@Param("site_id") Integer siteId, @Param("operator") String operatorName,
                                @Param("action") String action,@Param("startTime") String startTime,@Param("endTime") String endTime,
                                @Param("num") Integer pageNum, @Param("size") Integer pageSize,@Param("content") String content);

    Integer selectMerchantCount(@Param("site_id") Integer siteId, @Param("operator") String operatorName,
                                @Param("action") String action,@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("content") String content);
}
