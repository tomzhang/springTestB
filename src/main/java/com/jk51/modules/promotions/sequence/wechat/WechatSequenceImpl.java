package com.jk51.modules.promotions.sequence.wechat;

import com.gexin.fastjson.JSON;
import com.jk51.commons.java8datetime.ParseAndFormat;
import com.jk51.model.coupon.tags.TagsGoodsPromotions;
import com.jk51.model.coupon.tags.TagsParam;
import com.jk51.model.grouppurchase.GroupPurchase;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.FixedPriceRule;
import com.jk51.model.promotions.rule.GroupBookingRule;
import com.jk51.model.promotions.sequence.SequenceParam;
import com.jk51.model.promotions.sequence.SequenceResult;
import com.jk51.model.promotions.sequence.wechat.WechatSequenceBlock;
import com.jk51.model.promotions.sequence.wechat.WechatSequenceGoods;
import com.jk51.model.promotions.sequence.wechat.WechatSequenceResult;
import com.jk51.modules.coupon.tags.PromotionsTagsFilter;
import com.jk51.modules.promotions.sequence.SequenceAdapter;

import javax.servlet.ServletContext;
import java.time.ZoneOffset;
import java.util.*;

import static com.jk51.modules.promotions.constants.PromotionsConstant.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Created by javen73 on 2018/5/12.
 */
public class WechatSequenceImpl extends SequenceAdapter<WechatSequenceImpl> {
    public ServletContext servletContext;
    public Optional<PromotionsActivity> start = Optional.empty();
    public Optional<PromotionsActivity> end = Optional.empty();
    public List<PromotionsActivity> generalBlock = new ArrayList<>();
    //普通活动显示2个
    private Integer generalSize = 2;

    /**
     * step.1收集符合条件活动
     *
     * @return
     * @throws Exception
     */
    @Override
    public WechatSequenceImpl collection() throws Exception {
        List<PromotionsActivity> promotionsActivities = super.activityMapper.getPromotionsActivitiesWithRuleIn(param.getSiteId());
        promotionsActivities = promotionsActivities.stream()
            .filter(pa -> {
                PromotionsRule promotionsRule = pa.getPromotionsRule();
                if (Objects.isNull(promotionsRule))
                    return false;
                if (Objects.equals(promotionsRule.getPromotionsType(), param.getPromotionType()))
                    return true;
                return false;
            }).collect(toList());
        //过滤活动
        List<PromotionsActivity> collection = promotionsFilterService.getPromotionsActivitiesByUncheckedTime(promotionsActivities, param.getSiteId(), param.getMemberId());
        // SQL中只查询 0 正常 11 发布中未开始，所以分组，只有 已开始和未开始，两组
        Map<Integer, List<PromotionsActivity>> groupPromotions = collection.stream().collect(groupingBy(PromotionsActivity::getStatus));
        switch (param.getPromotionType()) {
            case PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
            case PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                List<PromotionsActivity> alreadyList = groupPromotions.get(ALREADY);
                List<PromotionsActivity> readyList = groupPromotions.get(READY);
                if (Objects.nonNull(alreadyList) && alreadyList.size() > 0) {
                    //无法解决，两个限价活动，一个统一限价，一个分别限价的情况，所以不做任何处理，只按照最新活动处理
                    PromotionsActivity p_end = alreadyList.get(alreadyList.size() - 1);
                    end = Optional.of(p_end);
                }
                //限价活动，不再具有未开始的列表
                boolean limit_promotions = param.getPromotionType() != PROMOTIONS_RULE_TYPE_LIMIT_PRICE;
                if (Objects.nonNull(readyList) && readyList.size() > 0 && limit_promotions) {
                    PromotionsActivity p_start = readyList.get(0);
                    start = Optional.of(p_start);
                }
                break;
            case PROMOTIONS_RULE_TYPE_MONEY_OFF:
            case PROMOTIONS_RULE_TYPE_FREE_POST:
            case PROMOTIONS_RULE_TYPE_DISCOUNT:
            case PROMOTIONS_RULE_TYPE_GIFT:
                List<PromotionsActivity> otherList = groupPromotions.get(ALREADY);
                if (Objects.nonNull(otherList) && otherList.size() > 0) {
                    otherList.sort(Comparator.comparing(PromotionsActivity::getCreateTime).reversed());
                    generalBlock = otherList.stream().limit(generalSize).collect(toList());
                }
                break;
            default:
                throw new RuntimeException("未知类型,收集活动失败");
        }
        return this;
    }

    /**
     * step.2 处理商品
     *
     * @return
     * @throws Exception
     */
    @Override
    public WechatSequenceImpl processGoods() throws Exception {
        switch (param.getPromotionType()) {
            case PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
            case PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                if (!result.isPresent())
                    throw new RuntimeException("结果集不能为空");
                WechatSequenceResult sequenceResult = (WechatSequenceResult) result.get();
                if (end.isPresent()) {
                    PromotionsActivity promotionsActivity = end.get();
                    sequenceResult.endBlock.setActivity(promotionsActivity);
//                    sequenceResult.endBlock.setEndTime(promotionsActivity.getEndTime().format(ParseAndFormat.longFormatter));
                    sequenceResult.endBlock.setPromotionsActivityId(promotionsActivity.getId());
                    sequenceResult.endBlock.setEndTime(promotionsActivity.getEndTime().toEpochSecond(ZoneOffset.of("+8")));
                    PromotionsRule end_rule = promotionsActivity.getPromotionsRule();
                    sequenceResult.endBlock.goodsIds = converseRuleTypeFindGoodsId(end_rule.getPromotionsType(), end_rule.getPromotionsRule());
                }
                if (start.isPresent()) {
                    PromotionsActivity promotionsActivity = start.get();
                    sequenceResult.startBlock.setActivity(promotionsActivity);
                    sequenceResult.startBlock.setPromotionsActivityId(promotionsActivity.getId());
                    sequenceResult.startBlock.setStartTime(promotionsActivity.getStartTime().toEpochSecond(ZoneOffset.of("+8")));
                    PromotionsRule start_rule = promotionsActivity.getPromotionsRule();
                    sequenceResult.startBlock.goodsIds = converseRuleTypeFindGoodsId(start_rule.getPromotionsType(), start_rule.getPromotionsRule());
                }
                break;
            case PROMOTIONS_RULE_TYPE_MONEY_OFF:
            case PROMOTIONS_RULE_TYPE_FREE_POST:
            case PROMOTIONS_RULE_TYPE_DISCOUNT:
            case PROMOTIONS_RULE_TYPE_GIFT:
                if (!result.isPresent())
                    throw new RuntimeException("结果集不能为空");
                WechatSequenceResult otherSequence = (WechatSequenceResult) result.get();
                if (Objects.nonNull(generalBlock) && generalBlock.size() > 0) {
                    generalBlock.forEach(pa -> {
                        WechatSequenceBlock block = new WechatSequenceBlock();
                        block.setActivity(pa);
                        //添加活动类型id
                        block.setPromotionsActivityId(pa.getId());
                        PromotionsRule promotionsRule = pa.getPromotionsRule();
                        block.goodsIds = converseRuleTypeFindGoodsId(promotionsRule.getPromotionsType(), promotionsRule.getPromotionsRule());
                        otherSequence.block.add(block);
                    });
                }
                break;
            default:
                throw new RuntimeException("未知类型");

        }
        return this;
    }

    /**
     * step.3 可到父类中查看，具体实现可见 {@link WechatSequenceHandlerImpl#sequence}
     * step.4 处理标签
     *
     * @return
     * @throws Exception
     */
    public WechatSequenceImpl processTags() throws Exception {
        Integer promotionType = param.getPromotionType();
        switch (promotionType) {
            case PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                if (!result.isPresent()) {
                    throw new RuntimeException("结果集为空");
                }
                WechatSequenceResult sequenceResult = (WechatSequenceResult) result.get();
                if (Objects.nonNull(sequenceResult.startBlock.getGoods()) && sequenceResult.startBlock.getGoods().size() > 0) {
                    Integer id = sequenceResult.startBlock.getActivity().getId();
                    List goodsList = sequenceResult.startBlock.getGoods();
                    List<WechatSequenceGoods> wechatGoods = goodsList;
                    groupPurcharseMemberNum(id, wechatGoods);
                    PromotionsRule promotionsRule = sequenceResult.startBlock.getActivity().getPromotionsRule();
                    groupMemberNum(wechatGoods, promotionsRule);
                    resolveGroupPrice(sequenceResult.startBlock, promotionsRule);
                    //设置活动解析标签
                    setPromotionsTag(sequenceResult.startBlock);
                }
                if (Objects.nonNull(sequenceResult.endBlock.getGoods()) && sequenceResult.endBlock.getGoods().size() > 0) {
                    Integer id = sequenceResult.endBlock.getActivity().getId();
                    List goodsList = sequenceResult.endBlock.getGoods();
                    List<WechatSequenceGoods> wechatGoods = goodsList;
                    groupPurcharseMemberNum(id, wechatGoods);
                    PromotionsRule promotionsRule = sequenceResult.endBlock.getActivity().getPromotionsRule();
                    groupMemberNum(wechatGoods, promotionsRule);
                    resolveGroupPrice(sequenceResult.endBlock, promotionsRule);
                    //设置活动解析标签
                    setPromotionsTag(sequenceResult.endBlock);
                }
                break;
            case PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                if (!result.isPresent()) {
                    throw new RuntimeException("结果集为空");
                }
                WechatSequenceResult limitResult = (WechatSequenceResult) result.get();

                if (Objects.nonNull(limitResult.startBlock.getGoods()) && limitResult.startBlock.getGoods().size() > 0) {
                    List goodsList = limitResult.startBlock.getGoods();
                    List<WechatSequenceGoods> wechatGoods = goodsList;
                    PromotionsRule promotionsRule = limitResult.startBlock.getActivity().getPromotionsRule();
                    limitBuyNum(wechatGoods, promotionsRule);
                    resolveLimitPrice(promotionsRule, limitResult.startBlock);
                    //设置活动解析标签
                    setPromotionsTag(limitResult.startBlock);
                }
                if (Objects.nonNull(limitResult.endBlock.getGoods()) && limitResult.endBlock.getGoods().size() > 0) {
                    List goodsList = limitResult.endBlock.getGoods();
                    List<WechatSequenceGoods> wechatGoods = goodsList;
                    PromotionsRule promotionsRule = limitResult.endBlock.getActivity().getPromotionsRule();
                    limitBuyNum(wechatGoods, promotionsRule);
                    resolveLimitPrice(promotionsRule, limitResult.endBlock);
                    //设置活动解析标签
                    setPromotionsTag(limitResult.endBlock);
                }
                break;
            default:
                if (!result.isPresent())
                    throw new RuntimeException("结果集为空");
                WechatSequenceResult defaultResult = (WechatSequenceResult) result.get();
                List<WechatSequenceBlock> block = defaultResult.block;
                if (Objects.nonNull(block) && block.size() > 0) {
                    for (WechatSequenceBlock sequenceBlock : block) {
                        //设置活动解析标签
                        setPromotionsTag(sequenceBlock);
                    }
                }
        }
        return this;
    }

    public void setPromotionsTag(WechatSequenceBlock block) throws Exception {
        //标签解析类需要参数，但goodsIds和tagsNum并不是必须
        TagsParam tags = new TagsParam(param.getSiteId(), param.getMemberId(), "0", Integer.MAX_VALUE);
        //创建标签解析类
        PromotionsTagsFilter tagsFilter = new PromotionsTagsFilter(servletContext, tags);
        tagsFilter.currentTags = TagsGoodsPromotions.buildTagsGoodsPromotions("0");
        tagsFilter.TYPE_ENV = 10; //假数据 都是假数据，假的都是假的 我在自己骗自己
        List<PromotionsActivity> activityList = new ArrayList<PromotionsActivity>() {{
            add(block.getActivity());
        }};
        String tag = tagsFilter.resolveRouter(activityList, block.getActivity().getPromotionsRule().getPromotionsType());
        block.setTag(tag);
    }

    private void resolveLimitPrice(PromotionsRule promotionsRule, WechatSequenceBlock block) {
        FixedPriceRule fixedPriceRule = JSON.parseObject(promotionsRule.getPromotionsRule(), FixedPriceRule.class);
        if (fixedPriceRule.getRuleType() == 1) {
            block.getGoods().forEach(gs -> {
                WechatSequenceGoods wechatSequenceGoods = (WechatSequenceGoods) gs;
                wechatSequenceGoods.setLimitPrice(fixedPriceRule.getFixedPrice());
            });
        } else if (fixedPriceRule.getRuleType() == 2) {
            List<Map<String, Integer>> rules = fixedPriceRule.getRules();
            rules.forEach(map -> {
                Integer goodsId = map.get("goodsId");
                block.getGoods().forEach(gs -> {
                    if (goodsId.equals(gs.getGoods().get("goodsId"))) {
                        WechatSequenceGoods wechatSequenceGoods = (WechatSequenceGoods) gs;
                        wechatSequenceGoods.setLimitPrice(map.get("fixedPrice"));
                    }
                });
            });
        }
    }

    //处理拼团价格
    private void resolveGroupPrice(WechatSequenceBlock block, PromotionsRule promotionsRule) {
        GroupBookingRule groupBookingRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GroupBookingRule.class);
        List<Map<String, Integer>> rules = groupBookingRule.getRules();
        switch (groupBookingRule.getRuleType()) {
            case 1: //统一拼团，限价
            case 2://指定拼团，限价
                rules.forEach(map -> {
                    Integer goodsId = map.get("goodsId");
                    if (Objects.isNull(goodsId)) {
                        Integer groupPrice = map.get("groupPrice");
                        block.getGoods().forEach(gs -> {
                            WechatSequenceGoods wechatSequenceGoods = (WechatSequenceGoods) gs;
                            wechatSequenceGoods.setLimitPrice(groupPrice);
                        });
                    } else {
                        block.getGoods().forEach(gs -> {
                            if (goodsId.equals(gs.getGoods().get("goodsId"))) {
                                Integer groupPrice = map.get("groupPrice");
                                WechatSequenceGoods wechatSequenceGoods = (WechatSequenceGoods) gs;
                                wechatSequenceGoods.setLimitPrice(groupPrice);
                            }
                        });
                    }
                });
                break;
            case 3://统一拼团，打折
            case 4://指定拼团，打折
                rules.forEach(map -> {
                    Integer goodsId = map.get("goodsId");
                    if (Objects.isNull(goodsId)) {
                        Integer groupDiscount = map.get("groupDiscount");
                        block.getGoods().forEach(gs -> {
                            WechatSequenceGoods wechatSequenceGoods = (WechatSequenceGoods) gs;
                            Integer showPrice = (Integer) wechatSequenceGoods.getGoods().get("shopPrice");
                            Integer discountMoney = orderDeductionUtils.discountMoney(showPrice, groupBookingRule.getIsMl(), groupBookingRule.getIsRound(), groupDiscount);
                            int limitPrice = showPrice - discountMoney;
                            ((WechatSequenceGoods) gs).setLimitPrice(limitPrice);
                        });
                    } else {
                        Integer groupDiscount = map.get("groupDiscount");
                        block.getGoods().forEach(gs -> {
                            if (goodsId.equals(gs.getGoods().get("goodsId"))) {
                                WechatSequenceGoods wechatSequenceGoods = (WechatSequenceGoods) gs;
                                Integer showPrice = (Integer) wechatSequenceGoods.getGoods().get("shopPrice");
                                Integer discountMoney = orderDeductionUtils.discountMoney(showPrice, groupBookingRule.getIsMl(), groupBookingRule.getIsRound(), groupDiscount);
                                int limitPrice = showPrice - discountMoney;
                                ((WechatSequenceGoods) gs).setLimitPrice(limitPrice);
                            }
                        });
                    }
                });
                break;
        }
    }

    private void limitBuyNum(List<WechatSequenceGoods> wechatGoods, PromotionsRule promotionsRule) {
        FixedPriceRule fixedPriceRule = JSON.parseObject(promotionsRule.getPromotionsRule(), FixedPriceRule.class);
        wechatGoods.forEach(gd -> {
            gd.setLimitNum(fixedPriceRule.getStorage());
        });
    }

    private void groupMemberNum(List<WechatSequenceGoods> wechatGoods, PromotionsRule promotionsRule) {
        if (promotionsRule.getPromotionsType() == PROMOTIONS_RULE_TYPE_GROUP_BOOKING) {
            GroupBookingRule groupBookingRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GroupBookingRule.class);
            List<Map<String, Integer>> rules = groupBookingRule.getRules();
            Map<String, Integer> rule = rules.get(0);
            Integer groupMemberNum = rule.get("groupMemberNum");
            wechatGoods.forEach(gd -> {
                //如果剩余拼团人数是默认值，那么将默认值更改为限制拼团人数
                if (gd.getLimitGroupMemberNum()== Integer.MAX_VALUE)
                    gd.setLimitGroupMemberNum(groupMemberNum);
                gd.setLimitGroup(groupMemberNum);
            });
        }
    }

    private void groupPurcharseMemberNum(Integer activityId, List<WechatSequenceGoods> wechatGoods) {
        wechatGoods.forEach(gs -> {
            Map goods = gs.getGoods();
            Integer goodsId = Integer.parseInt(goods.get("goodsId").toString());
            List<GroupPurchase> purcharseParentIdPurcharse = groupPurChaseMapper.getGroupPurcharseParentIdPurcharse(param.getSiteId(), activityId, goodsId);
            Integer min = Integer.MAX_VALUE;
            for (GroupPurchase groupPurchase : purcharseParentIdPurcharse) {
                int memberNumBeforeSuccess = groupPurChaseService.getAbsentMemberNumBeforeSuccess(groupPurchase);
                if (memberNumBeforeSuccess == 0)
                    continue;
                if (memberNumBeforeSuccess < min) {
                    min = memberNumBeforeSuccess;
                }
            }
            gs.setLimitGroupMemberNum(min);
        });
    }


    public WechatSequenceImpl(ServletContext servletContext, SequenceParam param) {
        super(servletContext, param);
        super.result = Optional.of(new WechatSequenceResult());
        if (Objects.nonNull(param.getGeneralSize()))
            this.generalSize = param.getGeneralSize();
        this.servletContext = servletContext;
    }
}
