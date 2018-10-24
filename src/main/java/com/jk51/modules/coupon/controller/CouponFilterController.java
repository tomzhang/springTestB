package com.jk51.modules.coupon.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.CouponFilterParams;
import com.jk51.model.coupon.tags.TagsGoods;
import com.jk51.model.coupon.tags.TagsGoodsCoupon;
import com.jk51.model.coupon.tags.TagsGoodsPromotions;
import com.jk51.model.coupon.tags.TagsParam;
import com.jk51.model.order.Member;
import com.jk51.model.promotions.*;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponFilterService;
import com.jk51.modules.coupon.tags.CouponTagsFilter;
import com.jk51.modules.coupon.tags.PromotionsTagsFilter;
import com.jk51.modules.coupon.tags.TagsGroupUtils;
import com.jk51.modules.promotions.request.LabelParam;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.stream.Collectors;

/**
 * filename :com.jk51.modules.coupon.controller.
 * author   :zw
 * date     :2017/6/28
 * Update   :
 * 微信端按商品查询是否有可领取的优惠券
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("couponFilter")
public class CouponFilterController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CouponFilterService couponFilterService;
    @Autowired
    private PromotionsFilterService promotionsFilterService;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private ServletContext sc;

    @RequestMapping(value = "/getActivityGroupByGoods")
    @ResponseBody
    public ReturnDto getActivityGroupByGoods(@RequestBody CouponFilterParams couponFilterParams) {
        ReturnDto returnDto = filterArgs(couponFilterParams);
        if (couponFilterParams.getUserId() == null) {
            returnDto = ReturnDto.buildFailedReturnDto("userId不能为空");
        }
        if (returnDto != null) {
            return returnDto;
        }
        try {
            /*Map<String, List<PromotionsActivity>> maps = promotionsFilterService.handlerGoods(couponFilterParams.getGoodsId());
            List<PromotionsActivity> promotionsActivityList = promotionsFilterService.filterByGoodsId(couponFilterParams);
            Map<String, List<PromotionsActivity>> group = getBuildActivtyGroup(maps, promotionsActivityList);*/
            TagsParam tagsParam = new TagsParam(couponFilterParams.getSiteId(),couponFilterParams.getUserId(),couponFilterParams.getGoodsId(),Integer.MAX_VALUE);
            PromotionsTagsFilter promotionsFilter = new PromotionsTagsFilter(sc,tagsParam);
            promotionsFilter.collection();
            HashMap<String, List<PromotionsActivity>> group = new HashMap<String, List<PromotionsActivity>>() {{
                promotionsFilter.getTags().forEach(tgp -> {
                    put(tgp.getGoodsId(), tgp.getRules());
                });
            }};
            returnDto = ReturnDto.buildSuccessReturnDto(group);
        } catch (Exception e) {
            logger.error("查询按商品分组活动失败{}",e);
            returnDto = ReturnDto.buildFailedReturnDto("查询按商品分组活动失败");
        }

        return returnDto;

    }

    //待稳定后删除
    /*private Map<String, List<PromotionsActivity>> getBuildActivtyGroup(Map<String,List<PromotionsActivity>> maps, List<PromotionsActivity> promotionsActivityList) {
        Map<String, Object> result = new HashMap<>();
        maps.keySet().forEach(goodsId->{
            promotionsActivityList.forEach(pa -> {
                PromotionsRule promotionsRule = promotionsFilterService.checkPromotionsRule(pa, goodsId);
                if (promotionsRule != null) {
                    pa.setPromotionsRule(promotionsRule);
                    List<PromotionsActivity> promotionsActivities = maps.get(goodsId);
                    promotionsActivities.add(pa);
                }
            });
        });
        return maps;
    }*/
    @ResponseBody
    @RequestMapping(value = "/canReceiveCouponLazy")
    public ReturnDto canReceiveCouponLazy(@RequestBody CouponFilterParams couponFilterParams) {
        logger.info("参数:{}", couponFilterParams);

        ReturnDto returnDto = filterArgs(couponFilterParams);
        if (returnDto != null) {
            return returnDto;
        }
        Integer siteId = couponFilterParams.getSiteId();
        Integer userId = couponFilterParams.getUserId();
        Map<String, Object> map = new HashMap<>();
        try {
            TagsParam tagsParam = new TagsParam(siteId,userId,couponFilterParams.getGoodsId(),EasyToSeeConstants.GOODS_DETAIL);
            CouponTagsFilter couponFilter = new CouponTagsFilter(sc,tagsParam);
            PromotionsTagsFilter promotionsFilter = new PromotionsTagsFilter(sc,tagsParam);
            couponFilter.collection();
            promotionsFilter.collection();
            List<TagsGoodsCoupon> coupons = couponFilter.getTags();
            List<TagsGoodsPromotions> promotions = promotionsFilter.getTags();
            TagsGoodsCoupon tagsGoodsCoupon = coupons.get(0);
            TagsGoodsPromotions tagsGoodsPromotions = promotions.get(0);
            List<CouponActivity> activities = tagsGoodsCoupon.getActivities();
            List<PromotionsActivity> promotionsActivities = tagsGoodsPromotions.getRules();
            map.put("promotionsList",promotionsActivities);
            map.put("couponList", activities);

        }catch (Exception e){
            logger.error("异常发生, {}", e);
            return ReturnDto.buildFailedReturnDto("request failed");
        }
        ReturnDto buildSuccessReturnDto = ReturnDto.buildSuccessReturnDto(map);
        logger.info("返回结果,{}", buildSuccessReturnDto);
        return buildSuccessReturnDto;
    }

    @ResponseBody
    @RequestMapping(value = "/canReceiveCoupon")
    public ReturnDto canReceiveCoupon(@RequestBody CouponFilterParams couponFilterParams) {
        logger.info("参数:{}", couponFilterParams);

        ReturnDto returnDto = filterArgs(couponFilterParams);
        if (returnDto != null) {
            return returnDto;
        }
        Integer siteId = couponFilterParams.getSiteId();
        Integer userId = couponFilterParams.getUserId();
        Integer buyerId = null;
        Map<String, Object> map = new HashMap<>();
        List<CouponActivity> couponActivities = new ArrayList<>();
        try {
            if (userId != null) {
                Member mobileById = memberMapper.getMemberByMemberId(couponFilterParams.getSiteId(), userId);
                if (mobileById != null) {
                    userId = mobileById.getMemberId();
                    buyerId = mobileById.getBuyerId();
                }
            }
            TagsParam tagsParam = new TagsParam(siteId,userId,couponFilterParams.getGoodsId(),EasyToSeeConstants.GOODS_DETAIL);
            CouponTagsFilter couponFilter = new CouponTagsFilter(sc,tagsParam);
            PromotionsTagsFilter promotionsFilter = new PromotionsTagsFilter(sc,tagsParam);
            couponFilter.collection().grouping().resolve();
            promotionsFilter.collection().grouping().resolve();
            List<TagsGoodsCoupon> coupons = couponFilter.getTags();
            List<TagsGoodsPromotions> promotions = promotionsFilter.getTags();
            TagsGoodsCoupon tagsGoodsCoupon = coupons.get(0);
            TagsGoodsPromotions tagsGoodsPromotions = promotions.get(0);
            List<CouponActivity> activities = tagsGoodsCoupon.getActivities();
            List<PromotionsActivity> promotionsActivities = tagsGoodsPromotions.getRules();
            LabelParam labelParam = promotionsFilterService.filterCouponAndPromotions(activities, promotionsActivities, couponFilterService.getCouponRules(couponFilterParams.getSiteId(), couponFilterParams.getUserId()));

            Set<String> titles = tagsGoodsCoupon.getTitles();
            titles.addAll(tagsGoodsPromotions.getTitles());
            String titleTagsString = titles.stream().collect(Collectors.joining(","));
            if (StringUtil.isBlank(titleTagsString)) {
                labelParam.setLabel("");
            } else {
                labelParam.setLabel("[" + titleTagsString + "]");
            }
            map.put("couponLabel", labelParam);
            //处理限价标签，清除 ---并分组
            Map<Integer, List<String>> couponTagsList = tagsGoodsCoupon.getTags().stream().filter(s->!s.contains("特价")).collect(Collectors.groupingBy(TagsGroupUtils::groupByIncludes));
            map.put("couponTagsList", couponTagsList);
            //处理拼团及限价标签，清除 ---并分组
            Map<Integer, List<String>> promotionsTagsList = tagsGoodsPromotions.getTags().stream().filter(s->!s.contains("特价")||s.contains("拼团")).collect(Collectors.groupingBy(TagsGroupUtils::groupByIncludes));
            map.put("promotionsTagsList", promotionsTagsList);
            TagsGoods.BestTitle bestTitle_promotions = tagsGoodsPromotions.getBestTitle();

            if(Objects.isNull(bestTitle_promotions.getBestType()) || Objects.isNull(bestTitle_promotions.getBestPrice())){
                map.put("promotions_price", "00");
                map.put("promotions_type", "00");
            }else {
                map.put("promotions_price", bestTitle_promotions.getBestPrice());
                map.put("promotions_type", bestTitle_promotions.getBestType());
                //当高等级优惠出现时，优惠券倒计时不显示（很久很久以前的优惠券倒计时）
                labelParam.setType(1);
                labelParam.setSaleTime(null);
            }
            map.put("isNeedAuth", couponFilter.getNeedAuth());
        } catch (Exception e) {
            logger.error("异常发生, {}", e);
            return ReturnDto.buildFailedReturnDto("request failed");
        }
        ReturnDto buildSuccessReturnDto = ReturnDto.buildSuccessReturnDto(map);
        logger.info("返回结果,{}", buildSuccessReturnDto);
        return buildSuccessReturnDto;
    }

    /*@ResponseBody  ------------Old 稳定后即可删除
    @RequestMapping(value = "/canReceiveCoupon")
    public ReturnDto canReceiveCoupon(@RequestBody CouponFilterParams couponFilterParams) {
        logger.info("参数:{}", couponFilterParams);

        ReturnDto returnDto = filterArgs(couponFilterParams);
        if (returnDto != null) {
            return returnDto;
        }
        Integer siteId = couponFilterParams.getSiteId();
        Integer userId = couponFilterParams.getUserId();
        Integer buyerId = null;
        Map<String, Object> map = new HashMap<>();
        List<CouponActivity> couponActivities = new ArrayList<>();
        try {
            if (userId != null) {
                Member mobileById = memberMapper.getMemberByMemberId(couponFilterParams.getSiteId(), userId);
                if (mobileById != null) {
                    userId = mobileById.getMemberId();
                    buyerId = mobileById.getBuyerId();
                }
            }
            TagsParam tagsParam = new TagsParam(siteId,userId,couponFilterParams.getGoodsId(),EasyToSeeConstants.GOODS_DETAIL);
            CouponTagsFilter couponFilter = new CouponTagsFilter(sc,tagsParam);
            PromotionsTagsFilter promotionsFilter = new PromotionsTagsFilter(sc,tagsParam);
            couponFilter.collection().grouping().resolve();
            promotionsFilter.collection().grouping().resolve();
            List<TagsGoodsCoupon> coupons = couponFilter.getTags();
            List<TagsGoodsPromotions> promotions = promotionsFilter.getTags();
            TagsGoodsCoupon tagsGoodsCoupon = coupons.get(0);
            TagsGoodsPromotions tagsGoodsPromotions = promotions.get(0);
            List<CouponActivity> activities = tagsGoodsCoupon.getActivities();
            List<PromotionsActivity> promotionsActivities = tagsGoodsPromotions.getRules();
            LabelParam labelParam = promotionsFilterService.filterCouponAndPromotions(activities, promotionsActivities, couponFilterService.getCouponRules(couponFilterParams.getSiteId(), couponFilterParams.getUserId()));

            Set<String> titles = tagsGoodsCoupon.getTitles();
            titles.addAll(tagsGoodsPromotions.getTitles());
            String titleTagsString = titles.stream().collect(Collectors.joining(","));
            if (StringUtil.isBlank(titleTagsString)) {
                labelParam.setLabel("");
            } else {
                labelParam.setLabel("[" + titleTagsString + "]");
            }
            map.put("couponList", activities);
            map.put("promotionsList", promotionsActivities);
            map.put("couponLabel", labelParam);
            Map<Integer, List<String>> couponTagsList = tagsGoodsCoupon.getTags().stream().collect(Collectors.groupingBy(TagsGroupUtils::groupByIncludes));
            map.put("couponTagsList", couponTagsList);
            Map<Integer, List<String>> promotionsTagsList = tagsGoodsPromotions.getTags().stream().collect(Collectors.groupingBy(TagsGroupUtils::groupByIncludes));
            map.put("promotionsTagsList", promotionsTagsList);
            String couponTagsString = tagsGoodsCoupon.getTags().stream().collect(Collectors.joining(","));
            if (StringUtil.isBlank(couponTagsString)) {
                map.put("couponTagsList", "");
            } else {
                map.put("couponTagsList", "[" + couponTagsString + "]");
            }

            String promTagsString = tagsGoodsPromotions.getTags().stream().collect(Collectors.joining(","));
            if (StringUtil.isBlank(promTagsString)) {
                map.put("promotionsTagsList", "");
            } else {
                map.put("promotionsTagsList", "[" + promTagsString + "]");
            }

            TagsGoods.BestTitle bestTitle_promotions = tagsGoodsPromotions.getBestTitle();
            if(Objects.isNull(bestTitle_promotions.getBestType()) || Objects.isNull(bestTitle_promotions.getBestPrice())){
                map.put("promotions_price", "00");
                map.put("promotions_type", "00");
            }else {
                map.put("promotions_price", bestTitle_promotions.getBestPrice());
                map.put("promotions_type", bestTitle_promotions.getBestType());
                //当高等级优惠出现时，优惠券倒计时不显示（很久很久以前的优惠券倒计时）
                labelParam.setType(1);
                labelParam.setSaleTime(null);
            }
            map.put("isNeedAuth", couponFilter.getNeedAuth());
        } catch (Exception e) {
            logger.error("异常发生, {}", e);
            return ReturnDto.buildFailedReturnDto("request failed");
        }
        ReturnDto buildSuccessReturnDto = ReturnDto.buildSuccessReturnDto(map);
        logger.info("返回结果,{}", buildSuccessReturnDto);
        return buildSuccessReturnDto;
    }*/

    private ReturnDto filterArgs(CouponFilterParams couponFilterParams) {
        if (couponFilterParams.getSiteId() == null) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }
        if (couponFilterParams.getGoodsId() == null) {
            return ReturnDto.buildFailedReturnDto("goodsId不能为空");
        }
        /*if (couponFilterParams.getUserId() == null) {
            return ReturnDto.buildFailedReturnDto("userId不能为空");
        }*/
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/couponSign")
    public ReturnDto couponSign(@RequestBody CouponFilterParams couponFilterParams) {
        ReturnDto returnDto = filterArgs(couponFilterParams);
        if (returnDto != null) {
            return returnDto;
        }
        Map<String, String> canUseCoupon = new HashMap<>();
        try {
            List<PromotionsActivity> promotionsActivityList = promotionsFilterService.filterByGoodsId(couponFilterParams);
            canUseCoupon = couponFilterService.getCanUseCoupon(
                couponFilterParams.getSiteId(), couponFilterParams.getGoodsId(), couponFilterParams.getUserId(),
                promotionsActivityList);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnDto.buildFailedReturnDto("request failed");
        }
        return ReturnDto.buildSuccessReturnDto(canUseCoupon);
    }


}
