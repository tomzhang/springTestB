package com.jk51.model.coupon;

import java.sql.Timestamp;

/**
 * Created by Administrator on 2017/5/24.
 */
public class CouponLog {
    private Integer id;
    private Integer siteId;
    private Integer ruleId;
    private Integer startCouponNo;
    private Integer endCouponNo;
    private  Integer isExport;
    private Timestamp createTime;
    private Timestamp updateTime;

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Timestamp getCreateTime() {

        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getIsExport() {

        return isExport;
    }

    public void setIsExport(Integer isExport) {
        this.isExport = isExport;
    }

    public Integer getEndCouponNo() {

        return endCouponNo;
    }

    public void setEndCouponNo(Integer endCouponNo) {
        this.endCouponNo = endCouponNo;
    }

    public Integer getStartCouponNo() {

        return startCouponNo;
    }

    public void setStartCouponNo(Integer startCouponNo) {
        this.startCouponNo = startCouponNo;
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

    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
