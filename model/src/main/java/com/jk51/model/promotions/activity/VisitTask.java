package com.jk51.model.promotions.activity;

import java.time.LocalDateTime;

/**
 * Created by Administrator on 2018/3/15.
 */
public class VisitTask {

    //活动名称
    private String promotionName;

    //活动类型
    private String promotionType;

    //活动商品的参与方式
    private String promotionGoods;

    //包含商品多少件
    private Integer containVisitGoodsNum;

    //活动会员参与方式
    private String joinObject;

    //活动会员参与数量
    private Integer containVisitMemberNum;

    //活动开始时间
    private String dateTime;

    //活动ID
    private Integer promotionId;

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public Integer getContainVisitMemberNum() {
        return containVisitMemberNum;
    }

    public void setContainVisitMemberNum(Integer containVisitMemberNum) {
        this.containVisitMemberNum = containVisitMemberNum;
    }

    public String getJoinObject() {
        return joinObject;
    }

    public void setJoinObject(String joinObject) {
        this.joinObject = joinObject;
    }

    public Integer getContainVisitGoodsNum() {

        return containVisitGoodsNum;

    }

    public void setContainVisitGoodsNum(Integer containVisitGoodsNum) {
        this.containVisitGoodsNum = containVisitGoodsNum;
    }

    public String getPromotionGoods() {

        return promotionGoods;
    }

    public void setPromotionGoods(String promotionGoods) {
        this.promotionGoods = promotionGoods;
    }

    public String getPromotionType() {

        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

    @Override
    public String toString() {
        return "VisitTask{" +
            "promotionName='" + promotionName + '\'' +
            ", promotionType='" + promotionType + '\'' +
            ", promotionGoods='" + promotionGoods + '\'' +
            ", containVisitGoodsNum=" + containVisitGoodsNum +
            ", joinObject='" + joinObject + '\'' +
            ", containVisitMemberNum=" + containVisitMemberNum +
            ", dateTime='" + dateTime + '\'' +
            ", promotionId=" + promotionId +
            '}';
    }
}
