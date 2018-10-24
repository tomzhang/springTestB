package com.jk51.modules.promotions.utils;

import com.jk51.modules.coupon.utils.CouponProcessUtils;
import com.jk51.modules.promotions.constants.PromotionsConstant;
import com.jk51.modules.promotions.request.OrderDeductionDto;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * filename :com.jk51.modules.promotions.utils.
 * author   :zw
 * date     :2017/8/14
 * Update   :
 */
@Service
public class OrderDeductionUtils {

    @Autowired
    private CouponProcessUtils couponProcessUtils;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 筛选参加的商品信息
     *
     * @param goodsIdsType
     * @param orderDeductionDto
     * @return
     */
    public Map<String, Object> excuseGoodsInfo(Integer goodsIdsType, String goodsIds, OrderDeductionDto orderDeductionDto) {
        List<Map<String, Integer>> mapList = orderDeductionDto.getGoodsInfo();
        Integer priceTotal = 0;
        Integer goodsNum = 0;
        List<Map<String, Object>> mapListGoodsInfo = new ArrayList<>();
        for (Map<String, Integer> goodsInfoMap : mapList) {
            Integer goodsId = goodsInfoMap.get("goodsId") == null ? 0 : goodsInfoMap.get("goodsId");
            Map<String, Object> objectMap = new HashedMap();
            boolean isExcuse = true;
            if (goodsIdsType != 0)
                isExcuse = judgeExcuse(goodsIds, goodsId);//筛选出参加的商品

            switch (goodsIdsType) {
                case PromotionsConstant.DISCOUNT_RULE_GOODS_TYPE_ALL: //全部商品参加
                    priceTotal += goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num");
                    goodsNum += goodsInfoMap.get("num");
                    objectMap.put("goodsPrice", goodsInfoMap.get("goodsPrice"));
                    objectMap.put("num", goodsInfoMap.get("num"));
                    objectMap.put("goodsId", goodsId.toString());
                    break;
                case PromotionsConstant.DISCOUNT_RULE_GOODS_TYPE_JOIN: //指定商品参加
                    if (isExcuse) {
                        priceTotal += goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num");
                        goodsNum += goodsInfoMap.get("num");
                        objectMap.put("goodsPrice", goodsInfoMap.get("goodsPrice"));
                        objectMap.put("num", goodsInfoMap.get("num"));
                        objectMap.put("goodsId", goodsId.toString());
                    }
                    break;
                case PromotionsConstant.DISCOUNT_RULE_GOODS_TYPE_SIT_OUT: //指定商品不参加
                    if (!isExcuse) {
                        priceTotal += goodsInfoMap.get("goodsPrice") * goodsInfoMap.get("num");
                        goodsNum += goodsInfoMap.get("num");
                        objectMap.put("goodsPrice", goodsInfoMap.get("goodsPrice"));
                        objectMap.put("num", goodsInfoMap.get("num"));
                        objectMap.put("goodsId", goodsId.toString());
                    }
                    break;
                default:
                    break;
            }
            if (!objectMap.keySet().isEmpty())
                mapListGoodsInfo.add(objectMap);
        }
        Map<String, Object> objectMap = new HashedMap();
        objectMap.put("priceTotal", priceTotal);
        objectMap.put("goodsNum", goodsNum);
        objectMap.put("mapListGoodsInfo", mapListGoodsInfo);
        return objectMap;
    }

    /**
     * 判断是否包含(商品和会员)
     *
     * @param ids
     * @param selectId
     * @return
     */
    public boolean judgeExcuse(String ids, Integer selectId) {
        if (selectId == null || ids == null) {
            return false;
        }
        if ("all".equals(ids)) {
            return true;
        }
        String[] goodsArray = ids.split(",");
        for (String id : goodsArray) {
            if (selectId.equals(Integer.parseInt(id))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 数据转list
     *
     * @param ids
     * @return
     */
    public List<String> arrayConvertList(String ids) {
        if (StringUtils.isBlank(ids)) {
            return new ArrayList<>();
        }
        String[] idsArr = ids.split(",");
        return new ArrayList<>(Arrays.asList(idsArr));
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
                double discountCount = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
                discount_money = (int) (discountCount * 10);
            } else if (is_round == 1) { //直接舍去
                BigDecimal b = new BigDecimal(countFee * discount * 0.01 * 0.1);
                double f1 = b.setScale(0, BigDecimal.ROUND_DOWN).doubleValue();
                discount_money = (int) (f1 * 10);
            }

        } else if (is_ml == 2) {
            if (is_round == 0) { //四舍五入
                BigDecimal b = new BigDecimal(countFee * discount * 0.01);
                double f1 = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
                discount_money = (int) f1;
            } else if (is_round == 1) { //直接舍去
                BigDecimal b = new BigDecimal(countFee * discount * 0.01);
                double f1 = b.setScale(0, BigDecimal.ROUND_DOWN).doubleValue();
                discount_money = (int) f1;
            }
        }
        return countFee - discount_money;
    }

    /**
     * 折扣活动的分别计算，不同的商品有不同的优惠折扣
     *
     * @param countFee
     * @param is_ml
     * @param is_round
     * @param is_ml
     * @return
     */

    public Integer discountMoneyForDifferentGoods(Integer countFee, Integer is_ml, Integer is_round, List<Map<String, String>> goodsList, List<Map<String, Integer>> ruleList) {
        Integer discount_money = 0;
        for (Map<String, String> map : goodsList) {
            String goodsId = map.get("goodsId").toString();
            boolean theGoods = false;
            for (Map<String, Integer> rulemap : ruleList) {
                String ruleGoodsId = rulemap.get("goodsId").toString();
                if (StringUtils.equalsIgnoreCase(goodsId, ruleGoodsId)) {
                    theGoods = true;
                    discount_money += discountMoney(is_ml, is_round, map, rulemap);
                }

            }
            if (!theGoods)
                discount_money += Integer.parseInt(map.get("goodsPrice").toString()) * Integer.parseInt(map.get("num").toString());
        }

        return countFee - discount_money;
    }

    public Integer discountMoney(Integer is_ml, Integer is_round, Map<String, String> map, Map<String, Integer> ruleMap) {
        try {
            Integer discount_money = 0;
            Integer countFee = Integer.parseInt(map.get("goodsPrice").toString()) * Integer.parseInt(map.get("num").toString());
            Integer discount = ruleMap.get("discount");

            if (is_ml == 0) { // 设置不抹零 默认为四舍五入
                discount_money = (int) (countFee * discount * 0.01);
            } else if (is_ml == 1) {
                if (is_round == 0) { //四舍五入
                    BigDecimal b = new BigDecimal(countFee * discount * 0.01 * 0.1);
                    double discountCount = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
                    discount_money = (int) (discountCount * 10);
                } else if (is_round == 1) { //直接舍去
                    BigDecimal b = new BigDecimal(countFee * discount * 0.01 * 0.1);
                    double f1 = b.setScale(0, BigDecimal.ROUND_DOWN).doubleValue();
                    discount_money = (int) (f1 * 10);
                }

            } else if (is_ml == 2) {
                if (is_round == 0) { //四舍五入
                    BigDecimal b = new BigDecimal(countFee * discount * 0.01);
                    double f1 = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
                    discount_money = (int) f1;
                } else if (is_round == 1) { //直接舍去
                    BigDecimal b = new BigDecimal(countFee * discount * 0.01);
                    double f1 = b.setScale(0, BigDecimal.ROUND_DOWN).doubleValue();
                    discount_money = (int) f1;
                }
            }

            if ((countFee - discount_money) > ruleMap.get("max_reduce"))
                discount_money = countFee - ruleMap.get("max_reduce");
            return discount_money;
        } catch (Exception e) {
            logger.info("商品对应折扣金额结算异常。。。。。。。。。。" + e);
            return null;
        }
    }


    public List<Map<String, String>> String2List(Object sJson) {
        List<Map<String, String>> mapList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(sJson.toString());
            int iSize = jsonArray.length();
            for (int i = 0; i < iSize; i++) {
                Map<String, String> stringMap = new HashMap<>();
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                if (jsonObj.has("goodsPrice") && jsonObj.has("num")) {
                    stringMap.put("goodsPrice", Obj2String(jsonObj.get("goodsPrice")));
                    stringMap.put("num", Obj2String(jsonObj.get("num")));
                    stringMap.put("goodsId", Obj2String(jsonObj.get("goodsId")));
                } else {
                    return null;
                }
                mapList.add(stringMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapList;
    }

    private String Obj2String(Object obj) {
        String str = obj == null ? "0" : obj.toString();
        return str;
    }
}
