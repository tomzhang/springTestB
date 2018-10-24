package com.jk51.modules.offline.mapper;

import com.jk51.modules.offline.utils.FaultStaticsUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-08-03
 * 修改记录:
 */
@Mapper
public interface BFaultStatisticsMapper {

    int insertStatics(FaultStaticsUtils faultStaticsUtils);

    int updateStaticsStatus(@Param("id") Integer id, @Param("siteId") Integer siteId, @Param("faultType") Integer faultType,
                            @Param("faultDetails") String faultDetails, @Param("isPush") Integer isPush);

    List<Map<String, Object>> findFaultStatics(@Param("siteId") Integer siteId, @Param("merchantName") String merchantName,
                                               @Param("type") Integer type, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<Map<String, Object>> selectFaultInfoById(@Param("faultIds") List<Integer> faultIds);

    Integer findByPushInfo(@Param("siteId") Integer siteId, @Param("type") Integer type, @Param("pushInfo") String pushInfo);
}
