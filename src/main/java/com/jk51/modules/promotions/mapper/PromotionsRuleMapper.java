package com.jk51.modules.promotions.mapper;

import com.jk51.model.goods.PageData;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.PromotionsRuleSqlParam;
import com.jk51.modules.promotions.request.ProCouponRuleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
@Mapper
public interface PromotionsRuleMapper {
    /* -- insert start -- */

    int create(@Param(value = "promotionsRule") PromotionsRule rule);

    int createAndGetId(PromotionsRule rule);

    /* -- insert end -- */

    /* -- update start -- */
    int updateStatusByIdAndSiteId(@Param("id") Integer id, @Param("siteId") Integer siteId, @Param("status") int status);

    int update(@Param(value = "promotionsRule") PromotionsRule rule);

    int updateOneFiled(@Param("siteId") Integer siteId, @Param("id") Integer id, @Param("field") String field, @Param("value") Object value);

    int updateEndTimeBySiteIdAndRuleId(@Param("siteId") Integer siteId, @Param("ruleId") Integer id, @Param("endTime") String time);

    int updateEndTimeBySiteIdAndActId(@Param("siteId") Integer siteId, @Param("act_id") Integer id, @Param("endTime") String time);
    /* -- update end -- */

    /* -- select start -- */
    List<PromotionsRule> findByParam(PromotionsRuleSqlParam param);

    List<PageData> promRuleList(@Param("proCouponRuleDto") ProCouponRuleDto proCouponRuleDto);

    PromotionsRule getPromotionsRule(ProCouponRuleDto proCouponRuleDto);

    PromotionsRule getPromotionsRuleByIdAndSiteId(@Param("siteId") Integer siteId, @Param("id") Integer id);
    PromotionsRule getPromotionsRuleByIdAndSiteIdAndStatus(@Param("siteId") Integer siteId, @Param("id") Integer id);

    PromotionsRule getPromotionsRuleBySiteIdAndActivityId(@Param("siteId") Integer siteId, @Param("activityId") Integer activityId);

    List<PromotionsRule> getPromotionsRuleBySiteIdAndActivityIds(@Param("siteId") Integer siteId,
                                                                 @Param("activityIdList") List<Integer> promotionsActivityIdList);

    List<PromotionsRule> getPromotionsRuleByIdsAndSiteId(@Param("siteId") Integer siteId, @Param("promotionsIdsMap") Map promotionsIdsMap);


    List<PromotionsRule> getPromotionsRuleByIdsAndSiteId2(@Param("siteId") Integer siteId, @Param("promotionsRuleIds") Set<Integer> promotionsRuleIds);

    List<PromotionsRule> getPromotionsRuleBySiteId(@Param("siteId") Integer siteId);

    List<Map<String, Object>> proRuleListForUsable(@Param(value = "siteId") Integer siteId);

    List<PageData> choosePromList(@Param("proCouponRuleDto") ProCouponRuleDto proCouponRuleDto);

    List<PromotionsRule> getPromotionsRuleToJob();

    List<PageData> couponRuleList(@Param("proCouponRuleDto") ProCouponRuleDto proCouponRuleDto);
    /* -- select end -- */
}
