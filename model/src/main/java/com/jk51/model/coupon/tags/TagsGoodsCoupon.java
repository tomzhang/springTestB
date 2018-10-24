package com.jk51.model.coupon.tags;

import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by javen on 2018/4/8.
 */
public class TagsGoodsCoupon extends TagsGoods {
    private List<CouponActivity> activities = new ArrayList<>();
    private List<CouponRule> rules = new ArrayList<>();
    private Map<Integer, List<CouponRule>> group = new HashMap<Integer, List<CouponRule>>();

    public static TagsGoodsCoupon buildTagsGoodsCoupon(String goodsId) {
        return new TagsGoodsCoupon(goodsId);
    }

    private TagsGoodsCoupon(String goodsId) {
        super.goodsId = goodsId;
    }


    public List<CouponRule> getRules() {
        return rules;
    }

    public void setRules(List<CouponRule> rules) {
        this.rules = rules;
    }

    public Map<Integer, List<CouponRule>> getGroup() {
        return group;
    }

    public void setGroup(Map<Integer, List<CouponRule>> group) {
        this.group = group;
    }

    public List<CouponActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<CouponActivity> activities) {
        this.activities = activities;
    }
}
