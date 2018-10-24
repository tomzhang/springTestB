package com.jk51.modules.promotions.request;


import java.util.List;

/**
 * filename :com.jk51.modules.promotions.request.
 * author   :zw
 * date     :2017/11/2
 * Update   :
 */
public class OrderCombinationsParam {
    private Integer goodsId;
    /**
     * 商品原价
     */
    private Integer originalPrice;
    /**
     * 商品限价后 如果此价格为空,则可以参加满折
     * 限价独立参加
     */
    private Integer checkPrice;
    /**
     * 商品折后价
     */
    private Integer discountPrice;
    /**
     * 商品减后价
     */
    private Integer reducePrice;
    /**
     * 如果有promotionsId则不做其他处理
     */
    private Integer promotionsId;
    /**
     * 此商品是否存在活动,不管满不满足价格或者满件条件,只针对折扣
     */
    private boolean isExistDiscountPromotions;

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Integer originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getCheckPrice() {
        return checkPrice;
    }

    public void setCheckPrice(Integer checkPrice) {
        this.checkPrice = checkPrice;
    }

    public Integer getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Integer discountPrice) {
        this.discountPrice = discountPrice;
    }

    public boolean isExistDiscountPromotions() {
        return isExistDiscountPromotions;
    }

    public void setExistDiscountPromotions(boolean existDiscountPromotions) {
        isExistDiscountPromotions = existDiscountPromotions;
    }

    public Integer getReducePrice() {
        return reducePrice;
    }

    public void setReducePrice(Integer reducePrice) {
        this.reducePrice = reducePrice;
    }

    public Integer getPromotionsId() {
        return promotionsId;
    }

    public void setPromotionsId(Integer promotionsId) {
        this.promotionsId = promotionsId;
    }

}
