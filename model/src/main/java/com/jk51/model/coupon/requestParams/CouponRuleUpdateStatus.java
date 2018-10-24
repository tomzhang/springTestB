package com.jk51.model.coupon.requestParams;

/**
 * Created by mqq on 2017/7/10.
 */
public class CouponRuleUpdateStatus {
    private Integer siteId;

    private Integer ruleId;

    private Integer preStatus;

    private Integer toUpdateStatus;


    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getPreStatus() {
        return preStatus;
    }

    public void setPreStatus(Integer preStatus) {
        this.preStatus = preStatus;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getToUpdateStatus() {
        return toUpdateStatus;
    }

    public void setToUpdateStatus(Integer toUpdateStatus) {
        this.toUpdateStatus = toUpdateStatus;
    }

}
