package com.jk51.modules.promotions.service;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.Member;
import com.jk51.model.order.response.UsePromotionsParams;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.rule.*;
import com.jk51.modules.coupon.service.CouponActiveForMemberService;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.goods.service.GoodsService;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.mapper.PromotionsDetailMapper;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.request.OrderDeductionDto;
import com.jk51.modules.promotions.request.ProRuleActivitySplitOrderParam;
import com.jk51.modules.promotions.request.ProRuleMessageParam;
import com.jk51.modules.promotions.utils.OrderDeductionUtils;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mapp on 2017/11/9.
 */
@Service
public class PromotionsRuleSplitOrderService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CouponSendService couponSendService;

    @Autowired
    private PromotionsRuleMapper mapper;

    @Autowired
    private PromotionsRuleService promotionsRuleService;

    @Autowired
    private CouponActiveForMemberService couponActiveForMemberService;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private PromotionsDetailMapper promotionsDetailMapper;

    @Autowired
    private OrderDeductionUtils orderDeductionUtils;

    @Autowired
    private OrderDeductionService orderDeductionService;

    @Autowired
    private PromotionsActivityMapper promotionsActivityMapper;

    @Autowired
    private GoodsService goodsService;


    /**
     * 优惠活动拆单总入口
     *
     * @param proRuleMessageParam
     * @return
     */
    public Map<String, Object> proRuleForSplitOrder (ProRuleMessageParam proRuleMessageParam) {
        List<Map<String, Object>> proRuleForSplitOrderList = null;
        Map<String, Object> splitOrderData = new HashMap<String, Object>();

        try {
            if (proRuleMessageParam == null)
                return null;
            proRuleMessageParam.setFirstOrder(couponSendService.isFirstOrder(proRuleMessageParam.getSiteId(), proRuleMessageParam.getUserId()));

            //查询 列表
            proRuleForSplitOrderList = mapper.proRuleListForUsable(proRuleMessageParam.getSiteId());

            //时间，用户校验
            proRuleForSplitOrderList = proRuleForSplitOrderList.stream()
                    .filter(map -> promotionsRuleService.checkproRuleTimeRule((String) map.get("time_rule")))
                    .filter(map -> couponActiveForMemberService.checkProActivity(proRuleMessageParam.getSiteId(), Integer.parseInt(map.get("proActivityId").toString()), proRuleMessageParam.getUserId()))
                    .collect(Collectors.toList());

            //门店订单类型等的校验
            proRuleForSplitOrderList = proRuleForSplitOrderList.stream().filter(map -> promotionsRuleService.checkproRuleFirstOrderTypeStore(proRuleMessageParam, map)).collect(Collectors.toList());


            //分开别类的单独过滤出优惠活动
            proRuleForSplitOrderList = proRuleForSplitOrderList.stream().filter(map -> promotionsRuleService.checkProRuleForPromotionsRule(proRuleMessageParam, map)).collect(Collectors.toList());

            splitOrderData = toSplitOrderResult(proRuleMessageParam, proRuleForSplitOrderList);
            //拆单过程在对活动类型过滤的过程中算出 最终返回结果数据和之前预下单一致
            return splitOrderData;
        } catch (Exception e) {
            logger.info("活动拆弹解析结算过程出现异常。。。。" + e);
            return null;
        }

    }

    private Map<String, Object> toSplitOrderResult (ProRuleMessageParam proRuleMessageParam, List<Map<String, Object>> paramList) {
        if (CollectionUtils.isEmpty(paramList))
            return null;
        Map<String, List<Map<String, Object>>> proListGroupByType = new HashMap<String, List<Map<String, Object>>>();
        List<Map<String, Object>> secondList = null;
        for (Map<String, Object> map : paramList) {
            if (!proListGroupByType.containsKey(map.get("promotions_type"))) {
                secondList = new ArrayList<Map<String, Object>>();
                for (Map<String, Object> secondMap : paramList) {
                    if (StringUtils.equalsIgnoreCase(secondMap.get("promotions_type").toString(), map.get("promotions_type").toString())) {
                        secondList.add(secondMap);
                    }
                }
                proListGroupByType.put(map.get("promotions_type").toString(), secondList);
            }
        }

        Map<String, Object> resultMap = proRuleSplitResult(proRuleMessageParam, proListGroupByType);
        List<String> proActivityIds = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(proListGroupByType.get("10"))) {
            resultMap.put("isHaveGiftProRuleActivity", true);
            resultMap.put("promotionsRuleId", Integer.parseInt(proListGroupByType.get("10").get(0).get("id").toString()));
            resultMap.put("promotionsActivityId", Integer.parseInt(proListGroupByType.get("10").get(0).get("proActivityId").toString()));
            proActivityIds.add(proListGroupByType.get("10").get(0).get("proActivityId").toString());
        } else {
            resultMap.put("isHaveGiftProRuleActivity", false);
        }


        if (resultMap.get("allProActivityId") != null && StringUtil.isNotEmpty(resultMap.get("allProActivityId").toString())) {
            List<UsePromotionsParams> usePromotionsParamsList = new ArrayList<UsePromotionsParams>();
            proActivityIds.addAll(new ArrayList<String>(Arrays.asList(resultMap.get("allProActivityId").toString().split(","))));

            proActivityIds.stream().forEach(pro -> {
                if (StringUtil.isNotEmpty(pro)) {
                    Integer site_id = proRuleMessageParam.getSiteId();
                    Integer promoActivityId = Integer.parseInt(pro);
                    PromotionsActivity promotionsActivity = promotionsActivityMapper.getPromotionsAndPromotionsRule(site_id, promoActivityId);
                    UsePromotionsParams usePromotionsParams = new UsePromotionsParams();
                    usePromotionsParams.setPromActivityId(Integer.parseInt(pro));
                    usePromotionsParams.setPromotionsName(promotionsActivity.getPromotionsRule().getPromotionsName());
                    usePromotionsParams.setPromotionsType(promotionsActivity.getPromotionsRule().getPromotionsType());
                    usePromotionsParams.setPromotionsId(promotionsActivity.getPromotionsRule().getId());
                    usePromotionsParamsList.add(usePromotionsParams);
                }
            });


            if (Boolean.parseBoolean(resultMap.get("isHaveGiftProRuleActivity").toString())) {
                UsePromotionsParams usePromotionsParams = usePromotionsParamsList.stream()
                        .filter(map -> map.getPromotionsType() == 10)
                        .findFirst()
                        .get();

                SelectGiftByGoodsIdParms params = new SelectGiftByGoodsIdParms() {{
                    setSiteId(proRuleMessageParam.getSiteId());
                    setId(usePromotionsParams.getPromotionsId());
                    setGoodsInfo(JSON.toJSONString(proRuleMessageParam.getGoodsInfo()));
                }};

                Map<String, Object> giftRuleMap = goodsService.selectGiftByGoodsIdParms(params);
                List<Map<String, Object>> giftList = (List) giftRuleMap.get("giftList");

                if (null != giftRuleMap.get("giftList") && giftList.size() == 1) {
                    resultMap.put("giftRuleMsg", goodsService.selectGiftByGoodsIdParms(params));
                }
            }
            resultMap.put("proRuleList", usePromotionsParamsList);
        }
        return resultMap;
    }

    private Map<String, Object> proRuleSplitResult (ProRuleMessageParam proRuleMessageParam, Map<String, List<Map<String, Object>>> map) {
        //分步拆单 限价->打折->满减->满赠->包邮   整个过程proRuleMessageParam 参数值会随着拆单的过程发生改变  整个过程resultMap 惯穿整个拆单过程 所有的优惠信息 都放在 resultMap 中
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (!CollectionUtils.isEmpty(map.get("50")))
            resultMap = controlPriceSplitOrder(proRuleMessageParam, map.get("50"), resultMap);
        if (!CollectionUtils.isEmpty(map.get("20")))
            resultMap = discountPriceSplitOrder(proRuleMessageParam, map.get("20"), resultMap);
        if (!CollectionUtils.isEmpty(map.get("40")))
            resultMap = reduceMoneySplitOrder(proRuleMessageParam, map.get("40"), resultMap);
       /* if (!CollectionUtils.isEmpty(map.get("10")))
            resultMap = giftSplitOrder(proRuleMessageParam, map.get("10"), resultMap);*/
        if (!CollectionUtils.isEmpty(map.get("30")))
            resultMap = freePostSplitOrder(proRuleMessageParam, map.get("30"), resultMap);
        return resultMap;
    }

    //限价拆单
    private Map<String, Object> controlPriceSplitOrder (ProRuleMessageParam proRuleMessageParam, List<Map<String, Object>> list, Map<String, Object> map) {
        List<Map<String, Integer>> goodsInfo = proRuleMessageParam.getGoodsInfo();
        Member member = memberMapper.getMemberByMemberId(proRuleMessageParam.getSiteId(), proRuleMessageParam.getUserId());
        Integer totalControlSplitPrice = 0;
        Integer proRuleDeductionPrice = map.get("proRuleDeductionPrice") == null ? 0 : Integer.parseInt(map.get("proRuleDeductionPrice").toString());
        String allProActivityId = "";
        for (Map<String, Integer> goodsMap : goodsInfo) {
            Integer goodsId = goodsMap.get("goodsId");
            Integer goodsNum = goodsMap.get("num");
            Integer goodsPrice = goodsMap.get("goodsPrice");
            List<Map<String, Integer>> conTrolPriceProActivityList = new ArrayList<Map<String, Integer>>();
            for (Map<String, Object> proMap : list) {
                try {
                    String fixPriceRule = proMap.get("promotions_rule").toString();
                    FixedPriceRule fixedPriceRule = JSON.parseObject(fixPriceRule, FixedPriceRule.class);
                    Map<String, Integer> proDiscountMap = null;
                    Integer proActivityId = Integer.parseInt(proMap.get("proActivityId").toString());
                    Integer proRuleId = Integer.parseInt(proMap.get("id").toString());
                    if (fixedPriceRule.getGoodsIdsType().intValue() == 0) {
                        if (promotionsDetailMapper.getUseBuyedGoodsNum(proRuleMessageParam.getSiteId(), goodsId, member.getBuyerId(), proActivityId, proRuleId) < fixedPriceRule.getTotal()) {
                            Integer maxGoodsNum = fixedPriceRule.getTotal() - promotionsDetailMapper.getUseBuyedGoodsNum(proRuleMessageParam.getSiteId(), goodsId, member.getBuyerId(), proActivityId, proRuleId);
                            if (goodsNum > maxGoodsNum) {
                                proDiscountMap.put("isControl", 0);
                                proDiscountMap.put("goodsNum", goodsNum);
                            } else if (goodsNum <= maxGoodsNum) {
                                maxGoodsNum = goodsNum;
                                proDiscountMap.put("isControl", 1);
                            }

                            proDiscountMap = new HashMap<String, Integer>();
                            proDiscountMap.put("totalfixedPrice", fixedPriceRule.getFixedPrice() * maxGoodsNum + (goodsNum - maxGoodsNum) * goodsPrice);
                            proDiscountMap.put("proActivityId", Integer.parseInt(proMap.get("proActivityId").toString()));
                            proDiscountMap.put("fixedPrice", fixedPriceRule.getFixedPrice());
                            conTrolPriceProActivityList.add(proDiscountMap);
                        }
                    } else if (fixedPriceRule.getGoodsIdsType().intValue() == 1) {
                        if (new HashSet<String>(Arrays.asList(fixedPriceRule.getGoodsIds().split(","))).contains(goodsId.toString())
                                && promotionsDetailMapper.getUseBuyedGoodsNum(proRuleMessageParam.getSiteId(), goodsId, member.getBuyerId(), proActivityId, proRuleId) < fixedPriceRule.getTotal()) {
                            proDiscountMap = new HashMap<String, Integer>();

                            Integer maxGoodsNum = fixedPriceRule.getTotal() - promotionsDetailMapper.getUseBuyedGoodsNum(proRuleMessageParam.getSiteId(), goodsId, member.getBuyerId(), proActivityId, proRuleId);
                            if (goodsNum > maxGoodsNum) {
                                proDiscountMap.put("isControl", 0);
                                proDiscountMap.put("goodsNum", goodsNum);
                            } else if (goodsNum <= maxGoodsNum) {
                                maxGoodsNum = goodsNum;
                                proDiscountMap.put("isControl", 1);
                            }

                            if (null == fixedPriceRule.getRuleType() || fixedPriceRule.getRuleType() == 1) {
                                proDiscountMap.put("totalfixedPrice", fixedPriceRule.getFixedPrice() * maxGoodsNum + (goodsNum - maxGoodsNum) * goodsPrice);
                                proDiscountMap.put("fixedPrice", fixedPriceRule.getFixedPrice());
                                proDiscountMap.put("proActivityId", Integer.parseInt(proMap.get("proActivityId").toString()));
                                conTrolPriceProActivityList.add(proDiscountMap);
                            } else if (fixedPriceRule.getRuleType() == 2) {
                                proDiscountMap.put("totalfixedPrice", maxGoodsNum * fixedPriceRule.getRules().stream().filter(stringIntegerMap -> null != stringIntegerMap.get("goodsId") && StringUtils.equalsIgnoreCase(goodsId.toString(), stringIntegerMap.get("goodsId").toString())).collect(Collectors.toList()).get(0).get("fixedPrice") + (goodsNum - maxGoodsNum) * goodsPrice);
                                proDiscountMap.put("fixedPrice", fixedPriceRule.getRules().stream().filter(stringIntegerMap -> null != stringIntegerMap.get("goodsId") && StringUtils.equalsIgnoreCase(goodsId.toString(), stringIntegerMap.get("goodsId").toString())).collect(Collectors.toList()).get(0).get("fixedPrice"));
                                proDiscountMap.put("proActivityId", Integer.parseInt(proMap.get("proActivityId").toString()));
                                conTrolPriceProActivityList.add(proDiscountMap);
                            }
                        }
                    } else if (fixedPriceRule.getGoodsIdsType().intValue() == 2) {

                        if (promotionsDetailMapper.getUseBuyedGoodsNum(proRuleMessageParam.getSiteId(), goodsId, member.getBuyerId(), proActivityId, proRuleId) < fixedPriceRule.getTotal() && !new HashSet<String>(Arrays.asList(fixedPriceRule.getGoodsIds().split(","))).contains(goodsId.toString())) {

                            Integer maxGoodsNum = fixedPriceRule.getTotal() - promotionsDetailMapper.getUseBuyedGoodsNum(proRuleMessageParam.getSiteId(), goodsId, member.getBuyerId(), proActivityId, proRuleId);
                            if (goodsNum > maxGoodsNum) {
                                proDiscountMap.put("isControl", 0);
                                proDiscountMap.put("goodsNum", goodsNum);
                            } else if (goodsNum <= maxGoodsNum) {
                                maxGoodsNum = goodsNum;
                                proDiscountMap.put("isControl", 1);
                            }

                            proDiscountMap = new HashMap<String, Integer>();
                            proDiscountMap.put("totalfixedPrice", fixedPriceRule.getFixedPrice() * maxGoodsNum + (goodsNum - maxGoodsNum) * goodsPrice);
                            proDiscountMap.put("proActivityId", Integer.parseInt(proMap.get("proActivityId").toString()));
                            proDiscountMap.put("fixedPrice", fixedPriceRule.getFixedPrice());
                            conTrolPriceProActivityList.add(proDiscountMap);
                        }
                    }

                } catch (Exception e) {
                    logger.info("限价拆单出现异常", e);
                    break;//一个活动的异常不影响其它活动的使用
                }

            }
            //优惠处理
            if (CollectionUtils.isNotEmpty(conTrolPriceProActivityList)) {
                Map<String, Integer> maxFixedPrice = conTrolPriceProActivityList.get(0);
                for (Map<String, Integer> integerMap : conTrolPriceProActivityList) {
                    if (integerMap.get("totalfixedPrice") < maxFixedPrice.get("totalfixedPrice"))
                        maxFixedPrice = integerMap;
                }
                if (goodsMap.get("goodsPrice") > maxFixedPrice.get("fixedPrice")) {
                    totalControlSplitPrice += goodsMap.get("goodsPrice") * goodsMap.get("num") - maxFixedPrice.get("totalfixedPrice");
                    allProActivityId += (maxFixedPrice.get("proActivityId") + ",");
                    goodsMap.put("goodsPrice", maxFixedPrice.get("fixedPrice"));
                    goodsMap.put("theGoodsContainsControlPrice", 11111);
                }
            }

        }
        map.put("totalControlSplitPrice", totalControlSplitPrice);
        map.put("allProActivityId", allProActivityId);
        map.put("proRuleDeductionPrice", proRuleDeductionPrice + totalControlSplitPrice);


        return map;
    }


    //打折拆单 先拆单个商品
    private Map<String, Object> discountPriceSplitOrder (ProRuleMessageParam proRuleMessageParam, List<Map<String, Object>> list, Map<String, Object> map) {
        //先做组合商品的拆单
        map = disCountSplitOrderForManyGoods(proRuleMessageParam, list, map);


        //组合商品拆单流程算完 在算单个商品打折单 先过滤一变活动 对于当前的参数还有没有成立的活动
        list = list.stream().filter(map2 -> promotionsRuleService.checkDiscountRule(proRuleMessageParam, map2.get("promotions_rule").toString())).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(list))
            return map;

        List<Map<String, Integer>> goodsInfo = proRuleMessageParam.getGoodsInfo();
        ProRuleMessageParam proRuleMessageParamForProActivity = null;
        Integer totalDiscountSplitPrice = 0;
        Integer proRuleDeductionPrice = map.get("proRuleDeductionPrice") == null ? 0 : Integer.parseInt(map.get("proRuleDeductionPrice").toString());
        String allProActivityId = map.get("allProActivityId") == null ? "" : map.get("allProActivityId").toString();
        Integer minDiscount = 100;
        List<Map<String, Integer>> goodsParam = null;
        //对于每件商品先算单独打折
        for (Map<String, Integer> goodsMap : goodsInfo) {
            Integer goodsId = goodsMap.get("goodsId");
            Integer num = goodsMap.get("num");
            Integer goodsPrice = goodsMap.get("goodsPrice");
            //拆单过程中整个商品 参数的变化和优惠的金额 使用到的优惠活动 统一放到afterDiscountMapList对象中
            List<Map<String, Integer>> afterDiscountMapList = new ArrayList<Map<String, Integer>>();
            for (Map<String, Object> proMap : list) {
                try {
                    Map<String, Integer> proDiscountMap = null;
                    String disCountRuleParam = proMap.get("promotions_rule").toString();
                    DiscountRule discountRule = JSON.parseObject(disCountRuleParam, DiscountRule.class);
                    Integer is_ml = discountRule.getIsMl();
                    Integer is_round = discountRule.getIsRound();
                    goodsParam = new ArrayList<Map<String, Integer>>();
                    proRuleMessageParamForProActivity = new ProRuleMessageParam();
                    goodsParam.add(goodsMap);
                    proRuleMessageParamForProActivity.setGoodsInfo(goodsParam);
                    proRuleMessageParamForProActivity.setTotalPrice(proRuleMessageParam.getTotalPrice());
                    proRuleMessageParamForProActivity.setCouponPrice(proRuleMessageParam.getCouponPrice());
                    switch (discountRule.getRuleType()) {
                        case 1:
                            if (promotionsRuleService.checkDiscountRule(proRuleMessageParamForProActivity, disCountRuleParam))
                                afterDiscountMapList = directDiscountSplitOrderList(discountRule, goodsPrice, num, goodsId, is_ml, is_round, proMap, afterDiscountMapList);
                            break;
                        case 2:
                            if (promotionsRuleService.checkDiscountRule(proRuleMessageParamForProActivity, disCountRuleParam)) {
                                proDiscountMap = new HashMap<String, Integer>();
                                for (Map<String, Integer> disCountMap : discountRule.getRules()) {
                                    Integer theMeetMoney = disCountMap.get("meet_money");
                                    Integer theDisCount = disCountMap.get("discount");
                                    if (goodsPrice * num >= theMeetMoney && minDiscount > theDisCount)
                                        minDiscount = theDisCount;
                                }
                                Integer afterDiscountPrice = orderDeductionUtils.discountMoney(goodsPrice, is_ml, is_round, minDiscount);
                                proDiscountMap.put("afterdisCountPrice", goodsPrice - afterDiscountPrice);
                                proDiscountMap.put("youhuiPrice", afterDiscountPrice * num);
                                proDiscountMap.put("proActivityId", Integer.parseInt(proMap.get("proActivityId").toString()));
                                proDiscountMap.put("proRuleId", Integer.parseInt(proMap.get("id").toString()));
                                afterDiscountMapList.add(proDiscountMap);
                            }
                            break;
                        case 3:
                            if (promotionsRuleService.checkDiscountRule(proRuleMessageParamForProActivity, disCountRuleParam)) {
                                proDiscountMap = new HashMap<String, Integer>();
                                for (Map<String, Integer> disCountMap : discountRule.getRules()) {
                                    Integer theMeetNum = disCountMap.get("meet_num");
                                    Integer theDisCount = disCountMap.get("discount");
                                    if (num >= theMeetNum && minDiscount > theDisCount)
                                        minDiscount = theDisCount;
                                }
                                Integer afterDiscountPrice = orderDeductionUtils.discountMoney(goodsPrice, is_ml, is_round, minDiscount);
                                proDiscountMap.put("afterdisCountPrice", goodsPrice - afterDiscountPrice);
                                proDiscountMap.put("youhuiPrice", afterDiscountPrice * num);
                                proDiscountMap.put("proActivityId", Integer.parseInt(proMap.get("proActivityId").toString()));
                                proDiscountMap.put("proRuleId", Integer.parseInt(proMap.get("id").toString()));
                                afterDiscountMapList.add(proDiscountMap);
                            }

                            break;
                        case 4:
                            if (promotionsRuleService.checkDiscountRule(proRuleMessageParamForProActivity, disCountRuleParam)) {
                                proDiscountMap = new HashMap<String, Integer>();
                                for (Map<String, Integer> disCountMap : discountRule.getRules()) {
                                    Integer theMeetRate = disCountMap.get("rate");
                                    Integer theDisCount = disCountMap.get("discount");
                                    Integer discountLimit = null != disCountMap.get("goods_amount_limit") ? disCountMap.get("goods_amount_limit") : 0;
                                    int discount_num = (int) Math.floor(num / theMeetRate);
                                    if (num >= theMeetRate) {
                                        if (discount_num > discountLimit)
                                            discount_num = discountLimit;
                                    }
                                    minDiscount = theDisCount;
                                    Integer afterDiscountMoney = orderDeductionUtils.discountMoney(discount_num * goodsPrice, is_ml, is_round, minDiscount);
                                    proDiscountMap.put("youhuiPrice", afterDiscountMoney);
                                    Integer singleGoodsDisCountPrice = orderDeductionUtils.discountMoney(goodsPrice, is_ml, is_round, minDiscount);
                                    proDiscountMap.put("afterdisCountPrice", goodsPrice - singleGoodsDisCountPrice);
                                    proDiscountMap.put("proActivityId", Integer.parseInt(proMap.get("proActivityId").toString()));
                                    proDiscountMap.put("proRuleId", Integer.parseInt(proMap.get("id").toString()));
                                    afterDiscountMapList.add(proDiscountMap);
                                }
                            }

                            break;
                        case 5:
                            if (promotionsRuleService.checkDiscountRule(proRuleMessageParamForProActivity, disCountRuleParam)) {
                                proDiscountMap = new HashMap<String, Integer>();
                                Map<String, Integer> directDiscountMapForTheGoods = discountRule.getRules().stream().filter(stringIntegerMap -> stringIntegerMap.get("goodsId").equals(goodsId)).collect(Collectors.toList()).get(0);
                                Integer afterDiscount = orderDeductionUtils.discountMoney(goodsPrice, is_ml, is_round, directDiscountMapForTheGoods.get("discount"));
                                proDiscountMap.put("afterdisCountPrice", goodsPrice - afterDiscount);
                                if (afterDiscount * num > directDiscountMapForTheGoods.get("goods_money_limit"))
                                    proDiscountMap.put("youhuiPrice", directDiscountMapForTheGoods.get("goods_money_limit"));
                                else
                                    proDiscountMap.put("youhuiPrice", afterDiscount * num);
                                afterDiscountMapList.add(proDiscountMap);
                            }
                            break;
                    }
                } catch (Exception e) {
                    logger.info("打折活动拆单出现异常", e);
                    break;//一个活动的异常不影响其它活动的使用
                }
            }
            //对于单个商品的单独拆单,每个活动列表需要根据优惠最多原则选出对应的对象
            if (CollectionUtils.isNotEmpty(afterDiscountMapList)) {
                Map<String, Integer> maxYouHuiForSingleGoods = afterDiscountMapList.get(0);
                for (Map<String, Integer> disCountMap : afterDiscountMapList) {
                    if (disCountMap.get("youhuiPrice") > maxYouHuiForSingleGoods.get("youhuiPrice"))
                        maxYouHuiForSingleGoods = disCountMap;
                }
                goodsMap.put("goodsPrice", maxYouHuiForSingleGoods.get("afterdisCountPrice"));
                goodsMap.put("theGoodsContainsSingleDisCountPro", 1111111);
                totalDiscountSplitPrice += maxYouHuiForSingleGoods.get("youhuiPrice");
                allProActivityId += (maxYouHuiForSingleGoods.get("proActivityId") + ",");
            }
        }
        map.put("totalSingleDiscountSplitPrice", totalDiscountSplitPrice);
        map.put("allProActivityId", allProActivityId);
        map.put("proRuleDeductionPrice", proRuleDeductionPrice + totalDiscountSplitPrice);


        return map;
    }

    //直折活动入口
    private List<Map<String, Integer>> directDiscountSplitOrderList (DiscountRule discountRule, Integer goodsPrice, Integer num, Integer goodsId, Integer is_ml, Integer is_round, Map<String, Object> proMap, List<Map<String, Integer>> afterDiscountMapList) {
        try {
            Map<String, Integer> directDiscountMap = discountRule.getRules().get(0);
            Map proDiscountMap = new HashMap<String, Integer>();
            if (discountRule.getGoodsIdsType() == 0) {
                //算出的是单个商品优惠的价格
                Integer afterDiscount = orderDeductionUtils.discountMoney(goodsPrice, is_ml, is_round, directDiscountMap.get("direct_discount"));
                proDiscountMap.put("afterdisCountPrice", goodsPrice - afterDiscount);
                if (afterDiscount * num > directDiscountMap.get("goods_money_limit") && directDiscountMap.get("goods_money_limit") > 0)
                    proDiscountMap.put("youhuiPrice", directDiscountMap.get("goods_money_limit"));
                else
                    proDiscountMap.put("youhuiPrice", afterDiscount * num);
                proDiscountMap.put("proRuleId", Integer.parseInt(proMap.get("id").toString()));
                proDiscountMap.put("proActivityId", Integer.parseInt(proMap.get("proActivityId").toString()));
                afterDiscountMapList.add(proDiscountMap);
            } else if (discountRule.getGoodsIdsType() == 1) {
                if (new HashSet<String>(Arrays.asList(discountRule.getGoodsIds().split(","))).contains(goodsId.toString())) {
                    proDiscountMap = new HashMap<String, Integer>();
                    Integer afterDiscount = orderDeductionUtils.discountMoney(goodsPrice, is_ml, is_round, directDiscountMap.get("direct_discount"));
                    proDiscountMap.put("afterdisCountPrice", goodsPrice - afterDiscount);

                    if (afterDiscount * num > directDiscountMap.get("goods_money_limit") && directDiscountMap.get("goods_money_limit") > 0)
                        proDiscountMap.put("youhuiPrice", directDiscountMap.get("goods_money_limit"));
                    else proDiscountMap.put("youhuiPrice", afterDiscount * num);

                    proDiscountMap.put("proRuleId", Integer.parseInt(proMap.get("id").toString()));
                    proDiscountMap.put("proActivityId", Integer.parseInt(proMap.get("proActivityId").toString()));
                    afterDiscountMapList.add(proDiscountMap);
                }
            } else if (discountRule.getGoodsIdsType() == 2) {
                if (!new HashSet<String>(Arrays.asList(discountRule.getGoodsIds().split(","))).contains(goodsId.toString())) {
                    proDiscountMap = new HashMap<String, Integer>();
                    Integer afterDiscountPrice = orderDeductionUtils.discountMoney(goodsPrice, is_ml, is_round, directDiscountMap.get("direct_discount"));
                    proDiscountMap.put("afterdisCountPrice", goodsPrice - afterDiscountPrice);
                    if (afterDiscountPrice * num > directDiscountMap.get("goods_money_limit") && directDiscountMap.get("goods_money_limit") > 0)
                        proDiscountMap.put("youhuiPrice", directDiscountMap.get("goods_money_limit"));
                    else
                        proDiscountMap.put("youhuiPrice", afterDiscountPrice * num);
                    proDiscountMap.put("proActivityId", Integer.parseInt(proMap.get("proActivityId").toString()));
                    proDiscountMap.put("proRuleId", Integer.parseInt(proMap.get("id").toString()));
                    afterDiscountMapList.add(proDiscountMap);
                }
            }
            return afterDiscountMapList;
        } catch (Exception e) {
            logger.info("直折活动解析出现异常", e);
            return afterDiscountMapList;
        }
    }

    //组合商品打折拆单
    private Map<String, Object> disCountSplitOrderForManyGoods (ProRuleMessageParam proRuleMessageParam, List<Map<String, Object>> list, Map<String, Object> map) {
        //对于组合商品拆单来说  因为对于单个商品来说 直折和对应商品折扣肯定会包含 而第几件打折的情况也只针对于单个商品
        //所以说组合商品拆单只需考虑 满件折 和满元折的情况
        List<Map<String, Integer>> goodsInfo = proRuleMessageParam.getGoodsInfo();
        goodsInfo = goodsInfo.stream().filter(stringIntegerMap -> null == stringIntegerMap.get("theGoodsContainsSingleDisCountPro")).collect(Collectors.toList());

        Integer proRuleDeductionPrice = map.get("proRuleDeductionPrice") == null ? 0 : Integer.parseInt(map.get("proRuleDeductionPrice").toString());
        //对组合商品进行分组 以商品goodsid 逗号分割的字符串为key   对应商品信息组成集合列表 作为对应的 value 值

        List<Map<String, List<Map<String, Integer>>>> goodsGrouptoList = goodsListInfoGroup(goodsInfo);


        List<ProRuleActivitySplitOrderParam> proRuleActivitySplitOrderParams = new ArrayList<ProRuleActivitySplitOrderParam>();

        for (Map<String, Object> proActivityMap : list) {
            String promotionRule = proActivityMap.get("promotions_rule").toString();
            DiscountRule discountRule = JSON.parseObject(promotionRule, DiscountRule.class);
            for (Map<String, List<Map<String, Integer>>> onegroupGoodsMap : goodsGrouptoList) {
                try {
                    //groupGoodsKey 对应组合商品逗号分隔的字符串 groupGoodsList 为groupGoodsKey对应的商品列表
                    String groupGoodsKey = null;
                    List<Map<String, Integer>> groupGoodsList = null;
                    ProRuleMessageParam proRuleMessageParamTime = new ProRuleMessageParam();
                    Iterator<Map.Entry<String, List<Map<String, Integer>>>> it = onegroupGoodsMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, List<Map<String, Integer>>> m = it.next();
                        groupGoodsKey = m.getKey().substring(0, m.getKey().length() - 1);
                        groupGoodsList = m.getValue();
                    }
                    proRuleMessageParamTime.setGoodsInfo(groupGoodsList);
                    proRuleMessageParamTime.setTotalPrice(proRuleMessageParam.getTotalPrice());
                    proRuleMessageParamTime.setCouponPrice(proRuleMessageParam.getCouponPrice());

                    Integer totalMoney = 0;
                    Integer totalNum = 0;
                    //数据库中的打折都是乘以100的 默认值设置为100
                    Integer discount = 100;
                    Integer goodsGroupYouhuiPrice = 0;
                    Integer is_ml = discountRule.getIsMl();
                    Integer is_round = discountRule.getIsRound();
                    String goodsIdsContainTheProActivity = "";

                    if (promotionsRuleService.checkDiscountRule(proRuleMessageParamTime, promotionRule)) {
                        //每一组商品对应该活动的优惠金额的 数据map 以逗号间隔的商品字符串为key 优惠金额为value
                        Map<String, Integer> oneGroupYouhui = new HashMap<String, Integer>();

                        ProRuleActivitySplitOrderParam proRuleActivitySplitOrderParam = new ProRuleActivitySplitOrderParam();
                        proRuleActivitySplitOrderParam.setProActivityId(proActivityMap.get("proActivityId").toString());
                        proRuleActivitySplitOrderParam.setGroupGoodsList(groupGoodsList);
                        proRuleActivitySplitOrderParam.setGroupGoodsKey(groupGoodsKey);

                        if (discountRule.getGoodsIdsType() == 0) {
                            totalMoney = proRuleMessageParamTime.getGoodsInfo()
                                    .stream()
                                    .mapToInt(pricemap -> pricemap.get("goodsPrice") * pricemap.get("num")).sum();
                            totalNum = proRuleMessageParamTime.getGoodsInfo()
                                    .stream()
                                    .mapToInt(pricemap -> pricemap.get("num")).sum();
                            goodsIdsContainTheProActivity = proRuleMessageParamTime.getGoodsInfo().stream().map(paramMap -> paramMap.get("goodsId").toString()).collect(Collectors.joining(","));
                        } else if (discountRule.getGoodsIdsType() == 1) {
                            totalMoney = proRuleMessageParamTime
                                    .getGoodsInfo()
                                    .stream()
                                    .filter(goodsIdMap -> new HashSet<String>(Arrays.asList(discountRule.getGoodsIds().split(","))).contains(goodsIdMap.get("goodsId").toString()))
                                    .mapToInt(pricemap -> pricemap.get("goodsPrice") * pricemap.get("num")).sum();
                            totalNum = proRuleMessageParamTime
                                    .getGoodsInfo()
                                    .stream()
                                    .filter(goodsIdMap -> new HashSet<String>(Arrays.asList(discountRule.getGoodsIds().split(","))).contains(goodsIdMap.get("goodsId").toString()))
                                    .mapToInt(pricemap -> pricemap.get("num")).sum();
                            goodsIdsContainTheProActivity = proRuleMessageParamTime.getGoodsInfo()
                                    .stream()
                                    .filter(goodsIdMap -> new HashSet<String>(Arrays.asList(discountRule.getGoodsIds().split(","))).contains(goodsIdMap.get("goodsId").toString()))
                                    .map(paramMap -> paramMap.get("goodsId").toString()).collect(Collectors.joining(","));
                        } else if (discountRule.getGoodsIdsType() == 2) {
                            totalMoney = proRuleMessageParamTime
                                    .getGoodsInfo()
                                    .stream()
                                    .filter(goodsIdMap -> !new HashSet<String>(Arrays.asList(discountRule.getGoodsIds().split(","))).contains(goodsIdMap.get("goodsId").toString()))
                                    .mapToInt(pricemap -> pricemap.get("goodsPrice") * pricemap.get("num")).sum();
                            totalNum = proRuleMessageParamTime
                                    .getGoodsInfo()
                                    .stream()
                                    .filter(goodsIdMap -> !new HashSet<String>(Arrays.asList(discountRule.getGoodsIds().split(","))).contains(goodsIdMap.get("goodsId").toString()))
                                    .mapToInt(pricemap -> pricemap.get("num")).sum();
                            goodsIdsContainTheProActivity = proRuleMessageParamTime.getGoodsInfo()
                                    .stream()
                                    .filter(goodsIdMap -> !new HashSet<String>(Arrays.asList(discountRule.getGoodsIds().split(","))).contains(goodsIdMap.get("goodsId").toString()))
                                    .map(paramMap -> paramMap.get("goodsId").toString()).collect(Collectors.joining(","));

                        }

                        switch (discountRule.getRuleType()) {
                            case 2:
                                for (Map<String, Integer> integerMap : discountRule.getRules()) {
                                    if (totalMoney >= integerMap.get("meet_money") && discount > integerMap.get("discount"))
                                        discount = integerMap.get("discount");
                                }
                                break;
                            case 3:
                                for (Map<String, Integer> integerMap : discountRule.getRules()) {
                                    if (totalNum >= integerMap.get("meet_num") && discount > integerMap.get("discount"))
                                        discount = integerMap.get("discount");
                                }
                                break;
                        }
                        goodsGroupYouhuiPrice = orderDeductionUtils.discountMoney(totalMoney, is_ml, is_round, discount);
                        oneGroupYouhui.put(groupGoodsKey, goodsGroupYouhuiPrice);
                        proRuleActivitySplitOrderParam.setGroupyGoodsSplitOrderMap(oneGroupYouhui);
                        proRuleActivitySplitOrderParam.setDiscount(discount);
                        proRuleActivitySplitOrderParam.setReduceMoneyFromDisCount(goodsGroupYouhuiPrice);
                        proRuleActivitySplitOrderParam.setGoodsIdsContainTheProActivity(goodsIdsContainTheProActivity);
                        proRuleActivitySplitOrderParam.setDiscount_is_ml(is_ml);
                        proRuleActivitySplitOrderParam.setDiscount_is_round(is_round);
                        proRuleActivitySplitOrderParam.setDiscountRule(discountRule);
                        boolean ifCanadd = proRuleActivitySplitOrderParams.stream().allMatch(onePojo -> !StringUtil.equalsIgnoreCase(onePojo.getGroupGoodsKey(), proRuleActivitySplitOrderParam.getGroupGoodsKey()));
                        if (ifCanadd) {
                            proRuleActivitySplitOrderParams.add(proRuleActivitySplitOrderParam);
                        } else {
                            ProRuleActivitySplitOrderParam theSameGroupGoods = proRuleActivitySplitOrderParams.stream().filter(activityPro -> StringUtil.equalsIgnoreCase(activityPro.getGroupGoodsKey(), proRuleActivitySplitOrderParam.getGroupGoodsKey())).collect(Collectors.toList()).get(0);
                            if (theSameGroupGoods.getReduceMoneyFromDisCount() > proRuleActivitySplitOrderParam.getReduceMoneyFromDisCount()) {
                                proRuleActivitySplitOrderParams.remove(theSameGroupGoods);
                                proRuleActivitySplitOrderParams.add(theSameGroupGoods);
                            }
                        }

                    }

                } catch (Exception e) {
                    logger.info("组合商品处理打折活动出现异常");
                    continue;//一组商品出现异常不影响其它组商品参与活动
                }
            }
        }

        List<ProRuleActivitySplitOrderParam> resultList = toSureGroupGoodsProActivity(proRuleActivitySplitOrderParams, proRuleMessageParam, "discount");
        List<Map<String, Integer>> goodsInfo2 = proRuleMessageParam.getGoodsInfo();
        goodsInfo2 = changeGoodsDisCountPrice(goodsInfo2, resultList);
        proRuleMessageParam.setGoodsInfo(goodsInfo2);

        String allProActivityId = map.get("allProActivityId") == null ? "" : map.get("allProActivityId").toString();
        String groupGoodsForProActivityId = resultList.stream().map(map4 -> map4.getProActivityId()).collect(Collectors.joining(","));
        Integer groupGoodsDiscontTotalPrice = resultList.stream().mapToInt(obj5 -> obj5.getReduceMoneyFromDisCount()).sum();

        allProActivityId = allProActivityId + groupGoodsForProActivityId + ",";

        map.put("groupGoodsDiscontTotalPrice", groupGoodsDiscontTotalPrice);
        map.put("allProActivityId", allProActivityId);
        map.put("proRuleDeductionPrice", proRuleDeductionPrice + groupGoodsDiscontTotalPrice);

        return map;
    }

    private List<ProRuleActivitySplitOrderParam> toSureGroupGoodsProActivity (List<ProRuleActivitySplitOrderParam> list, ProRuleMessageParam proRuleMessageParam, String type) {
        if (CollectionUtils.isEmpty(list))
            return list;

        //把商品组合进行拆单,按照优惠的大小从打到校倒叙排列，优惠相同的情况下 组合商品的种类数越少越往前
        Collections.sort(list, new ProActivityComparatorDesc());

        int size = list.size();

        for (int i = 0; i < list.size(); i++) {
            ProRuleActivitySplitOrderParam proRuleActivitySplitOrderParam = list.get(i);
            List<String> stringList = new ArrayList<String>(Arrays.asList(proRuleActivitySplitOrderParam.getGroupGoodsKey().split(",")));
            for (int j = i + 1; j < list.size(); j++) {
                ProRuleActivitySplitOrderParam proRuleActivitySplitOrderParam2 = list.get(j);
                List<String> stringList2 = new ArrayList<String>(Arrays.asList(proRuleActivitySplitOrderParam2.getGroupGoodsKey().split(",")));
                if ((!Collections.disjoint(stringList, stringList2)) && !StringUtil.equalsIgnoreCase(proRuleActivitySplitOrderParam.getGroupGoodsKey(), proRuleActivitySplitOrderParam2.getGroupGoodsKey())) {
                    list.remove(proRuleActivitySplitOrderParam2);
                    j = 0;
                }
            }
        }

        if (StringUtils.equalsIgnoreCase(type, "discount")) {
            proRuleMessageParam.getGoodsInfo().forEach(goodsMap -> {
                boolean isContainsDisCountMap = list.stream().anyMatch(oneproRule -> (new HashSet<String>(Arrays.asList(oneproRule.getGoodsIdsContainTheProActivity().split(","))).contains(goodsMap.get("goodsId").toString())
                        || StringUtils.equalsIgnoreCase(oneproRule.getGoodsIdsContainTheProActivity(), goodsMap.get("goodsId").toString()))
                        && discontGoodsIdCheck(oneproRule.getDiscountRule(), goodsMap.get("goodsId").toString()));
                if (isContainsDisCountMap)
                    goodsMap.put("theGoodsContainsSingleDisCountPro", 1111111);
            });


        } else if (StringUtils.equalsIgnoreCase(type, "reduce")) {
            proRuleMessageParam.getGoodsInfo().forEach(goodsMap -> {
                boolean isContainsDisCountMap = list.stream().anyMatch(oneproRule -> (new HashSet<String>(Arrays.asList(oneproRule.getGoodsIdsContainTheProActivity().split(","))).contains(goodsMap.get("goodsId"))
                        || StringUtils.equalsIgnoreCase(oneproRule.getGoodsIdsContainTheProActivity(), goodsMap.get("goodsId").toString()))
                        && reduceGoodsIdCheck(oneproRule.getReduceMoneyRule(), goodsMap.get("goodsId").toString()));
                if (isContainsDisCountMap)
                    goodsMap.put("theGoodsContainsSinglereDuPro", 1111111);
            });
        }


        return list;
    }

    private boolean discontGoodsIdCheck (DiscountRule discountRule, String goodsId) {
        switch (discountRule.getRuleType()) {
            case 2:
            case 3:
                if (discountRule.getGoodsIdsType() == 0)
                    return true;
                else if (discountRule.getGoodsIdsType() == 1)
                    return new HashSet<String>(Arrays.asList(discountRule.getGoodsIds().split(","))).contains(goodsId);
                else if (discountRule.getGoodsIdsType() == 2)
                    return !new HashSet<String>(Arrays.asList(discountRule.getGoodsIds().split(","))).contains(goodsId);
                break;
        }
        return false;
    }

    public boolean reduceGoodsIdCheck (ReduceMoneyRule reduceMoneyRule, String goodsId) {
        try {
            switch (reduceMoneyRule.getRuleType()) {
                case 2:
                case 3:
                    if (reduceMoneyRule.getGoodsIdsType() == 0)
                        return true;
                    else if (reduceMoneyRule.getGoodsIdsType() == 1)
                        return new HashSet<String>(Arrays.asList(reduceMoneyRule.getGoodsIds().split(","))).contains(goodsId);
                    else if (reduceMoneyRule.getGoodsIdsType() == 2)
                        return !new HashSet<String>(Arrays.asList(reduceMoneyRule.getGoodsIds().split(","))).contains(goodsId);
                    break;
            }
            return false;
        } catch (Exception e) {
            logger.info("组合拆单校验是否参与优惠出现异常");
            return false;
        }
    }

    private List<Map<String, Integer>> changeGoodsDisCountPrice (List<Map<String, Integer>> goodsMapList, List<ProRuleActivitySplitOrderParam> proRuleActivitySplitOrderParams) {
        for (Map<String, Integer> map : goodsMapList) {
            Integer goodsId = map.get("goodsId");
            Integer goodsPrice = map.get("goodsPrice");
            for (ProRuleActivitySplitOrderParam proRuleActivitySplitOrderParam : proRuleActivitySplitOrderParams) {
                if (new HashSet<String>(Arrays.asList(proRuleActivitySplitOrderParam.getGoodsIdsContainTheProActivity().split(","))).contains(goodsId.toString())) {
                    Integer afterDisCountPrice = orderDeductionUtils.discountMoney(goodsPrice, proRuleActivitySplitOrderParam.getDiscount_is_ml(), proRuleActivitySplitOrderParam.getDiscount_is_round(), proRuleActivitySplitOrderParam.getDiscount());
                    map.put("goodsPrice", goodsPrice - afterDisCountPrice);
                    break;
                }
            }

        }

        return goodsMapList;
    }


    //对商品进行分组处理 即单独拆单之后的商品 进行组合处理 在分别去 对应活动进行优惠金额处理
    public List<Map<String, List<Map<String, Integer>>>> goodsListInfoGroup (List<Map<String, Integer>> goodsInfo) {

        List<Map<String, List<Map<String, Integer>>>> list = new ArrayList<Map<String, List<Map<String, Integer>>>>();//用于存放商品组合的信息

        Map<String, Map<String, Integer>> goodsMapFor = new HashMap<String, Map<String, Integer>>();

        List<Integer> paramList = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList();

        for (int i = 0; i < goodsInfo.size(); i++) {
            goodsMapFor.put("" + i, goodsInfo.get(i));
            paramList.add(i);
            arrayList.add(i + "");
        }

        for (int j = 0; j < paramList.size(); j++) {
            arrayList.addAll(toGroupGoods(j, paramList));
        }


        for (String string : arrayList) {
            String goodskey = "";
            List<Map<String, Integer>> goodsList = new ArrayList<Map<String, Integer>>();
            String[] strings = string.split(",");
            for (String stringParam : strings) {
                goodskey += goodsMapFor.get(stringParam).get("goodsId") + ",";
                goodsList.add(goodsMapFor.get(stringParam));
            }
            Map<String, List<Map<String, Integer>>> listMap = new HashMap<String, List<Map<String, Integer>>>();
            listMap.put(goodskey, goodsList);
            list.add(listMap);
        }

        return list;
    }

    private List<String> toGroupGoods (int startNum, List<Integer> list) {

        ArrayList resultList = new ArrayList();
        String startGoodsId = "";
        for (int i = startNum; i < list.size(); i++) {
            startGoodsId += list.get(i) + ",";
            for (int j = i + 1; j < list.size(); j++) {
                resultList.add(startGoodsId + list.get(j));
            }
        }
        return resultList;
    }

    //满减拆单
    private Map<String, Object> reduceMoneySplitOrder (ProRuleMessageParam proRuleMessageParam, List<Map<String, Object>> list, Map<String, Object> map) {

        //先拆组合商品
        map = reDuceSplitOrderForManyGoods(proRuleMessageParam, list, map);

        //组合商品拆完,在去拆单品
        list = list.stream().filter(map1 -> promotionsRuleService.checkProRuleForPromotionsRule(proRuleMessageParam, map1)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list))
            return map;
        List<Map<String, Integer>> goodsInfo = proRuleMessageParam.getGoodsInfo();
        Integer totalReduceSplitPrice = 0;
        Integer proRuleDeductionPrice = map.get("proRuleDeductionPrice") == null ? 0 : Integer.parseInt(map.get("proRuleDeductionPrice").toString());
        String allProActivityId = map.get("allProActivityId") == null ? "" : map.get("allProActivityId").toString();
        for (Map<String, Integer> goodsInfoMap : goodsInfo) {
            List<Map<String, Integer>> reduceMoneyProActivityList = new ArrayList<Map<String, Integer>>();
            for (Map<String, Object> proMap : list) {
                try {
                    String promotions_rule = proMap.get("promotions_rule").toString();
                    ReduceMoneyRule reduceMoneyRule = JSON.parseObject(promotions_rule, ReduceMoneyRule.class);
                    switch (reduceMoneyRule.getRuleType()) {
                        case 1:
                            reduceMoneyProActivityList = directReduceSplitList(reduceMoneyRule, goodsInfoMap, proMap, reduceMoneyProActivityList);
                            break;
                        case 2:
                            reduceMoneyProActivityList = containsPerMoneySplitList(reduceMoneyRule, goodsInfoMap, proMap, reduceMoneyProActivityList);
                            break;
                        case 3:
                            reduceMoneyProActivityList = containsMoneySplitOrderList(reduceMoneyRule, goodsInfoMap, proMap, reduceMoneyProActivityList);
                            break;
                    }
                } catch (Exception e) {
                    logger.info("满减单独商品拆单出现异常", e);
                    break;//一个活动的异常不影响其它活动的使用
                }
            }

            //对于每个商品的满减活动需要选出优惠力度最大的
            if (CollectionUtils.isNotEmpty(reduceMoneyProActivityList)) {
                Map<String, Integer> maxReduceProActivityForGoods = reduceMoneyProActivityList.get(0);
                for (Map<String, Integer> paramMap : reduceMoneyProActivityList) {
                    if (paramMap.get("reduceMoney") > maxReduceProActivityForGoods.get("reduceMoney")) {
                        maxReduceProActivityForGoods = paramMap;
                    }
                }

                allProActivityId += maxReduceProActivityForGoods.get("proActivityId").toString() + ",";
                goodsInfoMap.put("theGoodsContainsSinglereDuPro", 1111111);
                totalReduceSplitPrice += maxReduceProActivityForGoods.get("reduceMoney");
            }
        }
        map.put("totalReduceSingleSplitPrice", totalReduceSplitPrice);
        map.put("allProActivityId", allProActivityId);
        map.put("proRuleDeductionPrice", proRuleDeductionPrice + totalReduceSplitPrice);

        return map;
    }

    //组合商品满减情况
    private Map<String, Object> reDuceSplitOrderForManyGoods (ProRuleMessageParam proRuleMessageParam, List<Map<String, Object>> list, Map<String, Object> map) {
        //对于组合商品满减来说 对于直减的活动只要有单个商品符合要求的  肯定是被过滤了  多以对于组合商品满减活动来说  只有满元减和每满减两种
        List<Map<String, Integer>> goodsInfo = proRuleMessageParam.getGoodsInfo();
        goodsInfo = goodsInfo.stream().filter(goodsMap -> goodsMap.get("theGoodsContainsSinglereDuPro") == null && goodsMap.get("theGoodsContainsControlPrice") == null).collect(Collectors.toList());

        Integer proRuleDeductionPrice = map.get("proRuleDeductionPrice") == null ? 0 : Integer.parseInt(map.get("proRuleDeductionPrice").toString());
        //对组合商品进行分组 以商品goodsid 逗号分割的字符串为key   对应商品信息组成集合列表 作为对应的 value 值
        List<Map<String, List<Map<String, Integer>>>> goodsGrouptoList = goodsListInfoGroup(goodsInfo);

        List<ProRuleActivitySplitOrderParam> proRuleActivitySplitOrderParams = new ArrayList<ProRuleActivitySplitOrderParam>();

        for (Map<String, Object> proActivityMap : list) {
            String promotionRule = proActivityMap.get("promotions_rule").toString();
            ReduceMoneyRule reduceMoneyRule = JSON.parseObject(promotionRule, ReduceMoneyRule.class);
            for (Map<String, List<Map<String, Integer>>> onegroupGoodsMap : goodsGrouptoList) {
                try {
                    //groupGoodsKey 对应组合商品逗号分隔的字符串 groupGoodsList 为groupGoodsKey对应的商品列表
                    String groupGoodsKey = null;
                    List<Map<String, Integer>> groupGoodsList = null;
                    ProRuleMessageParam proRuleMessageParamTime = new ProRuleMessageParam();
                    Iterator<Map.Entry<String, List<Map<String, Integer>>>> it = onegroupGoodsMap.entrySet().iterator();
                    String goodsIdsContainTheProActivity = "";
                    while (it.hasNext()) {
                        Map.Entry<String, List<Map<String, Integer>>> m = it.next();
                        groupGoodsKey = m.getKey().substring(0, m.getKey().length() - 1);
                        groupGoodsList = m.getValue();
                    }
                    proRuleMessageParamTime.setGoodsInfo(groupGoodsList);
                    proRuleMessageParamTime.setTotalPrice(proRuleMessageParam.getTotalPrice());
                    proRuleMessageParamTime.setCouponPrice(proRuleMessageParam.getCouponPrice());

                    Integer totalMoney = 0;

                    Integer discount = 100;
                    Integer goodsGroupYouhuiPrice = 0;

                    if (promotionsRuleService.checkReduceMoneyRule(proRuleMessageParamTime, promotionRule)) {
                        //每一组商品对应该活动的优惠金额的 数据map 以逗号间隔的商品字符串为key 优惠金额为value
                        Map<String, Integer> oneGroupYouhui = new HashMap<String, Integer>();

                        ProRuleActivitySplitOrderParam proRuleActivitySplitOrderParam = new ProRuleActivitySplitOrderParam();
                        proRuleActivitySplitOrderParam.setProActivityId(proActivityMap.get("proActivityId").toString());
                        proRuleActivitySplitOrderParam.setGroupGoodsList(groupGoodsList);
                        proRuleActivitySplitOrderParam.setGroupGoodsKey(groupGoodsKey);

                        if (reduceMoneyRule.getGoodsIdsType() == 0) {
                            totalMoney = proRuleMessageParamTime.getGoodsInfo()
                                    .stream()
                                    .mapToInt(pricemap -> pricemap.get("goodsPrice") * pricemap.get("num")).sum();
                            goodsIdsContainTheProActivity = proRuleMessageParamTime.getGoodsInfo().stream().map(paramMap -> paramMap.get("goodsId").toString()).collect(Collectors.joining(","));
                        } else if (reduceMoneyRule.getGoodsIdsType() == 1) {
                            totalMoney = proRuleMessageParamTime
                                    .getGoodsInfo()
                                    .stream()
                                    .filter(goodsIdMap -> new HashSet<String>(Arrays.asList(reduceMoneyRule.getGoodsIds().split(","))).contains(goodsIdMap.get("goodsId").toString()))
                                    .mapToInt(pricemap -> pricemap.get("goodsPrice") * pricemap.get("num")).sum();
                            goodsIdsContainTheProActivity = proRuleMessageParamTime.getGoodsInfo()
                                    .stream()
                                    .filter(goodsIdMap -> new HashSet<String>(Arrays.asList(reduceMoneyRule.getGoodsIds().split(","))).contains(goodsIdMap.get("goodsId").toString()))
                                    .map(paramMap -> paramMap.get("goodsId").toString()).collect(Collectors.joining(","));
                        } else if (reduceMoneyRule.getGoodsIdsType() == 2) {
                            totalMoney = proRuleMessageParamTime
                                    .getGoodsInfo()
                                    .stream()
                                    .filter(goodsIdMap -> !new HashSet<String>(Arrays.asList(reduceMoneyRule.getGoodsIds().split(","))).contains(goodsIdMap.get("goodsId").toString()))
                                    .mapToInt(pricemap -> pricemap.get("goodsPrice") * pricemap.get("num")).sum();
                            goodsIdsContainTheProActivity = proRuleMessageParamTime.getGoodsInfo()
                                    .stream()
                                    .filter(goodsIdMap -> !new HashSet<String>(Arrays.asList(reduceMoneyRule.getGoodsIds().split(","))).contains(goodsIdMap.get("goodsId").toString()))
                                    .map(paramMap -> paramMap.get("goodsId").toString()).collect(Collectors.joining(","));
                        }

                        List<ReduceMoneyRule.InnerRule> innerRules = reduceMoneyRule.getRules();
                        ReduceMoneyRule.InnerRule innerRule = null;

                        switch (reduceMoneyRule.getRuleType()) {
                            case 2:
                                innerRule = innerRules.get(0);
                                double grade = Math.floor(totalMoney / innerRule.getMeetMoney());
                                BigDecimal bigDecimal = new BigDecimal(grade * innerRule.getReduceMoney());
                                goodsGroupYouhuiPrice = (int) bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();

                                if (innerRule.getCap() != 0 && goodsGroupYouhuiPrice > innerRule.getCap()) {
                                    goodsGroupYouhuiPrice = innerRule.getCap();
                                }
                                break;
                            case 3:
                                for (ReduceMoneyRule.InnerRule ruleInner : innerRules) {
                                    if (totalMoney >= ruleInner.getMeetMoney()) {
                                        innerRule = ruleInner;
                                    }
                                }
                                goodsGroupYouhuiPrice = innerRule.getReduceMoney();
                                break;
                        }
                        oneGroupYouhui.put(groupGoodsKey, goodsGroupYouhuiPrice);
                        proRuleActivitySplitOrderParam.setGroupyGoodsSplitOrderMap(oneGroupYouhui);
                        proRuleActivitySplitOrderParam.setDiscount(discount);
                        proRuleActivitySplitOrderParam.setReduceMoneyFromDisCount(goodsGroupYouhuiPrice);
                        proRuleActivitySplitOrderParam.setReduceMoneyRule(reduceMoneyRule);
                        proRuleActivitySplitOrderParam.setGoodsIdsContainTheProActivity(goodsIdsContainTheProActivity);

                        boolean ifCanadd = proRuleActivitySplitOrderParams.stream().allMatch(onePojo -> !StringUtil.equalsIgnoreCase(onePojo.getGroupGoodsKey(), proRuleActivitySplitOrderParam.getGroupGoodsKey()));
                        if (ifCanadd) {
                            proRuleActivitySplitOrderParams.add(proRuleActivitySplitOrderParam);
                        } else {
                            ProRuleActivitySplitOrderParam theSameGroupGoods = proRuleActivitySplitOrderParams.stream().filter(activityPro -> StringUtil.equalsIgnoreCase(activityPro.getGroupGoodsKey(), proRuleActivitySplitOrderParam.getGroupGoodsKey())).collect(Collectors.toList()).get(0);
                            if (theSameGroupGoods.getReduceMoneyFromDisCount() > proRuleActivitySplitOrderParam.getReduceMoneyFromDisCount()) {
                                proRuleActivitySplitOrderParams.remove(theSameGroupGoods);
                                proRuleActivitySplitOrderParams.add(theSameGroupGoods);
                            }
                        }

                    }

                } catch (Exception e) {
                    logger.info("组合商品处理打折活动出现异常");
                    continue;//一组商品出现异常不影响其它组商品参与活动
                }
            }
        }

        List<ProRuleActivitySplitOrderParam> resultList = toSureGroupGoodsProActivity(proRuleActivitySplitOrderParams, proRuleMessageParam, "reduce");
        String allProActivityId = map.get("allProActivityId") == null ? "" : map.get("allProActivityId").toString();
        String groupGoodsForProActivityId = resultList.stream().map(map4 -> map4.getProActivityId()).collect(Collectors.joining(","));
        Integer groupGoodsReduceProTotalPrice = resultList.stream().mapToInt(obj5 -> obj5.getReduceMoneyFromDisCount()).sum();

        allProActivityId = allProActivityId + groupGoodsForProActivityId + ",";

        map.put("groupGoodsReduceProTotalPrice", groupGoodsReduceProTotalPrice);
        map.put("allProActivityId", allProActivityId);
        map.put("proRuleDeductionPrice", proRuleDeductionPrice + groupGoodsReduceProTotalPrice);

        return map;
    }

    //直减活动入口
    private List<Map<String, Integer>> directReduceSplitList (ReduceMoneyRule reduceMoneyRule, Map<String, Integer> goodsInfoMap, Map<String, Object> proMap, List<Map<String, Integer>> reduceMoneyProActivityList) {
        try {
            Integer directreDuceMoney = reduceMoneyRule.getRules().get(0).getReduceMoney();
            Map<String, Integer> reduceProMap = null;
            if (reduceMoneyRule.getGoodsIdsType() == 0) {
                if (null == goodsInfoMap.get("theGoodsContainsControlPrice")) {//参加过限价活动的商品不能参加满减活动
                    reduceProMap = directReduceSplit(goodsInfoMap, proMap, directreDuceMoney);
                    if (CollectionUtils.isNotEmpty(reduceProMap.keySet()))
                        reduceMoneyProActivityList.add(reduceProMap);
                }
            } else if (reduceMoneyRule.getGoodsIdsType() == 1) {
                if (null == goodsInfoMap.get("theGoodsContainsControlPrice")
                        && new HashSet<String>(Arrays.asList(reduceMoneyRule.getGoodsIds().split(",")))
                        .contains(goodsInfoMap.get("goodsId").toString())) {
                    reduceProMap = directReduceSplit(goodsInfoMap, proMap, directreDuceMoney);
                    if (CollectionUtils.isNotEmpty(reduceProMap.keySet()))
                        reduceMoneyProActivityList.add(reduceProMap);
                }
            } else if (reduceMoneyRule.getGoodsIdsType() == 2) {
                if (null == goodsInfoMap.get("theGoodsContainsControlPrice")
                        && !new HashSet<String>(Arrays.asList(reduceMoneyRule.getGoodsIds().split(",")))
                        .contains(goodsInfoMap.get("goodsId").toString())) {
                    reduceProMap = directReduceSplit(goodsInfoMap, proMap, directreDuceMoney);
                    if (CollectionUtils.isNotEmpty(reduceProMap.keySet()))
                        reduceMoneyProActivityList.add(reduceProMap);
                }

            }
            return reduceMoneyProActivityList;
        } catch (Exception e) {
            return reduceMoneyProActivityList;
        }
    }

    //直减拆单参数封装
    private Map<String, Integer> directReduceSplit (Map<String, Integer> goodsInfoMap, Map<String, Object> proActivityMap, Integer directreDuceMoney) {
        Map<String, Integer> resultMap = new HashMap<String, Integer>();
        try {
            if (goodsInfoMap.get("num") * goodsInfoMap.get("goodsPrice") > directreDuceMoney)
                resultMap.put("reduceMoney", directreDuceMoney);
            else
                resultMap.put("reduceMoney", goodsInfoMap.get("num") * goodsInfoMap.get("goodsPrice"));
            resultMap.put("proActivityId", Integer.parseInt(proActivityMap.get("proActivityId").toString()));
            return resultMap;
        } catch (Exception e) {
            return null;
        }
    }

    //每满减
    private List<Map<String, Integer>> containsPerMoneySplitList (ReduceMoneyRule reduceMoneyRule, Map<String, Integer> goodsInfoMap, Map<String, Object> proMap, List<Map<String, Integer>> reduceMoneyProActivityList) {
        try {
            Integer meetMoney = reduceMoneyRule.getRules().get(0).getMeetMoney();
            Integer reduceMoney = reduceMoneyRule.getRules().get(0).getReduceMoney();
            Integer reduceMoneyLimit = reduceMoneyRule.getRules().get(0).getCap();
            Map<String, Integer> reduceProMap = null;
            if (reduceMoneyRule.getGoodsIdsType() == 0) {
                if (null == goodsInfoMap.get("theGoodsContainsControlPrice")
                        && goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num") >= meetMoney) {
                    reduceProMap = containsPerMoneySplitOrder(goodsInfoMap, proMap, meetMoney, reduceMoney, reduceMoneyLimit);
                    if (CollectionUtils.isNotEmpty(reduceProMap.keySet()))
                        reduceMoneyProActivityList.add(reduceProMap);
                }
            } else if (reduceMoneyRule.getGoodsIdsType() == 1) {
                if (null == goodsInfoMap.get("theGoodsContainsControlPrice")
                        && goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num") >= meetMoney
                        && new HashSet<String>(Arrays.asList(reduceMoneyRule.getGoodsIds().split(","))).contains(goodsInfoMap.get("goodsId").toString())
                        ) {
                    reduceProMap = containsPerMoneySplitOrder(goodsInfoMap, proMap, meetMoney, reduceMoney, reduceMoneyLimit);
                    if (CollectionUtils.isNotEmpty(reduceProMap.keySet()))
                        reduceMoneyProActivityList.add(reduceProMap);
                }
            } else if (reduceMoneyRule.getGoodsIdsType() == 2) {
                if (null == goodsInfoMap.get("theGoodsContainsControlPrice")
                        && goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num") >= meetMoney
                        && !new HashSet<String>(Arrays.asList(reduceMoneyRule.getGoodsIds().split(","))).contains(goodsInfoMap.get("goodsId").toString())) {
                    reduceProMap = containsPerMoneySplitOrder(goodsInfoMap, proMap, meetMoney, reduceMoney, reduceMoneyLimit);
                    if (CollectionUtils.isNotEmpty(reduceProMap.keySet()))
                        reduceMoneyProActivityList.add(reduceProMap);
                }
            }
            return reduceMoneyProActivityList;
        } catch (Exception e) {
            return reduceMoneyProActivityList;
        }
    }


    //每满减活动信息的组装
    private Map<String, Integer> containsPerMoneySplitOrder (Map<String, Integer> goodsInfoMap, Map<String, Object> proActivityMap, Integer meetMoney, Integer reduceMoney, Integer reduceMoneyLimit) {
        Map<String, Integer> resultMap = new HashMap<String, Integer>();
        try {
            Integer totalGoodsPrice = goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num");
            int reduceNum = totalGoodsPrice / meetMoney;
            resultMap = new HashMap<String, Integer>();
            resultMap.put("reduceMoney", reduceNum * reduceMoney > reduceMoneyLimit ? reduceMoneyLimit : reduceNum * reduceMoney);
            resultMap.put("proActivityId", Integer.parseInt(proActivityMap.get("proActivityId").toString()));
            return resultMap;
        } catch (Exception e) {
            return null;
        }
    }

    //满减
    private List<Map<String, Integer>> containsMoneySplitOrderList (ReduceMoneyRule reduceMoneyRule, Map<String, Integer> goodsInfoMap, Map<String, Object> proMap, List<Map<String, Integer>> reduceMoneyProActivityList) {
        try {
            Map<String, Integer> reduceMap;
            if (reduceMoneyRule.getGoodsIdsType() == 0) {
                List<ReduceMoneyRule.InnerRule> innerRules = reduceMoneyRule.getRules();
                List<Integer> reduceMoneyList = new ArrayList<Integer>();
                for (ReduceMoneyRule.InnerRule innerRule : innerRules) {
                    if (goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num") >= innerRule.getMeetMoney())
                        reduceMoneyList.add(innerRule.getReduceMoney() > goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num") ? goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num") : innerRule.getReduceMoney());
                }
                if (CollectionUtils.isNotEmpty(reduceMoneyList)) {
                    reduceMap = new HashMap<String, Integer>();
                    reduceMap.put("reduceMoney", Collections.max(reduceMoneyList));
                    reduceMap.put("proActivityId", Integer.parseInt(proMap.get("proActivityId").toString()));
                    reduceMoneyProActivityList.add(reduceMap);
                }
            } else if (reduceMoneyRule.getGoodsIdsType() == 1) {
                if (new HashSet<String>(Arrays.asList(reduceMoneyRule.getGoodsIds().split(","))).contains(goodsInfoMap.get("goodsId").toString())) {
                    List<ReduceMoneyRule.InnerRule> innerRules = reduceMoneyRule.getRules();
                    List<Integer> reduceMoneyList = new ArrayList<Integer>();
                    for (ReduceMoneyRule.InnerRule innerRule : innerRules) {
                        if (goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num") >= innerRule.getMeetMoney())
                            reduceMoneyList.add(innerRule.getReduceMoney() > goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num") ? goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num") : innerRule.getReduceMoney());
                    }
                    if (CollectionUtils.isNotEmpty(reduceMoneyList)) {
                        reduceMap = new HashMap<String, Integer>();
                        reduceMap.put("proActivityId", Integer.parseInt(proMap.get("proActivityId").toString()));
                        reduceMap.put("reduceMoney", Collections.max(reduceMoneyList));
                        reduceMoneyProActivityList.add(reduceMap);
                    }
                }

            } else if (reduceMoneyRule.getGoodsIdsType() == 2) {
                if (!new HashSet<String>(Arrays.asList(reduceMoneyRule.getGoodsIds().split(","))).contains(goodsInfoMap.get("goodsId").toString())) {
                    List<ReduceMoneyRule.InnerRule> innerRules = reduceMoneyRule.getRules();
                    List<Integer> reduceMoneyList = new ArrayList<Integer>();
                    for (ReduceMoneyRule.InnerRule innerRule : innerRules) {
                        if (goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num") >= innerRule.getMeetMoney())
                            reduceMoneyList.add(innerRule.getReduceMoney() > goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num") ? goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num") : innerRule.getReduceMoney());
                    }
                    if (CollectionUtils.isNotEmpty(reduceMoneyList)) {
                        reduceMap = new HashMap<String, Integer>();
                        reduceMap.put("reduceMoney", Collections.max(reduceMoneyList));
                        reduceMap.put("proActivityId", Integer.parseInt(proMap.get("proActivityId").toString()));
                        reduceMoneyProActivityList.add(reduceMap);
                    }
                }

            }

        } catch (Exception e) {
            return reduceMoneyProActivityList;
        }
        return reduceMoneyProActivityList;
    }


    //满赠拆单
    private Map<String, Object> giftSplitOrder (ProRuleMessageParam proRuleMessageParam, List<Map<String, Object>> list, Map<String, Object> map) {
        //对于赠品拆单 只按照最近一次的满赠活动计算
        list = list.stream().filter(map1 -> promotionsRuleService.checkProRuleForPromotionsRule(proRuleMessageParam, map1)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list))
            return map;
        List<Map<String, Integer>> goodsInfo = proRuleMessageParam.getGoodsInfo();
        String allProActivityId = map.get("allProActivityId") == null ? "" : map.get("allProActivityId").toString();
        ProRuleMessageParam proRuleMessageParamForProActivity = null;//用于单个商品判别是否满足活动的参数封装
        List<Map<String, Integer>> goodsParam = null;//单独的商品存放于该集合中 判断满赠活动的条件
        for (Map<String, Integer> goodsInfoMap : goodsInfo) {
            proRuleMessageParamForProActivity = proRuleMessageParam;
            goodsParam = new ArrayList<Map<String, Integer>>();
            goodsParam.add(goodsInfoMap);
            proRuleMessageParamForProActivity.setGoodsInfo(goodsParam);

            for (Map<String, Object> giftproMap : list) {
                try {
                    String giftproString = giftproMap.get("promotions_rule").toString();
                    if (promotionsRuleService.checkGiftRule(proRuleMessageParamForProActivity, giftproString)) {
                        GiftRule giftRule = JSON.parseObject(giftproString, GiftRule.class);

                    }

                } catch (Exception e) {
                    logger.info("满赠商品单独拆单出现异常", e);
                    break;//一个活动的异常不影响其它活动的使用
                }
            }
        }
        return null;
    }

    //包邮拆单
    private Map<String, Object> freePostSplitOrder (ProRuleMessageParam proRuleMessageParam, List<Map<String, Object>> list, Map<String, Object> map) {
        try {


            list = list.stream().filter(map1 -> promotionsRuleService.checkProRuleForPromotionsRule(proRuleMessageParam, map1)).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(list))
                return map;
            Integer freePostPrice = 0;
            String allProActivityId = map.get("allProActivityId") == null ? "" : map.get("allProActivityId").toString();
            Integer proRuleDeductionPrice = map.get("proRuleDeductionPrice") == null ? 0 : Integer.parseInt(map.get("proRuleDeductionPrice").toString());
            OrderDeductionDto orderDeductionDto = new OrderDeductionDto(proRuleMessageParam, list);
            List<UsePromotionsParams> freepostlist = orderDeductionService.countDeduction(orderDeductionDto);
            if (freepostlist.isEmpty())
                return map;
            else {
                freePostPrice = freepostlist.get(0).getDeductionMoney();
                allProActivityId += freepostlist.get(0).getPromActivityId() + ",";
                map.put("allProActivityId", allProActivityId);
                map.put("freePostPrice", freePostPrice);
                map.put("proRuleDeductionPrice", proRuleDeductionPrice + freePostPrice);
            }
            return map;
        } catch (Exception e) {
            logger.info("拆单包邮出现异常", e);
            return map;
        }
    }


    static class ProActivityComparatorDesc implements Comparator<ProRuleActivitySplitOrderParam> {
        @Override
        public int compare (ProRuleActivitySplitOrderParam param1, ProRuleActivitySplitOrderParam param2) {
            Integer v1 = param1.getReduceMoneyFromDisCount();
            Integer v2 = param2.getReduceMoneyFromDisCount();
            Integer v3 = param1.getGroupGoodsKey().split(",").length;
            Integer v4 = param2.getGroupGoodsKey().split(",").length;

            if (v1.intValue() != v2.intValue()) {
                return v2.compareTo(v1);
            } else {
                return v3.compareTo(v4);
            }

        }

    }
}
