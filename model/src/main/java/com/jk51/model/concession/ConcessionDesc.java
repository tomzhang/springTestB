package com.jk51.model.concession;

import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsDetail;

import java.util.Objects;

/**
 * Created by ztq on 2018/1/9
 * Description: 用来描述用户领取的优惠券或参加的活动
 */
public class ConcessionDesc {
    /**
     * 1表示发给用户的优惠券，如果是，则根据{@link ConcessionDesc#couponDetailId}确定
     * 2表示活动（PromotionsActivityID），用于预下单等订单未知的情况下，如果是，则根据{@link ConcessionDesc#promotionsActivityId}确定
     * 3表示活动（PromotionsDetailID），根据活动详情确定，基本在下单后才会使用，如果是，根据{@link ConcessionDesc#promotionsDetailId}确定
     */
    private Integer concessionType;

    /**
     * 规则展示名称
     */
    private String ruleView;

    /**
     * {@link CouponDetail#id}
     */
    private Integer couponDetailId;

    /**
     * {@link PromotionsActivity#id}
     */
    private Integer promotionsActivityId;

    /**
     * {@link PromotionsDetail#id}
     */
    private Integer promotionsDetailId;


    /* -- setter & getter -- */

    public Integer getConcessionType() {
        return concessionType;
    }

    public void setConcessionType(Integer concessionType) {
        this.concessionType = concessionType;
    }

    public String getRuleView() {
        return ruleView;
    }

    public void setRuleView(String ruleView) {
        this.ruleView = ruleView;
    }

    public Integer getCouponDetailId() {
        return couponDetailId;
    }

    public void setCouponDetailId(Integer couponDetailId) {
        this.couponDetailId = couponDetailId;
    }

    public Integer getPromotionsActivityId() {
        return promotionsActivityId;
    }

    public void setPromotionsActivityId(Integer promotionsActivityId) {
        this.promotionsActivityId = promotionsActivityId;
    }

    public Integer getPromotionsDetailId() {
        return promotionsDetailId;
    }

    public void setPromotionsDetailId(Integer promotionsDetailId) {
        this.promotionsDetailId = promotionsDetailId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConcessionDesc)) return false;
        ConcessionDesc that = (ConcessionDesc) o;
        return Objects.equals(getConcessionType(), that.getConcessionType()) &&
            Objects.equals(getRuleView(), that.getRuleView()) &&
            Objects.equals(getCouponDetailId(), that.getCouponDetailId()) &&
            Objects.equals(getPromotionsActivityId(), that.getPromotionsActivityId()) &&
            Objects.equals(getPromotionsDetailId(), that.getPromotionsDetailId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getConcessionType(), getRuleView(), getCouponDetailId(), getPromotionsActivityId(), getPromotionsDetailId());
    }

    @Override
    public String toString() {
        return "ConcessionDesc{" +
            "concessionType=" + concessionType +
            ", ruleView='" + ruleView + '\'' +
            ", couponDetailId=" + couponDetailId +
            ", promotionsActivityId=" + promotionsActivityId +
            ", promotionsDetailId=" + promotionsDetailId +
            '}';
    }
}
