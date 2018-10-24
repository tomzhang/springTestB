package com.jk51.modules.coupon.mapper;

import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.rule.CouponRuleSqlParam;
import com.jk51.model.goods.PageData;
import com.jk51.modules.coupon.request.CouponGoods;
import com.jk51.modules.promotions.request.ProCouponRuleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-02
 * 修改记录:
 */
@Mapper
public interface CouponRuleMapper {

    List<CouponRule> findByParam(CouponRuleSqlParam param);

    List<CouponRule> getByUserId(@Param(value = "userId") Integer userId, @Param(value = "siteId") Integer siteId);

    CouponRule getByCouponId(@Param(value = "couponId") Integer couponId, @Param(value = "siteId") Integer siteId);

    CouponRule getCouponRuleByCouponNo(@Param(value = "siteId") Integer siteId, @Param(value = "couponNo") String couponNo);

    CouponRule findCouponRuleById(Integer ruleId, Integer siteId);

    int updateOneFiled(@Param("siteId") Integer siteId, @Param("id") Integer id, @Param("field") String field, @Param("value") Object value);

    /**
     * 通过ruleId和siteId查找正在发放中的活动
     *
     * @param ruleId
     * @param siteId
     * @return
     */
    CouponRule findCouponRuleInActionById(@Param("ruleId") Integer ruleId, @Param("siteId") Integer siteId);
    List<CouponRule> findCouponRuleIds(@Param("ruleIds")List<String> ruleIds, @Param("siteId") Integer siteId);
    CouponRule findMemberNumById(Integer ruleId, Integer siteId, String activeId);

    CouponRule findMemberNumByIdforExport(Integer ruleId, Integer siteId);

    List<CouponRule> findCouponRuleByActive(Integer siteId, Integer acitveId);

    List<CouponRule> findCouponRuleByActiveId(Integer siteId, Integer acitveId);

    int countCouponRuleByActive(Integer siteId, Integer acitveId);

    int addCouponRule(@Param(value = "couponRule") CouponRule couponRule);

    int addCouponRuleAndGetId(CouponRule couponRule);

    int updateCouponRuleById(@Param(value = "couponRule") CouponRule couponRule);

    void updateAmountByRuleId(CouponRule couponRule);

    List<CouponRule> findCouponRuleBySiteId(Integer siteId);

    List<Map<String,Object>> findCouponRuleBySiteIdTool(Integer siteId);

    int updateCouponRule(@Param(value = "couponRule") CouponRule couponRule);

    List<CouponRule> queryCouponRule(Map<String, Object> param);

    void updateCouponRulesById(CouponRule couponRule);

    int revampCouponRuleStatus(@Param(value = "ruleId") Integer ruleId, @Param(value = "siteId") Integer siteId,
                               @Param(value = "status") Integer status);

    List<Map<String, Object>> findReleaseCouponRules(@Param("siteId") int siteId, @Param("fields") String[] fields);

    List<CouponRule> findCouponRules();

    List<CouponRule> selectTimeRuleByValidityType();

    List<CouponRule> queryStatusByTimeRuleForSpikeTicket();

    void updateStatusByTime(Integer siteId, Integer rule_id, Integer status);

    CouponRule queryByCouponId(@Param("siteId") Integer siteId, @Param("ruleId") Integer ruleId);

    int updateUseAmount(@Param(value = "siteId") Integer siteId, @Param(value = "ruleId") Integer ruleId);

    int updateUseAmountByRuleId(@Param(value = "siteId") Integer siteId, @Param(value = "ruleId") Integer ruleId);

    List<PageData> queryCouponGoodsForCouponRule(CouponGoods goods);

    Integer getSendCouponDetailCount(@Param("site_id") Integer siteId, @Param("rule_id") Integer ruleId);

    List<Map<String, Object>> getSendCouponDetailList(@Param("site_id") Integer siteId, @Param("rule_id") Integer ruleId);

    List<CouponRule> findUserCanUseCoupon(@Param("siteId") Integer siteId, @Param("userId") Integer userId);

    List<Map<String, Object>> findCouponActivity(ProCouponRuleDto proCouponRuleDto);
}
