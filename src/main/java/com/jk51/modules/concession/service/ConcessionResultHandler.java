package com.jk51.modules.concession.service;

import com.alibaba.fastjson.JSON;
import com.jk51.exception.ParamErrorException;
import com.jk51.exception.UnknownTypeException;
import com.jk51.model.concession.ConcessionDesc;
import com.jk51.model.concession.result.GiftResult;
import com.jk51.model.concession.result.GoodsDataForResult;
import com.jk51.model.concession.result.PromotionsResult;
import com.jk51.model.concession.result.Result;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponDetailConcessionLog;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.CouponView;
import com.jk51.model.order.Orders;
import com.jk51.model.order.Trades;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsDetail;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.activity.PromotionsActivitySqlParam;
import com.jk51.model.promotions.rule.ProCouponRuleView;
import com.jk51.modules.coupon.mapper.CouponDetailConcessionLogMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponDetailService;
import com.jk51.modules.coupon.service.CouponRuleService;
import com.jk51.modules.coupon.service.ParsingCouponRuleService;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.service.PromotionsDetailService;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.jk51.commons.java8datetime.Transform.uDateToLocalDateTime;
import static com.jk51.modules.concession.constants.ConcessionConstant.*;
import static com.jk51.modules.promotions.constants.PromotionsConstant.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

/**
 * Created by ztq on 2018/1/19
 * Description:
 */
@Component
public class ConcessionResultHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CouponRuleService couponRuleService;
    @Autowired
    private CouponDetailService couponDetailService;
    @Autowired
    private PromotionsRuleService promotionsRuleService;
    @Autowired
    private PromotionsDetailService promotionsDetailService;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;

    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private PromotionsRuleMapper promotionsRuleMapper;
    @Autowired
    private PromotionsActivityMapper promotionsActivityMapper;
    @Autowired
    private PromotionsFilterService promotionsFilterService;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private CouponDetailConcessionLogMapper couponDetailConcessionLogMapper;

    /**
     * 把活动计算结果按照活动Id来收集信息
     * ps：活动计算出的结果是按照商品id为key来收集数据的
     *
     * @param concessionResult
     * @param giftGoods
     * @return
     */
    public Map<Integer, Integer> gatherDiscountResultByPromotionsId(@Nullable Result concessionResult, List<GiftResult> giftGoods) {
        Map<Integer, Integer> resultMap = new HashMap<>();
        if (concessionResult == null || !concessionResult.isUsePromotions())
            return resultMap;

        Map<Integer, GoodsDataForResult> goodsConcession = concessionResult.getGoodsConcession();
        if (goodsConcession.size() == 0)
            throw new ParamErrorException("数据异常");

        // 预下单计算免邮只会被计算一次
        Integer freePostagePromotionsId = null;
        for (Map.Entry<Integer, GoodsDataForResult> i : goodsConcession.entrySet()) {
            GoodsDataForResult v = i.getValue();

            for (Map.Entry<Integer, PromotionsResult> j : v.getPromotionsRemark().entrySet()) {
                Integer promotionsRuleType = j.getKey();
                PromotionsResult promotionsResult = j.getValue();

                switch (promotionsRuleType) {
                    case PROMOTIONS_RULE_TYPE_DISCOUNT:
                    case PROMOTIONS_RULE_TYPE_MONEY_OFF:
                    case PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                    case PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                        Integer discount = Optional.ofNullable(resultMap.get(promotionsResult.getPromotionsActivityId())).orElse(0);
                        int promotionsDiscount = com.jk51.commons.base.Preconditions.checkNotZero(checkNotNull(promotionsResult.getDiscount()));
                        resultMap.put(promotionsResult.getPromotionsActivityId(), discount + promotionsDiscount);

                        break;

                    case PROMOTIONS_RULE_TYPE_FREE_POST:
                        if (freePostagePromotionsId == null)
                            freePostagePromotionsId = promotionsResult.getPromotionsActivityId();
                        else if (!freePostagePromotionsId.equals(promotionsResult.getPromotionsActivityId()))
                            throw new RuntimeException("预下单计算免邮活动只会计算一次");

                        break;

                    case PROMOTIONS_RULE_TYPE_GIFT:
                        if (giftGoods == null || giftGoods.size() == 0) {
                            // doNothing
                        } else {
                            Map<Integer, GiftResult> giftResultMap = giftGoods.stream()
                                .filter(giftResult -> giftResult.getConcessionDesc().getConcessionType() == 2)
                                .collect(toMap(
                                    giftResult -> checkNotNull(giftResult.getConcessionDesc().getPromotionsActivityId()),
                                    giftResult -> giftResult));


                            if (giftResultMap.get(promotionsResult.getPromotionsActivityId()).getGiftList().size() != 0) {
                                resultMap.put(promotionsResult.getPromotionsActivityId(), 0);
                            }
                        }

                        break;

                    default:
                        throw new UnknownTypeException();
                }
            }
        }

        if (freePostagePromotionsId != null)
            resultMap.put(freePostagePromotionsId, concessionResult.getPostageDiscount());

        return resultMap;
    }

    /**
     * 当订单被取消时或订单发货前退款，且订单有赠送商品时，尝试恢复赠品
     *
     * @param tradesId
     * @param siteId
     * @return true 成功恢复，false 恢复失败
     */
    public boolean tryRestoreInventoryByTradesId(@Nonnull Long tradesId, @Nonnull Integer siteId) {
        try {
            List<Orders> ordersList = ordersMapper.getOrdersByTradesId(tradesId).stream()
                .filter(orders -> orders.getGoodsGifts() == 1)
                .collect(Collectors.toList());

            if (ordersList.size() == 0)
                return true;

            if (StringUtils.isBlank(ordersList.get(0).getConcessionDesc())) { // 超级优惠前创建的订单不提供恢复库存功能
                return false;
            } else {
                return tryRestoreInventoryForNewDataType(siteId, ordersList);
            }
        } catch (Exception e) {
            logger.error("发生异常，{}", e);
            return false;
        }
    }

    public void setRuleViewForGiftResult(@Nonnull Integer siteId, @Nonnull List<GiftResult> giftResults) {
        if (giftResults.size() == 0)
            return;

        for (GiftResult giftResult : giftResults) {
            setRuleViewForGiftResult(siteId, giftResult);
        }
    }

    public void setRuleViewForGiftResult(@Nonnull Integer siteId, @Nonnull GiftResult giftResult) {
        ConcessionDesc concessionDesc = giftResult.getConcessionDesc();
        String ruleView = getRuleViewByConcessionDesc(siteId, concessionDesc);

        concessionDesc.setRuleView(ruleView);
    }

    /**
     * 通过构建concessionDesc获取相应的规则快照(ruleView)
     *
     * @param siteId
     * @param concessionDesc
     * @return
     */
    public String getRuleViewByConcessionDesc(@Nonnull Integer siteId, @Nonnull ConcessionDesc concessionDesc) {
        String ruleView;
        if (concessionDesc.getConcessionType().equals(COUPON)) {
            Integer couponDetailId = checkNotNull(concessionDesc.getCouponDetailId());
            CouponRule couponRule = couponRuleMapper.getByCouponId(couponDetailId, siteId);
            CouponView couponView = parsingCouponRuleService.accountCoupon(couponRule.getRuleId(), siteId);
            ruleView = couponView.getRuleDetail();

        } else if (concessionDesc.getConcessionType().equals(PROMOTIONS)) {
            PromotionsActivitySqlParam param = new PromotionsActivitySqlParam();
            param.setPromotionsActivityId(concessionDesc.getPromotionsActivityId());
            param.setSiteId(siteId);
            List<PromotionsActivity> promotionsActivityList = promotionsActivityMapper.findByParamWithRuleIn(param);

            if (promotionsActivityList != null && promotionsActivityList.size() == 1) {
                PromotionsActivity promotionsActivity = promotionsActivityList.get(0);
                PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
                ProCouponRuleView promotionsRuleView = promotionsRuleService.promotionsRuleForType(promotionsRule.getPromotionsType(), promotionsRule.getPromotionsRule());
                ruleView = promotionsRuleView.getProruleDetail();

            } else {
                throw new RuntimeException();
            }
        } else {
            throw new UnknownTypeException();
        }

        return ruleView;
    }


    /**
     * 获取优惠信息集合
     *
     * @param trades
     * @return 返回的map字段有
     * tradesId,
     * concessionType (表示优惠是活动还是优惠券),
     * concessionNo (couponNo or promotionsNo),
     * isGift (是否是赠品，1表示是赠品，2表示非赠品),
     * concessionView 规则视图,
     * concessionResult 优惠结果，如果为数字表示优惠金额，其他为赠品,
     * operateTime 操作时间
     */
    @Nonnull
    public Optional<List<Map<String, String>>> getConcessionResultByTradesId(Trades trades) {
        try {
            Integer siteId = trades.getSiteId();
            Long tradesId = trades.getTradesId();
            List<Orders> ordersList = trades.getOrdersList();
            List<Map<String, String>> list = new ArrayList<>();

            if (ordersList == null || ordersList.size() == 0)
                ordersList = ordersMapper.getOrdersByTradesId(tradesId);

            ordersList = ordersList.stream()
                .filter(orders -> Integer.valueOf(1).equals(orders.getGoodsGifts()))
                .collect(Collectors.toList());

            CouponDetailConcessionLog couponDetailConcessionLog = couponDetailConcessionLogMapper.queryByTradesId(siteId, tradesId);
            List<PromotionsDetail> promotionsDetailList = promotionsDetailService.getPromotionsDetails(siteId, tradesId);

            if (couponDetailConcessionLog == null && promotionsDetailList.size() == 0)
                return Optional.of(new ArrayList<>());

            Long count = ordersList.stream()
                .filter(orders -> StringUtils.isNotBlank(orders.getConcessionDesc()))
                .count();

            if (ordersList.size() != 0 && count.intValue() == 0) // 超级优惠以前的数据才会出现这种情况，否则数据出现异常
                return Optional.empty();

            // 集中处理赠品
            if (count.intValue() != 0) {
                Map<Integer, Map<Integer, List<Orders>>> ordersMap = groupOrdersListByConcessionDesc(ordersList);

                for (Map.Entry<Integer, Map<Integer, List<Orders>>> entry : ordersMap.entrySet()) {
                    Map<Integer, List<Orders>> value = entry.getValue();
                    switch (entry.getKey()) {
                        case COUPON:
                            if (value.size() != 1) return Optional.empty();

                            Set<Map.Entry<Integer, List<Orders>>> entries = value.entrySet();

                            if (entries.size() > 1) {
                                throw new RuntimeException("一个订单应该只能使用一张优惠券");
                            } else if (entries.size() == 1) {
                                final CouponDetailConcessionLog finalCouponDetailConcessionLog = checkNotNull(couponDetailConcessionLog);
                                entries.forEach(innerEntry -> {
                                    Map<String, String> map = getGiftCouponViewForTradesDetailPage(tradesId, finalCouponDetailConcessionLog, innerEntry);
                                    list.add(map);
                                });
                            }

                            break;

                        case PROMOTIONS:
                            for (Map.Entry<Integer, List<Orders>> innerEntry : value.entrySet()) {
                                Map<String, String> map = getGiftPromotionsViewForTradesDetailPage(tradesId, promotionsDetailList, innerEntry);
                                list.add(map);
                            }

                            break;

                        default:
                            throw new UnknownTypeException();
                    }
                }


                if (ordersMap.get(COUPON) == null || ordersMap.get(COUPON).get(couponDetailConcessionLog.getCouponDetailId()) == null) {
                    // doNothing
                } else {
                    couponDetailConcessionLog = null;
                }

                promotionsDetailList = promotionsDetailList.stream()
                    .filter(promotionsDetail -> ordersMap.get(PROMOTIONS) == null || ordersMap.get(PROMOTIONS).get(promotionsDetail.getActivityId()) == null)
                    .collect(Collectors.toList());
            }

            if (couponDetailConcessionLog != null) {
                Map<String, String> map = getCouponViewForTradesDetailPage(tradesId, couponDetailConcessionLog);
                list.add(map);
            }

            if (promotionsDetailList.size() != 0) {
                for (PromotionsDetail promotionsDetail : promotionsDetailList) {
                    Map<String, String> map = getPromotionsViewForTradesDetailPage(tradesId, promotionsDetail);
                    list.add(map);
                }
            }
            list.forEach(map->{
                String concessionType = checkNotNull(map.get("concessionType"));
                String concessionNo = checkNotNull(map.get("concessionNo"));
                if(Objects.equals(concessionType,"优惠券")){
                    CouponRule couponRuleByCouponNo = couponRuleMapper.getCouponRuleByCouponNo(siteId, concessionNo);
                    map.put("ruleName",couponRuleByCouponNo.getRuleName());
                }else if (Objects.equals(concessionType,"活动")){
                    PromotionsActivity promotionsActivityByPromotionNo = promotionsActivityMapper.getPromotionsActivityByPromotionNo(siteId, concessionNo);
                    map.put("ruleName",promotionsActivityByPromotionNo.getTitle());
                }
            });

            return Optional.of(list);
        } catch (Exception e) {
            // 如果是超级优惠以前的数据原因出错，不处理
            logger.error("np问题出现有可能是老订单的问题, {}", e);
            return Optional.empty();
        }
    }


    /**
     * 查找该订单号的优惠券优惠金额和活动优惠金额
     *
     * @param siteId
     * @param tradesId
     * @return couponDiscount -> 优惠券优惠金额
     * promotionsDiscount -> 活动优惠金额
     */
    public Optional<Map<String, String>> getConcessionResultByTradesId2(Integer siteId, @Nonnull Long tradesId) {
        Map<String, String> map = new HashMap<>();

        try {
            List<CouponDetail> couponDetailList = couponDetailService.getCouponDetailsByTradesId(siteId, tradesId);
            List<PromotionsDetail> promotionsDetailList = promotionsDetailService.getPromotionsDetails(siteId, tradesId);

            Integer couponDiscount = 0;
            Integer promotionsDiscount = 0;

            if (couponDetailList.size() != 0) {
                couponDiscount = couponDetailList.stream()
                    .mapToInt(CouponDetail::getDiscountAmount)
                    .sum();
            }

            if (promotionsDetailList.size() != 0) {
                promotionsDiscount = promotionsDetailList.stream()
                    .mapToInt(PromotionsDetail::getDiscountAmount)
                    .sum();
            }

            map.put("couponDiscount", couponDiscount.toString());
            map.put("promotionsDiscount", promotionsDiscount.toString());
        } catch (Exception e) {
            logger.error("np问题出现有可能是老订单的问题, {}", e);
        }

        return Optional.of(map);
    }


    /* -- 私有方法领域 -- */

    /**
     * 把promotionsDetail转换成订单详情页面能使用的数据
     *
     * @param tradesId
     * @param promotionsDetail
     * @return
     */
    private Map<String, String> getPromotionsViewForTradesDetailPage(@Nonnull Long tradesId, PromotionsDetail promotionsDetail) {
        Map<String, String> map = new HashMap<>();
        map.put("tradesId", tradesId.toString());
        map.put("concessionType", "活动");
        map.put("isGift", "2");
        map.put("concessionNo", promotionsDetail.getPromotionsNo());
        ConcessionDesc concessionDesc = new ConcessionDesc() {{
            setConcessionType(PROMOTIONS);
            setPromotionsActivityId(checkNotNull(promotionsDetail.getActivityId()));
        }};

        map.put("concessionView", getRuleViewByConcessionDesc(promotionsDetail.getSiteId(), concessionDesc));
        map.put("concessionResult", checkNotNull(promotionsFilterService.save2Digit(promotionsDetail.getDiscountAmount()/100.00d)));
        map.put("operateTime", promotionsDetail.getUpdateTime().format(longFormatter));
        return map;
    }

    private Map<String, String> getGiftPromotionsViewForTradesDetailPage(@Nonnull Long tradesId, List<PromotionsDetail> promotionsDetailList, Map.Entry<Integer, List<Orders>> innerEntry) {
        Map<String, Integer> result = innerEntry.getValue().stream()
            .collect(Collectors.toMap(orders -> orders.getGoodsTitle() + "(" + orders.getGoodsId() + ")", Orders::getGoodsNum));

        PromotionsDetail pd = promotionsDetailList.stream()
            .filter(promotionsDetail -> promotionsDetail.getActivityId().equals(innerEntry.getKey()))
            .findFirst().orElseThrow(RuntimeException::new);

        Map<String, String> map = new HashMap<>();
        map.put("tradesId", tradesId.toString());
        map.put("concessionType", "活动");
        map.put("isGift", "1");
        map.put("concessionNo", pd.getPromotionsNo());
        ConcessionDesc concessionDesc = new ConcessionDesc() {{
            setConcessionType(PROMOTIONS);
            setPromotionsActivityId(checkNotNull(pd.getActivityId()));
        }};

        map.put("concessionView", getRuleViewByConcessionDesc(pd.getSiteId(), concessionDesc));
        map.put("concessionResult", JSON.toJSONString(result));
        map.put("operateTime", pd.getUpdateTime().format(longFormatter));
        return map;
    }


    /**
     * 把couponDetail转换成订单详情页面能使用的数据
     *
     * @param tradesId
     * @param concessionLog
     * @return
     */
    private Map<String, String> getCouponViewForTradesDetailPage(@Nonnull Long tradesId, CouponDetailConcessionLog concessionLog) {
        Map<String, String> map = new HashMap<>();
        map.put("tradesId", tradesId.toString());
        map.put("concessionType", "优惠券");
        map.put("isGift", "2");
        map.put("concessionNo", concessionLog.getCouponNo());
        ConcessionDesc concessionDesc = new ConcessionDesc() {{
            setConcessionType(COUPON);
            setCouponDetailId(checkNotNull(concessionLog.getCouponDetailId()));
        }};


        map.put("concessionView", getRuleViewByConcessionDesc(concessionLog.getSiteId(), concessionDesc));
        map.put("concessionResult", checkNotNull(promotionsFilterService.save2Digit(concessionLog.getDiscountAmount()/100.00d)));
        map.put("operateTime", concessionLog.getCreateTime().format(longFormatter));
        return map;
    }

    private Map<String, String> getGiftCouponViewForTradesDetailPage(@Nonnull Long tradesId, CouponDetailConcessionLog concessionLog, Map.Entry<Integer, List<Orders>> innerEntry) {
        Map<String, Integer> result = innerEntry.getValue().stream()
            .collect(Collectors.toMap(orders -> orders.getGoodsTitle() + "(" + orders.getGoodsId() + ")", Orders::getGoodsNum));

        Map<String, String> map = new HashMap<>();
        map.put("tradesId", tradesId.toString());
        map.put("concessionType", "优惠券");
        map.put("isGift", "1");
        map.put("concessionNo", concessionLog.getCouponNo());
        ConcessionDesc concessionDesc = new ConcessionDesc() {{
            setConcessionType(COUPON);
            setCouponDetailId(checkNotNull(concessionLog.getCouponDetailId()));
        }};

        map.put("concessionView", getRuleViewByConcessionDesc(concessionLog.getSiteId(), concessionDesc));
        map.put("concessionResult", JSON.toJSONString(result));
        map.put("operateTime", concessionLog.getCreateTime().format(longFormatter));
        return map;
    }

    private Map<Integer, Map<Integer, List<Orders>>> groupOrdersListByConcessionDesc(@Nonnull List<Orders> ordersList) {
        return ordersList.stream()
            .filter(orders -> StringUtils.isNotBlank(orders.getConcessionDesc()))
            .collect(groupingBy(orders -> JSON.parseObject(orders.getConcessionDesc(), ConcessionDesc.class).getConcessionType(),
                groupingBy(orders -> {
                    ConcessionDesc concessionDesc = JSON.parseObject(orders.getConcessionDesc(), ConcessionDesc.class);
                    if (concessionDesc.getConcessionType().equals(TYPE_COUPON_DETAIL))
                        return concessionDesc.getCouponDetailId();
                    else if (concessionDesc.getConcessionType().equals(TYPE_PROMOTIONS_ACTIVITY))
                        return concessionDesc.getPromotionsActivityId();
                    else
                        throw new UnsupportedOperationException("暂时不处理该类型");
                })));
    }

    /**
     * 超级优惠以后的赠品b_orders表的字段concession_desc存在表述赠品有优惠券/赠品活动的数据，所以按照该字段去计算
     *
     * @param siteId
     * @param ordersList
     * @return
     */
    private boolean tryRestoreInventoryForNewDataType(@Nonnull Integer siteId,
                                                      @Nonnull List<Orders> ordersList) {
        Boolean flag = ordersList.stream()
            .map(orders -> StringUtils.isNotBlank(orders.getConcessionDesc()))
            .reduce(true, (b1, b2) -> b1 && b2);

        if (!flag)
            return false;

        Map<Integer, Map<Integer, List<Orders>>> map = groupOrdersListByConcessionDesc(ordersList);

        Map<Integer, List<Orders>> couponDescMap = map.get(TYPE_COUPON_DETAIL);
        if (couponDescMap != null && couponDescMap.size() != 0) {
            return tryRestoreInventoryForCoupon(siteId, couponDescMap);
        }

        Map<Integer, List<Orders>> promotionsDescMap = map.get(TYPE_PROMOTIONS_ACTIVITY);
        if (promotionsDescMap != null && promotionsDescMap.size() != 0) {
            return tryRestoreInventoryForPromotions(siteId, promotionsDescMap);
        }

        return false;
    }

    private boolean tryRestoreInventoryForPromotions(@Nonnull Integer siteId, @Nonnull Map<Integer, List<Orders>> promotionsDescMap) {
        for (Map.Entry<Integer, List<Orders>> entry : promotionsDescMap.entrySet()) {
            Integer promotionsActivityId = entry.getKey();

            PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRuleBySiteIdAndActivityId(siteId, promotionsActivityId);

            Map<Integer, Integer> map = entry.getValue().stream()
                .collect(toMap(Orders::getGoodsId, Orders::getGoodsNum));

            promotionsRuleService.changeGiftNums(promotionsRule, map);
        }

        return true;
    }

    private boolean tryRestoreInventoryForCoupon(@Nonnull Integer siteId, @Nonnull Map<Integer, List<Orders>> couponDescMap) {
        if (couponDescMap.size() != 1)
            return false;

        for (Map.Entry<Integer, List<Orders>> entry : couponDescMap.entrySet()) {
            Integer couponDetailId = entry.getKey();
            CouponRule couponRule = couponRuleMapper.getByCouponId(couponDetailId, siteId);

            Map<Integer, Integer> map = entry.getValue().stream()
                .collect(toMap(Orders::getGoodsId, Orders::getGoodsNum));

            couponRuleService.changeGiftNums(couponRule, map);
        }

        return true;
    }
}
