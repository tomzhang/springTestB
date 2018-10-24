package com.jk51.modules.promotions.service;

import com.alibaba.fastjson.JSON;
import com.jk51.model.Goods;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.*;
import com.jk51.model.coupon.tags.TagsGoods;
import com.jk51.model.coupon.tags.TagsGoodsCoupon;
import com.jk51.model.coupon.tags.TagsGoodsPromotions;
import com.jk51.model.coupon.tags.TagsParam;
import com.jk51.model.order.Member;
import com.jk51.model.promotions.*;
import com.jk51.model.promotions.rule.*;
import com.jk51.modules.coupon.service.CouponActiveForMemberService;
import com.jk51.modules.coupon.service.CouponFilterService;
import com.jk51.modules.coupon.tags.CouponTagsFilter;
import com.jk51.modules.coupon.tags.PromotionsTagsFilter;
import com.jk51.modules.es.entity.GoodsInfos;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.promotions.constants.PromotionsConstant;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.request.LabelParam;
import com.jk51.modules.promotions.utils.OrderDeductionUtils;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.service.TradesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.jk51.model.promotions.EasyToSeeConstants.SEARCH_GOODS;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * filename :com.jk51.modules.promotions.service.
 * author   :zw
 * date     :2017/8/15
 * Update   :
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class PromotionsFilterService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PromotionsActivityMapper promotionsActivityMapper;
    @Autowired
    private CouponActiveForMemberService couponActiveForMemberService;
    @Autowired
    private OrderDeductionUtils orderDeductionUtils;
    @Autowired
    private PromotionsRuleMapper promotionsRuleMapper;
    @Autowired
    private PromotionsRuleService promotionsRuleService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private TradesService tradesService;
    @Autowired
    private CouponFilterService couponFilterService;
    @Autowired
    private ServletContext sc;



    public List<PromotionsActivity> getPromotionsActivitiesByChecked(List<PromotionsActivity> promotionsActivities,Integer siteId,Integer memberId,String goodsIds) {
        promotionsActivities = promotionsActivities.stream().filter(pa -> {
            //判断符合商品及会员等规则
            PromotionsRule promotionsRule = checkActivity(pa, memberId, goodsIds);
            if (Objects.isNull(promotionsRule))
                return false;
            boolean timeRule = promotionsRuleService.checkproRuleTimeRule(promotionsRule.getTimeRule());
            if (!timeRule)
                return false;
            //判断首单
            if (promotionsRule.getIsFirstOrder() == 1) {
                if (memberId == null) {
                    return false;
                } else {
                    boolean firstOrder = tradesService.checkFirstOrder(siteId, memberId);
                    if (!firstOrder)
                        return false;
                }
            }
            promotionsRule.setProCouponRuleView(promotionsRuleService.getProCouponRuleViewForProRule(
                promotionsRule.getSiteId(), promotionsRule.getId())
            );
            pa.setPromotionsRule(promotionsRule);
            return true;
        }).collect(toList());
        return promotionsActivities;
    }


    /**
     * 不判断商品包含
     * @param promotionsActivities
     * @param siteId
     * @param memberId
     * @return
     */
    public List<PromotionsActivity> getPromotionsActivitiesByUncheckedTime(List<PromotionsActivity> promotionsActivities,Integer siteId,Integer memberId) {
        return promotionsActivities.stream().filter(pa -> {
            //判断符合商品及会员等规则
            PromotionsRule promotionsRule = checkActivity2(pa, memberId);
            if(Objects.isNull(promotionsRule))
                return false;
            //判断首单
            if (promotionsRule.getIsFirstOrder() == 1) {
                if (memberId == null) {
                    return false;
                } else {
                    boolean firstOrder = tradesService.checkFirstOrder(siteId, memberId);
                    if (!firstOrder)
                        return false;
                }
            }
            promotionsRule.setProCouponRuleView(promotionsRuleService.getProCouponRuleViewForProRule(
                promotionsRule.getSiteId(), promotionsRule.getId())
            );
            pa.setPromotionsRule(promotionsRule);
            return true;
        }).collect(toList());
    }

    /**
     * 不判断商品包含
     * @param promotionsActivities
     * @param siteId
     * @param memberId
     * @return
     */
    public List<PromotionsActivity> getPromotionsActivitiesByCheckedNotGoods(List<PromotionsActivity> promotionsActivities,Integer siteId,Integer memberId) {
        return promotionsActivities.stream().filter(pa -> {
            //判断符合商品及会员等规则
            PromotionsRule promotionsRule = checkActivity2(pa, memberId);
            if (Objects.isNull(promotionsRule))
                return false;
            boolean timeRule = promotionsRuleService.checkproRuleTimeRule(promotionsRule.getTimeRule());
            if (!timeRule)
                return false;
            //判断首单
            if (promotionsRule.getIsFirstOrder() == 1) {
                if (memberId == null) {
                    return false;
                } else {
                    boolean firstOrder = tradesService.checkFirstOrder(siteId, memberId);
                    if (!firstOrder)
                        return false;
                }
            }
            promotionsRule.setProCouponRuleView(promotionsRuleService.getProCouponRuleViewForProRule(
                promotionsRule.getSiteId(), promotionsRule.getId())
            );
            pa.setPromotionsRule(promotionsRule);
            return true;
        }).collect(toList());
    }


    public List<PromotionsActivity> filterByGoodsId(CouponFilterParams couponFilterParams) {
        return promotionsActivityMapper.getPromotionsActivitiesByStatusAndSiteId(
            couponFilterParams.getSiteId()).stream()
            .filter(pa -> {
                PromotionsRule promotionsRule = checkActivity(pa, couponFilterParams.getUserId(), couponFilterParams.getGoodsId());

                if (promotionsRule == null) {
                    return false;
                } else {
                    if(promotionsRule.getIsFirstOrder() == 1){
                        //首单
                        if(couponFilterParams.getUserId() == null){
                            return false;
                        }else {
                            Member member = memberMapper.getMemberByMemberId(couponFilterParams.getSiteId(), couponFilterParams.getUserId());
                            boolean isFirstOrder = orderService.checkUserFirstOrderByPayment(couponFilterParams.getSiteId(), member.getBuyerId());
                            if(!isFirstOrder){
                                //用户已不是首单
                                return false;
                            }
                        }
                    }
                    promotionsRule.setProCouponRuleView(promotionsRuleService.getProCouponRuleViewForProRule(
                        promotionsRule.getSiteId(), promotionsRule.getId())
                    );
                    pa.setPromotionsRule(promotionsRule);
                    return true;
                }
            })
            .collect(Collectors.toList());
    }

    public List<EasyToSee> filterActivityGroupGoodsIds(EasyToSeeParam easyToSeeParam) {
        //初始化所有商品的Easy 2 See
        List<EasyToSee> easyToSeeList = new ArrayList<EasyToSee>() {{
            List<String> goodsIdsList = orderDeductionUtils.arrayConvertList(easyToSeeParam.getGoodsIds());
            goodsIdsList.forEach(goods -> add(new EasyToSee(goods)));
        }};
        promotionsActivityMapper.getPromotionsActivitiesByStatusAndSiteId(
            easyToSeeParam.getSiteId())
            .forEach(pa -> checkActivityIsContainsGoodsId(pa, easyToSeeParam.getGoodsIds(), easyToSeeList));

        return easyToSeeList;
    }


    public List<EasyToSee> activityGroupGoodsIds(EasyToSeeParam easyToSeeParam) {
        //初始化所有商品的Easy 2 See
        List<EasyToSee> easyToSeeList = new ArrayList<EasyToSee>() {{
            List<String> goodsIdsList = orderDeductionUtils.arrayConvertList(easyToSeeParam.getGoodsIds());
            goodsIdsList.forEach(goods -> add(new EasyToSee(goods)));
        }};
        promotionsActivityMapper.getPromotionsActivitiesByStatusTwoAndSiteId(
            easyToSeeParam.getSiteId())
            .forEach(pa -> checkActivityIsContainsGoodsId(pa, easyToSeeParam.getGoodsIds(), easyToSeeList));

        return easyToSeeList;
    }

    /**
     * 检查此活动是否满足这个会员发放  检查此商品是否在活动中
     *
     * @return
     */
    public PromotionsRule checkActivity(PromotionsActivity pa, Integer userId, String goodsId) {
        if (pa.getPromotionsId() == null || pa.getPromotionsId() < 1) {
            return null;
        }
        boolean ifExcuseUser = true;
        //判断活动
        SignMembers signMembers = new SignMembers();
        try {
            signMembers = JSON.parseObject(pa.getUseObject(), SignMembers.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderDeductionService signMembers类转换错误{}", e);
        }
        int type = signMembers.getType() == null ? 0 : signMembers.getType();
        switch (type) {
            case PromotionsConstant.MEMBER_TYPE_ALL:
                break;
            case PromotionsConstant.MEMBER_TYPE_TAGS:
            case PromotionsConstant.MEMBER_TYPE_TAGS_GROUP:
                ifExcuseUser = couponActiveForMemberService.checkProActivity(pa.getSiteId(), pa.getId(), userId);
                break;
            case PromotionsConstant.MEMBER_TYPE_SOME:
                ifExcuseUser = orderDeductionUtils.judgeExcuse(signMembers.getPromotion_members(), userId);
        }
        if (!ifExcuseUser) {
            return null;
        }
        return checkPromotionsRule(pa, goodsId);
    }

    /**
     * 迫不得已，业务需要
     * @param pa
     * @param userId
     * @return
     */
    public PromotionsRule checkActivity2(PromotionsActivity pa, Integer userId) {
        if (pa.getPromotionsId() == null || pa.getPromotionsId() < 1) {
            return null;
        }
        boolean ifExcuseUser = true;
        //判断活动
        SignMembers signMembers = new SignMembers();
        try {
            signMembers = JSON.parseObject(pa.getUseObject(), SignMembers.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderDeductionService signMembers类转换错误{}", e);
        }
        int type = signMembers.getType() == null ? 0 : signMembers.getType();
        switch (type) {
            case PromotionsConstant.MEMBER_TYPE_ALL:
                break;
            case PromotionsConstant.MEMBER_TYPE_TAGS:
            case PromotionsConstant.MEMBER_TYPE_TAGS_GROUP:
                ifExcuseUser = couponActiveForMemberService.checkProActivity(pa.getSiteId(), pa.getId(), userId);
                break;
            case PromotionsConstant.MEMBER_TYPE_SOME:
                ifExcuseUser = orderDeductionUtils.judgeExcuse(signMembers.getPromotion_members(), userId);
        }
        if (!ifExcuseUser) {
            return null;
        }
        //如果活动中已存在rule规则，则直接取出，避免多次访问数据库，浪费连接资源
        if(Objects.nonNull(pa) && Objects.nonNull(pa.getPromotionsRule())){
            return pa.getPromotionsRule();
        }
        return null;
    }

    public PromotionsRule checkPromotionsRule(PromotionsActivity pa, String goodsId) {
        //如果活动中已存在rule规则，则直接取出，避免多次访问数据库，浪费连接资源
        if(Objects.nonNull(pa) && Objects.nonNull(pa.getPromotionsRule())){
            if(isIfExcuseGoods(pa.getPromotionsRule(),goodsId)){
                return pa.getPromotionsRule();
            }else {
                return null;
            }
        }
        //判断规则
        PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRuleByIdAndSiteId(pa.getSiteId(), pa.getPromotionsId());
        if (promotionsRule != null && !isIfExcuseGoods(promotionsRule, goodsId)) {
            return null;
        } else {
            return promotionsRule;
        }
    }

    /**
     * 检查此商品是否在活动中
     *
     * @return
     */
    public void checkActivityIsContainsGoodsId(PromotionsActivity pa, String goodsId, List<EasyToSee> easyToSeeList) {
        if (pa.getPromotionsId() == null || pa.getPromotionsId() < 1) {
            return;
        }
        //判断规则
        PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRuleByIdAndSiteIdAndStatus(pa.getSiteId(), pa.getPromotionsId());
        if(Objects.isNull(promotionsRule)){
            return;
        }
        boolean timeRule = promotionsRuleService.checkproRuleTimeRule(promotionsRule.getTimeRule());
        if (!timeRule) {
            //时间不符合规则
            return;
        }
        isIfExcuseGoodsV2(promotionsRule, goodsId, pa, easyToSeeList);
    }

    /**
     * 判断商品是否包含
     *
     * @param promotionsRule
     * @param goodsId
     * @return
     */
    public boolean isIfExcuseGoods(PromotionsRule promotionsRule, String goodsId) {
        Integer type;
        List<String> promotionIds;
        List<String> goodsIdsList = orderDeductionUtils.arrayConvertList(goodsId);
        switch (promotionsRule.getPromotionsType()) {
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_GIFT:
                GiftRule giftRule = null;
                try {
                    giftRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GiftRule.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("OrderDeductionService GiftRule类转换错误{}", e);
                }

                if (giftRule == null) {
                    logger.error("解析promotionsRule失败, {}", promotionsRule);
                    return false;
                }
                type = 1;
                promotionIds = orderDeductionUtils.arrayConvertList(giftRule.getGoodsIds());
                break;

            case PromotionsConstant.PROMOTIONS_RULE_TYPE_DISCOUNT:
                DiscountRule discountRule = null;
                try {
                    discountRule = JSON.parseObject(promotionsRule.getPromotionsRule(), DiscountRule.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("OrderDeductionService DiscountRule类转换错误{}", e);
                }

                if (discountRule == null) {
                    logger.error("解析promotionsRule失败, {}", promotionsRule);
                    return false;
                }
                type = discountRule.getGoodsIdsType();
                promotionIds = orderDeductionUtils.arrayConvertList(discountRule.getGoodsIds());
                break;

            case PromotionsConstant.PROMOTIONS_RULE_TYPE_FREE_POST:
                FreePostageRule freePostageRule = null;
                try {
                    freePostageRule = JSON.parseObject(promotionsRule.getPromotionsRule(), FreePostageRule.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("OrderDeductionService FreePostageRule类转换错误{}", e);
                }

                if (freePostageRule == null) {
                    logger.error("解析promotionsRule失败, {}", promotionsRule);
                    return false;
                }
                type = freePostageRule.getGoodsIdsType();
                promotionIds = orderDeductionUtils.arrayConvertList(freePostageRule.getGoodsIds());
                break;

            case PromotionsConstant.PROMOTIONS_RULE_TYPE_MONEY_OFF:// 40满减活动
                ReduceMoneyRule reduceMoneyRule = null;
                try {
                    reduceMoneyRule = JSON.parseObject(promotionsRule.getPromotionsRule(), ReduceMoneyRule.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("OrderDeductionService FreePostageRule类转换错误{}", e);
                }

                if (reduceMoneyRule == null) {
                    logger.error("解析promotionsRule失败, {}", promotionsRule);
                    return false;
                }
                type = reduceMoneyRule.getGoodsIdsType();
                promotionIds = orderDeductionUtils.arrayConvertList(reduceMoneyRule.getGoodsIds());
                break;

            case PromotionsConstant.PROMOTIONS_RULE_TYPE_LIMIT_PRICE:// 50限价活动
                FixedPriceRule fixedPriceRule = null;
                try {
                    fixedPriceRule = JSON.parseObject(promotionsRule.getPromotionsRule(), FixedPriceRule.class);
                } catch (Exception e) {
                    logger.error("OrderDeductionService FreePostageRule类转换错误{}", e);
                    return false;
                }

                if (fixedPriceRule == null) {
                    logger.error("解析promotionsRule失败, {}", promotionsRule);
                    return false;
                }
                type = fixedPriceRule.getGoodsIdsType();
                promotionIds = orderDeductionUtils.arrayConvertList(fixedPriceRule.getGoodsIds());
                break;
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_GROUP_BOOKING://拼团
                GroupBookingRule groupBookingRule = null;
                try{
                    groupBookingRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GroupBookingRule.class);
                }catch (Exception e){
                    logger.error("OrderDeductionService FreePostageRule类转换错误{}", e);
                    return false;
                }
                if(groupBookingRule == null){
                    logger.error("解析promotionsRule失败, {}", promotionsRule);
                    return false;
                }
                type = groupBookingRule.getGoodsIdsType();
                promotionIds = orderDeductionUtils.arrayConvertList(groupBookingRule.getGoodsIds());
                break;
            default:
                logger.error("OrderDeductionService 未知活动类型{}");
                return false;
        }
        return isExcuse(type, promotionIds, goodsIdsList);
    }

    /**
     * 商品是否包含并处理V2
     *
     * @param promotionsRule
     * @param goodsId
     * @return
     */
    public void isIfExcuseGoodsV2(PromotionsRule promotionsRule, String goodsId, PromotionsActivity pa, List<EasyToSee> easyToSeeList) {
        Integer type = 0;
        List<String> promotionIds = new ArrayList<>();
        List<String> goodsIdsList = orderDeductionUtils.arrayConvertList(goodsId);
        switch (promotionsRule.getPromotionsType()) {
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_GIFT:
                GiftRule giftRule = null;
                try {
                    giftRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GiftRule.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("OrderDeductionService GiftRule类转换错误{}", e);
                }
                //此处没有不包含
                type = 1;
                promotionIds = orderDeductionUtils.arrayConvertList(giftRule.getGoodsIds());
                break;
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_DISCOUNT:
                DiscountRule discountRule = null;
                try {
                    discountRule = JSON.parseObject(promotionsRule.getPromotionsRule(), DiscountRule.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("OrderDeductionService DiscountRule类转换错误{}", e);
                }
                // 这里判断未完成
                type = discountRule.getGoodsIdsType();
                promotionIds = orderDeductionUtils.arrayConvertList(discountRule.getGoodsIds());
                break;
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_FREE_POST:
                FreePostageRule freePostageRule = null;
                try {
                    freePostageRule = JSON.parseObject(promotionsRule.getPromotionsRule(), FreePostageRule.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("OrderDeductionService FreePostageRule类转换错误{}", e);
                }
                type = freePostageRule.getGoodsIdsType();
                promotionIds = orderDeductionUtils.arrayConvertList(freePostageRule.getGoodsIds());
                break;
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_MONEY_OFF:// 40满减活动
                ReduceMoneyRule reduceMoneyRule = null;
                try {
                    reduceMoneyRule = JSON.parseObject(promotionsRule.getPromotionsRule(), ReduceMoneyRule.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("OrderDeductionService FreePostageRule类转换错误{}", e);
                }
                type = reduceMoneyRule.getGoodsIdsType();
                promotionIds = orderDeductionUtils.arrayConvertList(reduceMoneyRule.getGoodsIds());
                break;
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_LIMIT_PRICE:// 50限价活动
                FixedPriceRule fixedPriceRule = null;
                try {
                    fixedPriceRule = JSON.parseObject(promotionsRule.getPromotionsRule(), FixedPriceRule.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("OrderDeductionService FreePostageRule类转换错误{}", e);
                }
                type = fixedPriceRule.getGoodsIdsType();
                promotionIds = orderDeductionUtils.arrayConvertList(fixedPriceRule.getGoodsIds());
                break;
            case PromotionsConstant.PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                GroupBookingRule groupBookingRule = null;
                try {
                    groupBookingRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GroupBookingRule.class);
                    type = groupBookingRule.getGoodsIdsType();
                    promotionIds = orderDeductionUtils.arrayConvertList(groupBookingRule.getGoodsIds());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("OrderDeductionService FreePostageRule类转换错误{}", e);
                }
            default:
                logger.error("OrderDeductionService 未知活动类型{}");
        }
        //拾取活动
        isExcuseV2(type, promotionIds, goodsIdsList, pa, promotionsRule, easyToSeeList);
        return;
    }

    /**
     * 判断是否存在某类型
     *
     * @param goodsIdsType
     * @param goodsIds
     * @param promotionIds
     * @return
     */
    private boolean isExcuse(Integer goodsIdsType, List promotionIds, List goodsIds) {

        if (goodsIdsType == PromotionsConstant.DISCOUNT_RULE_GOODS_TYPE_ALL) {
            return true;
        } else if (goodsIdsType == PromotionsConstant.DISCOUNT_RULE_GOODS_TYPE_JOIN) {
            goodsIds.retainAll(promotionIds);
            return goodsIds.size() > 0;
        } else if (goodsIdsType == PromotionsConstant.DISCOUNT_RULE_GOODS_TYPE_SIT_OUT) {
            Integer startSize = goodsIds.size();
            goodsIds.retainAll(promotionIds);
            return !(startSize == goodsIds.size());
        }
        return true;
    }

    /**
     * 判断是否存在某类型
     *
     * @param goodsIdsType
     * @param goodsIds
     * @param promotionIds
     * @return
     */
    private void isExcuseV2(Integer goodsIdsType, List promotionIds, List<String> goodsIds, PromotionsActivity pa, PromotionsRule promotionsRule, List<EasyToSee> easyToSeeList) {

        if (goodsIdsType == PromotionsConstant.DISCOUNT_RULE_GOODS_TYPE_ALL) {
            pa.setPromotionsRule(promotionsRule);
            //所查的商品全部都加入这个活动
            easyToSeeList = easyToSeeList.stream().map(easy2See -> {
                AddEasyToSee(pa, easy2See);
                return easy2See;
            }).collect(Collectors.toList());
            return;
        } else if (goodsIdsType == PromotionsConstant.DISCOUNT_RULE_GOODS_TYPE_JOIN) {
            goodsIds.retainAll(promotionIds);
            if (goodsIds.size() > 0) {
                //可以
                pa.setPromotionsRule(promotionsRule);
                for (String id : goodsIds) {
                    for (EasyToSee easyToSee : easyToSeeList) {
                        if (easyToSee.getGoodsId().equals(id)) {
                            //加入
                            AddEasyToSee(pa, easyToSee);
                            break;
                        }
                    }
                }

            }
            return;
        } else if (goodsIdsType == PromotionsConstant.DISCOUNT_RULE_GOODS_TYPE_SIT_OUT) {
            goodsIds.removeAll(promotionIds);
            if (goodsIds.size() > 0) {
                //可以
                pa.setPromotionsRule(promotionsRule);
                for (String id : goodsIds) {
                    for (EasyToSee easyToSee : easyToSeeList) {
                        if (easyToSee.getGoodsId().equals(id)) {
                            //加入 肯德基豪华套餐
                            AddEasyToSee(pa, easyToSee);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 分离秒杀、限价、拼团等优先级最高的活动
     *
     * @param pa
     * @param easyToSee
     */
    public void AddEasyToSee(PromotionsActivity pa, EasyToSee easyToSee) {
        List<Integer> better_activities = Arrays.asList(EasyToSeeConstants.BETTER_ACTIVITY_ID);
        Integer dependent=getIndependent(pa);
        //如果ID是最高级活动，则加入     加: dependent是1的话也加入最高级活动列表中
        if (better_activities.contains(pa.getPromotionsRule().getPromotionsType()) || dependent==1) {
            easyToSee.getProCouponEasyToSee().getPricesActivity().add(pa);
        } else {
            easyToSee.getProCouponEasyToSee().getProCouponList().getPromotionsActivities().add(pa);
        }
    }

    private Integer getIndependent(PromotionsActivity pa) {
        return Optional.ofNullable(pa.getIsIndependent()).orElseGet(() -> PromotionsConstant.DEFAULT_INDEPENDENT.get(pa.getPromotionsRule().getPromotionsType()));
    }

    /**
     * 筛选出此用户此商品的可领优惠券数量和活动，并且优先显示秒杀券倒计时还是优惠标签
     *
     * @param crc
     * @param pal
     * @return
     */
    public LabelParam filterCouponAndPromotions(List<CouponActivity> crc, List<PromotionsActivity> pal,
                                                List<CouponRule> userCanUseCoupon) {
        if (crc == null && pal == null) {
            return null;
        }
        LabelParam labelParam = new LabelParam();
        //（旧）存放解析的标签map
        //List map = new ArrayList();
        List<CouponRule> collectCouponRule = crc.stream()
            .flatMap(couponActivity -> couponActivity.getCouponRules().stream())
            .collect(Collectors.toList());
        labelParam.setCouponCount(collectCouponRule.size());
        /*if (userCanUseCoupon != null && !userCanUseCoupon.isEmpty()) { //加入未使用的
            collectCouponRule.addAll(userCanUseCoupon);
        }*/
        collectCouponRule.stream()
            .sorted(comparing(CouponRule::getCreateTime)) //按时间升序
            .collect(Collectors.toList());
        List<PromotionsRule> collectPromotionsRule = pal.stream()
            .filter(promotionsActivity -> {
                labelParam.setProruleDetail(promotionsActivity.getTitle());
                return true;
            })
            .map(PromotionsActivity::getPromotionsRule)
            .collect(Collectors.toList());

        labelParam.setPromotionsCount(collectPromotionsRule.size());
        collectCouponRule.stream().forEach(p -> {
            TimeRule timeRule = JSON.parseObject(p.getTimeRule(), TimeRule.class);
            // 旧优惠规则解析  下面的秒杀时间不需要注释掉
            //map.add(resolveRuleByCoupon(p));
            if (timeRule.getValidity_type() == 4 && p.getStatus() == 0) { //秒杀券
                labelParam.setSaleTime(timeRule.getEndTime());
            }
        });
        //旧活动规则解析
        /*collectPromotionsRule.stream().forEach(pr -> {
            map.add(resolveRuleByPromotions(pr));
        });*/
        /*Collections.reverse(map);
        Object collect = map.stream().distinct().limit(5).collect(Collectors.toList());*/

        if (labelParam.getSaleTime() != null) {
            labelParam.setType(2);//倒计时
        } else {
            labelParam.setType(1);//优惠标签
            //labelParam.setLabel(collect.toString());
        }
        return labelParam;
    }

    /**
     * 解析优惠券规则
     *
     * @param couponRule
     * @return
     */
    public String resolveRuleByCoupon(CouponRule couponRule) {
        String labelStr = "";
        LimitRule limitRule = JSON.parseObject(couponRule.getLimitRule(), LimitRule.class);
        GoodsRule goodsRule = JSON.parseObject(couponRule.getGoodsRule(), GoodsRule.class);

        switch (couponRule.getCouponType()) {
            case 100:
                if (limitRule.getIs_first_order() == 1) {
                    labelStr = "首单满减";
                } else if (goodsRule != null && goodsRule.getRule_type() == 1) {
                    labelStr = "多买多减";
                } else {
                    labelStr = "满减";
                }
                break;
            case 200:
                if (limitRule.getIs_first_order() == 1) {
                    labelStr = "首单折扣";
                } else if (goodsRule != null && goodsRule.getRule_type() == 1) {
                    labelStr = "满元折";
                } else if (goodsRule != null && (couponRule.getCouponType() == 200 && goodsRule.getRule_type() == 5) ||
                    (couponRule.getCouponType() == 200 && goodsRule.getRule_type() == 2)) {
                    labelStr = "满件折";
                } else {
                    labelStr = "折扣";
                }
                break;
            case 300:
                if (limitRule.getIs_first_order() == 1) {
                    labelStr = "首单限价";
                } else {
                    labelStr = "限价";
                }
                break;
            case 400:
                if (limitRule.getIs_first_order() == 1) {
                    labelStr = "首单包邮";
                } else {
                    labelStr = "包邮";
                }
                break;
            case 500:
                if (limitRule.getIs_first_order() == 1) {
                    labelStr = "首单满赠";
                } else {
                    labelStr = "满赠";
                }
                break;
        }
        return labelStr;
    }


    /**
     * 解析优惠券活动规则
     *
     * @param promotionsRule
     * @return
     */
    public String resolveRuleByPromotions(PromotionsRule promotionsRule) {
        String labelStr = "";
        DiscountRule discountRule = new DiscountRule();
        ReduceMoneyRule reduceMoneyRule = new ReduceMoneyRule();
        try {
            if (promotionsRule.getPromotionsType() == 20) {
                discountRule = JSON.parseObject(promotionsRule.getPromotionsRule(), DiscountRule.class);
            }
            if (promotionsRule.getPromotionsType() == 40) {
                reduceMoneyRule = JSON.parseObject(promotionsRule.getPromotionsRule(), ReduceMoneyRule.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderDeductionService DiscountRule类转换错误{}", e);
        }

        switch (promotionsRule.getPromotionsType()) {
            case 10:
                if (promotionsRule.getIsFirstOrder() == 1) {
                    labelStr = "首单赠送";
                } else {
                    labelStr = "满赠";
                }
                break;
            case 20:
                if (promotionsRule.getIsFirstOrder() == 1) {
                    labelStr = "首单折扣";
                } else if (discountRule.getRuleType() == 2) {
                    labelStr = "满元折";
                } else if (discountRule.getRuleType() == 3) {
                    labelStr = "满件折";
                } else {
                    labelStr = "折扣";
                }
                break;
            case 30:
                if (promotionsRule.getIsFirstOrder() == 1) {
                    labelStr = "首单包邮";
                } else {
                    labelStr = "包邮";
                }
                break;
            case 40:
                if (promotionsRule.getIsFirstOrder() == 1) {
                    labelStr = "首单满减";
                } else if ((promotionsRule.getPromotionsType() == 40 && reduceMoneyRule.getRuleType() == 2) ||
                    (promotionsRule.getPromotionsType() == 40 && reduceMoneyRule.getRuleType() == 3)) {
                    labelStr = "多买多减";
                } else {
                    labelStr = "满减";
                }
                break;
            case 50:
                if (promotionsRule.getIsFirstOrder() == 1) {
                    labelStr = "首单限价";
                } else {
                    labelStr = "限价";
                }
                break;
        }
        return labelStr;
    }


    public void GroupEasy2See(Integer siteId, List<EasyToSee> easyToSees, Integer user_id, Map<String, Object> result) {
        for (EasyToSee e : easyToSees) {
            //处理优先活动
            List<PromotionsActivity> better = new ArrayList<PromotionsActivity>();
            if (e.getProCouponEasyToSee().getPricesActivity().size() > 0) {
                Map<Integer, List<PromotionsActivity>> pricesPronotionsGroup = e.getProCouponEasyToSee().getPricesActivity().stream().filter(pa -> {
                    //根据传入的user_id,如果user_id不为空，筛选掉没有参加资格的活动
                    //user_id为空则筛选掉所有活动，仅保留全部商品参加，并将isNeedAuth 标志设置为true
                    boolean isCanUsePro = couponActiveForMemberService.checkProActivityCanUse(pa, user_id);
                    if (!isCanUsePro) {
                        //设置商品是否有需要登录查看的优惠
                        result.put("isNeedAuth", true);
                        //移除
                        better.add(pa);
                    }
                    return isCanUsePro;
                }).collect(groupingBy(pa -> {
                    return pa.getPromotionsRule().getPromotionsType();
                }));
                if (pricesPronotionsGroup != null && !pricesPronotionsGroup.entrySet().isEmpty()) {
                    e.getProCouponEasyToSee().setPricesActivityGroup(pricesPronotionsGroup);
                }
            }
            //删除掉需要删除的
            e.getProCouponEasyToSee().getPricesActivity().removeAll(better);
            List<PromotionsActivity> activity = new ArrayList<>();
            //处理普通活动
            if (e.getProCouponEasyToSee().getProCouponList().getPromotionsActivities().size() > 0) {
                Map<Integer, List<PromotionsActivity>> promotionsActivitiesGroup = e.getProCouponEasyToSee().getProCouponList().getPromotionsActivities().stream().filter(pa -> {
                    //根据传入的user_id,如果user_id不为空，筛选掉没有参加资格的活动
                    //user_id为空则筛选掉所有活动，仅保留全部商品参加，并将isNeedAuth 标志设置为true
                    boolean isCanUsePro = couponActiveForMemberService.checkProActivityCanUse(pa, user_id);
                    if (!isCanUsePro) {
                        //设置商品是否有需要登录查看的优惠
                        result.put("isNeedAuth", true);
                        activity.add(pa);
                    }
                    return isCanUsePro;
                }).collect(groupingBy(pa -> {
                    return pa.getPromotionsRule().getPromotionsType();
                }));
                if (promotionsActivitiesGroup != null && !promotionsActivitiesGroup.entrySet().isEmpty()) {
                    e.getProCouponEasyToSee().getProCouponList().setPromotionsActivitiesGroup(promotionsActivitiesGroup);
                }
            }
            e.getProCouponEasyToSee().getProCouponList().getPromotionsActivities().removeAll(activity);
        }
    }

    /**
     * 将EasyToSee 解析成标签
     *
     * @param easyToSees
     * @param buyerId
     * @Param searchType 0搜索商品 1商品详情，根据类型所应用的解析规则略有不同
     * @Param tagsNum 生成tags数量
     */
    public void resolveEasy2SeeTransTags(List<EasyToSee> easyToSees, Integer buyerId, Integer searchType, Integer tagsNum) {
        easyToSees.forEach(e -> {
            //解析最高优先级活动，限价、秒杀、拼团等
            Map<Integer, List<PromotionsActivity>> pricesActivityGroup = e.getProCouponEasyToSee().getPricesActivityGroup();
            if (pricesActivityGroup != null && !pricesActivityGroup.entrySet().isEmpty()) {
                //里面有妖气
                resolveEasy2SeeByBetterActivity(pricesActivityGroup, e, buyerId, searchType);
            }
            /**
             * 搜索商品：活动不够拿券来凑，直到满足tagsNum、或者身体（券和活动列表）被掏空的时候
             * 商品详情：活动及券最多都可以显示5个
             */
            if (searchType == 0) {
                //普通活动
                Map<Integer, List<PromotionsActivity>> promotionsActivitiesGroup = e.getProCouponEasyToSee().getProCouponList().getPromotionsActivitiesGroup();
                if (promotionsActivitiesGroup != null && !promotionsActivitiesGroup.entrySet().isEmpty()) {
                    //解析普通活动
                    resolveEasy2SeeGeneralActivity(promotionsActivitiesGroup, e, buyerId, searchType);
                }
                while ((e.getTags().size() + e.getCouponTags().size()) < tagsNum && !e.getProCouponEasyToSee().getProCouponList().getCouponsRule().isEmpty()) {
                    //解析优惠券 循环条件与普通活动类似，每次解析优惠券成功便会从列表中剔除该券，直到满足tagsNum或者优惠券列表为空
                    Map<Integer, List<EasyToSeeCoupon>> e2sCouponsGroup = e.getProCouponEasyToSee().getProCouponList().getCouponsRuleGroup();
                    resolveGeneralCouponRule(e2sCouponsGroup, e, searchType);
                }
            } else if (searchType == 1) {
                //普通活动
                Map<Integer, List<PromotionsActivity>> promotionsActivitiesGroup = e.getProCouponEasyToSee().getProCouponList().getPromotionsActivitiesGroup();
                if (promotionsActivitiesGroup != null && !promotionsActivitiesGroup.entrySet().isEmpty()) {
                    //解析普通活动
                    resolveEasy2SeeGeneralActivity(promotionsActivitiesGroup, e, buyerId, searchType);
                }
                while (e.getCouponTags().size() < tagsNum && !e.getProCouponEasyToSee().getProCouponList().getCouponsRule().isEmpty()) {
                    Map<Integer, List<EasyToSeeCoupon>> e2sCouponsGroup = e.getProCouponEasyToSee().getProCouponList().getCouponsRuleGroup();
                    resolveGeneralCouponRule(e2sCouponsGroup, e, searchType);
                }
            }
        });
    }

    /**
     * 解析优惠券
     *
     * @param e2sCouponsGroup
     * @param e
     */
    public void resolveGeneralCouponRule(Map<Integer, List<EasyToSeeCoupon>> e2sCouponsGroup, EasyToSee e, Integer searchType) {
        Integer[] e2sCouponIds = EasyToSeeConstants.COUPONS_ACTIVITY_ID;
        for (Integer couponType : e2sCouponIds) {
            List<EasyToSeeCoupon> coupons = e.getProCouponEasyToSee().getProCouponList().getCouponsRuleGroup().get(couponType);
            if (coupons != null && coupons.size() > 0) {
                couponFilterService.resolveRuleAndPriceByPromotions(couponType, coupons, e, searchType);
            }
        }
    }

    /**
     * 解析最高活动
     *
     * @param pricesActivityGroup
     * @param e
     * @param buyerId             @return
     */
    private void resolveEasy2SeeByBetterActivity(Map<Integer, List<PromotionsActivity>> pricesActivityGroup, EasyToSee e, Integer buyerId, Integer searchType) {
        Integer[] betterActivityId = EasyToSeeConstants.BEST_ACTIVITY;
        for (Integer better_id : betterActivityId) {
            List<PromotionsActivity> promotionsActivities = pricesActivityGroup.get(better_id);
            if (promotionsActivities != null && promotionsActivities.size() > 0) {
                resolveRuleAndPriceByPromotions(buyerId, better_id, promotionsActivities, e, searchType);
            }
        }
    }

    /**
     * 解析普通活动
     *
     * @param promotionsActivitiesGroup
     * @param e
     * @param buyerId                   @return
     */
    private void resolveEasy2SeeGeneralActivity(Map<Integer, List<PromotionsActivity>> promotionsActivitiesGroup, EasyToSee e, Integer buyerId, Integer searchType) {
        Integer[] betterActivityId = EasyToSeeConstants.ORDINARY_ACTIVITY;
        for (Integer better_id : betterActivityId) {
            List<PromotionsActivity> promotionsActivities = promotionsActivitiesGroup.get(better_id);
            if (promotionsActivities != null && promotionsActivities.size() > 0) {
                resolveRuleAndPriceByPromotions(buyerId, better_id, promotionsActivities, e, searchType);
            }
        }
    }

    /**
     * 解析活动，及价格
     *
     * @param buyerId
     * @param better_id
     * @param promotionsActivities
     * @param e
     */
    private void resolveRuleAndPriceByPromotions(Integer buyerId, Integer better_id, List<PromotionsActivity> promotionsActivities, EasyToSee e, Integer searchType) {
        switch (better_id) {
            case 50: {
                //限价
                if (e.getBetter_price() != null) return;
                /*if (searchType == 0) {*/
                    Integer minFixedPrice = resolveFixedPrice(buyerId, promotionsActivities, e, better_id, searchType);
                    if (minFixedPrice != null) {
                        e.setBetter_price(minFixedPrice);
                        e.setBetter_type(better_id);
                    }
                /*} else {
                    Integer fixedPrice = resolveFixedPriceByDetail(buyerId, promotionsActivities, e, better_id, searchType);
                    if (fixedPrice != null) {
                        e.setBetter_price(fixedPrice);
                        e.setBetter_type(better_id);
                    }
                }*/
                return ;
            }
            case 60: {
                resolveGeneralBetter(buyerId, promotionsActivities, e, better_id, searchType);
                return;
            }
            case 20: {
                // 普通打折
                resolveGeneralRule(buyerId, promotionsActivities, e, better_id, searchType);
                break;
            }
            case 40: {
                // 普通满减
                resolveGeneralRule(buyerId, promotionsActivities, e, better_id, searchType);
                break;
            }
            case 10: {
                //满赠
                resolveGeneralRule(buyerId, promotionsActivities, e, better_id, searchType);
                break;
            }
            case 30: {
                //包邮
                resolveGeneralRule(buyerId, promotionsActivities, e, better_id, searchType);
            }
        }
    }

    private void resolveGeneralBetter(Integer buyerId, List<PromotionsActivity> promotionsActivities, EasyToSee e, Integer betterId, Integer searchType) {
        promotionsActivities.sort(((o1, o2) -> o2.getPromotionsRule().getCreateTime().compareTo(o1.getPromotionsRule().getCreateTime())));
        if (promotionsActivities.size() > 0) {
            PromotionsActivity promotionsActivity = promotionsActivities.get(0);
            resolvePromotionsTags(promotionsActivity, buyerId, betterId, e, searchType);
        }
    }

    /**
     * 解析普通活动
     *
     * @param buyerId
     * @param promotionsActivities
     * @param e
     */
    private void resolveGeneralRule(Integer buyerId, List<PromotionsActivity> promotionsActivities, EasyToSee e, Integer better_id, Integer searchType) {
        promotionsActivities.sort(((o1, o2) -> o2.getPromotionsRule().getCreateTime().compareTo(o1.getPromotionsRule().getCreateTime())));
        //使用普通for进行remove操作
        Integer min = null;
        Integer max = null;
        Integer count = 0;
        for (int i = 0; i < promotionsActivities.size(); i++) {
            PromotionsActivity promotionsActivity = promotionsActivities.get(i);
            Integer result = resolvePromotionsTags(promotionsActivity, buyerId, better_id, e, searchType);
            if (result == null) {
                return;
            } else if (result == 1 && better_id == 10 || better_id == 30) {
                //在分组中移除
                promotionsActivities.remove(i);
                //在列表中移除
                e.getProCouponEasyToSee().getProCouponList().getPromotionsActivities().remove(promotionsActivity);
                // 为啥要return 因为同种活动优先只显示1条 打折和满减则要找最优惠的，则需要全部遍历完
                return;
            }
            // 返回的都是打折的折扣和满减的元
            if (better_id == 20) {
                if (min == null || result < min) {
                    min = result;
                    count = i;
                }
                continue;
            } else if (better_id == 40) {
                if (max == null || result > max) {
                    max = result;
                    count = i;
                }
            }
        }
        String tags = "";
        PromotionsActivity promotionsActivity = promotionsActivities.get(count);
        switch (better_id) {
            case 20: {
                DiscountRule discountRule = JSON.parseObject(promotionsActivity.getPromotionsRule().getPromotionsRule(), DiscountRule.class);
                if (discountRule == null) {
                    return;
                }
                resolveTagsByDiscountRule(promotionsActivity, discountRule, buyerId, better_id, e, tags, searchType);
                break;
            }

            case 40: {
                //满减
                ReduceMoneyRule reduceMoneyRule = JSON.parseObject(promotionsActivity.getPromotionsRule().getPromotionsRule(), ReduceMoneyRule.class);
                if (reduceMoneyRule == null) {
                    return;
                }
                resolveTagsByReduceMoneyRule(promotionsActivity, reduceMoneyRule, buyerId, better_id, e, tags, searchType);
                break;
            }
        }
        //然后移除最优惠的
        promotionsActivities.remove(promotionsActivity);
        //在列表中移除(讲道理，这一步是多余的，但是~~~)
        e.getProCouponEasyToSee().getProCouponList().getPromotionsActivities().remove(promotionsActivity);

    }

    /**
     * 解析商品详情限价
     *
     * @param buyerId
     * @param promotionsActivities
     * @param e
     * @param better_id            @return
     */
    private Integer resolveFixedPriceByDetail(Integer buyerId, List<PromotionsActivity> promotionsActivities, EasyToSee e, Integer better_id, Integer searchType) {
        //排序
        //promotionsActivities.sort(((o1, o2) -> o1.getPromotionsRule().getCreateTime().isBefore(o2.getPromotionsRule().getCreateTime()) ? 1 : -1));
        promotionsActivities.sort(((o1, o2) -> o2.getPromotionsRule().getCreateTime().compareTo(o1.getPromotionsRule().getCreateTime())));
        //获取最新的
        for (int i = 0; i < promotionsActivities.size(); i++) {
            //尝试获取第一个
            PromotionsActivity promotionsActivity = promotionsActivities.get(i);
            Integer better_price = resolvePromotionsTags(promotionsActivity, buyerId, better_id, e, searchType);
            if (better_price != null) {
                //判断是否首单
                boolean isFirstOrder = isFirstOrder(promotionsActivity, buyerId);
                String tags = "";
                if (isFirstOrder) {
                    tags += "首单特价" + (save2Digit(better_price / 100.00d));
                } else {
                    tags += "特价" + (save2Digit(better_price / 100.00d));
                }
                isAddTags(tags, e, searchType);
                //移除
                promotionsActivities.remove(promotionsActivity);
                e.getProCouponEasyToSee().getPricesActivity().remove(promotionsActivity);
                return better_price;
            }
        }
        return null;

    }

    /**
     * 限价解析
     *
     * @param buyerId              用于对首单解析
     * @param promotionsActivities
     * @param e
     * @param better_id            活动类型
     */
    private Integer resolveFixedPrice(Integer buyerId, List<PromotionsActivity> promotionsActivities, EasyToSee e, Integer better_id, Integer searchType) {
        //排序
        //promotionsActivities.sort(((o1, o2) -> o1.getPromotionsRule().getCreateTime().isBefore(o2.getPromotionsRule().getCreateTime()) ? 1 : -1));
        promotionsActivities.sort(((o1, o2) -> o2.getPromotionsRule().getCreateTime().compareTo(o1.getPromotionsRule().getCreateTime())));
        //比较价格
        Integer min = null;
        Integer count = 0;
        for (int i = 0; i < promotionsActivities.size(); i++) {
            PromotionsActivity promotionsActivity = promotionsActivities.get(i);
            Integer better_price = resolvePromotionsTags(promotionsActivity, buyerId, better_id, e, searchType);
            if (better_price == null) {
                continue;
            }
            if (min == null || better_price < min) {
                count = i;
                min = better_price;
            }
        }
        //拿一遍它的引用 下面remove引用防止空指针
        PromotionsActivity promotionsActivity = promotionsActivities.get(count);
        //移除
        promotionsActivities.remove(count);
        e.getProCouponEasyToSee().getPricesActivity().remove(promotionsActivity);
        if (min != null) {
            String tags = "特价" + (save2Digit(min / 100.00d));
            isAddTags(tags, e, searchType);
        }
        //限价解析完成
        return min;

    }

    public String save2Digit(double source) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(source);
    }

    private Integer resolvePromotionsTags(PromotionsActivity promotionsActivity, Integer buyerId, Integer better_id, EasyToSee e, Integer searchType) {
        String tags = "";
        switch (better_id) {
            case 50: {
                //限价
                FixedPriceRule fixedPriceRule = JSON.parseObject(promotionsActivity.getPromotionsRule().getPromotionsRule(), FixedPriceRule.class);
                if (fixedPriceRule == null) {
                    return null;
                }
                if (fixedPriceRule.getRuleType() == 1) {
                    return fixedPriceRule.getFixedPrice();
                } else if (fixedPriceRule.getRuleType() == 2) {
                    for (Map<String, Integer> map : fixedPriceRule.getRules()) {
                        if (e.getGoodsId().equals(map.get("goodsId").toString())) {
                            return map.get("fixedPrice");
                        }
                    }
                    ;
                }
                return null;
            }
            case 60: {
                //拼团
                GroupBookingRule groupBookingRule = JSON.parseObject(promotionsActivity.getPromotionsRule().getPromotionsRule(), GroupBookingRule.class);
                if (groupBookingRule == null) {
                    return null;
                }
                return resolveGroupBookingRule(promotionsActivity, groupBookingRule, buyerId, better_id, e, searchType);
            }
            case 20: {
                //打折
                DiscountRule discountRule = JSON.parseObject(promotionsActivity.getPromotionsRule().getPromotionsRule(), DiscountRule.class);
                if (discountRule == null) {
                    return null;
                }
                return resolveTagsByDiscountRuleBetterPrice(promotionsActivity, discountRule, buyerId, better_id, e);
            }
            case 40: {
                //满减
                ReduceMoneyRule reduceMoneyRule = JSON.parseObject(promotionsActivity.getPromotionsRule().getPromotionsRule(), ReduceMoneyRule.class);
                if (reduceMoneyRule == null) {
                    return null;
                }
                return resolveTagsByReduceMoneyRuleBetterPrice(promotionsActivity, reduceMoneyRule, buyerId, better_id, e);

            }
            case 10: {
                //满赠
                GiftRule giftRule = JSON.parseObject(promotionsActivity.getPromotionsRule().getPromotionsRule(), GiftRule.class);
                if (giftRule == null) {
                    return null;
                }
                return resolveTagsByGiftRule(promotionsActivity, giftRule, buyerId, better_id, e, tags, searchType);

            }
            case 30: {
                //包邮
                FreePostageRule freePostageRule = JSON.parseObject(promotionsActivity.getPromotionsRule().getPromotionsRule(), FreePostageRule.class);
                if (freePostageRule == null) {
                    return null;
                }
                boolean isFireePost = e.getTitles().contains("包邮");
                if (!isFireePost) {
                    e.getTitles().add("包邮");
                    return 1;
                }
                return null;
            }
        }
        return -1;
    }

    public void isAddTags(String tags, EasyToSee e, Integer searchType) {
        int currentCount = 0;
        int maxCount = 0;
        if (searchType == 0) {
            maxCount = SEARCH_GOODS;
        } else if (searchType == 1) {
            maxCount = EasyToSeeConstants.GOODS_DETAIL;
        }
        currentCount = e.getTags().size();
        if (currentCount < maxCount) {
            if (!e.getTags().contains(tags))
                e.getTags().add(tags);
        }
    }

    /**
     * 解析拼团活动
     *
     * @param promotionsActivity
     * @param groupBookingRule
     * @param buyerId
     * @param better_id
     * @param e
     * @param searchType
     * @return
     */
    private Integer resolveGroupBookingRule(PromotionsActivity promotionsActivity, GroupBookingRule groupBookingRule, Integer buyerId, Integer better_id, EasyToSee e, Integer searchType) {
        List<Map<String, Integer>> rules = groupBookingRule.getRules();
        String tags = "";
        switch (groupBookingRule.getRuleType()) {
            case 1: {
                //统一拼团价
                Map<String, Integer> sigle = rules.get(0);
                tags += "拼团价:" + save2Digit(sigle.get("groupPrice") / 100.00d);
                e.setBetter_price(sigle.get("groupPrice"));
                e.setBetter_type(better_id);
                isAddTags(tags, e, searchType);
                return 1;
            }
            case 2: {
                for (Map<String, Integer> temp : rules) {
                    Integer goodsId = temp.get("goodsId");
                    if (e.getGoodsId().equals(goodsId.toString())) {
                        tags += "拼团价:" + save2Digit(temp.get("groupPrice") / 100.00d);
                        e.setBetter_price(temp.get("groupPrice"));
                        e.setBetter_type(better_id);
                        isAddTags(tags, e, searchType);
                        return 1;
                    }
                }
                return 1;
            }
            case 3: {
                //统一拼团价
                Map<String, Integer> sigle = rules.get(0);
                int goodsId = Integer.parseInt(e.getGoodsId());
                Goods goods = goodsMapper.getBySiteIdAndGoodsId(goodsId, promotionsActivity.getSiteId());
                Integer groupPrice = orderDeductionUtils.discountMoney(goods.getShopPrice(), groupBookingRule.getIsMl(), groupBookingRule.getIsRound(), sigle.get("groupDiscount"));
                groupPrice = goods.getShopPrice() - groupPrice;
                tags += "拼团价:" + save2Digit(groupPrice / 100.00d);
                e.setBetter_price(groupPrice);
                e.setBetter_type(better_id);
                isAddTags(tags, e, searchType);
                return 1;
            }
            case 4: {
                for (Map<String, Integer> temp : rules) {
                    Integer goodsId = temp.get("goodsId");
                    if (e.getGoodsId().equals(goodsId.toString())) {
                        Goods goods = goodsMapper.getBySiteIdAndGoodsId(goodsId, promotionsActivity.getSiteId());
                        Integer groupPrice = orderDeductionUtils.discountMoney(goods.getShopPrice(), groupBookingRule.getIsMl(), groupBookingRule.getIsRound(), temp.get("groupDiscount"));
                        groupPrice = goods.getShopPrice() - groupPrice;
                        tags += "拼团价:" + save2Digit(groupPrice / 100d);
                        e.setBetter_price(temp.get("groupPrice"));
                        e.setBetter_type(better_id);
                        isAddTags(tags, e, searchType);
                        return 1;
                    }
                }
            }
        }
        return null;
    }

    private Integer resolveTagsByGiftRule(PromotionsActivity promotionsActivity, GiftRule giftRule, Integer buyerId, Integer better_id, EasyToSee e, String tags, Integer searchType) {
        List<GiftRule.RuleCondition> ruleConditions = giftRule.getRuleConditions();
        boolean firstOrder = isFirstOrder(promotionsActivity, buyerId);
        if (firstOrder) {
            tags = "首单";
        }
        switch (giftRule.getRuleType()) {
            case 1: {
                //满几件送几件
                if (ruleConditions.size() == 1) {
                    //1层阶梯
                    GiftRule.RuleCondition ruleCondition = ruleConditions.get(0);
                    tags += "满" + ruleCondition.getMeetNum() + "件送" + (giftRule.getSendType() == 2 ? ruleCondition.getSendNum() + "件" : "礼品");
                } else {
                    OptionalInt meet_num = ruleConditions.stream().map(map -> map.getMeetNum()).mapToInt(Integer::intValue)
                        .max();
                    tags += "最高满" + meet_num.orElseGet(() -> 0) + "件送" + (giftRule.getSendType() == 2 ? ruleConditions.stream()
                        .map(map -> map.getSendNum()).mapToInt(Integer::intValue)
                        .max().orElseGet(() -> 0) + "件" : "礼品");
                }
                isAddTags(tags, e, searchType);
                return 1;
            }
            case 2: {
                //满多少元送几件
                if (ruleConditions.size() == 1) {
                    //1层阶梯
                    GiftRule.RuleCondition ruleCondition = ruleConditions.get(0);
                    tags += "满" + save2Digit(ruleCondition.getMeetMoney() / 100.00d) + "元送" + (giftRule.getSendType() == 2 ? ruleCondition.getSendNum() + "件" : "礼品");
                } else {
                    OptionalInt meet_money = ruleConditions.stream()
                        .map(GiftRule.RuleCondition::getMeetMoney)
                        .mapToInt(Integer::intValue)
                        .max();
                    tags += "最高满" + save2Digit(meet_money.orElseGet(() -> 0) / 100.00d) + "元送" + (giftRule.getSendType() == 2 ? ruleConditions.stream()
                        .map(map -> map.getSendNum()).mapToInt(Integer::intValue)
                        .max().orElseGet(() -> 0) + "件" : "礼品");
                }
                isAddTags(tags, e, searchType);
                return 1;
            }
        }
        return null;
    }

    private Integer resolveTagsByReduceMoneyRule(PromotionsActivity promotionsActivity, ReduceMoneyRule reduceMoneyRule, Integer buyerId, Integer better_id, EasyToSee e, String tags, Integer searchType) {
        List<ReduceMoneyRule.InnerRule> rules = reduceMoneyRule.getRules();
        boolean firstOrder = isFirstOrder(promotionsActivity, buyerId);
        if (firstOrder) {
            tags = "首单";
        }
        switch (reduceMoneyRule.getRuleType()) {
            case 1: {
                //立减
                ReduceMoneyRule.InnerRule innerRule = rules.get(0);
                tags += "立减" + save2Digit(innerRule.getReduceMoney() / 100.00d) + "元";
                isAddTags(tags, e, searchType);
                return 1;
            }
            case 2: {
                ReduceMoneyRule.InnerRule innerRule = rules.get(0);
                tags += "每满" + save2Digit(innerRule.getMeetMoney() / 100.00d) + "减" + save2Digit(innerRule.getReduceMoney() / 100.00d);
                tags += innerRule.getCap() == 0 ? "不封顶" : "";
                isAddTags(tags, e, searchType);
                return 1;
            }
            case 3: {
                if (rules.size() == 1) {
                    ReduceMoneyRule.InnerRule innerRule = rules.get(0);
                    tags += "满" + save2Digit(innerRule.getMeetMoney() / 100.00d) + "减" + save2Digit(innerRule.getReduceMoney() / 100.00d);
                } else {
                    OptionalInt meetMoney = rules.stream().map(inner -> inner.getMeetMoney()).mapToInt(Integer::intValue)
                        .max();
                    OptionalInt reduceMoney = rules.stream().map(inner -> inner.getReduceMoney()).mapToInt(Integer::intValue)
                        .max();
                    tags += "最高满" + save2Digit(meetMoney.orElseGet(() -> 0) / 100.00d) + "减" + save2Digit(reduceMoney.orElseGet(() -> 0) / 100.00d);
                }
                isAddTags(tags, e, searchType);
                return 1;
            }

        }
        return null;
    }

    private Integer resolveTagsByReduceMoneyRuleBetterPrice(PromotionsActivity promotionsActivity, ReduceMoneyRule reduceMoneyRule, Integer buyerId, Integer better_id, EasyToSee e) {
        List<ReduceMoneyRule.InnerRule> rules = reduceMoneyRule.getRules();
        switch (reduceMoneyRule.getRuleType()) {
            case 1: {
                //立减
                ReduceMoneyRule.InnerRule innerRule = rules.get(0);
                return innerRule.getReduceMoney();
            }
            case 2: {
                ReduceMoneyRule.InnerRule innerRule = rules.get(0);
                return innerRule.getReduceMoney();
            }
            case 3: {
                OptionalInt reduceMoney = rules.stream()
                    .map(inner -> inner.getReduceMoney())
                    .mapToInt(Integer::intValue)
                    .max();
                return reduceMoney.orElseGet(null);
            }

        }
        return null;
    }

    /**
     * 解析通过打折规则解析标签
     *
     * @param promotionsActivity
     * @param discountRule
     * @param buyerId
     * @param better_id
     * @param e
     * @param tags
     * @return
     */
    private Integer resolveTagsByDiscountRule(PromotionsActivity promotionsActivity, DiscountRule discountRule, Integer buyerId, Integer better_id, EasyToSee e, String tags, Integer searchType) {
        List<Map<String, Integer>> rules = discountRule.getRules();

        boolean firstOrder = isFirstOrder(promotionsActivity, buyerId);
        if (firstOrder) {
            tags = "首单";
        }
        switch (discountRule.getRuleType()) {
            case 1: {
                Integer direct_discount = rules.get(0).get("direct_discount");
                tags += (direct_discount / 10.0f) + "折";
                //返回-1 ，立刻结束该函数
                isAddTags(tags, e, searchType);
                return 1;
            }
            case 2: {
                if (rules.size() == 1) {
                    Map<String, Integer> discountRuleSigle = rules.get(0);
                    tags += "满" + save2Digit(discountRuleSigle.get("meet_money") / 100.00d) + "元" + discountRuleSigle.get("discount") / 10.0f + "折";
                } else {
                    //暂时用这种解析方式，比较清爽
                    OptionalInt meet_money = rules.stream().map(map -> map.get("meet_money")).mapToInt(Integer::intValue)
                        .max();
                    OptionalInt discount = rules.stream().map(map -> map.get("discount")).mapToInt(Integer::intValue)
                        .min();
                    tags += "最高满" + save2Digit(meet_money.orElseGet(() -> 0) / 100.00d) + "元" + discount.orElseGet(() -> 0) / 10.0f + "折";
                }
                isAddTags(tags, e, searchType);
                return 1;
            }
            case 3: {
                if (rules.size() == 1) {
                    Map<String, Integer> discountRuleSigle = rules.get(0);
                    tags += "满" + discountRuleSigle.get("meet_num") + "件" + discountRuleSigle.get("discount") / 10.0f + "折";
                } else {
                    OptionalInt meet_num = rules.stream().map(map -> map.get("meet_num")).mapToInt(Integer::intValue)
                        .max();
                    OptionalInt discount = rules.stream().map(map -> map.get("discount")).mapToInt(Integer::intValue)
                        .min();
                    tags += "最高" + meet_num.orElseGet(() -> 0) + "件" + discount.orElseGet(() -> 0) / 10.0f + "折";
                }
                isAddTags(tags, e, searchType);
                return 1;
            }
            case 4: {
                Map<String, Integer> discountRuleSigle = rules.get(0);
                tags += "第" + discountRuleSigle.get("rate") + "件" + discountRuleSigle.get("discount") / 10.0f + "折";
                isAddTags(tags, e, searchType);
                return 1;
            }
            case 5: {
                //分别打折，只需要匹配到自己的商品即可
                rules.stream().forEach(map -> {
                    Integer goodsId = map.get("goodsId");
                    if (e.getGoodsId().equals(goodsId.toString())) {
                        String tempTags = map.get("discount") / 10.0f + "折";
                        isAddTags(tempTags, e, searchType);
                    }
                });
                return 1;
            }

        }
        return null;
    }


    /**
     * 解析通过打折规则解析打折信息
     *
     * @param promotionsActivity
     * @param discountRule
     * @param buyerId
     * @param better_id
     * @param e
     * @return
     */
    private Integer resolveTagsByDiscountRuleBetterPrice(PromotionsActivity promotionsActivity, DiscountRule discountRule, Integer buyerId, Integer better_id, EasyToSee e) {
        List<Map<String, Integer>> rules = discountRule.getRules();
        switch (discountRule.getRuleType()) {
            case 1: {
                return rules.get(0).get("direct_discount");
            }
            case 2: {
                OptionalInt discount = rules.stream().map(map -> map.get("discount")).mapToInt(Integer::intValue)
                    .min();
                return discount.orElseGet(() -> 0);
            }
            case 3: {
                OptionalInt discount = rules.stream().map(map -> map.get("discount")).mapToInt(Integer::intValue)
                    .min();
                return discount.orElseGet(() -> 0);
            }
            case 4: {
                Map<String, Integer> discountRuleSigle = rules.get(0);
                return discountRuleSigle.get("discount");
            }
            case 5: {
                //分别打折，只需要匹配到自己的商品即可
                for (Map<String, Integer> map : rules) {
                    Integer goodsId = map.get("goodsId");
                    if (e.getGoodsId().equals(goodsId.toString())) {
                        return map.get("discount");
                    }
                }
            }

        }
        return null;
    }

    public boolean isFirstOrder(PromotionsActivity promotionsActivity, Integer buyerId) {
        if (promotionsActivity.getPromotionsRule().getIsFirstOrder() == 1) {
            //首单，判断当前用户是否具有订单
            if (buyerId == null) {
                return false;
            } else {
                return orderService.checkUserFirstOrderByPayment(promotionsActivity.getSiteId(), buyerId);
            }
        } else {
            return false;
        }
    }
    /* 13 期一眼看尽优惠 老代码
    public void proccessTagsForSearch(Integer siteId, Integer userId, List<GoodsInfos> goodsInfos, String goodsIds) {
        Map<String, Object> result = new HashMap();
        Integer buyerId = null;
        EasyToSeeParam easyToSeeParam = new EasyToSeeParam();
        easyToSeeParam.setSiteId(siteId);
        easyToSeeParam.setUserId(userId);
        easyToSeeParam.setGoodsIds(goodsIds);
        easyToSeeParam.setSearchType(0);
        try {
            if (userId != null) {
                Member mobileById = memberMapper.getMemberByMemberId(siteId, userId);
                if (mobileById != null) {
                    userId = mobileById.getMemberId();
                    buyerId = mobileById.getBuyerId();
                }
            }
            result.put("isNeedAuth", false);
            List<EasyToSee> easyToSees = filterActivityGroupGoodsIds(easyToSeeParam);
            if (userId == null) {
                couponFilterService.addCoupons(siteId, easyToSees, goodsIds, result);
            } else {
                couponFilterService.addCoupons4Easy2See(siteId, goodsIds, userId, easyToSees);
            }
            GroupEasy2See(siteId, easyToSees, userId, result);

            int tagsNum = EasyToSeeConstants.SEARCH_GOODS;
            // 0商品搜索，1 商品详情
            resolveEasy2SeeTransTags(easyToSees, buyerId, 0, tagsNum);
            for (int i = 0; i < goodsInfos.size(); i++) {
                GoodsInfos info = goodsInfos.get(i);
                easyToSees.stream().forEach(e -> {
                    if (e.getGoodsId().equals(String.valueOf(info.getGoods_id()))) {
                        List<String> coupon_types = new ArrayList();
                        coupon_types.addAll(e.getTags());
                        Map<String, Object> tags = new HashMap<String, Object>();
                        tags.put("promotion", e.getTags());
                        List<String> coupon = new ArrayList<String>();
                        info.setPromotions_price(e.getBetter_price());
                        info.setPromotions_type(e.getBetter_type());
                        e.getCouponTags().stream().forEach(ec -> {
                            coupon.add(ec.getTags());
                        });
                        tags.put("coupon", coupon);
                        info.setCoupon_types(tags);
                        info.setTags_titles(e.getTitles());
                        info.setNeedAuth((Boolean) result.get("isNeedAuth"));
                    }
                });
            }
        } catch (Exception e) {
            logger.error("查询优惠券标签失败:{}", e);
        }

    }*/

    public void proccessTagsForSearch(Integer siteId, Integer userId, List<GoodsInfos> goodsInfos, String goodsIds) {
        Map<String, Object> result = new HashMap();
        Integer buyerId = null;
        try {
            if (userId != null) {
                Member mobileById = memberMapper.getMemberByMemberId(siteId, userId);
                if (mobileById != null) {
                    userId = mobileById.getMemberId();
                    buyerId = mobileById.getBuyerId();
                }
            }
            TagsParam tagsParam = new TagsParam(siteId,userId,goodsIds,SEARCH_GOODS);
            CouponTagsFilter couponFilter = new CouponTagsFilter(sc,tagsParam);
            PromotionsTagsFilter promotionsFilter = new PromotionsTagsFilter(sc,tagsParam);
            couponFilter.collection().grouping().resolve();
            promotionsFilter.collection().grouping().resolve();
            List<TagsGoodsCoupon> couponFilterTags = couponFilter.getTags();
            List<TagsGoodsPromotions> promotionsFilterTags = promotionsFilter.getTags();
            for (int i = 0; i < goodsInfos.size(); i++) {
                GoodsInfos info = goodsInfos.get(i);
                //找到对应商品的标签类
                TagsGoodsCoupon tagsGoodsCoupon = couponFilterTags.stream().filter(tg -> tg.getGoodsId().equals(String.valueOf(info.getGoods_id()))).findFirst().get();
                TagsGoodsPromotions tagsGoodsPromotions = promotionsFilterTags.stream().filter(tg -> tg.getGoodsId().equals(String.valueOf(info.getGoods_id()))).findFirst().get();
               //置放活动和优惠券的标签
                Map<String, Object> tags = new HashMap<String, Object>();
                tags.put("promotion",tagsGoodsPromotions.getTags());
                tags.put("coupon",tagsGoodsCoupon.getTags());
                //置放 特价显示价格及类型
//                TagsGoods.BestTitle couponTitle = tagsGoodsCoupon.getBestTitle();
                TagsGoods.BestTitle promotionTitle = tagsGoodsPromotions.getBestTitle();
                info.setPromotions_price(promotionTitle.getBestPrice());
                info.setPromotions_type(promotionTitle.getBestType());
                info.setCoupon_types(tags);
                info.setNeedAuth(couponFilter.getNeedAuth());
                Set<String> titles = tagsGoodsCoupon.getTitles();
                titles.addAll(tagsGoodsPromotions.getTitles());
                info.setTags_titles(titles);
            }
        } catch (Exception e) {
            logger.error("查询优惠券标签失败:{}", e);
        }

    }

    public Map<String, List<PromotionsActivity>> handlerGoods(String goodsIds) {
        Map<String, List<PromotionsActivity>> map = new HashMap<>();
        List<String> gs = Arrays.asList(goodsIds.split(","));
        gs.forEach(goodsId -> {
            map.put(goodsId, new ArrayList<PromotionsActivity>());
        });
        return map;
    }
}
