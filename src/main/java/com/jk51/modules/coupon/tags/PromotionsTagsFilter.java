package com.jk51.modules.coupon.tags;

import com.gexin.fastjson.JSON;
import com.jk51.model.Goods;
import com.jk51.model.coupon.tags.TagsFilter;
import com.jk51.model.coupon.tags.TagsGoodsPromotions;
import com.jk51.model.coupon.tags.TagsParam;
import com.jk51.model.promotions.EasyToSeeConstants;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.*;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.service.PromotionsActivityService;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import com.jk51.modules.promotions.utils.OrderDeductionUtils;
import com.jk51.modules.trades.service.TradesService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.jk51.modules.promotions.constants.PromotionsConstant.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Created by Javen73 on 2018/3/23.
 * {@link com.jk51.modules.promotions.service.PromotionsFilterService#proccessTagsForSearch}  搜索接口中使用
 * {@link com.jk51.modules.coupon.controller.CouponFilterController#canReceiveCoupon}  商品详情中使用
 */
//@Slf4j
public class PromotionsTagsFilter implements TagsFilter {
    Logger log = LoggerFactory.getLogger(PromotionsTagsFilter.class);
    public TagsParam tagsParam;
    public PromotionsFilterService promotionsFilterService;
    private PromotionsActivityMapper promotionsActivityMapper;
    private PromotionsRuleService promotionsRuleService;
    private OrderDeductionUtils orderDeductionUtils;
    private GoodsMapper goodsMapper;
    private TradesService tradesService;
    public PromotionsActivityService promotionsActivityService;
    //结果集
    public List<TagsGoodsPromotions> tags = new ArrayList<>();
    //当前处理活动是独立的还是非独立的
    public Integer INDEPENDENT_ENV;
    //当前处理的活动类型
    public Integer TYPE_ENV;
    //当前处理的商品
    public TagsGoodsPromotions currentTags;

    @Override
    public PromotionsTagsFilter collection() {
        //收集全部符合商品的活动
        List<PromotionsActivity> promotionsActivities = promotionsActivityMapper.getPromotionsActivitiesByStatusAndSiteId(tagsParam.getSiteId());
        promotionsActivities = promotionsFilterService.getPromotionsActivitiesByChecked(promotionsActivities, tagsParam.getSiteId(), tagsParam.getMemberId(), tagsParam.getGoodsIds());
        for (TagsGoodsPromotions tag : tags) {
            List<PromotionsActivity> activities = promotionsActivities.stream()
                .filter(Objects::nonNull)
                .filter(pa -> promotionsFilterService.isIfExcuseGoods(pa.getPromotionsRule(), tag.goodsId))
                .collect(toList());
            tag.setRules(activities);
        }
        return this;
    }


    @Override
    public PromotionsTagsFilter grouping() {
        //对活动进行按独立非独立、类型进行分组
        for (TagsGoodsPromotions tag : tags) {
            Map<Integer, Map<Integer, List<PromotionsActivity>>> group = tag.getRules().stream()
                .collect(groupingBy(
                    promotionsActivity -> Optional.ofNullable(promotionsActivity.getIsIndependent())
                        .orElseGet(() -> DEFAULT_INDEPENDENT.get(promotionsActivity.getPromotionsRule().getPromotionsType())),
                    groupingBy(promotionsActivity -> promotionsActivity.getPromotionsRule().getPromotionsType())
                ));
            tag.setGroup(group);
        }
        return this;
    }

    @Override
    public PromotionsTagsFilter resolve() throws Exception {
        for (TagsGoodsPromotions tag : tags) {
            currentTags = tag; //设置当前循环的商品
            Map<Integer, List<PromotionsActivity>> independent = tag.getGroup().get(INDEPENDENT);
            if (Objects.nonNull(independent)) {
                INDEPENDENT_ENV = INDEPENDENT; //设置当前环境  在循环处理移除时用到
                ProcessPromotion(tag, independent);
            }

            Map<Integer, List<PromotionsActivity>> dependent = tag.getGroup().get(DEPENDENT);
            if (Objects.nonNull(dependent)) {
                INDEPENDENT_ENV = DEPENDENT; //设置当前环境  在循环处理移除时用到
                ProcessPromotion(tag, dependent);
            }
        }
        return this;
    }


    private void ProcessPromotion(TagsGoodsPromotions tag, Map<Integer, List<PromotionsActivity>> dependent_list) throws Exception {
        while (tag.getTags().size() < tagsParam.getTagsNum() && GroupCounter(dependent_list) != 0)
            for (Integer type : EasyToSeeConstants.BEST_ACTIVITY) {
                TYPE_ENV = type; //设置当前环境
                List<PromotionsActivity> pa = dependent_list.get(type);
                if (pa != null && pa.size() > 0)
                    resolveRouter(pa, type);
            }
    }

    private int GroupCounter(Map<Integer, List<PromotionsActivity>> paGroup) {
        int counter = 0;
        for (Integer type : EasyToSeeConstants.BEST_ACTIVITY) {
            List<PromotionsActivity> promotionsActivities = paGroup.get(type);
            if (promotionsActivities != null) {
                counter += promotionsActivities.size();
            }
        }
        return counter;
    }

    public String resolveRouter(List<PromotionsActivity> pa, Integer type) throws Exception {
        PromotionsActivity removePromotions = null;
        String tag = null;
        switch (type) {
            case PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                tag = resolveGroupBooking(pa);
                break;
            case PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                tag = resolveLimitPrice(pa);
                break;
            case PROMOTIONS_RULE_TYPE_DISCOUNT:
                tag = resolveDiscount(pa);
                break;
            case PROMOTIONS_RULE_TYPE_MONEY_OFF:
                tag = resolveMoney(pa);
                break;
            case PROMOTIONS_RULE_TYPE_GIFT:
                tag = resolveGift(pa);
                break;
            case PROMOTIONS_RULE_TYPE_FREE_POST:
                resolveExpressFree(pa);
                break;
            default:
                log.error("不支持的活动类型");
                throw new Exception("不支持的活动类型");
        }
        if (Objects.nonNull(tag) && Objects.nonNull(currentTags)) {
            if (currentTags.getTags().size() < tagsParam.getTagsNum())
                currentTags.getTags().add(tag);
        }
        return tag;
    }

    private void resolveExpressFree(List<PromotionsActivity> pa) {
        if (pa.size() > 0) {
            currentTags.getTitles().add("包邮");
            PromotionsActivity remove = pa.remove(0);
            if (Objects.nonNull(INDEPENDENT_ENV))
                currentTags.getGroup().get(INDEPENDENT_ENV).get(TYPE_ENV).remove(remove);
        }

    }

    private String resolveGift(List<PromotionsActivity> pa) {
        String tag = "";
        pa.sort(((o1, o2) -> o2.getPromotionsRule().getCreateTime().compareTo(o1.getPromotionsRule().getCreateTime())));
        PromotionsActivity promotionsActivity = pa.get(0);
        PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
        GiftRule giftRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GiftRule.class);
        List<GiftRule.RuleCondition> ruleConditions = giftRule.getRuleConditions();
        switch (giftRule.getRuleType()) {
            case 1:
                //满几件送几件
                if (ruleConditions.size() == 1) {
                    //1层阶梯
                    GiftRule.RuleCondition ruleCondition = ruleConditions.get(0);
                    tag = "满" + ruleCondition.getMeetNum() + "件送" + (giftRule.getSendType() == 2 ? ruleCondition.getSendNum() + "件" : "礼品");
                } else {
                    Integer meet_num = ruleConditions.stream().map(map -> map.getMeetNum()).mapToInt(Integer::intValue)
                        .max().getAsInt();
                    tag = "最高满" + meet_num + "件送" + (giftRule.getSendType() == 2 ? ruleConditions.stream()
                        .map(GiftRule.RuleCondition::getSendNum).mapToInt(Integer::intValue)
                        .max().getAsInt() + "件" : "礼品");
                }
                break;
            case 2:
                //满多少元送几件
                if (ruleConditions.size() == 1) {
                    //1层阶梯
                    GiftRule.RuleCondition ruleCondition = ruleConditions.get(0);
                    tag = "满" + promotionsFilterService.save2Digit(ruleCondition.getMeetMoney() / 100.00d) + "元送" + (giftRule.getSendType() == 2 ? ruleCondition.getSendNum() + "件" : "礼品");
                } else {
                    Integer meet_money = ruleConditions.stream()
                        .map(GiftRule.RuleCondition::getMeetMoney)
                        .mapToInt(Integer::intValue)
                        .max().getAsInt();
                    tag = "最高满" + promotionsFilterService.save2Digit(meet_money / 100.00d) + "元送" + (giftRule.getSendType() == 2 ? ruleConditions.stream()
                        .map(GiftRule.RuleCondition::getSendNum).mapToInt(Integer::intValue)
                        .max().getAsInt() + "件" : "礼品");
                }
        }
        PromotionsActivity remove = pa.remove(0);
        //重新排序 栈指向的堆内存空间不是原来的数组，而是排序后的，所以单纯的remove现有的排序后的数组可能无效，所以再进行一次删除
        if (Objects.nonNull(INDEPENDENT_ENV))
            currentTags.getGroup().get(INDEPENDENT_ENV).get(TYPE_ENV).remove(remove);
        String firstOrder = handleFirstOrder(remove);
        return firstOrder + tag;
    }

    private String resolveMoney(List<PromotionsActivity> pa) {
        Integer max = Integer.MIN_VALUE;
        int max_index = 0;
        String tag = "";
        for (int i = 0; i < pa.size(); i++) {
            PromotionsActivity promotionsActivity = pa.get(i);
            PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
            ReduceMoneyRule reduceMoneyRule = JSON.parseObject(promotionsRule.getPromotionsRule(), ReduceMoneyRule.class);
            List<ReduceMoneyRule.InnerRule> rules = reduceMoneyRule.getRules();
            switch (reduceMoneyRule.getRuleType()) {
                case 1:
                    //立减
                    ReduceMoneyRule.InnerRule innerRule_1 = rules.get(0);
                    Integer reduceMoney_1 = innerRule_1.getReduceMoney();
                    if (reduceMoney_1 > max) {
                        max = reduceMoney_1;
                        max_index = i;
                        tag = "立减" + promotionsFilterService.save2Digit(reduceMoney_1 / 100.00d) + "元";
                    }
                    break;

                case 2:
                    ReduceMoneyRule.InnerRule innerRule_2 = rules.get(0);
                    Integer reduceMoney_2 = innerRule_2.getReduceMoney();
                    if (reduceMoney_2 > max) {
                        max = reduceMoney_2;
                        max_index = i;
                        tag = "每满" + promotionsFilterService.save2Digit(innerRule_2.getMeetMoney() / 100.00d) + "减" + promotionsFilterService.save2Digit(reduceMoney_2 / 100.00d);
                        tag += innerRule_2.getCap() == 0 ? "不封顶" : "";
                    }
                    break;

                case 3:
                    if (rules.size() == 1) {
                        ReduceMoneyRule.InnerRule innerRule_3 = rules.get(0);
                        Integer reduceMoney_3 = innerRule_3.getReduceMoney();
                        if (reduceMoney_3 > max) {
                            max = reduceMoney_3;
                            max_index = i;
                            tag = "满" + promotionsFilterService.save2Digit(innerRule_3.getMeetMoney() / 100.00d) + "减" + promotionsFilterService.save2Digit(reduceMoney_3 / 100.00d);
                        }
                    } else {
                        Integer meetMoney_3 = rules.stream().map(ReduceMoneyRule.InnerRule::getMeetMoney).mapToInt(Integer::intValue)
                            .max().getAsInt();
                        Integer reduceMoney_3 = rules.stream().map(ReduceMoneyRule.InnerRule::getReduceMoney).mapToInt(Integer::intValue)
                            .max().getAsInt();
                        if (reduceMoney_3 > max) {
                            max = reduceMoney_3;
                            max_index = i;
                            tag = "最高满" + promotionsFilterService.save2Digit(meetMoney_3 / 100.00d) + "减" + promotionsFilterService.save2Digit(reduceMoney_3 / 100.00d);
                        }
                    }
                    break;
            }
        }
        PromotionsActivity remove = pa.remove(max_index);
        //担心在方法参数的传递过程中，形参和实参，传递的不是栈中的堆地址，而是复制整个列表所以重新删除一遍
        if (Objects.nonNull(INDEPENDENT_ENV))
            currentTags.getGroup().get(INDEPENDENT_ENV).get(TYPE_ENV).remove(remove);
        String firstOrder = handleFirstOrder(remove);
        return firstOrder + tag;
    }

    private String resolveDiscount(List<PromotionsActivity> pa) {
        Integer min = Integer.MAX_VALUE;
        int min_index = 0;
        String tag = "";
        for (int i = 0; i < pa.size(); i++) {
            PromotionsActivity promotionsActivity = pa.get(i);
            PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
            DiscountRule discountRule = JSON.parseObject(promotionsRule.getPromotionsRule(), DiscountRule.class);
            List<Map<String, Integer>> rules = discountRule.getRules();
            switch (discountRule.getRuleType()) {
                case 1:
                    Integer discount_1 = rules.get(0).get("direct_discount");
                    if (discount_1 < min) {
                        min = discount_1;
                        min_index = i;
                        tag = discount_1 / 10.0f + "折";
                    }
                    break;
                case 2:
                    if (rules.size() == 1) {
                        Map<String, Integer> discountRuleSigle = rules.get(0);
                        Integer discount_2 = discountRuleSigle.get("discount");
                        if (discount_2 < min) {
                            min_index = i;
                            min = discount_2;
                            tag = (discountRuleSigle.get("meet_money") / 100.00d) + "元" + discount_2 / 10.0f + "折";
                        }

                    } else {
                        //暂时用这种解析方式，比较清爽
                        Integer meet_money_2 = rules.stream().map(map -> map.get("meet_money")).mapToInt(Integer::intValue)
                            .max().getAsInt();
                        Integer discount_2 = rules.stream().map(map -> map.get("discount")).mapToInt(Integer::intValue)
                            .min().getAsInt();
                        if (discount_2 < min) {
                            min = discount_2;
                            min_index = i;
                            tag = "最高满" + promotionsFilterService.save2Digit(meet_money_2 / 100.00d) + "元" + discount_2 / 10.0f + "折";
                        }

                    }
                    break;

                case 3:
                    if (rules.size() == 1) {
                        Map<String, Integer> discountRuleSigle = rules.get(0);
                        Integer discount_3 = discountRuleSigle.get("discount");
                        if (discount_3 < min) {
                            min = discount_3;
                            min_index = i;
                            tag = "满" + discountRuleSigle.get("meet_num") + "件" + discount_3 / 10.0f + "折";
                        }
                    } else {
                        Integer meet_num_3 = rules.stream().map(map -> map.get("meet_num")).mapToInt(Integer::intValue)
                            .max().getAsInt();
                        Integer discount_3 = rules.stream().map(map -> map.get("discount")).mapToInt(Integer::intValue)
                            .min().getAsInt();

                        if (discount_3 < min) {
                            min = discount_3;
                            min_index = i;
                            tag = "最高" + meet_num_3 + "件" + discount_3 / 10.0f + "折";
                        }
                    }
                    break;

                case 4:
                    Map<String, Integer> discountRuleSigle = rules.get(0);
                    Integer discount_4 = discountRuleSigle.get("discount");
                    if (discount_4 < min) {
                        min = discount_4;
                        min_index = i;
                        tag = "第" + discountRuleSigle.get("rate") + "件" + discount_4 / 10.0f + "折";
                    }
                    break;

                case 5:
                    //分别打折，只需要匹配到自己的商品即可
                    for (Map<String, Integer> map : rules) {
                        Integer goodsId = map.get("goodsId");
                        if (currentTags.getGoodsId().equals(goodsId.toString())) {
                            Integer discount_5 = map.get("discount");
                            if (discount_5 < min) {
                                min = discount_5;
                                min_index = i;
                                tag = discount_5 / 10.0f + "折";
                            }
                        }
                    }
                    break;
            }

        }
        PromotionsActivity remove = pa.remove(min_index);
        //担心在方法参数的传递过程中，形参和实参，传递的不是栈中的堆地址，而是复制整个列表所以重新删除一遍
        if (Objects.nonNull(INDEPENDENT_ENV)) //其他业务需要使用解析规则，这地方会空指针
            currentTags.getGroup().get(INDEPENDENT_ENV).get(TYPE_ENV).remove(remove);
        String firstOrder = handleFirstOrder(remove);
        return firstOrder + tag;
    }

    private String resolveGroupBooking(List<PromotionsActivity> pa) {
        Integer min = Integer.MAX_VALUE;
        int min_index = 0;
        for (int i = 0; i < pa.size(); i++) {
            PromotionsActivity promotionsActivity = pa.get(i);
            PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
            GroupBookingRule groupBookingRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GroupBookingRule.class);
            List<Map<String, Integer>> rules = groupBookingRule.getRules();
            Integer groupPrice = 0;
            switch (groupBookingRule.getRuleType()) {
                case 1:
                    //统一拼团价
                    Map<String, Integer> sigle_1 = rules.get(0);
                    groupPrice = sigle_1.get("groupPrice");
                    if (groupPrice < min) {
                        min_index = i;
                        min = groupPrice;
                    }
                    break;
                case 2:
                    for (Map<String, Integer> temp : rules) {
                        Integer goodsId = temp.get("goodsId");
                        if (currentTags.goodsId.equals(goodsId.toString())) {
                            groupPrice = temp.get("groupPrice");
                            if (groupPrice < min) {
                                min_index = i;
                                min = groupPrice;
                            }
                        }
                    }
                    break;
                case 3:
                    //统一拼团价
                    Map<String, Integer> sigle_3 = rules.get(0);
                    Integer goodsId_3 = Integer.parseInt(currentTags.goodsId);
                    Goods goods_3 = goodsMapper.getBySiteIdAndGoodsId(goodsId_3, promotionsActivity.getSiteId());
                    groupPrice = orderDeductionUtils.discountMoney(goods_3.getShopPrice(), groupBookingRule.getIsMl(), groupBookingRule.getIsRound(), sigle_3.get("groupDiscount"));
                    groupPrice = goods_3.getShopPrice() - groupPrice;
                    if (groupPrice < min) {
                        min_index = i;
                        min = groupPrice;
                    }
                    break;
                case 4:
                    for (Map<String, Integer> temp : rules) {
                        Integer goodsId_4 = temp.get("goodsId");
                        if (currentTags.getGoodsId().equals(goodsId_4.toString())) {
                            Goods goods_4 = goodsMapper.getBySiteIdAndGoodsId(goodsId_4, promotionsActivity.getSiteId());
                            groupPrice = orderDeductionUtils.discountMoney(goods_4.getShopPrice(), groupBookingRule.getIsMl(), groupBookingRule.getIsRound(), temp.get("groupDiscount"));
                            groupPrice = goods_4.getShopPrice() - groupPrice;
                            if (groupPrice < min) {
                                min_index = i;
                                min = groupPrice;
                            }
                        }
                    }
                    break;
            }
        }
        currentTags.getBestTitle().setBestTitle(TYPE_ENV, min);
        PromotionsActivity remove = pa.remove(min_index);
        //担心在方法参数的传递过程中，形参和实参，传递的不是栈中的堆地址，而是复制整个列表所以重新删除一遍
        if (Objects.nonNull(INDEPENDENT_ENV))
            currentTags.getGroup().get(INDEPENDENT_ENV).get(TYPE_ENV).remove(remove);
        String firstOrder = handleFirstOrder(remove);
        String tag = "拼团价：" + promotionsFilterService.save2Digit(min / 100.00d);
        return firstOrder + tag;
    }

    private String resolveLimitPrice(List<PromotionsActivity> pa) {
        Integer min = Integer.MAX_VALUE;
        int min_index = 0;
        for (int i = 0; i < pa.size(); i++) {
            PromotionsActivity promotionsActivity = pa.get(i);
            PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
            FixedPriceRule fixedPriceRule = JSON.parseObject(promotionsRule.getPromotionsRule(), FixedPriceRule.class);
            if (fixedPriceRule.getRuleType() == 1) {
                if (fixedPriceRule.getFixedPrice() < min) {
                    min_index = i;
                    min = fixedPriceRule.getFixedPrice();
                }
            } else if (fixedPriceRule.getRuleType() == 2) {
                for (Map<String, Integer> map : fixedPriceRule.getRules()) {
                    if (currentTags.getGoodsId().equals(map.get("goodsId").toString())) {
                        Integer fixedPrice = map.get("fixedPrice");
                        if (fixedPrice < min) {
                            min_index = i;
                            min = fixedPrice;
                        }
                    }
                }
            }
        }
        currentTags.getBestTitle().setBestTitle(TYPE_ENV, min);
        PromotionsActivity remove = pa.remove(min_index);
        //担心在方法参数的传递过程中，形参和实参，传递的不是栈中的堆地址，而是复制整个列表所以重新删除一遍
        if (Objects.nonNull(INDEPENDENT_ENV)) //呵呵，迫不得已，其他业务也要用
            currentTags.getGroup().get(INDEPENDENT_ENV).get(TYPE_ENV).remove(remove);
        String firstOrder = handleFirstOrder(remove);
        String tag = "特价：" + promotionsFilterService.save2Digit(min / 100.00d);
        return firstOrder + tag;
    }


    private String handleFirstOrder(PromotionsActivity remove) {
        if (Objects.isNull(remove)) return "";
        if (remove.getPromotionsRule().getIsFirstOrder() == 1) {
            boolean firstOrder = tradesService.checkFirstOrder(tagsParam.getSiteId(), tagsParam.getMemberId());
            if (firstOrder)
                return "首单";
        }
        return "";
    }

    public PromotionsTagsFilter() {
    }


    public PromotionsTagsFilter(ServletContext servletContext, TagsParam tagsParam) {
        this.tagsParam = tagsParam;
        getServiceBean(checkNotNull(servletContext));
        Arrays.asList(tagsParam.getGoodsIds().split(",")).forEach(s -> tags.add(TagsGoodsPromotions.buildTagsGoodsPromotions(s)));
    }

    private void getServiceBean(ServletContext servletContext) {
        BeanFactory beanFactory = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        this.promotionsFilterService = beanFactory.getBean(PromotionsFilterService.class);
        this.tradesService = beanFactory.getBean(TradesService.class);
        this.promotionsActivityMapper = beanFactory.getBean(PromotionsActivityMapper.class);
        this.orderDeductionUtils = beanFactory.getBean(OrderDeductionUtils.class);
        this.goodsMapper = beanFactory.getBean(GoodsMapper.class);
        this.promotionsRuleService = beanFactory.getBean(PromotionsRuleService.class);
        this.promotionsActivityService = beanFactory.getBean(PromotionsActivityService.class);

    }


    public List<TagsGoodsPromotions> getTags() {
        return tags;
    }

    public void setTags(List<TagsGoodsPromotions> tags) {
        this.tags = tags;
    }
}

