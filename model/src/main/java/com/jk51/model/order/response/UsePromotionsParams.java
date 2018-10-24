package com.jk51.model.order.response;

/**
 * filename :com.jk51.modules.coupon.request.
 * author   :zw
 * date     :2017/8/14
 * Update   :
 */
public class UsePromotionsParams {

    private Integer promActivityId;//优惠活动id

    private Integer promotionsId;  //优惠活动规则id
    /**
     * 类型 10满赠活动 20打折活动 30包邮活动
     */
    private Integer promotionsType;
    /**
     * 优惠名称
     */
    private String promotionsName;

    private Integer deductionMoney;   //优惠多少

    public Integer getPromotionsId() {
        return promotionsId;
    }

    public void setPromotionsId(Integer promotionsId) {
        this.promotionsId = promotionsId;
    }

    public Integer getPromotionsType() {
        return promotionsType;
    }

    public void setPromotionsType(Integer promotionsType) {
        this.promotionsType = promotionsType;
    }

    public String getPromotionsName() {
        return promotionsName;
    }

    public void setPromotionsName(String promotionsName) {
        this.promotionsName = promotionsName;
    }

    public Integer getDeductionMoney() {
        return deductionMoney;
    }

    public void setDeductionMoney(Integer deductionMoney) {
        this.deductionMoney = deductionMoney;
    }

    public Integer getPromActivityId() {
        return promActivityId;
    }

    public void setPromActivityId(Integer promActivityId) {
        this.promActivityId = promActivityId;
    }

    @Override
    public String toString() {
        return "{" +
                "promActivityId=" + promActivityId +
                ",promotionsId=" + promotionsId +
                ", promotionsType=" + promotionsType +
                ", promotionsName='" + promotionsName + '\'' +
                ", deductionMoney='" + deductionMoney +
                '}';
    }
}
