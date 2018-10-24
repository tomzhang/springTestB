package com.jk51.modules.coupon.tags;

import com.gexin.fastjson.JSON;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.GoodsRule;
import com.jk51.model.coupon.requestParams.LimitRule;
import com.jk51.model.coupon.tags.TagsFilter;
import com.jk51.model.coupon.tags.TagsGoodsCoupon;
import com.jk51.model.coupon.tags.TagsParam;
import com.jk51.model.promotions.EasyToSeeConstants;
import com.jk51.model.promotions.EasyToSeeCoupon;
import com.jk51.modules.coupon.service.CouponFilterService;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import com.jk51.modules.trades.service.TradesService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.jk51.modules.coupon.constants.CouponConstant.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Created by Javen73 on 2018/3/23.
 */
//@Slf4j
public class CouponTagsFilter implements TagsFilter {
    Logger logger = LoggerFactory.getLogger(CouponTagsFilter.class);
    private TagsParam tagsParam;
    private CouponFilterService couponFilterService;
    private PromotionsFilterService promotionsFilterService;
    private TradesService tradesService;
    //结果集 tags
    private List<TagsGoodsCoupon> tags = new ArrayList<>();
    //能够领取的优惠券
    private List<CouponActivity> canReceiveCoupon = new ArrayList<>();
    //所符合条件的优惠券
    private List<CouponRule> collection;
    //当前正在处理的商品，对该商品进行标签解析
    private TagsGoodsCoupon currentGoods;
    //提示前端是否需要登录
    private Boolean NeedAuth;
    //Environment Type 当前循环处理的类型是 限价 or 打折 ?
    private Integer TYPE_ENV;

    //当前调用链的长度collection 、grouping、resolve 主要用于检验前面方法是否执行
    //当前这个没有实现
    private Integer currentProcess;

    @Override
    public CouponTagsFilter collection() {
        if (tagsParam.getMemberId() != null) {
            //领券中心可领取的
            canReceiveCoupon = couponFilterService.getCanReceiveCoupon(tagsParam.getSiteId(), tagsParam.getGoodsIds(), tagsParam.getMemberId());
            //自己已有的
            couponFilterService.addCanUseCoupon(tagsParam.getSiteId(), canReceiveCoupon, tagsParam.getSiteId(), tagsParam.getGoodsIds());
        } else {
            canReceiveCoupon = couponFilterService.allGoodsCouponsUnAuth(tagsParam.getSiteId(), tagsParam.getGoodsIds());
        }
        List<CouponRule> couponRules = new ArrayList<>();
        canReceiveCoupon.forEach(ca -> {
            couponRules.addAll(ca.getCouponRules());
        });


        tags.forEach(tg -> {
            List<CouponActivity> goodsCouponActivity = new ArrayList(Arrays.asList(new Object[canReceiveCoupon.size()]));
            //深层复制  深层复制是会比较两个数组的长度，目标长度少于源目标会抛越界异常
            Collections.copy(goodsCouponActivity, canReceiveCoupon);
            List<CouponActivity> couponActivities = goodsCouponActivity.stream()
                .filter(Objects::nonNull)
                .filter(pa -> Objects.nonNull(pa.getCouponRules()) && pa.getCouponRules().size() > 0)
                .filter(pa -> {
                    List<CouponRule> rules = pa.getCouponRules().stream()
                        .filter(Objects::nonNull)
                        .filter(rule -> {
                            GoodsRule goodsRule = JSON.parseObject(rule.getGoodsRule(), GoodsRule.class);
                            return couponFilterService.checkCouponGoods(tg.getGoodsId(), goodsRule);
                        }).collect(toList());
                    if (rules.size() > 0) {
                        pa.setCouponRules(rules);
                        return true;
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());
            tg.setActivities(couponActivities);
            List<CouponRule> rule = new ArrayList<>();
            couponActivities.forEach(pa -> {
                rule.addAll(pa.getCouponRules());
            });
            tg.setRules(rule);
        });


        //对优惠券的券规则进行收集 并**去重**
        Set<EasyToSeeCoupon> easyToSeeCoupons = couponRules.stream().map(EasyToSeeCoupon::buildEasyToSeeCoupon).collect(toSet());
        collection = easyToSeeCoupons.stream().map(EasyToSeeCoupon::getCoupon).collect(toList());
        tags.forEach(tg -> {
            List<CouponRule> within = collection.stream()
                .filter(Objects::nonNull)
                .filter(rule -> {
                    GoodsRule goodsRule = JSON.parseObject(rule.getGoodsRule(), GoodsRule.class);
                    return couponFilterService.checkCouponGoods(tg.getGoodsId(), goodsRule);
                }).collect(toList());
            tg.getRules().addAll(within);
        });
        return this;
    }

    @Override
    public CouponTagsFilter grouping() {
        tags.forEach(tg -> {
            Map<Integer, List<CouponRule>> tg_group = tg.getRules().stream().collect(Collectors.groupingBy(CouponRule::getCouponType));
            tg.setGroup(tg_group);
        });
        return this;
    }

    @Override
    public CouponTagsFilter resolve() throws Exception {
        for (TagsGoodsCoupon tag : tags) {
            currentGoods = tag;
            while (currentGoods.getTags().size() < tagsParam.getTagsNum() && CouponGroupSize() != 0)
                for (Integer couponType : EasyToSeeConstants.COUPONS_ACTIVITY_ID) {
                    TYPE_ENV = couponType;
                    //属同类型优惠券
                    List<CouponRule> couponRules = currentGoods.getGroup().get(couponType);
                    if (couponRules != null && couponRules.size() > 0)
                        resolveRouter(couponType, couponRules);
                }
        }
        return this;
    }

    private Integer CouponGroupSize() {
        Integer size = 0;
        for (Integer key : currentGoods.getGroup().keySet()) {
            List<CouponRule> couponRules = currentGoods.getGroup().get(key);
            size += couponRules.size();
        }
        return size;
    }

    private void resolveRouter(Integer couponType, List<CouponRule> couponRules) throws Exception {
        String tag = "";
        switch (couponType) {
            case LIMIT_PRICE_COUPON:
                tag = resolveLimitPriceCoupon(couponRules);
                break;
            case CASH_DISCOUNT_COUPON:
                tag = resolveDiscountCoupon(couponRules);
                break;
            case CASH_COUPON:
                tag = resolveCashCoupon(couponRules);
                break;
            case GIFT_COUPON:
                tag = resolveGiftCoupon(couponRules);
                break;
            case FREE_POSTAGE_COUPON:
                if (couponRules.size() > 0) {
                    currentGoods.getTitles().add("包邮");
                    CouponRule remove = couponRules.remove(0);
                    //以防有变，再删除一次
                    currentGoods.getGroup().get(TYPE_ENV).remove(remove);
                }
                break;
            default:
                throw new Exception("没有该类型");
        }
        if (currentGoods.getTags().size() < tagsParam.getTagsNum())
            currentGoods.tags.add(tag);
        if (currentGoods.getTags().size() > 0)
            currentGoods.getTitles().add("用券");
    }

    private String resolveGiftCoupon(List<CouponRule> couponRules) {
        String tag = "";
        couponRules.sort(Comparator.comparing(CouponRule::getCreateTime).reversed());
        CouponRule removeRule = couponRules.remove(0);
        GoodsRule goodsRule = JSON.parseObject(removeRule.getGoodsRule(), GoodsRule.class);
        List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
        switch (goodsRule.getRule_type()) {
            case GOODS_FULL_MONEY:
                if (rule.size() == 1) {
                    Map<String, Integer> ruleSigle = rule.get(0);
                    tag = "满" + ruleSigle.get("meetNum") + "件送" + (goodsRule.getGift_send_type() == 2 ? ruleSigle.get("sendNum") + "件" : "礼品");
                } else {
                    Integer meetNum = rule.stream().map(map -> map.get("meetNum")).mapToInt(Integer::intValue)
                        .max().getAsInt();
                    Integer sendNum = rule.stream().map(map -> map.get("sendNum")).mapToInt(Integer::intValue)
                        .max().getAsInt();
                    tag = "满" + meetNum + "件送" + (goodsRule.getGift_send_type() == 2 ? sendNum + "件" : "礼品");
                }
                break;

            case GOODS_FULL_NUM:
                if (rule.size() == 1) {
                    Map<String, Integer> ruleSigle = rule.get(0);
                    tag = "满" + promotionsFilterService.save2Digit(ruleSigle.get("meetMoney") / 100.00d) + "元送" + (goodsRule.getGift_send_type() == 2 ? ruleSigle.get("sendNum") + "件" : "礼品");
                } else {
                    Integer meetMoney = rule.stream().map(map -> map.get("meetMoney")).mapToInt(Integer::intValue)
                        .max().getAsInt();
                    Integer sendNum = rule.stream().map(map -> map.get("sendNum")).mapToInt(Integer::intValue)
                        .max().getAsInt();
                    tag = "满" + promotionsFilterService.save2Digit(meetMoney / 100.00d) + "元送" + (goodsRule.getGift_send_type() == 2 ? sendNum + "件" : "礼品");
                }
                break;
        }
        //以防有变，再删除一次
        currentGoods.getGroup().get(TYPE_ENV).remove(removeRule);
        String firstOrder = handleFirstOrder(removeRule);
        return firstOrder + tag;
    }

    /**
     * 解析 现金择优
     *
     * @param couponRules
     * @return
     */
    private String resolveCashCoupon(List<CouponRule> couponRules) throws Exception {
        Integer max = Integer.MIN_VALUE;
        int max_index = 0;
        String tags = "";
        for (int i = 0; i < couponRules.size(); i++) {
            CouponRule couponRule = couponRules.get(i);
            GoodsRule goodsRule = JSON.parseObject(couponRule.getGoodsRule(), GoodsRule.class);
            Integer meet_money = 0;
            Integer meet_num = 0;
            Integer reduce = 0;
            switch (goodsRule.getRule_type()) {
                case GOODS_PRICE:
                    Map<String, Integer> rule_4 = (Map<String, Integer>) goodsRule.getRule();
                    reduce = rule_4.get("direct_money");
                    if (reduce > max) {
                        max_index = i;
                        max = reduce;
                        tags = "立减" + promotionsFilterService.save2Digit(reduce / 100.00d) + "元";
                    }
                    break;
                case GOODS_FULL_MONEY:
                    List<Map<String, Integer>> rule_1 = (List<Map<String, Integer>>) goodsRule.getRule();
                    if (rule_1.size() == 1) {
                        Map<String, Integer> ruleSigle = rule_1.get(0);
                        reduce = ruleSigle.get("reduce_price");
                        if (reduce > max) {
                            max = reduce;
                            max_index = i;
                            tags = "满" + promotionsFilterService.save2Digit(ruleSigle.get("meet_money") / 100.00d) + "元减" + promotionsFilterService.save2Digit(reduce / 100.00d) + "元";
                        }

                    } else {
                        meet_money = rule_1.stream().map(map -> map.get("meet_money")).mapToInt(Integer::intValue)
                            .max().getAsInt();
                        reduce = rule_1.stream().map(map -> map.get("reduce_price")).mapToInt(Integer::intValue)
                            .max().getAsInt();
                        if (reduce > max) {
                            max = reduce;
                            max_index = i;
                            tags = "最高满" + promotionsFilterService.save2Digit(meet_money / 100.00d) + "元减" + promotionsFilterService.save2Digit(reduce / 100.00d) + "元";
                        }
                    }
                    break;
                case GOODS_EACH_FULL:
                    Map<String, Integer> rule_0 = (Map<String, Integer>) goodsRule.getRule();
                    reduce = rule_0.get("reduce_price");
                    if (reduce > max) {
                        max = reduce;
                        max_index = i;
                        tags = "每满" + promotionsFilterService.save2Digit(rule_0.get("each_full_money") / 100.00d) + "元减" + promotionsFilterService.save2Digit(reduce / 100.00d) + "元";
                    }
                    break;
                default:
                    throw new Exception("没有该类型");
            }
        }
        CouponRule removeRule = couponRules.remove(max_index);
        //以防有变，再删除一次
        currentGoods.getGroup().get(TYPE_ENV).remove(removeRule);
        String firstOrder = handleFirstOrder(removeRule);
        return firstOrder + tags;
    }

    /**
     * 解析打折择优
     *
     * @param couponRules
     */
    private String resolveDiscountCoupon(List<CouponRule> couponRules) throws Exception {
        Integer min = Integer.MAX_VALUE;
        int min_index = 0;
        String tags = "";
        for (int i = 0; i < couponRules.size(); i++) {
            CouponRule couponRule = couponRules.get(i);
            GoodsRule goodsRule = JSON.parseObject(couponRule.getGoodsRule(), GoodsRule.class);
            Integer discount = 0;
            Integer meet_money = 0;
            Integer meet_num = 0;
            switch (goodsRule.getRule_type()) {
                case GOODS_PRICE:
                    Map<String, Integer> rule_4 = (Map<String, Integer>) goodsRule.getRule();
                    discount = rule_4.get("direct_discount");
                    if (discount < min) {
                        min_index = i;
                        min = discount;
                        tags = discount / 10.0f + "折";
                    }
                    break;
                case GOODS_FULL_MONEY:
                    //每满多少折
                    List<Map<String, Integer>> rule_1 = (List<Map<String, Integer>>) goodsRule.getRule();
                    if (rule_1.size() == 1) {
                        Map<String, Integer> ruleSigle = rule_1.get(0);
                        meet_money = ruleSigle.get("meet_money");
                        discount = ruleSigle.get("discount");
                        if (discount < min) {
                            min = discount;
                            min_index = i;
                            tags = "满" + promotionsFilterService.save2Digit(meet_money / 100.00d) + "元" + discount / 10.0f + "折";
                        }
                    } else {
                        meet_money = rule_1.stream().map(map -> map.get("meet_money")).mapToInt(Integer::intValue)
                            .max().getAsInt();
                        discount = rule_1.stream().map(map -> map.get("discount")).mapToInt(Integer::intValue)
                            .min().getAsInt();
                        if (discount < min) {
                            min = discount;
                            min_index = i;
                            tags = "最高满" + promotionsFilterService.save2Digit(meet_money / 100.00d) + "元" + discount / 10.0f + "折";
                        }
                    }
                    break;
                case GOODS_SECOND_DISCOUNT:
                    Map<String, Integer> rule_5 = (Map<String, Integer>) goodsRule.getRule();
                    discount = rule_5.get("discount");
                    if (discount < min) {
                        min = discount;
                        min_index = i;
                        tags += "第" + rule_5.get("how_piece") + "件" + discount / 10.0f + "折";
                    }
                    break;
                case GOODS_FULL_NUM:
                    List<Map<String, Integer>> rule_2 = (List<Map<String, Integer>>) goodsRule.getRule();
                    if (rule_2.size() == 1) {
                        Map<String, Integer> sigle = rule_2.get(0);
                        discount = sigle.get("discount");
                        if (discount < min) {
                            min = discount;
                            min_index = i;
                            tags += "满" + sigle.get("meet_num") + "件" + sigle.get("discount") / 10.0f + "折";
                        }
                    } else {
                        meet_num = rule_2.stream().map(map -> map.get("meet_num")).mapToInt(Integer::intValue)
                            .max().getAsInt();
                        discount = rule_2.stream().map(map -> map.get("discount")).mapToInt(Integer::intValue)
                            .min().getAsInt();
                        if (discount < min) {
                            min = discount;
                            min_index = i;
                            tags += "最高满" + meet_num + "件" + discount / 10.0f + "折";
                        }
                    }
                    break;
                default:
                    throw new Exception("没有找到该类型");
            }
        }
        //删除最高折扣券
        CouponRule removeRule = couponRules.remove(min_index);
        //以防有变，再删除一次
        currentGoods.getGroup().get(TYPE_ENV).remove(removeRule);
        String firstOrder = handleFirstOrder(removeRule);
        return firstOrder + tags;
    }

    /**
     * 解析限价择优
     *
     * @param couponRules
     */
    private String resolveLimitPriceCoupon(List<CouponRule> couponRules) {
        Integer min = Integer.MAX_VALUE;
        int min_index = 0;
        for (int i = 0; i < couponRules.size(); i++) {
            CouponRule couponRule = couponRules.get(i);
            GoodsRule goodsRule = JSON.parseObject(couponRule.getGoodsRule(), GoodsRule.class);
            Map<String, Integer> rule = (Map<String, Integer>) goodsRule.getRule();
            Integer limit_price = rule.get("each_goods_price");
            if (limit_price < min) {
                min = limit_price;
                min_index = i;
            }
        }
        //限价 主显示
        currentGoods.getBestTitle().setBestTitle(TYPE_ENV, min);
        //移除特价最低优惠，并解析成标签字符串
        CouponRule removeRule = couponRules.remove(min_index);
        //以防有变，再删除一次
        currentGoods.getGroup().get(TYPE_ENV).remove(removeRule);
        String firstOrder = handleFirstOrder(removeRule);
        return firstOrder + "特价：" + promotionsFilterService.save2Digit(min / 100.00d);

    }

    private String handleFirstOrder(CouponRule removeRule) {
        if (Objects.isNull(removeRule)) return "";
        LimitRule limitRule = JSON.parseObject(removeRule.getLimitRule(), LimitRule.class);
        if (Objects.isNull(limitRule)) return "";
        if (limitRule.getIs_first_order() == 1) {
            boolean firstOrder = tradesService.checkFirstOrder(tagsParam.getSiteId(), tagsParam.getMemberId());
            if (firstOrder)
                return "首单";
        }
        return "";
    }

    public CouponTagsFilter(ServletContext servletContext, TagsParam tagsParam) {
        this.tagsParam = tagsParam;
        getServiceBean(checkNotNull(servletContext));
        Arrays.asList(tagsParam.getGoodsIds().split(",")).forEach(s -> tags.add(TagsGoodsCoupon.buildTagsGoodsCoupon(s)));
        if (Objects.isNull(tagsParam.getMemberId())) {
            this.NeedAuth = true;
        } else {
            this.NeedAuth = false;
        }
    }

    private void getServiceBean(ServletContext servletContext) {
        BeanFactory beanFactory = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        this.couponFilterService = beanFactory.getBean(CouponFilterService.class);
        this.promotionsFilterService = beanFactory.getBean(PromotionsFilterService.class);
        this.tradesService = beanFactory.getBean(TradesService.class);

    }


    public List<TagsGoodsCoupon> getTags() {
        return tags;
    }

    public void setTags(List<TagsGoodsCoupon> tags) {
        this.tags = tags;
    }

    public Boolean getNeedAuth() {
        return NeedAuth;
    }

    public void setNeedAuth(Boolean needAuth) {
        NeedAuth = needAuth;
    }
}
