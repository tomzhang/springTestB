package com.jk51.modules.promotions.utils;

import com.gexin.fastjson.JSON;
import com.jk51.exception.UnknownTypeException;
import com.jk51.model.Stores;
import com.jk51.model.order.Member;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.*;
import com.jk51.modules.coupon.service.CouponActiveForMemberService;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.promotions.constants.PromotionsConstant;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import com.jk51.modules.promotions.utils.PromotionsDecorator.GoodsData;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.*;

import static com.jk51.modules.promotions.constants.PromotionsConstant.*;
import static java.util.stream.Collectors.toList;

/**
 * Created by Administrator on 2017/12/27.
 * 活动条件校验过滤
 */
@Component
public class PromotionsConditionChecker {

    @Autowired
    private StoresService storesService;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private PromotionsRuleService promotionsRuleService;
    @Autowired
    private CouponActiveForMemberService couponActiveForMemberService;
    //是否首单的过滤
    public boolean checkFirstOrder(PromotionsDecorator.Param param) {
        Member member = memberMapper.getMemberByMemberId(param.getSiteId(), param.getMemberId());
        //是否有下订单
        int count = tradesMapper.queryUserPromotionsFirstOrder(param.getSiteId(), member.getBuyerId());
        //是否有退款记录
        int count1 = tradesMapper.queryUserPromotionsIsRefundOrder(param.getSiteId(), member.getBuyerId());
        if(count<1&&count1==0){
            return true;
        }
        return false;
    }
    //有效期的过滤
    public boolean checkPeriodOfValidity(PromotionsDecorator promotionsDecorator) {
        if (!promotionsRuleService.checkproRuleTimeRule(promotionsDecorator.getPromotionsRule().getTimeRule()))
            return false;
        return true;
    }
    //订单类型的过滤
    public boolean checkOrderType(PromotionsDecorator.Param param, PromotionsDecorator promotionsDecorator) {
        //校验订单类型
        String[] orderTytpes = promotionsDecorator.getPromotionsRule().getOrderType().split(",");
        Set<String> set = new HashSet<String>(Arrays.asList(orderTytpes));
        if (null == param.getOrderType())
            return false;
        else if (!set.contains(param.getOrderType().toString()))
            return false;
        return true;
    }
    //适用门店的过滤
    public boolean checkStore(PromotionsDecorator.Param param, PromotionsDecorator promotionsDecorator) {
        if (!"-1".equals(promotionsDecorator.getPromotionsRule().getUseStore()) && Integer.valueOf(0).equals(param.getStoreId())) {
            return false;
        }
        switch (promotionsDecorator.getPromotionsRule().getUseStore()) {
            case "-1":
                break;

            case "1":
                List<String> strings =Arrays.asList(promotionsDecorator.getPromotionsRule().getUseArea().split(","));
                String crStoreId = param.getStoreId().toString();
                return strings.contains(crStoreId);

            case "2":
                String thrAreaId = null;
                if ("200".equals(param.getOrderType()))
                    thrAreaId = param.getReceiverCityCode();
                else if ("100".equals(param.getOrderType()) || "300".equals(param.getOrderType())) {
                    Stores store = storesService.getStore(param.getStoreId(), param.getSiteId());
                    thrAreaId = store != null ? store.getCity_id() + "" : "";
                }

                String[] stores = promotionsDecorator.getPromotionsRule().getUseArea().split(",");
                Set<String> setStores = new HashSet<>(Arrays.asList(stores));
                if (null == param.getStoreId() || !setStores.contains(thrAreaId))
                    return false;
                break;

            default:
                throw new UnknownTypeException();
        }
        return true;
    }
    //参与对象的过滤
    public boolean checkJoinObject(PromotionsDecorator promotionsDecorator, PromotionsDecorator.Param param) {
        if (!couponActiveForMemberService.checkProActivity(param.getSiteId(), promotionsDecorator.getPromotionsActivity().getId(), param.getMemberId()))
            return false;
        return true;
    }
    //活动可用性过滤(主入口)
    public boolean checkPromotionsAvailableOrNot(PromotionsDecorator promotionsDecorator, PromotionsDecorator.Param param) {
        //多种商品判断出对于一种活动是否存在并满足活动条件  如果不满足返回false
        switch (promotionsDecorator.getPromotionsRule().getPromotionsType()) {
            //赠品(1)
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_GIFT:
                return checkGoodWhetherItMeetsGiftPromotions(param.getGoodsData(), promotionsDecorator);
            //打折(1)
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_DISCOUNT:
                return checkGoodWhetherItMeetsDiscountPromotions(param.getGoodsData(), promotionsDecorator);
            //包邮(1)
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_FREE_POST:
                return checkGoodWhetherItMeetsPostPromotions(param.getGoodsData(), promotionsDecorator);
            //满减(1)
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_MONEY_OFF:
                return checkGoodWhetherItMeetsFullReducePromotions(param.getGoodsData(), promotionsDecorator);
            //限价(1)
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                return checkGoodWhetherItMeetsLimitedPricePromotions(param.getGoodsData(), promotionsDecorator);
            //拼团(1)
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                return checkGoodWhetherItMeetsGroupBookingPromotions(param.getGoodsData(), promotionsDecorator);
        }
        return false;
    }
    //商品是否满足满赠活动过滤
    private boolean checkGoodWhetherItMeetsGiftPromotions(List<GoodsData> originalData, PromotionsDecorator promotionsDecorator) {
        //判断商品是否参加了这个活动
        try {
            GiftRule giftRule = JSON.parseObject(promotionsDecorator.getPromotionsRule().getPromotionsRule(), GiftRule.class);
            switch (giftRule.getSendType()) {
                //买啥送啥
                case 2:
                     return buyOneSendOne(originalData, giftRule);
                //多种赠品随便选
                case 3:
                    return buySomeOneSendList(originalData, giftRule);
                default:
                    throw new RuntimeException("赠品过滤中无法解析的sendType类型");
            }
        } catch (Exception e) {
            throw new RuntimeException("过滤满赠活动异常");
        }
    }
    //商品是否满足打折活动过滤
    private boolean checkGoodWhetherItMeetsDiscountPromotions(List<GoodsData> goodsData, PromotionsDecorator promotionsDecorator) {
        try {
            DiscountRule discountRule = JSON.parseObject(promotionsDecorator.getPromotionsRule().getPromotionsRule(), DiscountRule.class);
            switch (discountRule.getRuleType()) {
                //直接打折
                case 1:
                case 5:
                    return directDiscount(goodsData, discountRule, promotionsDecorator.getPromotionsRule());
                //满元打折
                case 2:
                    return contentedMoneyDiscount(goodsData, discountRule, promotionsDecorator.getPromotionsRule());
                //满件打折
                case 3:
                    return contentedPieceDiscount(goodsData, discountRule, promotionsDecorator.getPromotionsRule());
                //第n件打折
                case 4:
                    return secondDiscount(goodsData, discountRule, promotionsDecorator.getPromotionsRule());
                default:
                    throw new RuntimeException("无法解析的ruleType类型");
            }
        } catch (Exception e) {
            throw new RuntimeException("打折活动过滤异常");
        }
    }
    //商品是否满足包邮活动过滤
    private boolean checkGoodWhetherItMeetsPostPromotions(List<GoodsData> goodsData, PromotionsDecorator promotionsDecorator) {
        try {
            FreePostageRule freePostageRule = JSON.parseObject(promotionsDecorator.getPromotionsRule().getPromotionsRule(), FreePostageRule.class);
            List<String> strings = Arrays.asList(freePostageRule.getGoodsIds().split(","));
            //包邮活动满足活动下限
            Integer meetMoney = freePostageRule.getMeetMoney();
            //如果是门店自提，直接返回  100 门店自提，300 直购  200 送货上门
            if (promotionsDecorator.getParam().getOrderType().equals("100")|| promotionsDecorator.getParam().getOrderType().equals("300")) {
                goodsData.forEach(gd -> gd.setUsePromotions(false));
                return false;
            }
            //如果商品在包邮范围内
            if(freePostageRule.getAreaIdsType()==1) {
                if (freePostageRule.getAreaIds().contains(promotionsDecorator.getParam().getReceiverCityCode())) {
                    classifyInTheRange(goodsData, freePostageRule.getGoodsIdsType(), strings);
                    List<GoodsData> maybeSatisfyGoodsList = goodsData.stream().filter(GoodsData::getUsePromotions).collect(toList());
                    Integer priceSum = maybeSatisfyGoodsList.stream().mapToInt(g -> g.getShopPrice() * g.getNum() - g.getDiscount()).sum();
                    if (priceSum >= meetMoney) {
                        maybeSatisfyGoodsList.forEach(g -> g.setUsePromotions(true));
                    } else {
                        maybeSatisfyGoodsList.forEach(g -> g.setUsePromotions(false));
                    }
                }
            }else if(freePostageRule.getAreaIdsType()==2){
                if (!(freePostageRule.getAreaIds().contains(promotionsDecorator.getParam().getReceiverCityCode()))) {
                    classifyInTheRange(goodsData, freePostageRule.getGoodsIdsType(), strings);
                    List<GoodsData> maybeSatisfyGoodsList = goodsData.stream().filter(GoodsData::getUsePromotions).collect(toList());
                    Integer priceSum = maybeSatisfyGoodsList.stream().mapToInt(g -> g.getShopPrice() * g.getNum() - g.getDiscount()).sum();
                    if (priceSum >= meetMoney) {
                        maybeSatisfyGoodsList.forEach(g -> g.setUsePromotions(true));
                    } else {
                        maybeSatisfyGoodsList.forEach(g -> g.setUsePromotions(false));
                    }
                }
            }else {
                throw  new RuntimeException("未知的包邮区域");
            }
        } catch (Exception e) {
            throw new RuntimeException("过滤包邮活动异常");
        }
        return goodsData.stream().anyMatch(GoodsData::getUsePromotions);
    }
    //商品是否满足满减活动过滤
    private boolean checkGoodWhetherItMeetsFullReducePromotions(List<GoodsData> originalData, PromotionsDecorator promotionsDecorator) {
        try {
            ReduceMoneyRule reduceMoneyRule = JSON.parseObject(promotionsDecorator.getPromotionsRule().getPromotionsRule(), ReduceMoneyRule.class);
            switch (reduceMoneyRule.getRuleType()) {
                //立减
                case 1:
                    return checkInstantlyReduce(promotionsDecorator.getPromotionsRule(), reduceMoneyRule, originalData);
                //每满多少减多少，可以设置封顶
                //满多少减去多少（阶梯）
                case 2:
                case 3:
                    return everyTimeSatisfiedReduce(promotionsDecorator.getPromotionsRule(), reduceMoneyRule, originalData);
                default:
                    throw new RuntimeException("无法解析的ruleType类型");
            }
        } catch (Exception e) {
            throw new RuntimeException("过滤满减活动异常");
        }
    }
    //商品是否满足限价活动过滤
    private boolean checkGoodWhetherItMeetsLimitedPricePromotions(List<GoodsData> goodsData, PromotionsDecorator promotionsDecorator) {
        try {
            FixedPriceRule fixedPriceRule = JSON.parseObject(promotionsDecorator.getPromotionsRule().getPromotionsRule(), FixedPriceRule.class);
            classifyInTheRange(goodsData, fixedPriceRule.getGoodsIdsType(), Arrays.asList(fixedPriceRule.getGoodsIds().split(",")));
        } catch (Exception e) {
            throw new RuntimeException("过滤限价活动异常");
        }
        return goodsData.stream().anyMatch(GoodsData::getUsePromotions);
    }
    //商品是否满足拼团活动过滤
    private boolean checkGoodWhetherItMeetsGroupBookingPromotions(List<GoodsData> goodsData, PromotionsDecorator promotionsDecorator) {
        try {
            GroupBookingRule groupBookingRule = JSON.parseObject(promotionsDecorator.getPromotionsRule().getPromotionsRule(), GroupBookingRule.class);
            classifyInTheRange(goodsData, groupBookingRule.getGoodsIdsType(), Arrays.asList(groupBookingRule.getGoodsIds().split(",")));
        } catch (Exception e) {
            throw new RuntimeException("过滤限价活动异常");
        }
        if(goodsData.stream().noneMatch(GoodsData::getUsePromotions)) {
            promotionsDecorator.getResult().setErrorMsg(NOT_IN_RANGE);
        }
        return goodsData.stream().anyMatch(GoodsData::getUsePromotions);
    }
    //满减活动-立减过滤
    private Boolean checkInstantlyReduce(@Nonnull PromotionsRule promotionsRule, @Nonnull ReduceMoneyRule reduceMoneyRule, @Nonnull List<GoodsData> goodsData) {
        //获取默认计算类型
        int calculateBase = promotionsRuleService.getCalculateBase(reduceMoneyRule, promotionsRule.getPromotionsType());
        List<String> strings = Arrays.asList(reduceMoneyRule.getGoodsIds().split(","));
        Integer goodsIdsType = reduceMoneyRule.getGoodsIdsType();
        switch (calculateBase) {
            case CALCULATE_BASE_COMBINATION:
                //判断是否在指定范围内 goodsIdsType 0全部商品参加  1指定商品参加 2指定商品不参加
                classifyInTheRange(goodsData, goodsIdsType, strings);
                break;
            case CALCULATE_BASE_SINGLE_MEET_ALL:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new RuntimeException("无法解析的计算类型");
        }
        return goodsData.stream().anyMatch(GoodsData::getUsePromotions);
    }
    //【满减活动-每满多少减多少过滤】或者【满减活动-满多少减去多少过滤】
    private Boolean everyTimeSatisfiedReduce(@Nonnull PromotionsRule promotionsRule, @Nonnull ReduceMoneyRule reduceMoneyRule, @Nonnull List<GoodsData> goodsData) {
        //获取默认计算类型
        int calculateBase = promotionsRuleService.getCalculateBase(reduceMoneyRule, promotionsRule.getPromotionsType());
        //满足活动最低限度
        Integer meetMoney = reduceMoneyRule.getRules().get(0).getMeetMoney();
        List<String> strings = Arrays.asList(reduceMoneyRule.getGoodsIds().split(","));
        Integer goodsIdsType = reduceMoneyRule.getGoodsIdsType();
        switch (calculateBase) {
            case CALCULATE_BASE_COMBINATION:
                //根据是否在活动指定范围内分类商品 goodsIdsType 0全部商品参加  1指定商品参加 2指定商品不参加
                classifyInTheRange(goodsData, goodsIdsType, strings);
                List<GoodsData> maybeSatisfyGoodsList = goodsData.stream().filter(GoodsData::getUsePromotions).collect(toList());
                Integer priceSum = maybeSatisfyGoodsList.stream().mapToInt(g -> (g.getShopPrice() * g.getNum()-g.getDiscount())).sum();
                if (priceSum >= meetMoney) {
                    maybeSatisfyGoodsList.forEach(g -> g.setUsePromotions(true));
                } else {
                    maybeSatisfyGoodsList.forEach(g -> g.setUsePromotions(false));
                }
                break;
            case CALCULATE_BASE_SINGLE_MEET_ALL:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new RuntimeException("无法解析的计算类型");
        }
        return goodsData.stream().anyMatch(GoodsData::getUsePromotions);
    }
    //打折活动-直接打折过滤
    private Boolean directDiscount(@Nonnull List<GoodsData> goodsData, @Nonnull DiscountRule discountRule, @Nonnull PromotionsRule promotionsRule) {
        //获取默认计算类型
        int calculateBase = promotionsRuleService.getCalculateBase(discountRule, promotionsRule.getPromotionsType());
        List<String> strings = Arrays.asList(discountRule.getGoodsIds().split(","));
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                classifyInTheRange(goodsData, discountRule.getGoodsIdsType(), strings);
                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new RuntimeException("未知计算类型");
        }
        return goodsData.stream().anyMatch(GoodsData::getUsePromotions);
    }
    //打折活动-满元活动过滤
    private Boolean contentedMoneyDiscount(@Nonnull List<GoodsData> goodsData, @Nonnull DiscountRule discountRule, @Nonnull PromotionsRule promotionsRule) {
        //获取默认计算类型
        int calculateBase = promotionsRuleService.getCalculateBase(discountRule, promotionsRule.getPromotionsType());
        Integer lowerLimit = discountRule.getRules().get(0).get("meet_money");
        List<String> strings = Arrays.asList(discountRule.getGoodsIds().split(","));
        switch (calculateBase) {
            case CALCULATE_BASE_COMBINATION:
                classifyInTheRange(goodsData, discountRule.getGoodsIdsType(), strings);
                List<GoodsData> collect = goodsData.stream().filter(GoodsData::getUsePromotions).collect(toList());
                Integer priceSum = collect.stream().mapToInt(g -> (g.getShopPrice() * g.getNum()-g.getDiscount())).sum();
                if (priceSum >= lowerLimit) {
                    collect.forEach(g -> g.setUsePromotions(true));
                } else {
                    collect.forEach(g -> g.setUsePromotions(false));
                }
                break;
            case CALCULATE_BASE_SINGLE_MEET_ALL:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new RuntimeException("未知计算类型");
        }
        return goodsData.stream().anyMatch(GoodsData::getUsePromotions);
    }
    //打折活动-满件活动过滤
    private Boolean contentedPieceDiscount(@Nonnull List<GoodsData> goodsData, @Nonnull DiscountRule discountRule, @Nonnull PromotionsRule promotionsRule) {
        //获取默认计算类型
        int calculateBase = promotionsRuleService.getCalculateBase(discountRule, promotionsRule.getPromotionsType());
        Integer lowerLimit = discountRule.getRules().get(0).get("meet_num");
        List<String> strings = Arrays.asList(discountRule.getGoodsIds().split(","));
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                classifyInTheRange(goodsData, discountRule.getGoodsIdsType(), strings);
                List<GoodsData> collect = goodsData.stream().filter(GoodsData::getUsePromotions).collect(toList());
                Integer pieceSum =collect.stream().mapToInt(GoodsData::getNum).sum();
                if (pieceSum >= lowerLimit) {
                    collect.forEach(g -> g.setUsePromotions(true));
                } else {
                    collect.forEach(g -> g.setUsePromotions(false));
                }
                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new RuntimeException("未知计算类型");
        }
        return goodsData.stream().anyMatch(GoodsData::getUsePromotions);
    }
    //打折活动-第二件半价活动过滤
    private Boolean secondDiscount(@Nonnull List<GoodsData> goodsData, @Nonnull DiscountRule discountRule, @Nonnull PromotionsRule promotionsRule) {
        //获取默认计算类型
        int calculateBase = promotionsRuleService.getCalculateBase(discountRule, promotionsRule.getPromotionsType());
        List<String> strings = Arrays.asList(discountRule.getGoodsIds().split(","));
        switch (calculateBase) {
            case CALCULATE_BASE_SINGLE_MEET_ALL:
                classifyInTheRange(goodsData, discountRule.getGoodsIdsType(), strings);
                List<GoodsData> collect = goodsData.stream().filter(GoodsData::getUsePromotions).collect(toList());
                Integer pieceSum = collect.stream().mapToInt(GoodsData::getNum).sum();
                if (pieceSum >=2) {
                    collect.forEach(g -> g.setUsePromotions(true));
                } else {
                    collect.forEach(g -> g.setUsePromotions(false));
                }
                break;
            case CALCULATE_BASE_COMBINATION:
            case CALCULATE_BASE_SINGLE_MEET_ONE:
            default:
                throw new RuntimeException("未知计算类型");
        }
        return goodsData.stream().anyMatch(GoodsData::getUsePromotions);
    }
    //赠品活动-买啥送啥
    private Boolean buyOneSendOne(@Nonnull List<GoodsData> goodsData, @Nonnull GiftRule giftRule) {
        List<String> strings = Arrays.asList(giftRule.getGoodsIds().split(","));
        switch (giftRule.getCalculateBase()) {
            //买啥送啥目前只支持 calculateBase为1 满件送的情况
            case 1:
                if(giftRule.getRuleType()==1){
                    classifyInTheRange(goodsData,null,strings);
                }else {
                    throw new RuntimeException("过滤中暂时不支持的ruleType类型");
                }
                break;
            default:
                throw new RuntimeException("赠品过滤暂不支持的类型");
        }
        return goodsData.stream().anyMatch(GoodsData::getUsePromotions);
    }
    //赠品活动-多种赠品随便选
    private Boolean buySomeOneSendList(@Nonnull List<GoodsData> goodsData, @Nonnull GiftRule giftRule) {
        List<String> strings = Arrays.asList(giftRule.getGoodsIds().split(","));
        switch (giftRule.getCalculateBase()) {
            //买啥送啥目前只支持 calculateBase为1 满件送的情况
            case 1:
                if(giftRule.getRuleType()==1){
                    classifyInTheRange(goodsData,null,strings);
                }else {
                    throw new RuntimeException("过滤中暂时不支持的ruleType类型");
                }
                break;
            case 2:
                if(giftRule.getRuleType()==1||giftRule.getRuleType()==2){
                    classifyInTheRange(goodsData,null,strings);
                }else {
                    throw new RuntimeException("过滤中暂时不支持的ruleType类型");
                }
                break;
            default:
                throw new RuntimeException("赠品过滤暂不支持的类型");
        }
        return goodsData.stream().anyMatch(GoodsData::getUsePromotions);
    }
    //分类在活动指定范围内的商品
    public void classifyInTheRange(List<GoodsData> goodsData, Integer goodsIdsType, List<String> strings) {
        //目前只有是赠品活动的时候 goodsIdsType为null
        if(goodsIdsType!=null) {
            switch (goodsIdsType) {
                case 0:
                    goodsData.forEach(g -> g.setUsePromotions(true));
                    break;
                case 1:
                    goodsData.forEach(g -> {
                        if (strings.contains(g.getGoodsId().toString())) {
                            g.setUsePromotions(true);
                        }
                    });
                    break;
                case 2:
                    goodsData.forEach(g -> {
                        if (!strings.contains(g.getGoodsId().toString())) {
                            g.setUsePromotions(true);
                        }
                    });
                    break;
                default:
                    throw new RuntimeException("未知的goodsIdsType类型");
            }
        }else {
            goodsData.forEach(g->{
                if(strings.contains((g.getGoodsId().toString()))){
                    g.setUsePromotions(true);
                }
            });
        }
    }

}
