package com.jk51.modules.promotions.service;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.ParamErrorException;
import com.jk51.exception.UnknownTypeException;
import com.jk51.model.concession.GiftMsg;
import com.jk51.model.concession.result.GiftResult;
import com.jk51.model.concession.result.Result;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponDetailConcessionLog;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.GoodsRule;
import com.jk51.model.order.*;
import com.jk51.model.order.response.DistributeResponse;
import com.jk51.model.order.response.OrderResponse;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsDetail;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.GiftRule;
import com.jk51.modules.concession.service.ConcessionResultHandler;
import com.jk51.modules.coupon.mapper.CouponDetailConcessionLogMapper;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponInformErpService;
import com.jk51.modules.coupon.service.CouponRuleService;
import com.jk51.modules.coupon.utils.CouponNoUtils;
import com.jk51.modules.order.mapper.DistributeOrderMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.promotions.mapper.PromotionsDetailMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jk51.commons.CommonConstant.TRADES_RESP_CODE_MISSINF_PARAMS;
import static com.jk51.modules.coupon.constants.CouponConstant.*;
import static com.jk51.modules.promotions.constants.PromotionsConstant.PROMOTIONS_RULE_TYPE_GIFT;
import static java.util.stream.Collectors.*;

/**
 * filename :com.jk51.modules.promotions.service.
 * author   :zw
 * date     :2017/8/17
 * Update   :
 */
@Service
public class PromotionsOrderService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PromotionsRuleService promotionsRuleService;
    @Autowired
    private PromotionsActivityService promotionsActivityService;
    @Autowired
    private CouponRuleService couponRuleService;
    @Autowired
    private CouponInformErpService couponInformErpService;

    @Autowired
    private CouponNoUtils couponNoUtils;
    @Autowired
    private ConcessionResultHandler concessionResultHandler;

    @Autowired
    private PromotionsDetailMapper promotionsDetailMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private DistributeOrderMapper distributeOrderMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private CouponDetailConcessionLogMapper couponDetailConcessionLogMapper;


    /**
     * 优惠券和优惠活动赠品信息库存校验 入口
     *
     * @param giftList
     * @param siteId
     * @param ids
     * @param couponId
     * @return
     */
    public OrderResponse verifyGiftGoodsStock(List<GiftResult> giftList, Integer siteId, String ids, Integer couponId) {

        // 满赠优惠券校验入口
        if (couponId != null && couponId > 0) {
            CouponRule couponRule = couponRuleMapper.getByCouponId(couponId, siteId);

            if (couponRule != null && couponRule.getCouponType() == GIFT_COUPON)
                return verifyGiftGoodsStockByCouponId(giftList, siteId, couponId);
        }

        // 满赠活动校验入口
        if (StringUtils.isNotBlank(ids)) {
            return verifyGiftGoodsStockByActivity(giftList, siteId, ids);
        }

        return new OrderResponse();
    }

    /**
     * 记录优惠计算结果到表
     *
     * @param siteId
     * @param tradesId
     * @param homeDeliveryAndStoresInvite
     * @param distributeResponse          预下单的回调结果
     */
    public void
    saveConcessionResultToTable(Integer siteId,
                                long tradesId,
                                HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite,
                                DistributeResponse distributeResponse) {

        List<GiftResult> giftGoods = homeDeliveryAndStoresInvite.getGiftGoods();
        Integer couponDetailId = homeDeliveryAndStoresInvite.getUserCouponId();

        // 如果是赠品列表，则保存到b_orders表的同时记录 优惠券or活动 信息
        if (giftGoods != null && giftGoods.size() > 0) {
            addOrdersGiftGoods(giftGoods, tradesId, siteId);
        }

        // 优惠券优惠记录（包括优惠金钱和赠送物品）
        if (couponDetailId != null && couponDetailId > 0) {
            saveCouponDataAfterOrder(siteId, tradesId, homeDeliveryAndStoresInvite, distributeResponse, couponDetailId);
        }

        // 活动优惠记录（包括优惠金钱和赠送物品）
        if (!StringUtil.isBlank(distributeResponse.getEfficientPromotionsActivityId())) {
            // 保存活动优惠金额数据
            savePromotionsDetailAfterOrder(siteId, tradesId, homeDeliveryAndStoresInvite, distributeResponse);

            // 修改活动规则库存数据
            if (giftGoods != null && giftGoods.size() > 0)
                changePromotionsRuleStorageAfterOrder(siteId, giftGoods);
        }
    }

    /**
     * 记录优惠计算结果到表
     *
     * @param siteId
     * @param tradesId
     * @param storeDirect
     * @param distributeResponse 预下单的回调结果
     */
    public void
    saveConcessionResultToTable(Integer siteId,
                                long tradesId,
                                StoreDirect storeDirect,
                                DistributeResponse distributeResponse) {
        Integer couponDetailId;
        String userCouponId = storeDirect.getUserCouponId();
        if (StringUtils.isBlank(userCouponId) || "0".equals(userCouponId)) {
            couponDetailId = null;
        } else {
            couponDetailId = Integer.parseInt(userCouponId);
        }

        HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite = new HomeDeliveryAndStoresInvite();
        homeDeliveryAndStoresInvite.setSiteId(storeDirect.getSiteId());
        homeDeliveryAndStoresInvite.setGiftGoods(storeDirect.getGiftGoods());
        homeDeliveryAndStoresInvite.setUserCouponId(couponDetailId);
        homeDeliveryAndStoresInvite.setMobile(storeDirect.getMobile());

        saveConcessionResultToTable(siteId, tradesId, homeDeliveryAndStoresInvite, distributeResponse);
    }


    /**
     * 改变赠品规则内的赠品库存
     *
     * @param siteId
     * @param giftGoods
     */
    private void changePromotionsRuleStorageAfterOrder(Integer siteId, List<GiftResult> giftGoods) {
        giftGoods.stream()
            .filter(giftResult -> giftResult.getConcessionDesc().getConcessionType() == 2)
            .forEach(giftResult -> {
                Map<Integer, Integer> map = new HashMap<>();
                giftResult.getGiftList().forEach(giftMsg -> map.put(giftMsg.getGoodsId(), -giftMsg.getSendNum()));

                PromotionsActivity promotionsActivity = promotionsActivityService
                    .getActivePromotionsActivityWithPromotionsRule(siteId, giftResult.getConcessionDesc().getPromotionsActivityId())
                    .orElseThrow(ParamErrorException::new);

                try {
                    promotionsRuleService.changeGiftNums(promotionsActivity.getPromotionsRule(), map);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * 保存活动优惠金额数据
     *
     * @param siteId
     * @param tradesId
     * @param homeDeliveryAndStoresInvite
     * @param distributeResponse
     */
    private void savePromotionsDetailAfterOrder(Integer siteId,
                                                long tradesId,
                                                HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite,
                                                DistributeResponse distributeResponse) {

        Result concessionResult = distributeResponse.getConcessionResult();
        Map<Integer, Integer> resultByPromotionsId = concessionResultHandler.gatherDiscountResultByPromotionsId(concessionResult, homeDeliveryAndStoresInvite.getGiftGoods());

        if (resultByPromotionsId.size() == 0)
            return;

        Member member = memberMapper.queryMember(homeDeliveryAndStoresInvite.getMobile(), siteId);

        for (Map.Entry<Integer, Integer> entry : resultByPromotionsId.entrySet()) {
            Integer promotionsActivityId = entry.getKey();
            Optional<PromotionsActivity> optional = promotionsActivityService.getActivePromotionsActivityWithPromotionsRule(siteId, promotionsActivityId);
            PromotionsActivity promotionsActivity = optional.orElseThrow(ParamErrorException::new);
            PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
            Integer promotionsRuleId = promotionsRule.getId();

            Integer discount = entry.getValue();

            PromotionsDetail promotionsDetail = new PromotionsDetail();
            promotionsDetail.setSiteId(siteId);
            promotionsDetail.setPromotionsNo(couponNoUtils.getCouponDownDetailNum(promotionsRuleId, siteId, 1));
            promotionsDetail.setOrderId(Long.valueOf(tradesId).toString());
            promotionsDetail.setStatus(0);
            promotionsDetail.setActivityId(promotionsActivityId);
            promotionsDetail.setRuleId(promotionsRuleId);
            promotionsDetail.setUserId(member.getMemberId());
            promotionsDetail.setDiscountAmount(discount);

            int insert = promotionsDetailMapper.insert(promotionsDetail);
            if (insert != 1)
                throw new RuntimeException();

            promotionsRuleService.changeUseAmount(promotionsRule, 1);
            promotionsRuleService.changeSendAmount(promotionsRule, 1);
        }
    }

    /**
     * 在创建订单后记录优惠券使用，包括优惠记录和优惠券状态记录
     *
     * @param siteId
     * @param tradesId
     * @param homeDeliveryAndStoresInvite
     * @param distributeResponse
     * @param couponDetailId
     */
    private void saveCouponDataAfterOrder(Integer siteId,
                                          long tradesId,
                                          HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite,
                                          DistributeResponse distributeResponse,
                                          Integer couponDetailId) {

        List<GiftResult> giftGoods = homeDeliveryAndStoresInvite.getGiftGoods();
        CouponRule couponRule = couponRuleMapper.getByCouponId(couponDetailId, siteId);
        LocalDateTime now = LocalDateTime.now();

        if (couponRule != null) {
            switch (couponRule.getCouponType()) {
                case CASH_COUPON:
                case CASH_DISCOUNT_COUPON:
                case LIMIT_PRICE_COUPON:
                case FREE_POSTAGE_COUPON:
                    // 如果是金钱优惠，记录到couponDetail表
                    recordDiscountToCouponDetail(siteId, couponDetailId, distributeResponse);
                    break;

                case GIFT_COUPON:
                    // 赠品的记录已经记录在b_orders表
                    // 这里修改赠品优惠券的库存记录
                    changeGiftCouponStorage(couponDetailId, couponRule, giftGoods);
                    break;

                default:
                    throw new UnknownTypeException();
            }

            // 修改优惠券使用状态
            CouponDetail couponDetail = couponDetailMapper.getCouponDetailByCouponId(siteId, couponDetailId);
            if (null != couponDetail) {
                couponRuleMapper.updateUseAmountByRuleId(siteId, couponDetail.getRuleId());
                couponDetailMapper.updateStatusById(siteId, couponDetailId, String.valueOf(tradesId));
                couponInformErpService.ifContainCrashCouponThenSendQueueMessage(couponDetail);

                // insert couponDetailLog for search quickly
                couponDetail = couponDetailMapper.getCouponDetailByCouponId(siteId, couponDetailId);
                CouponDetailConcessionLog log = new CouponDetailConcessionLog(couponDetail, now);
                couponDetailConcessionLogMapper.insertWithoutId(log);
            }
        }
    }

    /**
     * 创建订单赠品商品模块  zw
     *
     * @param giftList
     * @param tradesId
     * @param siteId
     */
    private void addOrdersGiftGoods(List<GiftResult> giftList, long tradesId, Integer siteId) {
        //记录子订单信息
        for (GiftResult giftResult : giftList) {
            String concessionDescJSON = JSON.toJSONString(giftResult.getConcessionDesc());

            for (GiftMsg giftMsg : giftResult.getGiftList()) {
                GoodsInfo goodsInfo = distributeOrderMapper.getGoodsInfo(siteId, giftMsg.getGoodsId());
                if (goodsInfo != null) {
                    Orders orders = new Orders();
                    orders.setSiteId(siteId);
                    orders.setOrderId(getOrdersId(siteId));
                    orders.setGoodsId(goodsInfo.getGoodsId());
                    orders.setGoodsTitle(goodsInfo.getGoodsTitle());
                    orders.setGoodsPrice(goodsInfo.getDiscountPrice() == 0 ? goodsInfo.getShopPrice() : goodsInfo.getDiscountPrice());
                    orders.setGoodsNum(giftMsg.getSendNum());
                    orders.setGoodsGifts(1);//赠品
                    orders.setConcessionDesc(concessionDescJSON);
                    orders.setApprovalNumber(goodsInfo.getApprovalNumber());
                    orders.setSpecifCation(goodsInfo.getSpecifCation());
                    orders.setGoodsCategory(goodsInfo.getDrugCategory());
                    orders.setTradesId(tradesId);
                    orders.setGoodsImgurl("");
                    orders.setOrdersStatus(CommonConstant.WAIT_PAYMENT_BUYERS);
                    orders.setGoodsCode(goodsInfo.getGoodsCode());
                    orders.setYbGoodsId(goodsInfo.getYbGoodsId());
                    orders.setGoodsBatchNo("");
                    ordersMapper.addOrders(orders);
                }
            }
        }
    }

    /**
     * 生成订单交易号，siteId + 时间
     *
     * @param siteId
     * @return
     */
    private long getOrdersId(int siteId) {
        String tradesId = String.valueOf(siteId) + String.valueOf(System.currentTimeMillis());
        return Long.parseLong(tradesId);
    }

    private void recordDiscountToCouponDetail(Integer siteId, Integer couponDetailId, DistributeResponse
        distributeResponse) {
        CouponDetail couponDetail = couponDetailMapper.getCouponDetailByCouponId(siteId, couponDetailId);
        if (couponDetail == null)
            return;

        Integer discountAmount = distributeResponse.getCouponDeductionPrice();
        if (discountAmount != 0) {
            couponDetailMapper.updateDiscountAmountById(siteId, couponDetailId, discountAmount);
        }
    }

    /**
     * 退款和取消订单修改赠品库存 入口
     *
     * @param orderId
     * @param type
     */
    public void refundPromotions(String orderId, Integer type) {
        try {
            refundCouponByCouponId(orderId);
        } catch (Exception e) {
            logger.error("退款和取消订单修改赠品库存{}", e);
            e.printStackTrace();
        }
        refundPromotionsByActivity(orderId, type);
    }


    private void changeGiftCouponStorage(Integer couponDetailId,
                                         CouponRule couponRule,
                                         List<GiftResult> giftResultList) {
        if (null != giftResultList && !giftResultList.isEmpty()) {
            Map<Integer, Integer> map_ = new HashMap<>();

            Optional<GiftResult> optional = giftResultList.stream()
                .filter(giftResult -> giftResult.getConcessionDesc().getCouponDetailId().equals(couponDetailId))
                .findFirst();

            if (optional.isPresent()) {
                GiftResult giftResult = optional.get();
                giftResult.getGiftList().forEach(giftMsg -> map_.put(giftMsg.getGoodsId(), -giftMsg.getSendNum()));
            }

            try {
                //修改赠品数量
                couponRuleService.changeGiftNums(couponRule, map_);
            } catch (Exception e) {
                logger.error("修改赠品活动中赠品库存错误：{}", e);
            }
        }
    }


    /**
     * 退款和取消订单修改赠品库存 优惠券
     *
     * @param orderId
     */
    private void refundCouponByCouponId(String orderId) {
        CouponDetail couponDetail = couponDetailMapper.findCouponDetailByOrderId(orderId);
        if (couponDetail != null && couponDetail.getOrderId() != null) {
            int rule_id = couponDetail.getRuleId();
            int siteId = couponDetail.getSiteId();
            CouponRule couponRule = couponRuleMapper.getByCouponId(couponDetail.getId(), siteId);
            if (couponRule != null && couponRule.getCouponType() == 500) {
                //查询次订单的所有赠品信息
                Map<Integer, Integer> map_ = new HashMap<>();
                promotionsDetailMapper.queryGiftByOrderId(orderId, siteId)
                    .stream()
                    .forEach(giftMap -> {
                        Integer goods_id = giftMap.get("goods_id") == null ? 0 : Integer.parseInt(giftMap.get("goods_id").toString());
                        Integer goods_num = giftMap.get("goods_num") == null ? 0 : Integer.parseInt(giftMap.get("goods_num").toString());
                        map_.put(goods_id, goods_num);
                    });
                try {
                    couponRuleService.changeGiftNums(siteId, rule_id, map_);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("refundCouponByCouponId 修改赠品活动中赠品库存错误：{}", e);
                }
            }
        }
    }

    /**
     * @param orderId
     * @param type    0取消订单 1订单退款 优惠活动
     */
    private void refundPromotionsByActivity(String orderId, Integer type) {
        try {
            List<PromotionsDetail> list = promotionsDetailMapper.queryPromotionsDetailListByOrderId(orderId);
            if (!list.isEmpty()) {
                list.stream().forEach(pd -> {
                    PromotionsDetail promotionsDetail = new PromotionsDetail();
                    promotionsDetail.setSiteId(pd.getSiteId());
                    promotionsDetail.setId(pd.getId());
                    promotionsDetail.setOrderId(orderId);
                    Integer status = type == 0 ? 2 : 1;
                    promotionsDetail.setStatus(status);
                    try {
                        Integer count = promotionsDetailMapper.updateByCancel(promotionsDetail);
                        if (count != 1) {
                            throw new RuntimeException();
                        } else {
                            //修改使用数量
                            promotionsRuleService.changeUseAmount(pd.getSiteId(), pd.getRuleId(), -1);
                        }
                    } catch (Exception e) {
                        logger.error("PromotionsOrderService 取消订单，订单退款，保存数据异常，错误：{}", e);
                        e.printStackTrace();
                    }
                });
            }

            //筛选出满赠的活动
            List<Map<String, Object>> collect = promotionsDetailMapper.queryDetailAndRuleListByOrderId(orderId).stream()
                .filter(p -> {
                    Integer proType = p.get("promotions_type") == null ? 0 : Integer.parseInt(p.get("promotions_type").toString());
                    if (proType == PROMOTIONS_RULE_TYPE_GIFT) {
                        return true;
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());
            if (collect != null && !collect.isEmpty() && collect.size() > 0) {
                collect.stream().forEach(map -> {
                    if (map.get("order_id") != null && map.get("site_id") != null) {
                        int site_id = Integer.parseInt(map.get("site_id").toString());
                        int rule_id = Integer.parseInt(map.get("rule_id").toString());
                        //查询次订单的所有赠品信息
                        Map<Integer, Integer> map_ = new HashMap<>();
                        promotionsDetailMapper.queryGiftByOrderId(map.get("order_id").toString(), site_id)
                            .stream()
                            .forEach(giftMap -> {
                                Integer goods_id = giftMap.get("goods_id") == null ? 0 : Integer.parseInt(giftMap.get("goods_id").toString());
                                Integer goods_num = giftMap.get("goods_num") == null ? 0 : Integer.parseInt(giftMap.get("goods_num").toString());
                                map_.put(goods_id, goods_num);
                            });
                        try {
                            promotionsRuleService.changeGiftNums(site_id, rule_id, map_);
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("refundPromotions 修改赠品活动中赠品库存错误 ：{}", e);
                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("refundPromotions 取消订单错误：{}", e);
        }
    }


    /**
     * 判断商品库存 优惠活动
     *
     * @param giftList
     * @param siteId
     * @param promotionsActivityIds
     * @return
     */
    @SuppressWarnings("ConstantConditions")
    private OrderResponse verifyGiftGoodsStockByActivity(List<GiftResult> giftList, Integer siteId, String
        promotionsActivityIds) {
        OrderResponse failOrderResponse = new OrderResponse() {{
            setCode(TRADES_RESP_CODE_MISSINF_PARAMS);
            setMessage("无法根据优惠券ID找到优惠券");
        }};
        OrderResponse orderResponse = new OrderResponse();

        if (StringUtil.isBlank(promotionsActivityIds) || giftList.isEmpty()) {
            return failOrderResponse;
        }

        // 根据promotionsActivityId查找对应的满赠规则
        List<Integer> promotionsActivityIdList = Arrays.stream(promotionsActivityIds.split(","))
            .map(Integer::valueOf)
            .collect(toList());

        Optional<List<PromotionsActivity>> optional = promotionsActivityService.getProActivityWithProRuleByProActivityIds(siteId, promotionsActivityIdList);
        if (!optional.isPresent())
            return failOrderResponse;

        List<PromotionsActivity> promotionsActivities = optional.get();
        // 根据ProActivityId为key，对应的ProRule为value的map
        Map<Integer, PromotionsRule> ruleMap = promotionsActivities.stream()
            .collect(groupingBy(PromotionsActivity::getId, collectingAndThen(reducing((pa1, pa2) -> pa1), op -> op.get().getPromotionsRule())));

        for (GiftResult giftResult : giftList) {
            if (!giftResult.getConcessionDesc().getConcessionType().equals(2))
                continue;

            Integer promotionsActivityId = giftResult.getConcessionDesc().getPromotionsActivityId();
            PromotionsRule promotionsRule = ruleMap.get(promotionsActivityId);
            GiftRule giftRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GiftRule.class);
            if (giftRule == null)
                return failOrderResponse;

            Map<Integer, GiftRule.sendGifts> giftsMap = giftRule.getSendGifts().stream()
                .collect(Collectors.toMap(GiftRule.sendGifts::getGiftId, Function.identity(), (o1, o2) -> o1));

            for (GiftMsg giftMsg : giftResult.getGiftList()) {
                if (giftMsg.getSendNum() == 0)
                    return failOrderResponse;

                Integer goodsId = giftMsg.getGoodsId();
                GiftRule.sendGifts sendGifts = giftsMap.get(goodsId);

                if (giftMsg.getSendNum() > sendGifts.getSendNum()) {
                    orderResponse.setCode(CommonConstant.TRADES_RESP_CODE_GIFT_BUY_NUM);
                    orderResponse.setMessage("您选择的赠品库存不足，请刷新当前页面后再试");
                }
            }
        }

        return orderResponse;
    }


    /**
     * 判断商品库存 优惠券
     *
     * @param giftList
     * @param siteId
     * @param couponId
     * @return
     */
    private OrderResponse verifyGiftGoodsStockByCouponId(List<GiftResult> giftList, Integer siteId, Integer
        couponId) {
        OrderResponse failOrderResponse = new OrderResponse() {{
            setCode(TRADES_RESP_CODE_MISSINF_PARAMS);
            setMessage("无法根据优惠券ID找到优惠券");
        }};
        OrderResponse orderResponse = new OrderResponse();

        if (couponId == null || couponId <= 0 || giftList.isEmpty())
            return failOrderResponse;

        // 找到优惠券ID对应的赠品列表
        Optional<GiftResult> optional = giftList.stream()
            .filter(giftResult -> giftResult.getConcessionDesc().getConcessionType().equals(1)
                && giftResult.getConcessionDesc().getCouponDetailId().equals(couponId))
            .findFirst();

        if (!optional.isPresent())
            return failOrderResponse;

        GiftResult giftResult = optional.get();
        CouponRule couponRule = couponRuleMapper.getByCouponId(couponId, siteId);

        if (couponRule != null) {
            GoodsRule goodsRule = JSON.parseObject(couponRule.getGoodsRule(), GoodsRule.class);

            Map<Integer, GoodsRule.GiftStorage> map = goodsRule.getGift_storage().stream()
                .collect(Collectors.toMap(GoodsRule.GiftStorage::getGiftId, Function.identity(), (gs1, gs2) -> gs1));

            giftResult.getGiftList().forEach(giftMsg -> {
                if (giftMsg.getSendNum() > map.get(giftMsg.getGoodsId()).getSendNum()) {
                    orderResponse.setCode(CommonConstant.TRADES_RESP_CODE_GIFT_BUY_NUM);
                    orderResponse.setMessage("您选择的赠品库存不足，请刷新当前页面后再试");
                }
            });
        } else {
            return failOrderResponse;
        }

        return orderResponse;
    }
}
