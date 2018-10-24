package com.jk51.model.promotions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by javen on 2017/11/20.
 */
public class ProCouponList {
    //商品下符合的优惠券
    private List<EasyToSeeCoupon> couponsRule = new ArrayList<EasyToSeeCoupon>();
    //商品下符合的活动
    private List<PromotionsActivity> promotionsActivities = new ArrayList<PromotionsActivity>();

    //活动分组 按照活动的Type 进行分组
    private Map<Integer,List<PromotionsActivity>> promotionsActivitiesGroup = new HashMap<Integer,List<PromotionsActivity>>();

    //优惠券分组 按照优惠券的Type 进行分组
    private Map<Integer,List<EasyToSeeCoupon>> couponsRuleGroup = new HashMap<Integer,List<EasyToSeeCoupon>>();

    public List<EasyToSeeCoupon> getCouponsRule() {
        return couponsRule;
    }

    public void setCouponsRule(List<EasyToSeeCoupon> couponsRule) {
        this.couponsRule = couponsRule;
    }

    public List<PromotionsActivity> getPromotionsActivities() {
        return promotionsActivities;
    }

    public void setPromotionsActivities(List<PromotionsActivity> promotionsActivities) {
        this.promotionsActivities = promotionsActivities;
    }

    public Map<Integer, List<PromotionsActivity>> getPromotionsActivitiesGroup() {
        return promotionsActivitiesGroup;
    }

    public void setPromotionsActivitiesGroup(Map<Integer, List<PromotionsActivity>> promotionsActivitiesGroup) {
        this.promotionsActivitiesGroup = promotionsActivitiesGroup;
    }

    public Map<Integer, List<EasyToSeeCoupon>> getCouponsRuleGroup() {
        return couponsRuleGroup;
    }

    public void setCouponsRuleGroup(Map<Integer, List<EasyToSeeCoupon>> couponsRuleGroup) {
        this.couponsRuleGroup = couponsRuleGroup;
    }

    @Override
    public String toString() {
        return "ProCouponList{" +
            "couponsRule=" + couponsRule +
            ", promotionsActivities=" + promotionsActivities +
            ", promotionsActivitiesGroup=" + promotionsActivitiesGroup +
            ", couponsRuleGroup=" + couponsRuleGroup +
            '}';
    }
}
