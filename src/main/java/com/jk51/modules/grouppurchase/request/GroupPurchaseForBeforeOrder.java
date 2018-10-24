package com.jk51.modules.grouppurchase.request;

/**
 * Created by mqq on 2017/11/17.
 */
public class GroupPurchaseForBeforeOrder {
    /**
     * 预下单的拼团商品id
     */
    private Integer goodsId;

    /**
     * 预下单参加的拼团活动Id
     */
    private Integer promotionsActivityId;

    /**
     * 团长id，如果为null，表示是开团订单，否则是
     */
    private Integer groupPurchaseParentId;

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getPromotionsActivityId() {
        return promotionsActivityId;
    }

    public void setPromotionsActivityId(Integer promotionsActivityId) {
        this.promotionsActivityId = promotionsActivityId;
    }

    public Integer getGroupPurchaseParentId() {
        return groupPurchaseParentId;
    }

    public void setGroupPurchaseParentId(Integer groupPurchaseParentId) {
        this.groupPurchaseParentId = groupPurchaseParentId;
    }

    @Override
    public String toString() {
        return "GroupPurchaseForBeforeOrder{" +
            "goodsId=" + goodsId +
            ", promotionsActivityId=" + promotionsActivityId +
            ", groupPurchaseParentId=" + groupPurchaseParentId +
            '}';
    }
}
