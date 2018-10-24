package com.jk51.model.coupon.returnParams;

import java.util.Map;

/**
 * filename :com.jk51.model.coupon.returnParams.
 * author   :zw
 * date     :2017/3/8
 * Update   :
 */
public class UseCouponReturnParams {
    private Integer couponId;  //优惠券id
    private Integer accountAmount; //计算后实付金额
    private Integer couponMoney;   //优惠多少
    private Map<String,Object> giftRuleMsg;//赠品信息

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public Integer getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(Integer accountAmount) {
        this.accountAmount = accountAmount;
    }

    public Integer getCouponMoney() {
        return couponMoney;
    }

    public void setCouponMoney(Integer couponMoney) {
        this.couponMoney = couponMoney;
    }

    public Map<String, Object> getGiftRuleMsg() {
        return giftRuleMsg;
    }

    public void setGiftRuleMsg(Map<String, Object> giftRuleMsg) {
        this.giftRuleMsg = giftRuleMsg;
    }
}
