package com.jk51.model.coupon.requestParams;

/**
 * filename :com.jk51.model.coupon.requestParams.
 * author   :zw
 * date     :2017/4/20
 * Update   :
 */
public class GetAddClerkParams {
    private String phone;
    private String activityIds;
    private Integer ruleId;
    private Integer siteId;
    private Integer storeId;
    private Integer managerId;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(String activityIds) {
        this.activityIds = activityIds;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
}
