package com.jk51.model.concession.result;

import java.util.Objects;

/**
 * Created by ztq on 2018/1/6
 * Description:
 */
public class PromotionsResult {
    /**
     * 优惠类型，1代表优惠金额，2表示赠送礼品
     */
    private Integer concessionType;

    private Integer promotionsActivityId;

    private Integer promotionsRuleType;

    private Integer discount;


    /* -- setter & getter -- */

    public Integer getConcessionType() {
        return concessionType;
    }

    public void setConcessionType(Integer concessionType) {
        this.concessionType = concessionType;
    }

    public Integer getPromotionsActivityId() {
        return promotionsActivityId;
    }

    public void setPromotionsActivityId(Integer promotionsActivityId) {
        this.promotionsActivityId = promotionsActivityId;
    }

    public Integer getPromotionsRuleType() {
        return promotionsRuleType;
    }

    public void setPromotionsRuleType(Integer promotionsRuleType) {
        this.promotionsRuleType = promotionsRuleType;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PromotionsResult)) return false;
        PromotionsResult that = (PromotionsResult) o;
        return Objects.equals(getConcessionType(), that.getConcessionType()) &&
            Objects.equals(getPromotionsActivityId(), that.getPromotionsActivityId()) &&
            Objects.equals(getPromotionsRuleType(), that.getPromotionsRuleType()) &&
            Objects.equals(getDiscount(), that.getDiscount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getConcessionType(), getPromotionsActivityId(), getPromotionsRuleType(), getDiscount());
    }

    @Override
    public String toString() {
        return "PromotionsResult{" +
            "concessionType=" + concessionType +
            ", promotionsActivityId=" + promotionsActivityId +
            ", promotionsRuleType=" + promotionsRuleType +
            ", discount=" + discount +
            '}';
    }
}
