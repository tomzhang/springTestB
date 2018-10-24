package com.jk51.model.promotions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by javen on 2017/11/20.
 */
public class ProCouponEasyToSee {
    // 存储 活动及优惠券
    private ProCouponList ProCouponList = new ProCouponList();
    // 优先限定价格活动，秒杀、限价、拼团等
    private List<PromotionsActivity> pricesActivity = new ArrayList<PromotionsActivity>();
    //优先活动的优先级分组
    private Map<Integer,List<PromotionsActivity>> pricesActivityGroup = new HashMap<Integer,List<PromotionsActivity>>();

    public com.jk51.model.promotions.ProCouponList getProCouponList() {
        return ProCouponList;
    }

    public void setProCouponList(com.jk51.model.promotions.ProCouponList proCouponList) {
        ProCouponList = proCouponList;
    }

    public List<PromotionsActivity> getPricesActivity() {
        return pricesActivity;
    }

    public void setPricesActivity(List<PromotionsActivity> pricesActivity) {
        this.pricesActivity = pricesActivity;
    }

    public Map<Integer, List<PromotionsActivity>> getPricesActivityGroup() {
        return pricesActivityGroup;
    }

    public void setPricesActivityGroup(Map<Integer, List<PromotionsActivity>> pricesActivityGroup) {
        this.pricesActivityGroup = pricesActivityGroup;
    }

    @Override
    public String toString() {
        return "ProCouponEasyToSee{" +
            ", ProCouponList=" + ProCouponList +
            ", pricesActivity=" + pricesActivity +
            '}';
    }
}
