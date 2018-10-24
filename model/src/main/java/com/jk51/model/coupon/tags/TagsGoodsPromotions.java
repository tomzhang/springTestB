package com.jk51.model.coupon.tags;

import com.jk51.model.promotions.PromotionsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by javen on 2018/4/8.
 */
public class TagsGoodsPromotions extends TagsGoods {
    private List<PromotionsActivity> rules = new ArrayList<>();
    private Map<Integer, Map<Integer, List<PromotionsActivity>>> group = new HashMap<Integer, Map<Integer, List<PromotionsActivity>>>();

    public static TagsGoodsPromotions buildTagsGoodsPromotions(String goodsId) {
        return new TagsGoodsPromotions(goodsId);
    }

    private TagsGoodsPromotions(String goodsId) {
        super.goodsId = goodsId;
    }

    public List<PromotionsActivity> getRules() {
        return rules;
    }

    public void setRules(List<PromotionsActivity> rules) {
        this.rules = rules;
    }

    public Map<Integer, Map<Integer, List<PromotionsActivity>>> getGroup() {
        return group;
    }

    public void setGroup(Map<Integer, Map<Integer, List<PromotionsActivity>>> group) {
        this.group = group;
    }
}
