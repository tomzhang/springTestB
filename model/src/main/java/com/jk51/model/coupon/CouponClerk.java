package com.jk51.model.coupon;

import java.util.Date;

/**
 * filename :com.jk51.model.coupon.
 * author   :zw
 * date     :2017/3/26
 * Update   :
 */
public class CouponClerk {
    private Integer siteId;
    private Integer id;
    private Integer ruleId;
    private Integer useCouponNum;
    private String managerId;
    private Date createTime;
    private Date updateTime;
    private Integer activeId;

    public Integer getActiveId() {
        return activeId;
    }

    public void setActiveId(Integer activeId) {
        this.activeId = activeId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getUseCouponNum() {
        return useCouponNum;
    }

    public void setUseCouponNum(Integer useCouponNum) {
        this.useCouponNum = useCouponNum;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
