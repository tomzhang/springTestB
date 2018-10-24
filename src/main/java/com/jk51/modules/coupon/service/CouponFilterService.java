package com.jk51.modules.coupon.service;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponActivityRulesForJson;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.GoodsRule;
import com.jk51.model.coupon.requestParams.TimeRule;
import com.jk51.model.order.Member;
import com.jk51.model.promotions.*;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleActivityMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import com.jk51.modules.promotions.utils.OrderDeductionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.*;


/**
 * filename :com.jk51.modules.coupon.service.
 * author   :zw
 * date     :2017/6/28
 * Update   :
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class CouponFilterService {
    private static final Logger log = LoggerFactory.getLogger(CouponFilterService.class);
    @Autowired
    private CouponActivityMapper couponActivityMapper;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;
    @Autowired
    private CouponDetailService couponDetailService;
    @Autowired
    private CouponRuleActivityMapper couponRuleActivityMapper;
    @Autowired
    private PromotionsFilterService promotionsFilterService;
    @Autowired
    private CouponRuleService couponRuleService;
    @Autowired
    private OrderDeductionUtils orderDeductionUtils;

    public List<CouponActivity> getCanReceiveCoupon(Integer siteId, String goodsIds, Integer userId) {
        //1.先找用户自领形式的未过期活动，然后过滤活动下的优惠券选出可用的，判断是否领取过，
        // 然后判断是否是全品类的，限品的则判断是否包涵此goodsId或是否没包含此goodsId

        List<CouponActivity> couponActivityList = couponActivityMapper.getCouponActivityListBySendWay(siteId).stream()
//            .filter(d -> isReceive(d.getSiteId(), d.getId(), userId)) // 已领取的也显示
            .filter(couponActivity -> org.apache.commons.lang3.StringUtils.isNotBlank(couponActivity.getSendRules()))
            .filter(d -> {
                CouponActivityRulesForJson couponActivityRulesForJson = JSON.parseObject(d.getSendRules(), CouponActivityRulesForJson.class);
                if (couponActivityRulesForJson == null)
                    return false;
                if (couponActivityRulesForJson.getSendNumTag() == 2 || couponActivityRulesForJson.getSendNumTag() ==4)
                    return false;
                List<CouponRule> couponRuleList = filterCoupon(d.getSiteId(), d.getId(), goodsIds, userId);
                if (couponRuleList != null && !couponRuleList.isEmpty()) {
                    d.setCouponRules(couponRuleList);
                    return true;
                } else {
                    return false;
                }
            })
            .collect(Collectors.toList());
        return couponActivityList;
    }


    /**
     * 获取用户可以使用或者可以领取的优惠券
     *
     * @param siteId
     * @param goodsIds
     * @param userId
     * @return
     */
    public Map<String, String> getCanUseCoupon(Integer siteId, String goodsIds, Integer userId,
                                               List<PromotionsActivity> promotionsActivityList) {
        // 1.查询出用户未使用的所有优惠券规则
        // 2.查询出此用户可以领取的所有优惠券
        List<CouponRule> userCanUseCoupon = getCouponRules(siteId, userId);

        List<CouponRule> couponRuleList = canReceiveCoupon(siteId, goodsIds, userId);
        if (couponRuleList != null && !couponRuleList.isEmpty()) {
            userCanUseCoupon.addAll(couponRuleList);
        }
        Map<String, String> map = new HashedMap();
        //选出指定id的优惠券
        userCanUseCoupon.stream()
            .sorted(Comparator.comparing(CouponRule::getCreateTime)) //按时间升序
            .collect(Collectors.toList());

        arrayConvertList(goodsIds).stream().forEach(goodsId -> {
            List list = new ArrayList();
            userCanUseCoupon.stream().forEach(couponRule -> {
                String lab = couponSign(couponRule, goodsId);
                if (lab != null) {
                    list.add(lab);
                }
            });

            promotionsActivityList.stream().forEach(promotionsActivity -> {
                PromotionsRule promotionsRule = promotionsFilterService.checkActivity(promotionsActivity, userId, goodsId);
                if (promotionsRule != null) {
                    list.add(promotionsFilterService.resolveRuleByPromotions(promotionsRule));
                }
            });
            //去掉重复标签
            Collections.reverse(list);
            Object collect = list.stream().distinct().limit(5).collect(Collectors.toList());
            map.put(goodsId, collect.toString());
        });
        return map;
    }

    /**
     * 获取用户未过期的券
     *
     * @param siteId
     * @param userId
     * @return
     */
    public List<CouponRule> getCouponRules(Integer siteId, Integer userId) {
        return couponRuleMapper.findUserCanUseCoupon(siteId, userId)
            .stream()
            .filter(couponRule -> { //主要是判断相对时间的券是否过期
                Map<String, Object> map = new HashedMap();
                map.put("time_rule", couponRule.getTimeRule());
                List<CouponDetail> couponDetailList = couponDetailMapper.getCouponDetailByRuleId(
                    couponRule.getSiteId(), couponRule.getRuleId(), userId
                );
                boolean isExcuse = false;
                for (CouponDetail couponDetail : couponDetailList) {
                    map.put("create_time", couponDetail.getCreateTime());
                    if (couponRuleService.checkTimeRuleOverdue(map))
                        isExcuse = true;
                }
                return isExcuse;
            }).collect(Collectors.toList());
    }

    /**
     * 查询用户下未过期的优惠券  （返回的是map,map包含所有的detail字段及rule的timerule和goodsrule等字段）
     * @param siteId
     * @param userId
     * @return
     */
    public List<Map<String,Object>> getCouponDetails(Integer siteId,Integer userId){
        return couponDetailMapper.findUserCoupon(siteId,userId)
            .stream()
            .filter(map -> {
                Map<String, Object> param = new HashedMap();
                param.put("time_rule", map.get("time_rule").toString());
                param.put("create_time",map.get("create_time"));
                return couponRuleService.checkTimeRuleOverdue(param);
            })
            .collect(toList());
    }


    /**
     * 返回标签
     *
     * @param couponRule
     * @param goodsId
     * @return
     */
    private String couponSign(CouponRule couponRule, String goodsId) {
        GoodsRule goodsRule = null;
        try {
            goodsRule = JSON.parseObject(couponRule.getGoodsRule(), GoodsRule.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //解析出来为空则表示针对订单的,都是全品类
        if (goodsRule == null && couponRule.getOrderRule() != null) {
            return promotionsFilterService.resolveRuleByCoupon(couponRule);
        }
        String promotion_goods = goodsRule.getPromotion_goods();

        String[] prArr = promotion_goods.split(",");
        boolean contains = Arrays.asList(prArr).contains(goodsId);
        switch (goodsRule.getType()) {
            case 0:
                return promotionsFilterService.resolveRuleByCoupon(couponRule);
            case 1: //按类目暂时无，所以默认true
                break;
            case 2:
                if (contains) {
                    return promotionsFilterService.resolveRuleByCoupon(couponRule);
                }
                break;
            case 3:
                if (!contains) {
                    return promotionsFilterService.resolveRuleByCoupon(couponRule);
                }
                break;
            default:
                log.error("couponSign not find type");
                break;
        }
        return null;
    }

    private List<CouponRule> canReceiveCoupon2(Integer siteId, String goodsIds, Integer userId) {
        List<CouponRule> couponRuleList = new ArrayList<>();
        couponActivityMapper.getCouponActivityList(siteId).stream()
            .filter(d -> isReceive(d.getSiteId(), d.getId(), userId))
            .forEach(couponActivity -> {
                List<CouponRule> crl = filterCoupon(couponActivity.getSiteId(), couponActivity.getId(), goodsIds, userId);
                if (crl != null && !crl.isEmpty()) {
                    couponRuleList.addAll(crl);
                }
            });
        return couponRuleList;
    }

    private List<CouponRule> canReceiveCoupon(Integer siteId, String goodsIds, Integer userId) {
        List<CouponRule> couponRuleList = new ArrayList<>();
        couponActivityMapper.getCouponActivityListBySendWay(siteId).stream()
            .filter(d -> isReceive(d.getSiteId(), d.getId(), userId))
            .forEach(couponActivity -> {
                List<CouponRule> crl = filterCoupon(couponActivity.getSiteId(), couponActivity.getId(), goodsIds, userId);
                if (crl != null && !crl.isEmpty()) {
                    couponRuleList.addAll(crl);
                }
            });
        return couponRuleList;
    }

    public List<CouponActivity> allGoodsCouponsUnAuth(Integer siteId, String goodsIds) {
        return couponActivityMapper.getCouponActivityListBySendWayUnAuth(siteId).stream()
            .filter(couponActivity -> couponActivity.getSendObj() == 1)
            .filter(couponActivity -> org.apache.commons.lang3.StringUtils.isNotBlank(couponActivity.getSendRules()))
            .filter(d -> {
                CouponActivityRulesForJson couponActivityRulesForJson = JSON.parseObject(d.getSendRules(), CouponActivityRulesForJson.class);
                if (couponActivityRulesForJson == null)
                    return false;
                if (couponActivityRulesForJson.getSendNumTag() == 2)
                    return false;
                List<CouponRule> couponRules = filterCouponUnAuth(siteId, d, goodsIds);
                if (couponRules != null && !couponRules.isEmpty()) {
                    d.setCouponRules(couponRules);
                    return true;
                } else {
                    return false;
                }
            }).collect(toList());
    }

    private List<CouponRule> filterCouponUnAuth(Integer siteId, CouponActivity d, String goodsIds) {
        List<CouponRule> couponRuleByActive = couponRuleMapper.findCouponRuleByActive(siteId, d.getId()).stream()
            .filter(p -> p.getStatus() == 0  && (p.getAmount() > 0 || p.getAmount() == -1))
            .filter(p-> couponRuleService.judgeLimitTime(p.getTimeRule()))
            .filter(p -> p.getAimAt() == 1)
            .map(p -> {
                //没有登录的都置为可领取
                p.setIsCanGet(0);
                return p;
            }).collect(toList());
        //没有正常状态的优惠券了
        if (couponRuleByActive == null || couponRuleByActive.isEmpty()) {
            return null;
        }
        return getAccordWithTheGoodsCouponRules(goodsIds, couponRuleByActive);
    }

    private List<CouponRule> AllGoodsCoupon(Integer siteId, String goodsIds) {
        List<CouponRule> couponRuleList = new ArrayList<>();
        allGoodsCouponsUnAuth(siteId, goodsIds)
            .forEach(couponActivity -> {
                List<CouponRule> crl = findCouponByActivity(couponActivity.getSiteId(), couponActivity);
//                List<CouponRule> crl = couponActivity.getCouponRules();
                if (crl != null && !crl.isEmpty()) {
                    List<CouponRule> coupons = crl.stream().filter(c -> {
                        CouponRule addCoupon4Easy2See = isAddCoupon4Easy2See(c, goodsIds);
                        if (addCoupon4Easy2See != null) {
                            return true;
                        }
                        return false;
                    }).collect(toList());
                    //将全类品的券添加进来
                    couponRuleList.addAll(coupons);
                }
            });
        return couponRuleList;
    }

    private List<CouponRule> findCouponByActivity(Integer siteId, CouponActivity activityId) {
        if (StringUtil.isBlank(activityId.getSendRules())) {
            return null;
        }
        List<CouponRule> couponRuleByActive = couponRuleMapper.findCouponRuleByActive(siteId, activityId.getId());
        return couponRuleByActive.stream()
            .filter(p -> p.getStatus() == 0 && (p.getAmount() > 0 || p.getAmount() == -1))
            .filter(p -> p.getAimAt() == 1)
            .collect(toList());
    }


    /**
     * 判断会员是否领取过
     *
     * @param siteId
     * @param activityId
     * @param userId
     * @return
     */
    private boolean isReceive(Integer siteId, Integer activityId, Integer userId) {
        log.info("CouponFilterService siteId:" + siteId + "||activityId:" + activityId + "||userId:" + userId);
        Integer isReceiveCount = couponDetailMapper.findIsReceiveCount(siteId, userId, activityId); //此活动领取过多少张
        Integer ruleByActiveCount = couponRuleActivityMapper.getRuleByActiveCount(siteId, activityId);//此活动下一共有多少优惠券
        if (ruleByActiveCount != 0 && isReceiveCount < ruleByActiveCount) {
            return true;
        }
        return false;
    }

    /**
     * 找出商品详情页可以使用的优惠券
     *
     * @param siteId     站点id
     * @param activityId 活动id
     * @param goodsIds   商品id
     * @return 所有优惠券的一个list集合
     */
    private List<CouponRule> filterCoupon(Integer siteId, Integer activityId, String goodsIds, Integer userId) {
        CouponActivity couponActivity = couponActivityMapper.getCouponActivity(siteId, activityId);
        List<CouponRule> couponRuleByActive = couponRuleMapper.findCouponRuleByActive(siteId, activityId).stream()
            .filter(p -> p.getStatus() == 0&& (p.getAmount() > 0 || p.getAmount() == -1))
            .filter(p->couponRuleService.judgeLimitTime(p.getTimeRule()))
            .filter(p -> p.getAimAt() == 1)
            .filter(p -> { //用户已经领取过的优惠券 isCanGet=1 否则为0
                Integer value = couponDetailMapper.countCouponByUserRuleAndActivity(userId.toString(), p.getRuleId(),
                    activityId, siteId);
                Integer sendLimit = couponActivity.getSendLimit() == null ? 1 : couponActivity.getSendLimit();
                if (value.intValue() >= sendLimit.intValue()) {
                    p.setIsCanGet(1);
                } else {
                    p.setIsCanGet(0);
                }
                return true;
            })
            .filter(p -> {
                //没有领取的
                if (p.getIsCanGet() == 0) {
                    // 发放方式是 自动 红包 门店 店员 的方式并且没有领取的不解析
                    if (couponActivity.getSendWay() == 1 || couponActivity.getSendWay() == 3 || couponActivity.getSendWay() == 4 || couponActivity.getSendWay() == 5) {
                        return false;
                    } else {
                        return true;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
        //没有正常状态的优惠券了
        if (couponRuleByActive == null || couponRuleByActive.isEmpty()) {
            return null;
        }
        return getAccordWithTheGoodsCouponRules(goodsIds, couponRuleByActive);
    }

    private List<CouponRule> getAccordWithTheGoodsCouponRules(String goodsIds, List<CouponRule> couponRuleByActive) {
        Iterator<CouponRule> iterator = couponRuleByActive.iterator();
        CouponRule couponRule;
        GoodsRule goodsRule = null;
        List<CouponRule> ruleList = new ArrayList<>();
        while (iterator.hasNext()) {
            couponRule = iterator.next();
            couponRule.setCouponView(parsingCouponRuleService.accountCoupon(couponRule.getAimAt(),
                couponRule.getCouponType(), couponRule.getOrderRule(), couponRule.getGoodsRule()));
            couponRule.setEffectiveTime(couponDetailService.getEffectiveTimeForGoodsDetail(couponRule.getTimeRule(),
                couponRule.getCreateTime()));
            couponRule.setEffectiveTimeType(couponDetailService.getEffictiveTimeType(couponRule.getTimeRule()));

            //针对订单的都是全品类的所以无需判断是否包涵此商品
            try {
                goodsRule = JacksonUtils.json2pojo(couponRule.getGoodsRule(), GoodsRule.class);
            } catch (Exception e) {
                log.error("解析GoodsRule 失败:{}", e);
            }
            if (goodsRule == null) {
                ruleList.add(couponRule);
                continue;
            }
            String promotionGoods = goodsRule.getPromotion_goods();
            List<String> promotionGoodsList = arrayConvertList(promotionGoods);
            List<String> goodsIdsList = arrayConvertList(goodsIds);
            if (goodsIdsList == null || goodsIdsList.isEmpty()) {
                return null;
            }
            switch (goodsRule.getType()) {
                case 0:
                    ruleList.add(couponRule);
                    break;
                case 1: //按类目暂时无，所以默认true
                    ruleList.add(couponRule);
                    break;
                case 2:
                    goodsIdsList.retainAll(promotionGoodsList);
                    if (goodsIdsList.size() > 0) {
                        ruleList.add(couponRule);
                    }
                    break;
                case 3:
                    int size = goodsIdsList.size();
                    goodsIdsList.retainAll(promotionGoodsList);
                    if (!(size == goodsIdsList.size())) {
                        ruleList.add(couponRule);
                    }
                    break;
                default:
                    ruleList.add(couponRule);
                    break;
            }

        }
        return ruleList;
    }

    /**
     * 数据转list
     *
     * @param ids
     * @return
     */
    private List<String> arrayConvertList(String ids) {
        if (StringUtils.isBlank(ids)) {
            return null;
        }
        String[] idsArr = ids.split(",");
        return new ArrayList<>(Arrays.asList(idsArr));

    }

    /**
     * 添加优惠券并分组
     *
     * @param siteId
     * @param goodsIds
     * @param user_id
     * @param easyToSees
     */
    public void addCoupons4Easy2See(Integer siteId, String goodsIds, Integer user_id, List<EasyToSee> easyToSees) {
        //可使用券
        List<CouponRule> userCanUseCoupon = getCouponRules(siteId, user_id);
        userCanUseCoupon = getCanUserCoupon(goodsIds, userCanUseCoupon);

        /*//可领取券
        List<CouponRule> couponRuleList = canReceiveCoupon2(siteId, goodsIds, user_id);*/
        List<EasyToSeeCoupon> coupons = new ArrayList<EasyToSeeCoupon>();
        List<CouponActivity> canReceiveCoupon = getCanReceiveCoupon(siteId, goodsIds, user_id);
        canReceiveCoupon.forEach(ca -> {
            List<CouponRule> couponRules = ca.getCouponRules();
            couponRules.forEach(c -> {
                coupons.add(EasyToSeeCoupon.buildEasyToSeeCoupon(c));
            });
        });
        //可使用 添加
        userCanUseCoupon.forEach(c->{
            coupons.add(EasyToSeeCoupon.buildEasyToSeeCoupon(c));
        });
        //对可使用及可领取的券进行去重
        Set<EasyToSeeCoupon> distinct = new HashSet<EasyToSeeCoupon>(coupons);
//        coupons.
        //合并分组
        easyToSees.forEach(e -> {
            String goodsId = e.getGoodsId();
            distinct.forEach(c -> {
                CouponRule addCoupon4Easy2See = isAddCoupon4Easy2See(c.getCoupon(), goodsId);
                if (addCoupon4Easy2See != null) {
                    e.getProCouponEasyToSee().getProCouponList().getCouponsRule().add(c);
                }
            });
            Map<Integer, List<EasyToSeeCoupon>> e2CouponsGroup = new ArrayList<>(e.getProCouponEasyToSee().getProCouponList().getCouponsRule()).stream().collect(groupingBy(e2s -> {
                return e2s.getCoupon().getCouponType();
            }));
            System.out.println(e2CouponsGroup);
            if (e2CouponsGroup != null && !e2CouponsGroup.entrySet().isEmpty()) {
                e.getProCouponEasyToSee().getProCouponList().setCouponsRuleGroup(e2CouponsGroup);
            }
        });
        System.out.println(easyToSees);
    }

    /**
     * 查询出符合商品的优惠券
     * @param goodsIds
     * @param usercanUserCouponDetail
     * @return
     */
    private List<Map<String,Object>>getCanUserCouponDetail(String goodsIds,List<Map<String,Object>> usercanUserCouponDetail){
        return usercanUserCouponDetail.stream().filter(m->{
            GoodsRule goods = JSON.parseObject(m.get("goods_rule").toString(), GoodsRule.class);
            return checkCouponGoods(goodsIds,goods);
        }).collect(toList());

    }
    private List<CouponRule> getCanUserCoupon(String goodsIds, List<CouponRule> userCanUseCoupon) {
        userCanUseCoupon = userCanUseCoupon.stream().filter(c -> {
            try {
                GoodsRule goods = JacksonUtils.json2pojo(c.getGoodsRule(), GoodsRule.class);
                return checkCouponGoods(goodsIds, goods);
            } catch (Exception e) {
                return false;
            }
        }).collect(toList());
        return userCanUseCoupon;
    }

    public Boolean checkCouponGoods(String goodsIds, GoodsRule goods) {
        List<String> promotionGoodsList = arrayConvertList(goods.getPromotion_goods());
        List<String> goodsIdsList = arrayConvertList(goodsIds);
        switch (goods.getType()) {
            case 0:
                return true;
            case 1: //按类目暂时无，所以默认true
                return true;
            case 2:
                goodsIdsList.retainAll(promotionGoodsList);
                if (goodsIdsList.size() > 0) {
                    return true;
                }
                break;
            case 3:
                int size = goodsIdsList.size();
                goodsIdsList.retainAll(promotionGoodsList);
                if (!(size == goodsIdsList.size())) {
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

    public CouponRule isAddCoupon4Easy2See(CouponRule couponRule, String goodsId) {
        GoodsRule goodsRule = null;
        try {
            goodsRule = JSON.parseObject(couponRule.getGoodsRule(), GoodsRule.class);
        } catch (Exception e) {
            log.error("解析GoodsRule失败:{}", e);
            e.printStackTrace();
        }
        if (goodsRule == null && couponRule.getOrderRule() != null) {
            return couponRule;
        }
        String coupon_goods = goodsRule.getPromotion_goods();

        String[] couponArr = coupon_goods.split(",");
        boolean contains = Arrays.asList(couponArr).contains(goodsId);
        switch (goodsRule.getType()) {
            case 0:
                return couponRule;
            case 1:
                return null;
            case 2:
                if (contains) {
                    return couponRule;
                }
                break;
            case 3:
                if (!contains) {
                    return couponRule;
                }
                break;
        }
        return null;
    }

    public void resolveRuleAndPriceByPromotions(Integer couponType, List<EasyToSeeCoupon> coupons, EasyToSee e, Integer searchType) {
        Integer max = null;
        Integer min = null;
        Integer count = 0;
        //使用普通for进行remove操作
        for (int i = 0; i < coupons.size(); i++) {
            EasyToSeeCoupon easyToSeeCoupon = coupons.get(i);
            Integer result = resolveCouponsTags(easyToSeeCoupon, couponType, e, searchType);
            if (result == null) {
                log.error("优惠券优惠券解析失败");
                //出现问题，移除该券
                e.getProCouponEasyToSee().getProCouponList().getCouponsRuleGroup().get(couponType).remove(i);
                e.getProCouponEasyToSee().getProCouponList().getCouponsRule().remove(easyToSeeCoupon);
                return;
            } else if (result == 1 || couponType == 300 || couponType == 500) {
                //在分组中移除
                e.getProCouponEasyToSee().getProCouponList().getCouponsRuleGroup().get(couponType).remove(i);
                //在列表中移除
                e.getProCouponEasyToSee().getProCouponList().getCouponsRule().remove(easyToSeeCoupon);
                boolean useCoupon = e.getTitles().contains("用券");
                if (!useCoupon) {
                    e.getTitles().add("用券");
                }
                return;
            }
            if (couponType == 100) {
                if (max == null || result > max) {
                    count = i;
                    max = result;
                }
                continue;
            }
            if (couponType == 200) {
                if (min == null || result < min) {
                    count = i;
                    min = result;
                }
                continue;
            }
        }
        EasyToSeeCoupon easyToSeeCoupon = coupons.get(count);
        //只有打折和满减
        switch (couponType) {
            case 100: {
                //满减
                resolveReduceCouponBetterPrice(easyToSeeCoupon, couponType, e, searchType);
                break;
            }
            case 200: {
                //打折
                resolveDiscountCouponBetterPrice(easyToSeeCoupon, couponType, e, searchType);
                break;
            }
        }
        boolean useCoupon = e.getTitles().contains("用券");
        if (!useCoupon) {
            e.getTitles().add("用券");
        }
        //移除
        e.getProCouponEasyToSee().getProCouponList().getCouponsRuleGroup().get(couponType).remove(easyToSeeCoupon);
        //讲道理的，列表和分组列表所引用的EasyToSeeCoupon 应该都是同一个引用，上面使用remove，列表也会删除，保险起见也再remove列表
        e.getProCouponEasyToSee().getProCouponList().getCouponsRule().remove(easyToSeeCoupon);
    }

    /**
     * 解析打折券标签
     *
     * @param easyToSeeCoupon
     * @param couponType
     * @param e
     * @param searchType
     */
    private void resolveDiscountCouponBetterPrice(EasyToSeeCoupon easyToSeeCoupon, Integer couponType, EasyToSee e, Integer searchType) {
        String tags = "";
        GoodsRule goodsRule = JSON.parseObject(easyToSeeCoupon.getCoupon().getGoodsRule(), GoodsRule.class);
        //打折 立打多少折
        if (goodsRule.getRule_type() == 4) {
            Map<String, Integer> rule = (Map<String, Integer>) goodsRule.getRule();
            tags += rule.get("direct_discount") / 10.0f + "折";
        } else if (goodsRule.getRule_type() == 1) {
            //每满多少折
            List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
            if (rule.size() == 1) {
                Map<String, Integer> ruleSigle = rule.get(0);

                tags += "满" + promotionsFilterService.save2Digit(ruleSigle.get("meet_money") / 100.00d) + "元" + ruleSigle.get("discount") / 10.0f + "折";
            } else {

                OptionalInt meet_money = rule.stream().map(map -> map.get("meet_money")).mapToInt(Integer::intValue)
                    .max();
                OptionalInt discount = rule.stream().map(map -> map.get("discount")).mapToInt(Integer::intValue)
                    .min();
                tags += "最高满" + promotionsFilterService.save2Digit(meet_money.orElseGet(() -> 0) / 100.00d) + "元" + discount.orElseGet(() -> 0) / 10.0f + "折";
            }
        } else if (goodsRule.getRule_type() == 5) {
            Map<String, Integer> rule = (Map<String, Integer>) goodsRule.getRule();
            tags += "第" + rule.get("how_piece") + "件" + rule.get("discount") / 10.0f + "折";
        } else if (goodsRule.getRule_type() == 2) {
            List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
            if (rule.size() == 1) {
                Map<String, Integer> sigle = rule.get(0);
                tags += "满" + sigle.get("meet_num") + "件" + sigle.get("discount") / 10.0f + "折";
            } else {
                OptionalInt meet_num = rule.stream().map(map -> map.get("meet_num")).mapToInt(Integer::intValue)
                    .max();
                OptionalInt discount = rule.stream().map(map -> map.get("discount")).mapToInt(Integer::intValue)
                    .min();
                tags += "最高满" + meet_num.orElseGet(() -> 0) + "件" + discount.orElseGet(() -> 0) / 10.0f + "折";
            }
        }
        easyToSeeCoupon.setTags(tags);
        easyToSeeCoupon.setRuleId(easyToSeeCoupon.getCoupon().getRuleId());
        isCanAddCouponTag(easyToSeeCoupon, e, searchType);
    }

    /**
     * 解析满减券标签
     *
     * @param easyToSeeCoupon
     * @param couponType
     * @param e
     * @param searchType
     */
    private void resolveReduceCouponBetterPrice(EasyToSeeCoupon easyToSeeCoupon, Integer couponType, EasyToSee e, Integer searchType) {
        String tags = "";
        GoodsRule goodsRule = JSON.parseObject(easyToSeeCoupon.getCoupon().getGoodsRule(), GoodsRule.class);
        if (goodsRule.getRule_type() == 4) {
            Map<String, Integer> rule = (Map<String, Integer>) goodsRule.getRule();
            tags += "立减" + promotionsFilterService.save2Digit(rule.get("direct_money") / 100.00d) + "元";

        } else if (goodsRule.getRule_type() == 1) {
            List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
            if (rule.size() == 1) {
                Map<String, Integer> ruleSigle = rule.get(0);
                tags += "满" + promotionsFilterService.save2Digit(ruleSigle.get("meet_money") / 100.00d) + "元减" + promotionsFilterService.save2Digit(ruleSigle.get("reduce_price") / 100.00d) + "元";

            } else {
                OptionalInt meet_money = rule.stream().map(map -> map.get("meet_money")).mapToInt(Integer::intValue)
                    .max();
                OptionalInt reduce_price = rule.stream().map(map -> map.get("reduce_price")).mapToInt(Integer::intValue)
                    .max();
                tags += "最高满" + promotionsFilterService.save2Digit(meet_money.orElseGet(() -> 0) / 100.00d) + "元减" + promotionsFilterService.save2Digit(reduce_price.orElseGet(() -> 0) / 100.00d) + "元";
            }
        } else if (goodsRule.getRule_type() == 0) {
            Map<String, Integer> rule = (Map<String, Integer>) goodsRule.getRule();
            tags += "每满" + promotionsFilterService.save2Digit(rule.get("each_full_money") / 100.00d) + "元减" + promotionsFilterService.save2Digit(rule.get("reduce_price") / 100.00d) + "元";
        }
        easyToSeeCoupon.setTags(tags);
        easyToSeeCoupon.setRuleId(easyToSeeCoupon.getCoupon().getRuleId());
        isCanAddCouponTag(easyToSeeCoupon, e, searchType);
    }

    private void isCanAddCouponTag(EasyToSeeCoupon easyToSeeCoupon, EasyToSee e, Integer searchType) {
        Integer currentSize = 0;
        Integer tagsMax = 5;
        if (searchType == 0) {
            // 搜索的
            currentSize = e.getTags().size() + e.getCouponTags().size();
            tagsMax = EasyToSeeConstants.SEARCH_GOODS;
        } else if (searchType == 1) {
            currentSize = e.getCouponTags().size();
            tagsMax = EasyToSeeConstants.GOODS_DETAIL;
        }
        // 当前收集标签是否满足，未满足则添加
        boolean isadd = true;
        if (currentSize < tagsMax || tagsMax == -1) {
            for (EasyToSeeCoupon tags : e.getCouponTags()) {
                if (tags.getTags().equals(easyToSeeCoupon.getTags())) {
                    isadd = false;
                }
            }
            if (isadd) {
                e.getCouponTags().add(easyToSeeCoupon);
            }
        }

    }

    private Integer resolveCouponsTags(EasyToSeeCoupon easyToSeeCoupon, Integer couponType, EasyToSee e, Integer searchType) {
        String tags = "";
        GoodsRule goodsRule = JSON.parseObject(easyToSeeCoupon.getCoupon().getGoodsRule(), GoodsRule.class);
        if (goodsRule == null) {
            log.error("resolveCouponsTags goodsRule is null");
            return null;
        }
        switch (couponType) {
            case 300: {
                Map<String, Integer> rule = (Map<String, Integer>) goodsRule.getRule();
                //限价
                if (goodsRule.getRule_type() == 3) {
                    tags += "特价:" + promotionsFilterService.save2Digit(rule.get("each_goods_price") / 100.00d);
                    easyToSeeCoupon.setTags(tags);
                    easyToSeeCoupon.setRuleId(easyToSeeCoupon.getCoupon().getRuleId());
                    //加入 肯德基豪华套餐
                    isCanAddCouponTag(easyToSeeCoupon, e, searchType);
                    return 1;
                }
                break;
            }
            case 200: {
                //打折 立打多少折
                if (goodsRule.getRule_type() == 4) {
                    Map<String, Integer> rule = (Map<String, Integer>) goodsRule.getRule();
                    return rule.get("direct_discount");
                } else if (goodsRule.getRule_type() == 1) {
                    //每满多少折
                    List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
                    OptionalInt discount = rule.stream().map(map -> map.get("discount")).mapToInt(Integer::intValue)
                        .min();
                    return discount.orElseGet(() -> 0);
                } else if (goodsRule.getRule_type() == 5) {
                    Map<String, Integer> rule = (Map<String, Integer>) goodsRule.getRule();
                    return rule.get("discount");
                } else if (goodsRule.getRule_type() == 2) {
                    List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
                    OptionalInt discount = rule.stream().map(map -> map.get("discount")).mapToInt(Integer::intValue)
                        .min();
                    return discount.orElseGet(() -> 0);
                }
                break;
            }
            case 100: {
                if (goodsRule.getRule_type() == 4) {
                    Map<String, Integer> rule = (Map<String, Integer>) goodsRule.getRule();
                    return rule.get("direct_money");
                } else if (goodsRule.getRule_type() == 1) {
                    List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
                    OptionalInt price = rule.stream().map(map -> map.get("reduce_price")).mapToInt(Integer::intValue)
                        .max();
                    return price.orElseGet(() -> 0);
                } else if (goodsRule.getRule_type() == 0) {
                    Map<String, Integer> rule = (Map<String, Integer>) goodsRule.getRule();
                    return rule.get("reduce_price");
                }
                break;
            }
            case 500: {
                if (goodsRule.getRule_type() == 1) {
                    List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
                    if (rule.size() == 1) {
                        Map<String, Integer> ruleSigle = rule.get(0);
                        tags += "满" + ruleSigle.get("meetNum") + "件送" + (goodsRule.getGift_send_type() == 2 ? ruleSigle.get("sendNum") + "件" : "礼品");
                    } else {
                        OptionalInt meetNum = rule.stream().map(map -> map.get("meetNum")).mapToInt(Integer::intValue)
                            .max();
                        OptionalInt sendNum = rule.stream().map(map -> map.get("sendNum")).mapToInt(Integer::intValue)
                            .max();
                        tags += "满" + meetNum.orElseGet(() -> 0) + "件送" + (goodsRule.getGift_send_type() == 2 ? sendNum.orElseGet(() -> 0) + "件" : "礼品");
                    }
                    easyToSeeCoupon.setTags(tags);
                    easyToSeeCoupon.setRuleId(easyToSeeCoupon.getCoupon().getRuleId());
                    isCanAddCouponTag(easyToSeeCoupon, e, searchType);
                    return 1;
                } else if (goodsRule.getRule_type() == 2) {
                    List<Map<String, Integer>> rule = (List<Map<String, Integer>>) goodsRule.getRule();
                    if (rule.size() == 1) {
                        Map<String, Integer> ruleSigle = rule.get(0);
                        tags += "满" + promotionsFilterService.save2Digit(ruleSigle.get("meetMoney") / 100.00d) + "元送" + (goodsRule.getGift_send_type() == 2 ? ruleSigle.get("sendNum") + "件" : "礼品");
                    } else {
                        OptionalInt meetMoney = rule.stream().map(map -> map.get("meetMoney")).mapToInt(Integer::intValue)
                            .max();
                        OptionalInt sendNum = rule.stream().map(map -> map.get("sendNum")).mapToInt(Integer::intValue)
                            .max();
                        tags += "满" + promotionsFilterService.save2Digit(meetMoney.orElseGet(() -> 0) / 100.00d) + "元送" + (goodsRule.getGift_send_type() == 2 ? sendNum.orElseGet(() -> 0) + "件" : "礼品");
                    }
                    easyToSeeCoupon.setTags(tags);
                    easyToSeeCoupon.setRuleId(easyToSeeCoupon.getCoupon().getRuleId());
                    isCanAddCouponTag(easyToSeeCoupon, e, searchType);
                    return 1;
                }
            }

        }
        return null;
    }

    public List<EasyToSee> createEasy2See(EasyToSeeParam easyToSeeParam) {
        //初始化所有商品的Easy 2 See
        List<EasyToSee> easyToSeeList = new ArrayList<EasyToSee>() {{
            List<String> goodsIdsList = orderDeductionUtils.arrayConvertList(easyToSeeParam.getGoodsIds());
            goodsIdsList.stream().forEach(goods -> {
                add(new EasyToSee(goods));
            });
        }};
        return easyToSeeList;
    }

    public void resolveEasy2SeeTransTags(List<EasyToSee> easyToSees) {
        easyToSees.stream().forEach(e -> {
            Map<Integer, List<EasyToSeeCoupon>> e2sCouponsGroup = e.getProCouponEasyToSee().getProCouponList().getCouponsRuleGroup();
            //-1 查询券没有限制
            promotionsFilterService.resolveGeneralCouponRule(e2sCouponsGroup, e, -1);
        });
    }

    public void addCoupons(Integer siteId, List<EasyToSee> easyToSees, String goodsIds, Map<String, Object> result) {
        List<CouponRule> couponRules = AllGoodsCoupon(siteId, goodsIds);
        easyToSees.stream().forEach(e -> {
            couponRules.stream().filter(c -> {
                CouponRule addCoupon4Easy2See = isAddCoupon4Easy2See(c, goodsIds);
                if (addCoupon4Easy2See != null) {
                    return true;
                }
                return false;
            }).forEach(c -> {
                e.getProCouponEasyToSee().getProCouponList().getCouponsRule().add(EasyToSeeCoupon.buildEasyToSeeCoupon(c));
            });
            if (e.getProCouponEasyToSee().getProCouponList().getCouponsRule().size() > 0) {
                Map<Integer, List<EasyToSeeCoupon>> group = e.getProCouponEasyToSee().getProCouponList().getCouponsRule().stream().collect(groupingBy(c -> {
                    return c.getCoupon().getCouponType();
                }));
                e.getProCouponEasyToSee().getProCouponList().getCouponsRuleGroup().putAll(group);
                result.put("isNeedAuth", true);
            }
        });
    }

    public void addCanUseCoupon(Integer siteId,List<CouponActivity> canReceiveCoupon, Integer userId, String goodsId) {
        //可使用券
        List<Map<String, Object>> couponDetails = getCouponDetails(siteId, userId);
        List<Map<String, Object>> canUserCouponDetail = getCanUserCouponDetail(goodsId, couponDetails);
        //对两边的券进行去重处理
        canReceiveCoupon.forEach(c->{
            List<Integer> rules = c.getCouponRules().stream().map(CouponRule::getRuleId).collect(toList());
            for(int i=0;i<canUserCouponDetail.size();){
                Map<String, Object> m = canUserCouponDetail.get(i);
                Integer activityId = Integer.parseInt(m.get("source").toString());
                Integer rule_id = Integer.parseInt(m.get("rule_id").toString());
                if(c.getId().equals(activityId)){
                    if(!rules.contains(rule_id)){
                        //可领取活动中没有包含用户已领取的券
                        CouponRule couponRuleById = couponRuleMapper.findCouponRuleById(rule_id, siteId);
                        //已领取
                        couponRuleById.setIsCanGet(1);
                        //加入到当前活动下
                        c.getCouponRules().add(couponRuleById);
                    }
                    //删除掉优惠券
                    canUserCouponDetail.remove(i);
                }else {
                    i++;
                }
            }
        });
        //活动不重复的优惠券进行合并  合并前处理
        List<CouponActivity> couponActivities = canUserCouponDetail.stream().map(m -> {
            Integer activityId = Integer.parseInt(m.get("source").toString());
            Integer rule_id = Integer.parseInt(m.get("rule_id").toString());
            CouponActivity couponActivity = couponActivityMapper.getCouponActivity(siteId, activityId);
            List<CouponRule> coupons = new ArrayList<CouponRule>();
            CouponRule couponRule = couponRuleMapper.findCouponRuleById(rule_id, siteId);
            //设置优惠显示信息--------分隔
            couponRule.setCouponView(parsingCouponRuleService.accountCoupon(couponRule.getAimAt(),
                couponRule.getCouponType(), couponRule.getOrderRule(), couponRule.getGoodsRule()));
            couponRule.setEffectiveTime(couponDetailService.getEffectiveTimeForGoodsDetail(couponRule.getTimeRule(),
                couponRule.getCreateTime()));
            couponRule.setEffectiveTimeType(couponDetailService.getEffictiveTimeType(couponRule.getTimeRule()));
            //已领取
            couponRule.setIsCanGet(1);
            //------
            coupons.add(couponRule);
            couponActivity.setCouponRules(coupons);
            return couponActivity;
        }).collect(toList());
        //合并
        canReceiveCoupon.addAll(couponActivities);
    }
}
