package com.jk51.modules.coupon.service;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.AreaRule;
import com.jk51.model.coupon.requestParams.GoodsRule;
import com.jk51.model.coupon.requestParams.OrderMessageParams;
import com.jk51.model.coupon.requestParams.OrderRule;
import com.jk51.model.coupon.returnParams.UseCouponReturnParams;
import com.jk51.modules.coupon.constants.CouponConstant;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.utils.CouponProcessUtils;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.mapper.GoodsmMapper;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * filename :com.jk51.modules.coupon.goodsService.
 * author   :zw
 * date     :2017/3/7
 * Update   :
 */
@Service
public class CouponProcessService {


    private static final Logger logs = LoggerFactory.getLogger(CouponRuleService.class);
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private CouponProcessUtils couponProcessUtils;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsmMapper goodsmMapper;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;

    /**
     * 计算优惠金额
     *
     * @param orderMessageParams
     * @return
     */
    public ReturnDto accountCoupon(OrderMessageParams orderMessageParams) {

        CouponDetail couponDetail = couponDetailMapper.getCouponDetailByUserId(orderMessageParams.getSiteId(),
                                                                               orderMessageParams.getCouponId());
        if (couponDetail == null) {
            logs.info("没有找到数据");
            return ReturnDto.buildFailedReturnDto("没有找到数据");
        }

        CouponRule couponRule = couponRuleMapper.findCouponRuleById(couponDetail.getRuleId(), orderMessageParams.getSiteId());

        if (couponRule == null) {
            logs.info("没有找到数据");
            return ReturnDto.buildFailedReturnDto("没有找到数据");
        }
        //满赠券做单独处理
        if (couponRule.getCouponType() == 500) {
            return CouponGiftRuleForOrderReturn(orderMessageParams, JSON.parseObject(couponRule.getGoodsRule(), GoodsRule.class));
        }
        logs.info("----------------" + couponRule.getSiteId());
        switch (couponRule.getAimAt()) {
            case CouponConstant.AIM_AT_ORDER:
                logs.info("is order model");
                return CouponOrder(couponRule.getCouponType(), JSON.parseObject(couponRule.getOrderRule(), OrderRule.class),
                                   JSON.parseObject(couponRule.getAreaRule(), AreaRule.class), orderMessageParams);
            case CouponConstant.AIM_AT_GOODS:
                logs.info("is goods model");
                return goodsRuleType(couponRule.getCouponType(), JSON.parseObject(couponRule.getGoodsRule(),
                                                                                  GoodsRule.class), orderMessageParams, couponDetail);
        }


        return ReturnDto.buildFailedReturnDto("异常");
    }

    /**
     * 获取满赠券对于订单的返回数据
     */

    public ReturnDto CouponGiftRuleForOrderReturn(OrderMessageParams orderMessageParams, GoodsRule goodsRule) {

        try {
            UseCouponReturnParams useCouponReturnParams = new UseCouponReturnParams();
            Map<String, Object> resultMap = new HashMap<String, Object>();
            List<Map<String, Object>> listGift = new ArrayList<Map<String, Object>>();

            if (goodsRule.getGift_send_type() == 3) {

                List<GoodsRule.GiftStorage> sendGifts = goodsRule.getGift_storage();
                logs.info("rMap-------------sendGifts-----{}", ParameterUtil.ObjectConvertJson(sendGifts));
                for (int i = 0; i < sendGifts.size(); i++) {
                    Integer giftId = sendGifts.get(i).getGiftId();
                    logs.info("rMap-------------giftId-----{}", giftId);
                    Map<String, Object> rMap = goodsMapper.getGiftById(orderMessageParams.getSiteId(), giftId);
                    if (rMap == null || rMap.size() <= 0) {
                        continue;
                    }
                    logs.info("rMap--------------rMap----{}", ParameterUtil.ObjectConvertJson(rMap));
                    Integer goodsId = rMap.get("goods_id") == null ? 0 : Integer.parseInt(rMap.get("goods_id").toString());
                    Map<String, Object> imgMap = goodsmMapper.selectImgById(orderMessageParams.getSiteId(), goodsId);
                    rMap.put("sendNum", sendGifts.get(i).getSendNum());
                    if (imgMap != null) {
                        rMap.put("hash", imgMap.get("hash"));
                    } else {
                        rMap.put("hash", "");
                    }
                    listGift.add(rMap);
                }
            } else if (goodsRule.getGift_send_type() == 2) {
                listGift = getGiftListForeSendTypeSecond(orderMessageParams, goodsRule, orderMessageParams.getGoodsInfo());
            }

            resultMap.put("giftList", listGift);
            resultMap.put("couponRuleView", parsingCouponRuleService.getGoodsRule(500, goodsRule));
            resultMap.put("maxSenNum", maxSenNumFroBeforeOrder(goodsRule, orderMessageParams.getGoodsInfo()));
            useCouponReturnParams.setGiftRuleMsg(resultMap);
            return ReturnDto.buildSuccessReturnDto(useCouponReturnParams);
        } catch (Exception e) {
            logs.info("优惠券获取赠品信息异常:" + e);
        }

        return null;
    }

    private List<Map<String, Object>> getGiftListForeSendTypeSecond(OrderMessageParams orderMessageParams, GoodsRule goodsRule, List<Map<String, Integer>> goodsInfo) {

        List<Map<String, Object>> listGift = new ArrayList<Map<String, Object>>();
        List<Map<String, String>> mapList = couponProcessUtils.String2List(goodsRule.getRule());
        for (int i = 0; i < goodsInfo.size(); i++) {
            Map<String, Integer> map = goodsInfo.get(i);
            if (goodsRule.getGift_calculate_base() == 1) {
                if (new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(","))).contains(map.get("goodsId").toString())) {
                    if (goodsRule.getRule_type() == 1) {
                        if (!mapList.stream().anyMatch(cond -> Integer.parseInt(cond.get("meetNum").toString()) <= Integer.parseInt(map.get("num").toString())))
                            continue;
                    }

                    if (goodsRule.getRule_type() == 2) {
                        if (!mapList.stream().anyMatch(cond -> Integer.parseInt(cond.get("meetMoney").toString()) <= Integer.parseInt(map.get("num").toString()) * Integer.parseInt(map.get("goodsPrice").toString())))
                            continue;
                    }


                    Map<String, Object> rMap = goodsMapper.getGiftById(orderMessageParams.getSiteId(), map.get("goodsId"));
                    if (rMap == null || rMap.size() <= 0) {
                        continue;
                    }
                    logs.info("rMap--------------rMap----{}", ParameterUtil.ObjectConvertJson(rMap));
                    Integer goodsId = rMap.get("goods_id") == null ? 0 : Integer.parseInt(rMap.get("goods_id").toString());
                    Map<String, Object> imgMap = goodsmMapper.selectImgById(orderMessageParams.getSiteId(), goodsId);

                    List<GoodsRule.GiftStorage> sendGifts = goodsRule.getGift_storage().stream().filter(storage -> storage.getGiftId().equals(goodsId)).collect(Collectors.toList());
                    rMap.put("sendNum", sendGifts.get(0).getSendNum());
                    if (imgMap != null) {
                        rMap.put("hash", imgMap.get("hash"));
                    } else {
                        rMap.put("hash", "");
                    }
                    listGift.add(rMap);
                }
            }

        }

        return listGift;
    }


    private Integer maxSenNumFroBeforeOrder(GoodsRule goodsRule, List<Map<String, Integer>> goodsInfo) {

        List<Integer> sendNumList = new ArrayList<Integer>();
        sendNumList.add(0);
        List<Map<String, String>> mapList = couponProcessUtils.String2List(goodsRule.getRule());
        if (goodsRule.getRule_type() == 1) {
            if (goodsRule.getGift_calculate_base() == 1)
                return Collections.max(mapList
                                               .stream()
                                               .filter(ruleCondition -> goodsInfo.stream()
                                                       .filter(goodsMap -> new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(",")))
                                                               .contains(goodsMap.get("goodsId").toString()))
                                                       .anyMatch(map -> Integer.parseInt(map.get("num").toString()) >= Integer.parseInt(ruleCondition.get("meetNum").toString())))
                                               .map(map -> Integer.parseInt(map.get("sendNum").toString()))
                                               .collect(Collectors.toList()));

            if (goodsRule.getGift_calculate_base() == 2)
                return Collections.max(mapList
                                               .stream()
                                               .filter(ruleCondition ->
                                                               goodsInfo.stream()
                                                                       .filter(goodsMap -> new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(",")))
                                                                               .contains(goodsMap.get("goodsId").toString()))
                                                                       .mapToInt(goodsMap -> Integer.parseInt(goodsMap.get("num").toString())).sum() >= Integer.parseInt(ruleCondition.get("meetNum").toString()))
                                               .map(map -> Integer.parseInt(map.get("sendNum").toString()))
                                               .collect(Collectors.toList()));

        } else if (goodsRule.getRule_type() == 2) {

            if (goodsRule.getGift_calculate_base() == 1)
                return Collections.max(mapList
                                               .stream()
                                               .filter(ruleCondition -> goodsInfo.stream()
                                                       .filter(goodsMap -> new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(",")))
                                                               .contains(goodsMap.get("goodsId").toString()))
                                                       .anyMatch(map -> Integer.parseInt(map.get("num").toString()) * Integer.parseInt(map.get("goodsPrice").toString()) >= Integer.parseInt(ruleCondition.get("meetMoney").toString())))
                                               .map(map -> Integer.parseInt(map.get("sendNum").toString()))
                                               .collect(Collectors.toList()));


            if (goodsRule.getGift_calculate_base() == 2)
                return Collections.max(mapList
                                               .stream()
                                               .filter(ruleCondition ->
                                                               goodsInfo.stream()
                                                                       .filter(goodsMap -> new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(",")))
                                                                               .contains(goodsMap.get("goodsId").toString()))
                                                                       .mapToInt(goodsMap -> Integer.parseInt(goodsMap.get("num").toString())
                                                                               * Integer.parseInt(goodsMap.get("goodsPrice").toString())).sum()
                                                                       >= Integer.parseInt(ruleCondition.get("meetMoney").toString()))
                                               .map(map -> Integer.parseInt(map.get("sendNum").toString()))
                                               .collect(Collectors.toList()));


        }
        return Collections.max(sendNumList);

    }


    /**
     * 计算订单优惠券金额入口
     *
     * @param couponType
     * @param orderRule
     * @param areaRule
     * @param orderMessageParams
     * @return
     */
    private ReturnDto CouponOrder(Integer couponType, OrderRule orderRule, AreaRule areaRule,
                                  OrderMessageParams orderMessageParams) {
        switch (orderRule.getRule_type()) {
            case CouponConstant.ORDER_DEDUCT: //订单立减
                return orderRuleTypeZero(couponType, orderRule, orderMessageParams);
            case CouponConstant.ORDER_EACH_FULL:
                return orderRuleTypeOnce(couponType, orderRule, orderMessageParams);
            case CouponConstant.ORDER_FULL_MONEY:
                return orderRuleTypeTwo(couponType, orderRule, orderMessageParams);
            case CouponConstant.ORDER_FULL_NUM:
                return orderRuleTypeThree(couponType, orderRule, orderMessageParams);
            case CouponConstant.ORDER_FULL_POST:
                return orderRuleTypeFour(orderRule, orderMessageParams);
        }
        return ReturnDto.buildFailedReturnDto("异常");
    }

    /**
     * 计算商品优惠券金额入口  循环判断
     *
     * @param couponType
     * @param goodsRule
     * @param orderMessageParams
     * @return
     */
    public ReturnDto goodsRuleType(Integer couponType, GoodsRule goodsRule, OrderMessageParams orderMessageParams,
                                   CouponDetail couponDetail) {
        List<Map<String, Integer>> goodsInfoList = orderMessageParams.getGoodsInfo();
        Integer reduceMoney = 0;
        Integer priceTotal = 0;
        Integer goodsNum = 0;
        Integer type = goodsRule.getType();
        List<Map<String, Object>> mapListGoodsInfo = new ArrayList<>();
        for (Map<String, Integer> goodsInfoMap : goodsInfoList) {
            boolean isExcuse = true;
            Map<String, Object> objectMap = new HashedMap();
            if (type != 0)
                isExcuse = judgeExcuse(goodsRule.getPromotion_goods(), goodsInfoMap.get("goodsId"));
            switch (type) {
                case 0:
                    priceTotal += goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num");
                    goodsNum += goodsInfoMap.get("num");
                    objectMap.put("goodsPrice", goodsInfoMap.get("goodsPrice"));
                    objectMap.put("num", goodsInfoMap.get("num"));
                    break;
                case 1://指定类目参加,暂时没有此类
                    break;
                case 2:
                    if (isExcuse) {
                        priceTotal += goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num");
                        goodsNum += goodsInfoMap.get("num");
                        objectMap.put("goodsPrice", goodsInfoMap.get("goodsPrice"));
                        objectMap.put("num", goodsInfoMap.get("num"));
                    }
                    break;
                case 3:
                    if (!isExcuse) {
                        priceTotal += goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num");
                        goodsNum += goodsInfoMap.get("num");
                        objectMap.put("goodsPrice", goodsInfoMap.get("goodsPrice"));
                        objectMap.put("num", goodsInfoMap.get("num"));
                    }
                    break;
                default:
                    break;
            }
            mapListGoodsInfo.add(objectMap);
        }
        Integer is_post = 0; //是否计算邮费邮，0不计算邮费 1计算
        try {
            is_post = goodsRule.getIs_post() == null ? 0 : goodsRule.getIs_post();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (is_post == 1) {
            priceTotal += orderMessageParams.getPostFee();
        }
        reduceMoney = CouponGoods(couponType, priceTotal, goodsRule, goodsNum, mapListGoodsInfo, couponDetail);

        UseCouponReturnParams useCouponReturnParams = new UseCouponReturnParams();
        useCouponReturnParams.setCouponId(orderMessageParams.getCouponId());
        useCouponReturnParams.setAccountAmount(orderMessageParams.getOrderFee() - reduceMoney);
        useCouponReturnParams.setCouponMoney(reduceMoney);
        //如果针对商品总价金额为0了，并且邮费不为空，并且设置了开关为不计算邮费，邮费因改不被减去
        if (null != orderMessageParams.getPostFee() && null != useCouponReturnParams.getAccountAmount() &&
                orderMessageParams.getPostFee() > 0 && useCouponReturnParams.getAccountAmount() <= 0 && is_post == 0) {
            useCouponReturnParams.setAccountAmount(orderMessageParams.getPostFee());
            useCouponReturnParams.setCouponMoney(orderMessageParams.getOrderFee() - orderMessageParams.getPostFee());
        }
        // 限价券判断，如果限定价格大于商品价格则定单金额为订单金额+限定价格（参与计算的商品）-原来价格（参与计算的商品）
        if (couponType == CouponConstant.LIMIT_PRICE_COUPON && reduceMoney == 0) {
            Map<String, Object> goodsRuleMap = couponProcessUtils.String2Map(goodsRule.getRule());
            useCouponReturnParams.setAccountAmount(orderMessageParams.getOrderFee() +
                                                           couponProcessUtils.convertInt(goodsRuleMap.get("each_goods_price")) * goodsNum - priceTotal);
            useCouponReturnParams.setCouponMoney(priceTotal - couponProcessUtils.convertInt(goodsRuleMap.get("each_goods_price")) * goodsNum);
        }
        return ReturnDto.buildSuccessReturnDto(useCouponReturnParams);
    }


    public boolean judgeExcuse(String promotionGoods, Integer goodsId) {
        String[] goodsArray = promotionGoods.split(",");
        for (String id : goodsArray) {
            if (goodsId.equals(couponProcessUtils.convertInt(id))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据分类调用不懂的解析方法
     *
     * @param couponType
     * @param priceTotal
     * @param goodsRule
     * @param goodsNum
     * @return
     */
    private Integer CouponGoods(Integer couponType, Integer priceTotal, GoodsRule goodsRule, Integer goodsNum,
                                List<Map<String, Object>> mapListGoodsInfo, CouponDetail couponDetail) {
        Integer reduceMoney = 0;
        switch (goodsRule.getRule_type()) {
            case CouponConstant.GOODS_EACH_FULL: //商品
                reduceMoney = goodsRuleTypeZero(priceTotal, goodsRule);
                break;
            case CouponConstant.GOODS_FULL_MONEY:
                reduceMoney = goodsRuleTypeOnce(couponType, priceTotal, goodsRule);
                break;
            case CouponConstant.GOODS_FULL_NUM:
                reduceMoney = goodsRuleTypeTwo(couponType, priceTotal, goodsRule, goodsNum);
                break;
            case CouponConstant.GOODS_LIMIT_PRICE:
                reduceMoney = goodsRuleTypeThree(priceTotal, goodsRule, goodsNum);
                break;
            case CouponConstant.GOODS_PRICE:
                reduceMoney = goodsRuleTypeFourth(couponType, priceTotal, goodsRule);
                break;
            case CouponConstant.GOODS_SECOND_DISCOUNT:
                reduceMoney = goodsRuleTypeFifth(couponType, goodsRule, mapListGoodsInfo);
                break;
            case CouponConstant.GOODS_DISTANCE:
                reduceMoney = goodsRuleTypeSixth(couponType, goodsRule, priceTotal, couponDetail);
                break;
        }
        return reduceMoney;

    }


    /**
     * 商品满减  只有现金券 rule_type 0
     *
     * @param goodsPrice
     * @param goodsRule
     * @return
     */
    private Integer goodsRuleTypeZero(Integer goodsPrice, GoodsRule goodsRule) {
        Map<String, Object> goodsMap = couponProcessUtils.String2Map(goodsRule.getRule());
        Integer reduce_price = couponProcessUtils.convertInt(goodsMap.get("reduce_price"));
        Integer each_full_money = couponProcessUtils.convertInt(goodsMap.get("each_full_money"));
        Integer money_max = couponProcessUtils.convertInt(goodsMap.get("max_reduce"));
        Integer couponMoney = (int) Math.floor(goodsPrice / each_full_money) * reduce_price;
        if (couponMoney > money_max && money_max != 0) {
            couponMoney = money_max;
        }
        if (couponMoney > goodsPrice) {
            couponMoney = goodsPrice;
        }
        return couponMoney;
    }

    /**
     * goods 商品美满多少减多少(折) rule_type 1
     *
     * @param couponType
     * @param priceTotal
     * @param goodsRule
     * @return
     */
    private Integer goodsRuleTypeOnce(Integer couponType, Integer priceTotal, GoodsRule goodsRule) {
        List<Map<String, String>> mapList = couponProcessUtils.String2List(goodsRule.getRule());
        Map<String, String> reachGrade = new HashMap<>();
        for (Map<String, String> stringMap : mapList) {
            if (couponProcessUtils.convertInt(stringMap.get("meet_money")) <= priceTotal) {
                reachGrade = stringMap;
            }
        }
        return fullNumAndMoney(couponType, reachGrade, priceTotal, goodsRule);
    }


    /**
     * 取读配置是否抹零 四舍五入，直接舍去等等
     *
     * @param goodsTotalPrice
     * @param discount
     * @param goodsRule
     * @return
     */
    private Integer returnDiscountGoodsCoupon(Integer goodsTotalPrice, Integer discount, GoodsRule goodsRule) {
        Integer is_ml = goodsRule.getIs_ml();
        Integer is_round = goodsRule.getIs_round();
        Integer discount_money = discountMoney(goodsTotalPrice, is_ml, is_round, discount);
        return discount_money;
    }

    /***
     *  goods 商品美满多少件减多少(折) rule_type 2
     * @param couponType
     * @param priceTotal
     * @param goodsRule
     * @return
     */
    private Integer goodsRuleTypeTwo(Integer couponType, Integer priceTotal, GoodsRule goodsRule, Integer goodsNum) {
        List<Map<String, String>> mapList = couponProcessUtils.String2List(goodsRule.getRule());
        Map<String, String> reachGrade = new HashMap<>();
        for (Map<String, String> stringMap : mapList) {
            if (couponProcessUtils.convertInt(stringMap.get("meet_num")) <= goodsNum) {
                reachGrade = stringMap;
            }
        }
        return fullNumAndMoney(couponType, reachGrade, priceTotal, goodsRule);
    }

    /**
     * goods 每个商品限价多少元 rule_type 3
     *
     * @param priceTotal
     * @param goodsRule
     * @param goodsNum
     * @return
     */
    private Integer goodsRuleTypeThree(Integer priceTotal, GoodsRule goodsRule, Integer goodsNum) {
        Map<String, Object> goodsRuleMap = couponProcessUtils.String2Map(goodsRule.getRule());
        Integer each_goods_price = couponProcessUtils.convertInt(goodsRuleMap.get("each_goods_price"));
        Integer reduceMoney = priceTotal - (goodsNum * each_goods_price);
        if (reduceMoney <= 0) {
            reduceMoney = 0;
        }
        return reduceMoney;
    }

    /**
     * goods 立减或者立折 rule_type 4
     *
     * @param priceTotal
     * @param goodsRule
     * @return
     */
    private Integer goodsRuleTypeFourth(Integer couponType, Integer priceTotal, GoodsRule goodsRule) {
        Map<String, Object> goodsRuleMap = couponProcessUtils.String2Map(goodsRule.getRule());
        Integer reduceMoney = 0;
        Integer spreadMoney = 0;
        if (couponType == CouponConstant.CASH_COUPON) {//现金券满减
            reduceMoney = couponProcessUtils.convertInt(goodsRuleMap.get("direct_money"));
            spreadMoney = priceTotal - reduceMoney;
            if (spreadMoney <= 0) {
                reduceMoney = priceTotal;
            }
        } else if (couponType == CouponConstant.CASH_DISCOUNT_COUPON) { //折扣券满折
            Integer is_ml = couponProcessUtils.convertInt(goodsRule.getIs_ml());
            Integer is_round = couponProcessUtils.convertInt(goodsRule.getIs_round());
            reduceMoney += discountMoney(priceTotal, is_ml, is_round,
                                         couponProcessUtils.convertInt(goodsRuleMap.get("direct_discount")));

        }
        Integer max_reduce = couponProcessUtils.convertInt(goodsRuleMap.get("max_reduce"));
        if (max_reduce != 0 && reduceMoney > max_reduce) {
            reduceMoney = max_reduce;
        }
        return reduceMoney;
    }

    /**
     * goods 第几件多少折 rule_type 5  没满几件多少折 如第一件原件第二件半价第三件原价第四件半价
     *
     * @param couponType
     * @param mapListGoodsInfo
     * @return
     */
    private Integer goodsRuleTypeFifth(Integer couponType, GoodsRule goodsRule, List<Map<String, Object>> mapListGoodsInfo) {
        Integer reduceMoney = 0;
        Map<String, Object> goodsRuleMap = couponProcessUtils.String2Map(goodsRule.getRule());
        for (Map<String, Object> objectMap : mapListGoodsInfo) {
            if (couponProcessUtils.convertInt(objectMap.get("num")) >= couponProcessUtils.convertInt(goodsRuleMap.get("how_piece"))) {
                int max_buy_num = couponProcessUtils.convertInt(goodsRuleMap.get("max_buy_num"));//最大购买数第二件半价的

                int discount_num = (int) Math.floor(couponProcessUtils.convertInt(objectMap.get("num")) /
                                                            couponProcessUtils.convertInt(goodsRuleMap.get("how_piece")));
                if (max_buy_num > 0 && discount_num > max_buy_num) {
                    discount_num = max_buy_num;
                }
                Integer is_ml = couponProcessUtils.convertInt(goodsRule.getIs_ml());
                Integer is_round = couponProcessUtils.convertInt(goodsRule.getIs_round());
                reduceMoney += discountMoney(couponProcessUtils.convertInt(objectMap.get("goodsPrice")) * discount_num,
                                             is_ml, is_round, couponProcessUtils.convertInt(goodsRuleMap.get("discount")));
            }

        }
        return reduceMoney;
    }

    /**
     * 距离券,用于现金和折扣券
     * @param couponType
     * @param goodsRule
     * @param priceTotal
     * @param couponDetail
     * @return
     */
    private Integer goodsRuleTypeSixth(Integer couponType, GoodsRule goodsRule, Integer priceTotal, CouponDetail couponDetail) {
        Integer reduceMoney = 0;
        switch (couponType) {
            case CouponConstant.CASH_COUPON://现金券
                Integer distanceReduce = couponDetail.getDistanceReduce() == null ? 0 : couponDetail.getDistanceReduce();
                if (distanceReduce > priceTotal)
                    reduceMoney = priceTotal;
                else
                    reduceMoney = distanceReduce;
                break;
            case CouponConstant.CASH_DISCOUNT_COUPON://折扣券
                Integer distanceDiscount = couponDetail.getDistanceDiscount() == null ? 0 : couponDetail.getDistanceDiscount();
                Integer isMl = couponProcessUtils.convertInt(goodsRule.getIs_ml());
                Integer isRound = couponProcessUtils.convertInt(goodsRule.getIs_round());
                Integer distanceMoney = discountMoney(priceTotal, isMl, isRound, distanceDiscount);
                if (distanceMoney > priceTotal)
                    reduceMoney = priceTotal;
                else
                    reduceMoney = distanceMoney;
                break;
            default:
                logs.error("距离券,数据异常,错误{}");
        }

        return reduceMoney;
    }

    /**
     * 订单 rule_type 0 立减多少
     *
     * @param orderRule
     * @param orderMessageParams
     * @return
     */
    private ReturnDto orderRuleTypeZero(Integer couponType, OrderRule orderRule, OrderMessageParams orderMessageParams) {
        Map<String, Object> orderMap = couponProcessUtils.String2Map(orderRule.getRule());
        UseCouponReturnParams useCouponReturnParams = new UseCouponReturnParams();
        useCouponReturnParams.setCouponId(orderMessageParams.getCouponId());
        if (couponType == CouponConstant.CASH_COUPON) {//现金券满减
            useCouponReturnParams.setAccountAmount(
                    orderMessageParams.getOrderFee() - couponProcessUtils.convertInt(orderMap.get("direct_money")));
            useCouponReturnParams.setCouponMoney(couponProcessUtils.convertInt(orderMap.get("direct_money")));

        } else if (couponType == CouponConstant.CASH_DISCOUNT_COUPON) { //折扣券满折
            returnDiscountCoupon(orderRule, useCouponReturnParams, orderMessageParams.getOrderFee(),
                                 couponProcessUtils.convertInt(orderMap.get("discount_money")));
        }
        return ReturnDto.buildSuccessReturnDto(useCouponReturnParams);
    }

    /**
     * 订单 每满多少减多少 rule_type1  没有每满多少折多少
     *
     * @param orderRule
     * @param orderMessageParams
     * @return
     */
    private ReturnDto orderRuleTypeOnce(Integer couponType, OrderRule orderRule, OrderMessageParams orderMessageParams) {
        Map<String, Object> orderMap = couponProcessUtils.String2Map(orderRule.getRule());
        UseCouponReturnParams useCouponReturnParams = new UseCouponReturnParams();
        useCouponReturnParams.setCouponId(orderMessageParams.getCouponId());
        Integer reduce_price = couponProcessUtils.convertInt(orderMap.get("reduce_price"));
        Integer each_full_money = couponProcessUtils.convertInt(orderMap.get("each_full_money"));
        Integer money_max = couponProcessUtils.convertInt(orderMap.get("max_reduce"));
        Integer couponMoney = ((int) Math.floor(orderMessageParams.getOrderFee() / each_full_money)) * reduce_price.intValue();
        if (couponMoney > money_max && money_max != 0) {
            couponMoney = money_max;
        }
        if (couponMoney > orderMessageParams.getOrderFee()) {
            couponMoney = orderMessageParams.getOrderFee();
        }
        useCouponReturnParams.setAccountAmount(orderMessageParams.getOrderFee() - couponMoney);
        useCouponReturnParams.setCouponMoney(couponMoney);
        return ReturnDto.buildSuccessReturnDto(useCouponReturnParams);
    }

    /**
     * 订单 满多少元减/折多少 rule_type 2
     *
     * @param couponType
     * @param orderRule
     * @param orderMessageParams
     * @return
     */
    private ReturnDto orderRuleTypeTwo(Integer couponType, OrderRule orderRule, OrderMessageParams orderMessageParams) {
        UseCouponReturnParams useCouponReturnParams = new UseCouponReturnParams();
        useCouponReturnParams.setCouponId(orderMessageParams.getCouponId());
        Integer orderFee = orderMessageParams.getOrderFee();
        List<Map<String, String>> mapList = couponProcessUtils.String2List(orderRule.getRule());
        Map<String, String> reachGrade = new HashMap<>();
        for (Map<String, String> stringMap : mapList) {
            if (couponProcessUtils.convertInt(stringMap.get("meet_money")) <= orderFee) {
                reachGrade = stringMap;
            }
        }
        if (couponType == CouponConstant.CASH_COUPON) {//现金券满减
            returnReduceCoupon(useCouponReturnParams, orderFee, couponProcessUtils.convertInt(reachGrade.get("reduce_price")));
        } else if (couponType == CouponConstant.CASH_DISCOUNT_COUPON) {
            returnDiscountCoupon(orderRule, useCouponReturnParams, orderFee, couponProcessUtils.convertInt(reachGrade.get("discount")));
        }
        return ReturnDto.buildSuccessReturnDto(useCouponReturnParams);
    }

    /**
     * 订单 满多少件减/折多少 rule_type3
     *
     * @param couponType
     * @param orderRule
     * @param orderMessageParams
     * @return
     */
    private ReturnDto orderRuleTypeThree(Integer couponType, OrderRule orderRule, OrderMessageParams orderMessageParams) {

        Integer goodsNum = 0; //商品数量
        List<Map<String, Integer>> goodsInfoList = orderMessageParams.getGoodsInfo();
        for (Map<String, Integer> goodsInfoMap : goodsInfoList) {
            goodsNum += goodsInfoMap.get("num");
        }
        UseCouponReturnParams useCouponReturnParams = new UseCouponReturnParams();
        useCouponReturnParams.setCouponId(orderMessageParams.getCouponId());
        List<Map<String, String>> mapList = couponProcessUtils.String2List(orderRule.getRule());
        Map<String, String> reachGrade = new HashMap<>();
        for (Map<String, String> stringMap : mapList) {
            if (couponProcessUtils.convertInt(stringMap.get("meet_num")) <= goodsNum) {
                reachGrade = stringMap;
            }
        }
        if (couponType == CouponConstant.CASH_COUPON) {//现金券满减
            returnReduceCoupon(useCouponReturnParams, orderMessageParams.getOrderFee(),
                               couponProcessUtils.convertInt(reachGrade.get("reduce_price")));
        } else if (couponType == CouponConstant.CASH_DISCOUNT_COUPON) {
            returnDiscountCoupon(orderRule, useCouponReturnParams, orderMessageParams.getOrderFee(),
                                 couponProcessUtils.convertInt(reachGrade.get("discount")));
        }
        return ReturnDto.buildSuccessReturnDto(useCouponReturnParams);
    }

    /**
     * 包邮计算 仅针对订单 rule_type4
     *
     * @param orderRule
     * @param orderMessageParams
     * @return
     */
    private ReturnDto orderRuleTypeFour(OrderRule orderRule, OrderMessageParams orderMessageParams) {
        Map<String, Object> orderMap = couponProcessUtils.String2Map(orderRule.getRule());
        // 我不需判断地区是否包邮，jin我这里的都是包邮的
        UseCouponReturnParams useCouponReturnParams = new UseCouponReturnParams();
        useCouponReturnParams.setCouponId(orderMessageParams.getCouponId());
        if (couponProcessUtils.convertInt(orderMap.get("order_full_num")) > 0) {
            if (orderMessageParams.getPostFee() > couponProcessUtils.convertInt(orderMap.get("order_full_num_post_max"))) {
                useCouponReturnParams.setAccountAmount(orderMessageParams.getOrderFee() -
                                                               couponProcessUtils.convertInt(orderMap.get("order_full_num_post_max")));
                useCouponReturnParams.setCouponMoney(couponProcessUtils.convertInt(orderMap.get("order_full_num_post_max")));
            } else {
                useCouponReturnParams.setAccountAmount(orderMessageParams.getOrderFee() - orderMessageParams.getPostFee());
                useCouponReturnParams.setCouponMoney(orderMessageParams.getPostFee());
            }
        } else if (couponProcessUtils.convertInt(orderMap.get("order_full_money")) > 0) {
            if (orderMessageParams.getPostFee() > couponProcessUtils.convertInt(orderMap.get("order_full_money_post_max"))) {
                useCouponReturnParams.setAccountAmount(orderMessageParams.getOrderFee() -
                                                               couponProcessUtils.convertInt(orderMap.get("order_full_money_post_max")));
                useCouponReturnParams.setCouponMoney(couponProcessUtils.convertInt(orderMap.get("order_full_money_post_max")));
            } else {
                useCouponReturnParams.setAccountAmount(orderMessageParams.getOrderFee() - orderMessageParams.getPostFee());
                useCouponReturnParams.setCouponMoney(orderMessageParams.getPostFee());
            }
        }
        return ReturnDto.buildSuccessReturnDto(useCouponReturnParams);
    }

    /**
     * 返回优惠金额  订单
     *
     * @param useCouponReturnParams
     * @param orderFee
     * @param reduce
     * @return
     */
    private UseCouponReturnParams returnReduceCoupon(UseCouponReturnParams useCouponReturnParams, Integer orderFee, Integer reduce) {
        useCouponReturnParams.setAccountAmount(orderFee - reduce);
        useCouponReturnParams.setCouponMoney(reduce);
        return useCouponReturnParams;
    }

    /**
     * 折扣金额计算方法
     *
     * @param countFee
     * @param is_ml
     * @param is_round
     * @param discount
     * @return private Integer is_ml; //0不抹零 1，按角抹零 2，按分抹零
     * private Integer is_round;//0四舍五入 1直接抹去
     */
    public Integer discountMoney(Integer countFee, Integer is_ml, Integer is_round, Integer discount) {
        Integer discount_money = 0;

        if (is_ml == 0) { // 设置不抹零 默认为四舍五入
            discount_money = (int) (countFee * discount * 0.01);
        } else if (is_ml == 1) {
            if (is_round == 0) { //四舍五入
                BigDecimal b = new BigDecimal(countFee * discount * 0.01 * 0.1);
                double f1 = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
                discount_money = (int) (f1 * 10);
            } else if (is_round == 1) { //直接舍去
                BigDecimal b = new BigDecimal(countFee * discount * 0.01 * 0.1);
                double f1 = b.setScale(0, BigDecimal.ROUND_DOWN).doubleValue();
                discount_money = (int) (f1 * 10);
            }

        } else if (is_ml == 2) {
            if (is_round == 0) { //四舍五入
                // discount_money = (int) Math.rint(countFee * discount * 0.01);
                BigDecimal b = new BigDecimal(countFee * discount * 0.01);
                double f1 = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
                discount_money = (int) f1;
            } else if (is_round == 1) { //直接舍去
                BigDecimal b = new BigDecimal(countFee * discount * 0.01);
                double f1 = b.setScale(0, BigDecimal.ROUND_DOWN).doubleValue();
                discount_money = (int) f1;
                //  discount_money = (int) Math.floor(countFee * discount * 0.01);
            }
        }
        return countFee - discount_money;
    }


    private Integer fullNumAndMoney(Integer couponType, Map<String, String> goodsRuleMap, Integer goodsTotalPrice,
                                    GoodsRule goodsRule) {
        Integer reduceMoney = 0;
        if (couponType == CouponConstant.CASH_COUPON) {//现金券满多少减多少
            reduceMoney = couponProcessUtils.convertInt(goodsRuleMap.get("reduce_price"));
        } else if (couponType == CouponConstant.CASH_DISCOUNT_COUPON) {
            reduceMoney = returnDiscountGoodsCoupon(goodsTotalPrice, couponProcessUtils.convertInt(goodsRuleMap.get("discount")),
                                                    goodsRule);
        }
        return reduceMoney;
    }


    private UseCouponReturnParams returnDiscountCoupon(OrderRule orderRule, UseCouponReturnParams useCouponReturnParams,
                                                       Integer orderFee, Integer discount) {
        Integer is_ml = orderRule.getIs_ml();
        Integer is_round = orderRule.getIs_round();
        Integer discount_money = discountMoney(orderFee, is_ml, is_round, discount);
        useCouponReturnParams.setAccountAmount(orderFee - discount_money);
        useCouponReturnParams.setCouponMoney(discount_money);
        return useCouponReturnParams;
    }

}
