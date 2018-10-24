package com.jk51.model.concession.result;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by ztq on 2018/1/6
 * Description:
 */
@SuppressWarnings("unused")
public class GoodsDataForResult extends GoodsData {
    /* -- 该商品已参加过的优惠券和活动的优惠金额 -- */
    private int discount = 0;

    /* -- 该商品参加的优惠券的记录 -- */
    /**
     * 根据活动确定该商品是否可以参加优惠券
     */
    private boolean canUseCoupon = true;

    private boolean useCoupon = false;

    private int couponDiscount = 0;

    /* -- 该商品参加的活动的记录 -- */
    /**
     * 记录该商品参加的活动信息记录
     * key为活动规则类型，value为该商品参加的所有的活动信息和优惠结果
     */
    private Map<Integer, PromotionsResult> promotionsRemark = new HashMap<>();


    /* -- setter&getter -- */

    public boolean isCanUseCoupon() {
        return canUseCoupon;
    }

    public void setCanUseCoupon(boolean canUseCoupon) {
        this.canUseCoupon = canUseCoupon;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public boolean isUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(boolean useCoupon) {
        this.useCoupon = useCoupon;
    }

    public int getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(int couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public Map<Integer, PromotionsResult> getPromotionsRemark() {
        return promotionsRemark;
    }

    public void setPromotionsRemark(Map<Integer, PromotionsResult> promotionsRemark) {
        this.promotionsRemark = promotionsRemark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GoodsDataForResult)) return false;
        GoodsDataForResult that = (GoodsDataForResult) o;
        return getDiscount() == that.getDiscount() &&
            isUseCoupon() == that.isUseCoupon() &&
            getCouponDiscount() == that.getCouponDiscount() &&
            Objects.equals(getPromotionsRemark(), that.getPromotionsRemark());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDiscount(), isUseCoupon(), getCouponDiscount(), getPromotionsRemark());
    }

    @Override
    public String toString() {
        return "GoodsDataForResult{" +
            "discount=" + discount +
            ", useCoupon=" + useCoupon +
            ", couponDiscount=" + couponDiscount +
            ", promotionsRemark=" + promotionsRemark +
            '}';
    }
}
