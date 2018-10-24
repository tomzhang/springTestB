package com.jk51.modules.clerkvisit.mapper;


import com.jk51.model.clerkvisit.BVisitStatistics;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface BVisitStatisticsMapper {


    int deleteByPrimaryKey(Integer id);

    int insert(BVisitStatistics record);

    int insertSelective(BVisitStatistics record);

    BVisitStatistics selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BVisitStatistics record);

    int updateByPrimaryKey(BVisitStatistics record);

    int updateSmsnum(@Param("siteId") String siteId,@Param("bvsId") String bvsId);

    int updateRealMemberNum(Map<String,Object> map);
}
