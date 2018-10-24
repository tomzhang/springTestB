package com.jk51.model.concession.result;

/**
 * Created by ztq on 2018/3/6
 * Description: 金钱优惠
 */
public class MoneyResultForPromotions {

    private Integer promotionsRuleId;

    private Integer promotionsActivityId;

    private String promotionsName;

    private Integer promotionsType;

    private Integer discount = 0;


    /* -- setter & getter -- */

    public Integer getPromotionsRuleId() {
        return promotionsRuleId;
    }

    public void setPromotionsRuleId(Integer promotionsRuleId) {
        this.promotionsRuleId = promotionsRuleId;
    }

    public Integer getPromotionsActivityId() {
        return promotionsActivityId;
    }

    public void setPromotionsActivityId(Integer promotionsActivityId) {
        this.promotionsActivityId = promotionsActivityId;
    }

    public String getPromotionsName() {
        return promotionsName;
    }

    public void setPromotionsName(String promotionsName) {
        this.promotionsName = promotionsName;
    }

    public Integer getPromotionsType() {
        return promotionsType;
    }

    public void setPromotionsType(Integer promotionsType) {
        this.promotionsType = promotionsType;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }
}
