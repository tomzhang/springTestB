package com.jk51.modules.coupon.mapper;

import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponRuleActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-06
 * 修改记录:
 */
@Mapper
public interface CouponRuleActivityMapper {

    Integer insert(CouponRuleActivity couponActivity);

    int insertByBatch(List<CouponRuleActivity> activityList);

    int deleteBySiteIdAndActiveId(@Param("siteId") Integer siteId,
                                  @Param("activeId") Integer activeId);


    int deleteBySiteIdAndActiveIdAndRuleId(@Param("siteId") Integer siteId,
                                  @Param("activeId") Integer activeId,@Param("ruleId") String ruleId);

    List<CouponRuleActivity> getRuleByActive(@Param("siteId")Integer siteId, @Param("activeId")Integer activeId);

    List<CouponRuleActivity> getRuleByActiveWithRule(@Param("siteId")Integer siteId, @Param("activeId")Integer activeId);

    Integer getRuleByActiveCount(@Param("siteId")Integer siteId, @Param("activeId")Integer activeId);

    List<Map<String, Object>> findCouponActivityAndRule(Integer siteId, Integer activeId);

    List<CouponActivity> selectStatusByCoupon(@Param("status") Integer status);

    List<CouponRuleActivity> selectActiveIdByRuleId(@Param("rule_id") Integer ruleId);

    List<Integer> selectActiveIdByRuleIdAndSiteId(@Param("siteId") Integer siteId, @Param("ruleId") Integer ruleId);

    List<CouponRuleActivity> selectActiveId(@Param("active_id") Integer activeId);

    int updateCouponCommon(CouponRuleActivity couponActivity);


}
