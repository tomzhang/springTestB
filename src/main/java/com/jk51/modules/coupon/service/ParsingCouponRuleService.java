package com.jk51.modules.coupon.service;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.CouponView;
import com.jk51.model.coupon.requestParams.GoodsRule;
import com.jk51.model.coupon.requestParams.OrderRule;
import com.jk51.modules.coupon.constants.CouponConstant;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.utils.CouponProcessUtils;
import com.jk51.modules.promotions.utils.AmountUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * filename :com.jk51.modules.coupon.goodsService.
 * author   :zw
 * date     :2017/3/22
 * Update   :
 */
@Service
public class ParsingCouponRuleService {

    private static final Logger logs = LoggerFactory.getLogger(ParsingCouponRuleService.class);
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private CouponProcessUtils couponProcessUtils;

    /**
     * 获取优惠券计算规则视图
     *
     * @param aimAt
     * @param couponType
     * @param orderRule
     * @param goodsRule
     * @return
     */
    public CouponView accountCoupon (Integer aimAt, Integer couponType, String orderRule, String goodsRule) {
        logs.info("is order model aimAt {}", aimAt);
        switch (aimAt) {
            case CouponConstant.AIM_AT_ORDER:
                logs.info("is order model");
                return getOrderRule(couponType, JSON.parseObject(orderRule, OrderRule.class));
            case CouponConstant.AIM_AT_GOODS:
                logs.info("is goods model");
                return getGoodsRule(couponType, JSON.parseObject(goodsRule, GoodsRule.class));
        }
        return null;
    }

    public CouponView accountCoupon (Integer ruleId, Integer siteId) {
        CouponRule couponRule = couponRuleMapper.findCouponRuleById(ruleId, siteId);
        if (couponRule == null) {
            logs.error("没有找到优惠券");
            return null;
        }
        return getCouponView(couponRule);
    }

    private CouponView getCouponView(CouponRule couponRule) {
        switch (couponRule.getAimAt()) {
            case CouponConstant.AIM_AT_ORDER:
                logs.info("is order model");
                return getOrderRule(couponRule.getCouponType(), JSON.parseObject(couponRule.getOrderRule(), OrderRule.class));
            case CouponConstant.AIM_AT_GOODS:
                logs.info("is goods model");
                return getGoodsRule(couponRule.getCouponType(), JSON.parseObject(couponRule.getGoodsRule(), GoodsRule.class));
        }
        return null;
    }

    public CouponView accountCoupon (CouponRule couponRule) {
        if (couponRule == null) {
            logs.error("没有找到优惠券");
            return null;
        }
        return getCouponView(couponRule);
    }

    //针对订单
    public CouponView getOrderRule (Integer couponType, OrderRule orderRule) {
        switch (orderRule.getRule_type()) {
            case CouponConstant.ORDER_DEDUCT: //订单立减
                return orderRuleTypeZero(couponType, orderRule);
            case CouponConstant.ORDER_EACH_FULL:
                return orderRuleTypeOnce(couponType, orderRule);
            case CouponConstant.ORDER_FULL_MONEY:
                return orderRuleTypeTwo(couponType, orderRule);
            case CouponConstant.ORDER_FULL_NUM:
                return orderRuleTypeThree(couponType, orderRule);
            case CouponConstant.ORDER_FULL_POST:
                return orderRuleTypeFour(orderRule);
        }
        return null;
    }

    //针对商品
    public CouponView getGoodsRule(Integer couponType, GoodsRule goodsRule) {
        if(couponType==500)
            return goodsRuleTypeGift(couponType, goodsRule);
        switch (goodsRule.getRule_type()) {
            case CouponConstant.GOODS_EACH_FULL: //商品
                return goodsRuleTypeZero(couponType, goodsRule);
            case CouponConstant.GOODS_FULL_MONEY:
                return goodsRuleTypeOnce(couponType, goodsRule);
            case CouponConstant.GOODS_FULL_NUM:
                return goodsRuleTypeTwo(couponType, goodsRule);
            case CouponConstant.GOODS_LIMIT_PRICE:
                return goodsRuleTypeThree(couponType, goodsRule);
            case CouponConstant.GOODS_PRICE:
                return goodsRuleTypeFourth(couponType, goodsRule);
            case CouponConstant.GOODS_SECOND_DISCOUNT:
                return goodsRuleTypeFifth(couponType, goodsRule);
            case CouponConstant.GOODS_DISTANCE:
                return goodsRuleTypeSixth(couponType, goodsRule);
        }
        return null;
    }

    private int isAllTypeProcess(Integer type) {
        if (type == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    private Double checkNull(String str) {
        if (StringUtils.isBlank(str)) {
            return -1d;
        } else {
            return Double.parseDouble(str);
        }
    }

    private Double checkNull(Object str) {
        if (str == null) {
            return -1d;
        } else {
            return Double.parseDouble(str.toString());
        }
    }

    private Integer checkNullReturnInt(String str) {
        if (StringUtils.isBlank(str)) {
            return -1;
        } else {
            return Integer.parseInt(str);
        }
    }

    private Integer checkNullReturnInt(Object str) {
        if (null == str) {
            return -1;
        } else {
            return Integer.parseInt(str.toString());
        }
    }

    private CouponView goodsRuleTypeZero(Integer couponType, GoodsRule goodsRule) {

        Map<String, Object> goodsMap = couponProcessUtils.String2Map(goodsRule.getRule());
        if (couponType == CouponConstant.CASH_COUPON) {//现金券满减
            int isAllType = isAllTypeProcess(goodsRule.getType());
            String ruleDetail = "";

            double maxMoney = -1d;
            double maxDiscount = -1d;
            if (null != goodsMap.get("max_reduce") && checkNull(goodsMap.get("max_reduce")) > 0) {
                ruleDetail = "商品总价" + isPost(goodsRule) + "每满" + checkNull(goodsMap.get("each_full_money")) / 100 + "元，减" +
                        checkNull(goodsMap.get("reduce_price")) / 100 + "元，最多优惠" + checkNull(goodsMap.get("max_reduce")) / 100 + "元。";
                maxMoney = checkNull(goodsMap.get("reduce_price"));
            } else {
                ruleDetail = "商品总价" + isPost(goodsRule) + "每满" + checkNull(goodsMap.get("each_full_money")) / 100 + "元，减" +
                        checkNull(goodsMap.get("reduce_price")) / 100 + "元，上不封顶。";
                maxMoney = checkNull(goodsMap.get("reduce_price"));

            }
            ruleDetail += viewCouponRound(couponType, goodsRule);

            return processBin(isAllType, maxMoney, maxDiscount,-1, ruleDetail);
        }
        return null;
    }

    private String isPost (GoodsRule goodsRule) {
        Integer is_post = goodsRule.getIs_post() == null ? 0 : goodsRule.getIs_post();
        if (is_post == 1) {
            return "加运费";
        } else {
            return "";
        }
    }
    private CouponView goodsRuleTypeSixth(Integer couponType, GoodsRule goodsRule) {
        List<Map<String, String>> mapList = couponProcessUtils.String2List(goodsRule.getRule());
        int isAllType = isAllTypeProcess(goodsRule.getType());
        String ruleDetail="";
        double maxMoney = -1d;
        double maxDiscount = -1d;
        for (Map<String, String> stringMap : mapList) {
            if (couponType == CouponConstant.CASH_COUPON) {//现金券满减
                if (checkNull(stringMap.get("distance_meter")) > 0) {
                    ruleDetail +=  isPost(goodsRule) + "距最近门店" + (checkNull(stringMap.get("distance_meter"))).intValue() + "米之内，减" +
                        checkNull(stringMap.get("reduce_price")) / 100 + "元；";
                    maxMoney = checkNull(stringMap.get("reduce_price"));
                }
            } else if (couponType == CouponConstant.CASH_DISCOUNT_COUPON) {
                if (checkNull(stringMap.get("distance_meter")) > 0) {
                    ruleDetail += isPost(goodsRule) + "距最近门店" + (checkNull(stringMap.get("distance_meter"))).intValue() + "米之内，打" +
                        checkNull(stringMap.get("discount")) / 10 + "折；";
                    maxDiscount=checkNull(stringMap.get("discount"));
                }
            }
        }
        ruleDetail="位置"+ruleDetail;
        ruleDetail += viewCouponRound(couponType, goodsRule);
        ruleDetail = ruleDetail.substring(0,ruleDetail.length()-1);
        ruleDetail+="。";
        return processBin(isAllType, maxMoney, maxDiscount,-1, ruleDetail);
    }
    private CouponView goodsRuleTypeOnce(Integer couponType, GoodsRule goodsRule) {
        List<Map<String, String>> mapList = couponProcessUtils.String2List(goodsRule.getRule());
        int isAllType = isAllTypeProcess(goodsRule.getType());
        String ruleDetail = "";
        double maxMoney = -1d;
        double maxDiscount = -1d;
        for (Map<String, String> stringMap : mapList) {
            if (couponType == CouponConstant.CASH_COUPON) {//现金券满减
                if (checkNull(stringMap.get("meet_money")) > 0) {
                    ruleDetail +=  isPost(goodsRule) + "满" + checkNull(stringMap.get("meet_money")) / 100 + "元，减" +
                            checkNull(stringMap.get("reduce_price")) / 100 + "元；";
                    maxMoney = checkNull(stringMap.get("reduce_price"));
                }
            } else if (couponType == CouponConstant.CASH_DISCOUNT_COUPON) {
                if (checkNull(stringMap.get("meet_money")) > 0) {
                    ruleDetail += isPost(goodsRule) + "满" + checkNull(stringMap.get("meet_money")) / 100 + "元，打" +
                            checkNull(stringMap.get("discount")) / 10 + "折；";
                    maxDiscount = checkNull(stringMap.get("discount")) ;
                }
            }
        }
        ruleDetail="商品总价"+ruleDetail;
        ruleDetail += viewCouponRound(couponType, goodsRule);
        ruleDetail = ruleDetail.substring(0,ruleDetail.length()-1);
        ruleDetail+="。";
        return processBin(isAllType, maxMoney, maxDiscount,-1, ruleDetail);
    }

    private CouponView goodsRuleTypeTwo(Integer couponType, GoodsRule goodsRule) {
        int isAllType = isAllTypeProcess(goodsRule.getType());
        List<Map<String, String>> mapList = couponProcessUtils.String2List(goodsRule.getRule());
        String ruleDetail = "";
        double maxMoney = -1d;
        double maxDiscount = -1d;
        for (Map<String, String> stringMap : mapList) {
            if (couponType == CouponConstant.CASH_COUPON) {//现金券满减
                if (checkNull(stringMap.get("meet_num")) > 0) {
                    ruleDetail +=  isPost(goodsRule) + "满" + checkNullReturnInt(stringMap.get("meet_num")) + "件，减" +
                            checkNull(stringMap.get("reduce_price")) / 100 + "元。";
                    maxMoney = checkNull(stringMap.get("reduce_price")) / 10;
                }
            } else if (couponType == CouponConstant.CASH_DISCOUNT_COUPON) {
                if (checkNull(stringMap.get("meet_num")) > 0) {
                    ruleDetail += isPost(goodsRule) + "满" + checkNullReturnInt(stringMap.get("meet_num")) + "件，打" +
                            checkNull(stringMap.get("discount")) / 10 + "折；";
                    maxDiscount = checkNull(stringMap.get("discount"));
                }
            }
        }
        ruleDetail += viewCouponRound(couponType, goodsRule);
        ruleDetail="商品数量"+ruleDetail;
        return processBin(isAllType, maxMoney, maxDiscount,-1, ruleDetail);
    }

    private CouponView goodsRuleTypeThree(Integer couponType, GoodsRule goodsRule) {
        int isAllType = isAllTypeProcess(goodsRule.getType());
        Map<String, Object> goodsMap = couponProcessUtils.String2Map(goodsRule.getRule());
        String ruleDetail = "特价" + checkNull(goodsMap.get("each_goods_price")) / 100 + "元，在有效期内每种商品每次最多可买" +
                checkNullReturnInt(goodsMap.get("buy_num_max")) + "件，累计可买" +
                checkNullReturnInt(goodsMap.get("each_goods_max_buy_num")) + "件。";
        ruleDetail += viewCouponRound(couponType, goodsRule);

        return processBin(isAllType, checkNull(goodsMap.get("each_goods_price")), -1,-1, ruleDetail);
    }

    private CouponView goodsRuleTypeFifth(Integer couponType, GoodsRule goodsRule) {
        int isAllType = isAllTypeProcess(goodsRule.getType());
        Map<String, Object> goodsMap = couponProcessUtils.String2Map(goodsRule.getRule());
        String ruleDetail = null;
        if (null != goodsMap.get("discount")) {
            ruleDetail = "第二件打" + checkNull(goodsMap.get("discount")) / 10 + "折。";
        }
        if (null != goodsMap.get("max_buy_num") && checkNullReturnInt(goodsMap.get("max_buy_num")) > 0) {
            ruleDetail += "最多优惠" + checkNullReturnInt(goodsMap.get("max_buy_num")) + "件。";
        }
        ruleDetail += viewCouponRound(couponType, goodsRule);
        ruleDetail = ruleDetail.replace("。","，");
        ruleDetail = ruleDetail.substring(0,ruleDetail.length()-1);
        ruleDetail+="。";
        return processBin(isAllType, -1, checkNull(goodsMap.get("discount")) ,-1, ruleDetail);
    }

    private CouponView goodsRuleTypeFourth(Integer couponType, GoodsRule goodsRule) {
        int isAllType = isAllTypeProcess(goodsRule.getType());
        Map<String, Object> goodsMap = couponProcessUtils.String2Map(goodsRule.getRule());
        String ruleDetail = null;
        if (null != goodsMap.get("direct_money")) {
            ruleDetail = "商品总价" + isPost(goodsRule) + "立减" + checkNull(goodsMap.get("direct_money")) / 100 + "元。";
            return processBin(isAllType, checkNull(goodsMap.get("direct_money")), -1,-1, ruleDetail);
        } else if (null != goodsMap.get("direct_discount")) {
            ruleDetail = "商品总价打" + checkNull(goodsMap.get("direct_discount")) / 10 + "折。";
            if (null != goodsMap.get("max_reduce") && checkNull(goodsMap.get("max_reduce")) > 0) {
                ruleDetail += "最多优惠" + checkNull(goodsMap.get("max_reduce")) / 100 + "元。";
            }
            ruleDetail += viewCouponRound(couponType, goodsRule);
            ruleDetail = ruleDetail.replace("。","，");
            ruleDetail = ruleDetail.substring(0,ruleDetail.length()-1);
            ruleDetail+="。";
            return processBin(isAllType, checkNull(goodsMap.get("max_reduce")), checkNull(goodsMap.get("direct_discount")),-1, ruleDetail);
        }
        ruleDetail += viewCouponRound(couponType, goodsRule);

        return processBin(isAllType, checkNull(goodsMap.get("direct_money")), -1,-1, ruleDetail);
    }

    private CouponView goodsRuleTypeGift(Integer couponType, GoodsRule goodsRule) {
        List<Map<String, String>> mapList = couponProcessUtils.String2List(goodsRule.getRule());
        String ruleDetail="";
        int isAllType = 1;
        Integer maxSendNum=null;
        if (goodsRule.getRule_type()==1){
            ruleDetail= couponProcessUtils.String2List(goodsRule.getRule()).stream().map(map -> "满" + map.get("meetNum") + "件送" +  map.get("sendNum") + "件").collect(Collectors.joining(";"));
            ruleDetail+="。";
        }else if(goodsRule.getRule_type()==2){
            ruleDetail = couponProcessUtils.String2List(goodsRule.getRule()).stream().map(map -> "满" + AmountUtils.changeF2Y(map.get("meetMoney").toString()) + "元送" +  map.get("sendNum") + "件").collect(Collectors.joining(";"));
            ruleDetail+="。";
        }

        maxSendNum= Collections.max(goodsRule.getGift_storage().stream().map(map->map.getSendNum()).collect(Collectors.toList()));
        ruleDetail="购买指定商品"+ruleDetail;
        return processBin(isAllType, -1, -1,maxSendNum, ruleDetail);
    }


    private CouponView orderRuleTypeZero(Integer couponType, OrderRule orderRule) {
        Map<String, Object> orderMap = couponProcessUtils.String2Map(orderRule.getRule());
        if (couponType == CouponConstant.CASH_COUPON) {//现金券满减
            String ruleDetail = "商品总价立减" + checkNull(orderMap.get("direct_money")) / 100 + "元。";
            return processBin(0, checkNull(orderMap.get("direct_money")), -1, -1,ruleDetail);
        } else if (couponType == CouponConstant.CASH_DISCOUNT_COUPON) { //折扣券满折
            String ruleDetail = "商品总价打" + checkNullReturnInt(orderMap.get("discount_money")) / 10 + "折。";
            return processBin(0, -1, checkNull(orderMap.get("discount_money")),-1, ruleDetail);
        }
        return null;
    }

    private CouponView orderRuleTypeOnce(Integer couponType, OrderRule orderRule) {
        Map<String, Object> orderMap = couponProcessUtils.String2Map(orderRule.getRule());
        String ruleDetail = "";
        if (couponType == CouponConstant.CASH_COUPON) {//现金券满减
            if (checkNull(orderMap.get("max_reduce")) > 0) {
                ruleDetail = "商品总价每满" + checkNull(orderMap.get("each_full_money")) / 100 + "元，减" +
                        checkNull(orderMap.get("reduce_price")) / 100 + "元，最多优惠" + checkNull(orderMap.get("max_reduce")) / 100 + "元。";
            } else {
                ruleDetail = "商品总价每满" + checkNull(orderMap.get("each_full_money")) / 100 + "元，减" +
                        checkNull(orderMap.get("reduce_price")) / 100 + "元，上不封顶。";
            }
            return processBin(0, checkNull(orderMap.get("reduce_price")), -1,-1, ruleDetail);
        }
        return null;
    }


    private CouponView orderRuleTypeTwo(Integer couponType, OrderRule orderRule) {
        List<Map<String, String>> mapList = couponProcessUtils.String2List(orderRule.getRule());
        String ruleDetail = "";
        double maxMoney = -1d;
        double maxDiscount = -1d;
        for (Map<String, String> stringMap : mapList) {
            if (couponType == CouponConstant.CASH_COUPON) {//现金券满减
                if (checkNull(stringMap.get("meet_money")) > 0) {
                    ruleDetail += "满" + checkNull(stringMap.get("meet_money")) / 100 + "元，减" +
                            checkNull(stringMap.get("reduce_price")) / 100 + "元。";
                    maxMoney = checkNull(stringMap.get("reduce_price"));
                }
            } else if (couponType == CouponConstant.CASH_DISCOUNT_COUPON) {
                if (checkNull(stringMap.get("meet_money")) > 0) {
                    ruleDetail += "满" + checkNull(stringMap.get("meet_money")) / 100 + "元，打" +
                            checkNullReturnInt(stringMap.get("discount")) / 10 + "折。";
                    maxDiscount = checkNull(stringMap.get("discount"));
                }
            }
        }
        ruleDetail+="商品总价"+ruleDetail;
        return processBin(0, maxMoney, maxDiscount,-1, ruleDetail);

    }

    private CouponView orderRuleTypeThree(Integer couponType, OrderRule orderRule) {
        List<Map<String, String>> mapList = couponProcessUtils.String2List(orderRule.getRule());
        String ruleDetail = "";
        double maxMoney = -1d;
        double maxDiscount = -1d;
        for (Map<String, String> stringMap : mapList) {
            if (couponType == CouponConstant.CASH_COUPON) {//现金券满减
                if (checkNull(stringMap.get("meet_num")) > 0) {
                    ruleDetail += "订单满" + checkNull(stringMap.get("meet_num")) + "件商品，减" +
                            checkNull(stringMap.get("reduce_price")) / 100 + "元。";
                    maxMoney = checkNull(stringMap.get("reduce_price"));
                }
            } else if (couponType == CouponConstant.CASH_DISCOUNT_COUPON) {
                if (checkNull(stringMap.get("meet_num")) > 0) {
                    ruleDetail += "满" + checkNull(stringMap.get("meet_num")) + "件，打" +
                            checkNullReturnInt(stringMap.get("discount")) / 10 + "折。";
                    maxDiscount = checkNull(stringMap.get("discount"));
                }
            }
        }
        return processBin(0, maxMoney, maxDiscount,-1, ruleDetail);
    }

    private CouponView orderRuleTypeFour(OrderRule orderRule) {
        Map<String, Object> orderMap = couponProcessUtils.String2Map(orderRule.getRule());
        if (checkNull(orderMap.get("order_full_money")) > 0) {
            String ruleDetail = "";
            if (checkNull(orderMap.get("order_full_money_post_max")) > 0) {
                ruleDetail += "订单满" + checkNull(orderMap.get("order_full_money")) / 100 + "元，邮费最多免" +
                        checkNull(orderMap.get("order_full_money_post_max")) / 100 + "元。";
                return processBin(0, checkNull(orderMap.get("order_full_money_post_max")),
                        -1,-1, ruleDetail);

            } else {
                ruleDetail += "订单满" + checkNull(orderMap.get("order_full_money")) / 100 + "元，包邮。";
                return processBin(0, -1,
                        -1,-1, ruleDetail);
            }
        } else if (checkNull(orderMap.get("order_full_num")) > 0) {

            String ruleDetail = "";
            if (checkNull(orderMap.get("order_full_num_post_max")) > 0) {
                ruleDetail = "订单满" + checkNull(orderMap.get("order_full_num")) + "件商品，邮费最多免" +
                        checkNull(orderMap.get("order_full_num_post_max")) / 100 + "元。";
                return processBin(0, checkNull(orderMap.get("order_full_num_post_max")),
                        -1,-1, ruleDetail);

            } else {
                ruleDetail = "订单满" + checkNull(orderMap.get("order_full_num")) + "件商品，包邮。";
                return processBin(0, -1,
                        -1,-1, ruleDetail);
            }
        }
        return null;

    }


    private CouponView processBin(int isAllType, double maxMoney, double maxDiscount,Integer maxSendNum, String ruleDetail) {
        CouponView couponView = new CouponView();
        couponView.setIsAllType(isAllType);
        couponView.setMaxMoney(maxMoney);
        couponView.setMaxDiscount(maxDiscount);
        couponView.setMaxSendNum(maxSendNum);
        couponView.setRuleDetail(ruleDetail);
        return couponView;
    }

    /**
     * 解释优惠券抹零数据成可读数据
     *
     * @param couponType 优惠券类型（100 现金券 200 打折券。。。）
     * @param goodsRule
     * @return
     */
    private String viewCouponRound(Integer couponType, GoodsRule goodsRule) {
        if (couponType.equals(200)) {
            Integer is_ml = goodsRule.getIs_ml();
            Integer is_round = goodsRule.getIs_round();

            StringBuilder builder = new StringBuilder();
            builder.append("商品总价");

            if (is_ml.equals(0)) { // 不抹零
                builder.append("不抹零");
            } else if (is_ml.equals(1)) { // 按角抹零
                if (is_round.equals(0)) {
                    builder.append("四舍五入到角");
                } else if (is_round.equals(1)) {
                    builder.append("角以后直接抹去");
                }
            } else if (is_ml.equals(2)) { // 按分抹零
                if (is_round.equals(0)) {
                    builder.append("四舍五入到分");
                } else if (is_round.equals(1)) {
                    builder.append("分以后直接抹去");
                }
            } else {
                logs.error("GoodsRule.is_ml 不可能是 {}", is_ml);
                throw new RuntimeException("goodsRule.is_ml 不可能为 " + is_ml);
            }

            builder.append("。");
            return builder.toString();
        }

        return "";
    }

}
