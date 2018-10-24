package com.jk51.modules.merchant.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:统计mapper配置文件
 * 作者: dumingliang
 * 创建日期: 2017-07-14
 * 修改记录:
 */
public interface StaticsRecordMapper {

    int insertRecord(Map map);

    String queryFlowAnalysisRecords(@Param("siteId") Integer siteId,@Param("dates") String dates,@Param("type") String type);

    List<Integer> getSiteId();

    String queryStaticsRecords(@Param("siteId") Integer siteId,@Param("dates") String dates,@Param("type") String type);
}
