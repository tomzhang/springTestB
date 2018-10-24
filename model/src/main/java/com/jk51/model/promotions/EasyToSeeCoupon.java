package com.jk51.model.promotions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponRule;

import java.util.Comparator;
import java.util.Objects;

/**
 * Created by javen on 2017/11/23.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EasyToSeeCoupon{
    @JsonIgnore
    private CouponRule coupon;
    private String tags;
    private Integer ruleId;

    public static EasyToSeeCoupon buildEasyToSeeCoupon(CouponRule coupon) {
        EasyToSeeCoupon easy2Coupon=new EasyToSeeCoupon();
        easy2Coupon.setCoupon(coupon);
        easy2Coupon.setRuleId(coupon.getRuleId());
        return easy2Coupon;
    }

    public CouponRule getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponRule coupon) {
        this.coupon = coupon;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public EasyToSeeCoupon(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EasyToSeeCoupon that = (EasyToSeeCoupon) o;
        return Objects.equals(ruleId, that.ruleId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ruleId);
    }

    @Override
    public String toString() {
        return "EasyToSeeCoupon{" +
            "coupon=" + coupon +
            ", tags='" + tags + '\'' +
            ", ruleId=" + ruleId +
            '}';
    }

}
