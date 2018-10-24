package com.jk51.modules.concession.service;

import com.jk51.model.concession.ConcessionDesc2;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.rule.CouponRuleSqlParam;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.activity.PromotionsActivitySqlParam;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponFilterService;
import com.jk51.modules.coupon.service.ParsingCouponRuleService;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import org.apache.commons.collections4.map.SingletonMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ztq on 2018/2/3
 * Description:
 */
@Service
public class ConcessionQueryService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CouponFilterService couponFilterService;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;
    @Autowired
    private PromotionsFilterService promotionsFilterService;
    @Autowired
    private PromotionsRuleService promotionsRuleService;


    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private PromotionsActivityMapper promotionsActivityMapper;


    /**
     * 根据商品id查询优惠信息
     *
     * @param siteId
     * @param goodsId
     * @param status  1表示全部 2表示券可以使用/活动可以参加 3表示券不能使用/活动不能参加
     * @return
     */
    public List<ConcessionDesc2> findConcessionRuleByGoodsId(@Nonnull Integer siteId,
                                                             @Nonnull Integer goodsId,
                                                             @Nullable Integer status) {

        if (status == null)
            status = 2;

        SingletonMap<List<CouponRule>, List<PromotionsActivity>> map = getConcessionByStatus(siteId, status);
        List<CouponRule> couponRuleList = map.getKey();
        List<PromotionsActivity> promotionsRuleList = map.getValue();

        List<ConcessionDesc2> result = couponRuleList.stream()
            .filter(couponRule -> couponFilterService.isAddCoupon4Easy2See(couponRule, goodsId.toString()) != null)
            .map(couponRule -> {
                ConcessionDesc2 desc = new ConcessionDesc2();
                desc.setConcessionType(11);
                desc.setId(couponRule.getRuleId());
                desc.setTitle(couponRule.getRuleName());
                desc.setRuleView(parsingCouponRuleService.accountCoupon(couponRule.getRuleId(), siteId).getRuleDetail());

                return desc;
            }).collect(Collectors.toList());

        List<ConcessionDesc2> result_1 = promotionsRuleList.stream()
            .filter(promotionsActivity -> promotionsActivity.getPromotionsRule() != null)
            .filter(promotionsActivity -> promotionsFilterService.isIfExcuseGoods(promotionsActivity.getPromotionsRule(), goodsId.toString()))
            .map(promotionsActivity -> {
                PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
                ConcessionDesc2 desc = new ConcessionDesc2();
                desc.setConcessionType(22);
                desc.setId(promotionsActivity.getId());
                desc.setTitle(promotionsActivity.getTitle());
                desc.setRuleView(promotionsRuleService.promotionsRuleForType(promotionsRule.getPromotionsType(), promotionsRule.getPromotionsRule()).getProruleDetail());

                return desc;
            }).collect(Collectors.toList());

        result.addAll(result_1);

        return result;
    }

    private SingletonMap<List<CouponRule>, List<PromotionsActivity>> getConcessionByStatus(Integer siteId, @Nonnull Integer status) {
        List<Integer> couponStatus = new ArrayList<>();
        List<Integer> promotionsStatus = new ArrayList<>();

        if (status == 1) {
            couponStatus = null;
            promotionsStatus = null;
        } else if (status == 2) {
            couponStatus.addAll(Arrays.asList(0, 2, 3));
            promotionsStatus.add(0);
        } else {
            couponStatus.addAll(Arrays.asList(1, 4, 10));
            promotionsStatus.addAll(Arrays.asList(2, 3, 4, 10, 11));
        }

        CouponRuleSqlParam param = new CouponRuleSqlParam();
        param.setSiteId(siteId);
        param.setStatusList(couponStatus);
        List<CouponRule> couponRuleList = couponRuleMapper.findByParam(param);

        PromotionsActivitySqlParam param1 = new PromotionsActivitySqlParam();
        param1.setSiteId(siteId);
        param1.setStatusList(promotionsStatus);
        List<PromotionsActivity> promotionsRuleList = promotionsActivityMapper.findByParamWithRuleIn(param1);

        SingletonMap<List<CouponRule>, List<PromotionsActivity>> result = new SingletonMap<>(couponRuleList, promotionsRuleList);

        return result;
    }
}
