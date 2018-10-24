package com.jk51.modules.promotions.request;

import com.jk51.model.promotions.rule.DiscountRule;
import com.jk51.model.promotions.rule.ReduceMoneyRule;

import java.util.List;
import java.util.Map;

/**
 * Created by mqq on 2017/11/8.
 */
public class ProRuleActivitySplitOrderParam {

    //活动id
    private String proActivityId;

    private String groupGoodsKey;

    //每个商品组 对应的数值 以逗号分隔的商品为key 优惠金额为value
    private Map<String, Integer> groupyGoodsSplitOrderMap;

    //打的折扣
    private Integer discount;

    //优惠的金额
    private Integer reduceMoneyFromDisCount;

    //商品组当中的所有商品信息
    private List<Map<String, Integer>> groupGoodsList;

    //参与折扣的商品的id以逗号分隔开
    private String goodsIdsContainTheProActivity;

    private Integer discount_is_ml;

    private Integer discount_is_round;

    private DiscountRule discountRule;

    private ReduceMoneyRule reduceMoneyRule;

    public Integer getDiscount () {
        return discount;
    }

    public void setDiscount (Integer discount) {
        this.discount = discount;
    }

    public List<Map<String, Integer>> getGroupGoodsList () {
        return groupGoodsList;
    }

    public void setGroupGoodsList (List<Map<String, Integer>> groupGoodsList) {
        this.groupGoodsList = groupGoodsList;
    }

    public Map<String, Integer> getGroupyGoodsSplitOrderMap () {
        return groupyGoodsSplitOrderMap;
    }

    public void setGroupyGoodsSplitOrderMap (Map<String, Integer> groupyGoodsSplitOrderMap) {
        this.groupyGoodsSplitOrderMap = groupyGoodsSplitOrderMap;
    }

    public String getProActivityId () {
        return proActivityId;
    }

    public void setProActivityId (String proActivityId) {
        this.proActivityId = proActivityId;
    }

    public String getGroupGoodsKey () {
        return groupGoodsKey;
    }

    public void setGroupGoodsKey (String groupGoodsKey) {
        this.groupGoodsKey = groupGoodsKey;
    }

    public Integer getReduceMoneyFromDisCount () {
        return reduceMoneyFromDisCount;
    }

    public void setReduceMoneyFromDisCount (Integer reduceMoneyFromDisCount) {
        this.reduceMoneyFromDisCount = reduceMoneyFromDisCount;
    }

    public String getGoodsIdsContainTheProActivity () {
        return goodsIdsContainTheProActivity;
    }

    public void setGoodsIdsContainTheProActivity (String goodsIdsContainTheProActivity) {
        this.goodsIdsContainTheProActivity = goodsIdsContainTheProActivity;
    }

    public Integer getDiscount_is_ml () {
        return discount_is_ml;
    }

    public void setDiscount_is_ml (Integer discount_is_ml) {
        this.discount_is_ml = discount_is_ml;
    }

    public Integer getDiscount_is_round () {
        return discount_is_round;
    }

    public void setDiscount_is_round (Integer discount_is_round) {
        this.discount_is_round = discount_is_round;
    }

    public DiscountRule getDiscountRule () {
        return discountRule;
    }

    public void setDiscountRule (DiscountRule discountRule) {
        this.discountRule = discountRule;
    }

    public ReduceMoneyRule getReduceMoneyRule () {
        return reduceMoneyRule;
    }

    public void setReduceMoneyRule (ReduceMoneyRule reduceMoneyRule) {
        this.reduceMoneyRule = reduceMoneyRule;
    }
}
