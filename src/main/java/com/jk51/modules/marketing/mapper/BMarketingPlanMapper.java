package com.jk51.modules.marketing.mapper;

import com.jk51.model.BMarketingPlan;
import com.jk51.modules.marketing.request.Winner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface BMarketingPlanMapper {
    int insertSelective(BMarketingPlan record);
    int updateByPrimaryKeySelective(BMarketingPlan record);
    List<BMarketingPlan> getExistPlanList(@Param("siteId") Integer siteId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("planId") Integer planId);
    BMarketingPlan selectById(@Param("siteId") Integer siteId, @Param("id") Integer id);
    List<BMarketingPlan> getPlanList(@Param("bMarketingPlan") BMarketingPlan bMarketingPlan, @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);
    int deleteById(@Param("siteId") Integer siteId, @Param("id") Integer id);
    int startOrStop(@Param("siteId") Integer siteId, @Param("id") Integer id, @Param("stop") Boolean stop);
    int updateTypeById(@Param("siteId") Integer siteId, @Param("id") Integer id, @Param("type") Integer type, @Param("style") Integer style);
    Integer getWinnersSum(@Param("siteId") Integer siteId, @Param("planId") Integer planId);
    int updateStatusById(@Param("siteId") Integer siteId, @Param("planId") Integer planId, @Param("status") int status);
    List<Winner> getWinnersByPlanId(@Param("siteId") Integer siteId, @Param("planId") Integer planId);
    int updateCouponActivityId(@Param("siteId") Integer siteId, @Param("planId") Integer planId, @Param("couponActivityId") Integer couponActivityId);
    BMarketingPlan selectAllById(@Param("siteId") Integer siteId, @Param("id") Integer id);
}
