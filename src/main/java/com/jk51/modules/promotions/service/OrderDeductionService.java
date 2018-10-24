package com.jk51.modules.promotions.service;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.order.BeforeCreateOrderReq;
import com.jk51.model.order.GoodsInfo;
import com.jk51.model.order.OrderGoods;
import com.jk51.model.order.response.UsePromotionsParams;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.DiscountRule;
import com.jk51.model.promotions.rule.FixedPriceRule;
import com.jk51.model.promotions.rule.FreePostageRule;
import com.jk51.model.promotions.rule.ReduceMoneyRule;
import com.jk51.modules.promotions.constants.PromotionsConstant;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.request.OrderDeductionDto;
import com.jk51.modules.promotions.utils.OrderDeductionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * filename :com.jk51.modules.promotions.service.
 * author   :zw
 * date     :2017/8/10
 * Update   :
 */
@Service
public class OrderDeductionService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PromotionsRuleMapper promotionsRuleMapper;
    @Autowired
    private OrderDeductionUtils orderDeductionUtils;


    /**
     * 预下单使用，入参活动ids，出参抵扣金额
     *
     * @param ids
     * @return
     */
    public int deductionMoneyByActivityIds(String ids, BeforeCreateOrderReq req, int postFee, int orderFee, int areaId, List<GoodsInfo> goodsInfoInfos) {
        String[] strArray = ids.split(",");
        Map map = new HashMap();
        int z = 0;
        for (String i : strArray) {
            map.put(z, i);
            z++;
        }
        OrderDeductionDto orderDeductionDto = new OrderDeductionDto();
        orderDeductionDto.setSiteId(req.getSiteId());
        orderDeductionDto.setStoreId(req.getStoresId());
        orderDeductionDto.setUserId(req.getBuyerId());
        orderDeductionDto.setAreaId(areaId);
        orderDeductionDto.setPromotionsIdsMap(map);
        orderDeductionDto.setPostFee(postFee);
        orderDeductionDto.setOrderFee(orderFee);
        List<OrderGoods> orderGoodsList = req.getOrderGoods();
        List<Map<String, Integer>> goodsList = new ArrayList<Map<String, Integer>>();
        for (GoodsInfo goodsInfo : goodsInfoInfos) {
            for (OrderGoods orderGoods : orderGoodsList) {
                if (goodsInfo.getGoodsId() == orderGoods.getGoodsId()) {
                    Map<String, Integer> goodsMap = new HashMap<>();
                    goodsMap.put("goodsId", orderGoods.getGoodsId());
                    goodsMap.put("num", orderGoods.getGoodsNum());
                    goodsMap.put("goodsPrice", goodsInfo.getShopPrice());
                    goodsList.add(goodsMap);
                }
            }

        }
        orderDeductionDto.setGoodsInfo(goodsList);
        Integer countMoney = 0;
        try {
            List<UsePromotionsParams> list = countDeduction(orderDeductionDto);
            for (UsePromotionsParams upp : list) {
                countMoney += upp.getDeductionMoney();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countMoney;
    }

    /**
     * 抵扣入口
     *
     * @param orderDeductionDto
     * @return
     */
    public List<UsePromotionsParams> countDeduction(OrderDeductionDto orderDeductionDto) throws Exception {
        List<PromotionsRule> promotionsRuleList = promotionsRuleMapper.getPromotionsRuleByIdsAndSiteId(
            orderDeductionDto.getSiteId(), orderDeductionDto.getPromotionsIdsMap()).parallelStream()
            .collect(Collectors.toList());

        List<PromotionsRule> discountList = new ArrayList<>(); // 打折
        List<PromotionsRule> freePostList = new ArrayList<>(); // 免邮
        List<PromotionsRule> giftList = new ArrayList<>(); // 满赠
        List<PromotionsRule> moneyOffList = new ArrayList<>(); // 满减
        List<PromotionsRule> limitPriceList = new ArrayList<>(); // 限价

        for (PromotionsRule promotionsRule : promotionsRuleList) {
            switch (promotionsRule.getPromotionsType()) {
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_GIFT:
                    giftList.add(promotionsRule);
                    break;
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_DISCOUNT:
                    discountList.add(promotionsRule);
                    break;
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_FREE_POST:
                    freePostList.add(promotionsRule);
                    break;
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_MONEY_OFF:
                    moneyOffList.add(promotionsRule);
                    break;
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                    limitPriceList.add(promotionsRule);
                    break;
                default:
                    logger.error("OrderDeductionService 未知活动类型{}", orderDeductionDto.toString());
                    break;
            }
        }

        List<UsePromotionsParams> promotionsParamsList = new ArrayList<>();
        List<UsePromotionsParams> reduceParamsList = new ArrayList<>();
        if (!discountList.isEmpty()) {
            reduceParamsList.add(selectMaxActivity(discountList, orderDeductionDto, PromotionsConstant.PROMOTIONS_RULE_TYPE_DISCOUNT));
        }
        if (!freePostList.isEmpty()) {
            promotionsParamsList.add(freePostCount(freePostList, orderDeductionDto));
        }
        if (!giftList.isEmpty()) {
            promotionsParamsList.add(meetSendCount(giftList));
        }

        if (!moneyOffList.isEmpty()) {
            reduceParamsList.add(selectMaxActivity(moneyOffList, orderDeductionDto, PromotionsConstant.PROMOTIONS_RULE_TYPE_MONEY_OFF));
        }
        if (!limitPriceList.isEmpty()) {
            reduceParamsList.add(selectMaxActivity(limitPriceList, orderDeductionDto, PromotionsConstant.PROMOTIONS_RULE_TYPE_LIMIT_PRICE));
        }
        //满减 限价 满折 三取一
        if (!CollectionUtils.isEmpty(reduceParamsList)) {
            promotionsParamsList.add(discountCount(reduceParamsList));
        }
        discountCount(promotionsParamsList);
        return promotionsParamsList;
    }


    /**
     * 查出最优活动，并计算抵扣金额
     *
     * @param prList
     * @param orderDeductionDto
     * @return
     */
    private UsePromotionsParams selectMaxActivity(List<PromotionsRule> prList, OrderDeductionDto orderDeductionDto,
                                                  Integer activityType) {
        List<UsePromotionsParams> resultMap = new ArrayList<>();
        prList.stream().forEach(rule -> {

            UsePromotionsParams usePromotionsParams = null;
            Integer goodsIdsType = 0;
            String goodsIds = "";
            Map<String, Object> excuseGoods = new HashedMap();
            switch (activityType) {
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_DISCOUNT:
                    DiscountRule discountRule = JSON.parseObject(rule.getPromotionsRule(), DiscountRule.class);
                    goodsIdsType = discountRule.getGoodsIdsType() == null ? 0 : discountRule.getGoodsIdsType();
                    goodsIds = discountRule.getGoodsIds();
                    excuseGoods = orderDeductionUtils.excuseGoodsInfo(goodsIdsType, goodsIds,
                        orderDeductionDto);
                    usePromotionsParams = discountDetail(rule, excuseGoods, orderDeductionDto);
                    break;
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_MONEY_OFF:
                    ReduceMoneyRule reduceMoneyRule = JSON.parseObject(rule.getPromotionsRule(), ReduceMoneyRule.class);
                    goodsIdsType = reduceMoneyRule.getGoodsIdsType() == null ? 0 : reduceMoneyRule.getGoodsIdsType();
                    goodsIds = reduceMoneyRule.getGoodsIds();
                    excuseGoods = orderDeductionUtils.excuseGoodsInfo(goodsIdsType, goodsIds,
                        orderDeductionDto);
                    usePromotionsParams = moneyOffDetail(rule, excuseGoods);
                    break;
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                    FixedPriceRule fixedPriceRule = JSON.parseObject(rule.getPromotionsRule(), FixedPriceRule.class);
                    goodsIdsType = fixedPriceRule.getGoodsIdsType() == null ? 0 : fixedPriceRule.getGoodsIdsType();
                    goodsIds = fixedPriceRule.getGoodsIds();
                    excuseGoods = orderDeductionUtils.excuseGoodsInfo(goodsIdsType, goodsIds,
                        orderDeductionDto);
                    usePromotionsParams = checkPriceDetail(rule, excuseGoods);
                    break;
                default:
                    logger.error("没有找到相对应的类型");
                    break;

            }
            if (usePromotionsParams != null)
                resultMap.add(usePromotionsParams);
        });
        return discountCount(resultMap);
    }

    /**
     * list中选出优惠最大的一个
     *
     * @param promotionsParamsList
     * @return
     * @throws Exception
     */
    private UsePromotionsParams discountCount(List<UsePromotionsParams> promotionsParamsList) {
        //选出最大的折扣活动
        UsePromotionsParams usePromotionsParams = null;
        for (UsePromotionsParams map : promotionsParamsList) {
            Integer accountAmount = map.getDeductionMoney();
            if (usePromotionsParams != null && usePromotionsParams.getDeductionMoney() < accountAmount || usePromotionsParams == null) {
                usePromotionsParams = map;
            }
        }
        return usePromotionsParams;
    }


    /**
     * 折扣活动计算
     *
     * @param rule
     * @param excuseGoods
     * @param orderDeductionDto
     * @return
     */
    private UsePromotionsParams discountDetail(PromotionsRule rule, Map<String, Object> excuseGoods,
                                               OrderDeductionDto orderDeductionDto) {
        DiscountRule discountRule = new DiscountRule();
        try {
            discountRule = JSON.parseObject(rule.getPromotionsRule(), DiscountRule.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderDeductionService DiscountRule类型转换错误 ");
        }
        if (null == discountRule) {
            return null;
        }
        Integer priceTotal = Integer.valueOf(excuseGoods.get("priceTotal").toString());
        Integer goodsNum = Integer.valueOf(excuseGoods.get("goodsNum").toString());

        List<Map<String, String>> mapListGoodsInfo = orderDeductionUtils.String2List(excuseGoods.get("mapListGoodsInfo"));
        if (mapListGoodsInfo == null || mapListGoodsInfo.isEmpty()) {
            return null;
        }
        // 是否包邮逻辑去掉了,这块代码为了兼容老数据
        if (null != discountRule.getIsPost() && discountRule.getIsPost() == 2) {
            priceTotal += orderDeductionDto.getPostFee();
        }
        Integer is_ml = discountRule.getIsMl();
        Integer is_round = discountRule.getIsRound();
        Integer discount_money = 0;

        Map<String, Integer> stringIntegerMap = discountRule.getRules().get(0);
        List<Map<String, Integer>> maps = discountRule.getRules();
        Map<String, Integer> reachGrade = new HashMap<>();
        Integer goods_money_limit = null;
        switch (discountRule.getRuleType()) {
            case 1: //直接折扣
                goods_money_limit = stringIntegerMap.get("goods_money_limit");
                discount_money = orderDeductionUtils.discountMoney(
                    priceTotal, is_ml, is_round, stringIntegerMap.get("direct_discount")
                );

                if (goods_money_limit != 0 && discount_money > goods_money_limit) {
                    discount_money = goods_money_limit;
                }
                return assemblyBin(discount_money, rule);
            case 2: //满元折扣
                for (Map<String, Integer> stringMap : maps) {
                    Integer meet_money = stringMap.get("meet_money") == null ? 0 : stringMap.get("meet_money");
                    if (meet_money > priceTotal) {
                        break;
                    }
                    reachGrade = stringMap;
                }
                discount_money = orderDeductionUtils.discountMoney(priceTotal, is_ml, is_round,
                    reachGrade.get("discount"));
                return assemblyBin(discount_money, rule);
            case 3://满件折扣
                for (Map<String, Integer> stringMap : maps) {
                    Integer meet_num = stringMap.get("meet_num") == null ? 0 : stringMap.get("meet_num");
                    if (meet_num > goodsNum) {
                        break;
                    }
                    reachGrade = stringMap;
                }
                discount_money = orderDeductionUtils.discountMoney(priceTotal, is_ml, is_round,
                    reachGrade.get("discount"));
                return assemblyBin(discount_money, rule);
            case 4://第几件折扣
                for (Map<String, String> goodsMap : mapListGoodsInfo) {
                    if (stringIntegerMap.get("rate") <= Integer.parseInt(goodsMap.get("num"))) {
                        int max_buy_num = discountRule.getRules().get(0).get("goods_amount_limit") == null ? 0 : Integer.parseInt(discountRule.getRules().get(0).get("goods_amount_limit").toString());//最大购买数
                        int discount_num = (int) Math.floor(Integer.parseInt(goodsMap.get("num")) / stringIntegerMap.get("rate"));
                        if (max_buy_num > 0 && discount_num > max_buy_num) {
                            discount_num = max_buy_num;
                        }
                        discount_money += orderDeductionUtils.discountMoney(
                            Integer.parseInt(goodsMap.get("goodsPrice")) * discount_num, is_ml, is_round,
                            stringIntegerMap.get("discount")
                        );
                    }
                }
                return assemblyBin(discount_money, rule);
            case 5:
                discount_money = orderDeductionUtils.discountMoneyForDifferentGoods(
                    priceTotal, is_ml, is_round, mapListGoodsInfo
                    , maps);
                return assemblyBin(discount_money, rule);
            default:
                logger.error("没有找到相对应的类型");

        }
        return null;
    }

    /**
     * 满减活动
     *
     * @param rule
     * @param excuseGoods
     * @return
     */
    private UsePromotionsParams moneyOffDetail(PromotionsRule rule, Map<String, Object> excuseGoods) {
        ReduceMoneyRule reduceMoneyRule = null;
        try {
            reduceMoneyRule = JSON.parseObject(rule.getPromotionsRule(), ReduceMoneyRule.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderDeductionService reduceMoneyRule类型转换错误 ");
        }
        if (null == reduceMoneyRule) {
            return null;
        }
        Integer priceTotal = Integer.valueOf(excuseGoods.get("priceTotal").toString());

        List<Map<String, String>> mapListGoodsInfo = orderDeductionUtils.String2List(excuseGoods.get("mapListGoodsInfo"));
        if (mapListGoodsInfo == null || mapListGoodsInfo.isEmpty()) {
            return null;
        }
        List<ReduceMoneyRule.InnerRule> innerRules = reduceMoneyRule.getRules();
        ReduceMoneyRule.InnerRule innerRule = innerRules.get(0);
        Integer reduceMoney = 0;
        switch (reduceMoneyRule.getRuleType()) {
            case 1: //立减多少元
                reduceMoney = innerRule.getReduceMoney();
                return assemblyBin(reduceMoney, rule);
            case 2: //每满多少元减多少元，可设置封顶
                double grade = Math.floor(priceTotal / innerRule.getMeetMoney());
                BigDecimal bigDecimal = new BigDecimal(grade * innerRule.getReduceMoney());
                reduceMoney = (int) bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();

                if (innerRule.getCap() != 0 && reduceMoney > innerRule.getCap()) {
                    reduceMoney = innerRule.getCap();
                }
                return assemblyBin(reduceMoney, rule);
            case 3: //满多少元减多少元（多少级阶梯可自定义）
                for (ReduceMoneyRule.InnerRule ruleInner : innerRules) {
                    if (priceTotal > ruleInner.getMeetMoney()) {
                        innerRule = ruleInner;
                    }
                }
                return assemblyBin(innerRule.getReduceMoney(), rule);
            default:
                logger.error("没有找到相对应的类型");
        }
        return null;

    }

    /**
     * 限价活动
     *
     * @param rule
     * @param excuseGoods
     * @return
     */
    private UsePromotionsParams checkPriceDetail(PromotionsRule rule, Map<String, Object> excuseGoods) {
        FixedPriceRule fixedPriceRule = null;
        try {
            fixedPriceRule = JSON.parseObject(rule.getPromotionsRule(), FixedPriceRule.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderDeductionService fixedPriceRule类型转换错误 ");
        }
        if (null == fixedPriceRule) {
            return null;
        }
        Integer priceTotal = Integer.valueOf(excuseGoods.get("priceTotal").toString());
        Integer goodsNum = Integer.valueOf(excuseGoods.get("goodsNum").toString());
        Integer reduceMoney = 0;
        switch (fixedPriceRule.getRuleType()) {
            case 1:
                Integer fixedPrice = fixedPriceRule.getFixedPrice();
                reduceMoney = priceTotal - fixedPrice * goodsNum;
                break;
            case 2: // 每个商品有自己的限价
                Integer checkPrice = 0;
                List<Map<String, String>> mapListGoodsInfo = orderDeductionUtils.String2List(excuseGoods.get("mapListGoodsInfo"));
                List<Map<String, Integer>> goodsCheckPrice = fixedPriceRule.getRules();
                if (mapListGoodsInfo == null || mapListGoodsInfo.isEmpty()) {
                    return null;
                }
                for (Map<String, String> goodsMap : mapListGoodsInfo) {
                    for (Map<String, Integer> checkPriceMap : goodsCheckPrice) {
                        if (Integer.parseInt(goodsMap.get("goodsId")) == checkPriceMap.get("goodsId").intValue()) {
                            checkPrice += checkPriceMap.get("fixedPrice") * Integer.parseInt(goodsMap.get("num"));
                        }
                    }
                }
                reduceMoney = priceTotal - checkPrice;
                break;
            default:
                logger.error("OrderDeductionService 未知类型 ");
        }
        if (reduceMoney <= 0) {
            reduceMoney = 0;
        }
        return assemblyBin(reduceMoney, rule);
    }

    /**
     * 组装bin
     *
     * @param discount_money
     * @param rule
     * @return
     */
    private UsePromotionsParams assemblyBin(Integer discount_money, PromotionsRule rule) {
        UsePromotionsParams usePromotionsParams = new UsePromotionsParams();
        usePromotionsParams.setPromotionsId(rule.getId());
        usePromotionsParams.setDeductionMoney(discount_money);
        usePromotionsParams.setPromotionsName(rule.getPromotionsName());
        usePromotionsParams.setPromotionsType(rule.getPromotionsType());
        usePromotionsParams.setPromActivityId(rule.getActivityId());
        return usePromotionsParams;
    }


    /**
     * 包邮活动 选择第一个活动
     *
     * @param freePost
     * @param orderDeductionDto
     * @return
     */
    private UsePromotionsParams freePostCount(List<PromotionsRule> freePost, OrderDeductionDto orderDeductionDto) throws Exception {
        PromotionsRule promotionsRule = freePost.get(0);
        FreePostageRule freePostageRuleParam = JacksonUtils.json2pojo(promotionsRule.getPromotionsRule(), FreePostageRule.class);
        Integer postFee = orderDeductionDto.getPostFee();
        postFee = freePostageRuleParam.getReducePostageLimit() < postFee ? freePostageRuleParam.getReducePostageLimit() : postFee;
        return assemblyBin(postFee, promotionsRule);
    }

    /**
     * 满赠活动  选最近的一个活动
     *
     * @param meetSendList
     * @return
     */
    private UsePromotionsParams meetSendCount(List<PromotionsRule> meetSendList) throws Exception {
        PromotionsRule promotionsRule = meetSendList.get(0);
        return assemblyBin(0, promotionsRule);
    }


}
