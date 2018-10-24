package com.jk51.modules.marketing.mapper;

import com.jk51.model.BMarketingPlanExt;
import com.jk51.modules.marketing.request.PlanExtListParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BMarketingPlanExtMapper {
    int insertSelective(BMarketingPlanExt record);
    int updateByPrimaryKeySelective(BMarketingPlanExt record);
    int insertByList(@Param("list") List<PlanExtListParam.MarketingPlanExt> list, @Param("siteId") Integer siteId, @Param("planId") Integer planId);
    int deleteById(@Param("siteId") Integer siteId, @Param("id") Integer id);
    int deleteByPlanId(@Param("siteId") Integer siteId, @Param("planId") Integer planId);
    int updateReceiveByPrizesId(@Param("prizesId") Integer prizesId, @Param("siteId") Integer siteId, @Param("receiveNum") Integer receiveNum);
    int updateByList(@Param("list") List<PlanExtListParam.MarketingPlanExt> list, @Param("siteId") Integer siteId, @Param("planId") Integer planId);
}
