package com.jk51.model.promotions.activity;

/**
 * Created by Administrator on 2018/3/16.
 */
public class TempDateModel {

    Integer goodsIdType;

    String goodsIds;

    Integer promotionType;

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
    }

    @Override
    public String toString() {
        return "TempDateModel{" +
            "goodsIdType=" + goodsIdType +
            ", goodsIds='" + goodsIds + '\'' +
            ", promotionType=" + promotionType +
            '}';
    }

    public TempDateModel(Integer goodsIdType, String goodsIds,Integer promotionType){
        this.goodsIds = goodsIds;
        this.goodsIdType=goodsIdType;
        this.promotionType =promotionType;
    }

    public String getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }

    public Integer getGoodsIdType() {

        return goodsIdType;
    }

    public void setGoodsIdType(Integer goodsIdType) {
        this.goodsIdType = goodsIdType;
    }
}
