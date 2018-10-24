package com.jk51.model.coupon;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/5/23.
 */
public class CouponExportLog implements Serializable {
    private Integer siteId;
    private Integer id;
    private Integer ruleId;
    private Integer startCouponNo;
    private Integer endCouponNo;
    private Date createTime;
    private Date updateTime;
    private String createTimeInfo;
    private String updateTimeInfo;
    private Integer isExport;

    public String getCreateTimeInfo() {
        return createTimeInfo;
    }

    public void setCreateTimeInfo(String createTimeInfo) {
        this.createTimeInfo = createTimeInfo;
    }

    public String getUpdateTimeInfo() {
        return updateTimeInfo;
    }

    public void setUpdateTimeInfo(String updateTimeInfo) {
        this.updateTimeInfo = updateTimeInfo;
    }

    public Integer getIsExport() {
        return isExport;
    }

    public void setIsExport(Integer isExport) {
        this.isExport = isExport;
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

    public void setEndCouponNo(Integer endCouponNo) {
        this.endCouponNo = endCouponNo;
    }

    public Integer getEndCouponNo() {
        return endCouponNo;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setStartCouponNo(Integer startCouponNo) {
        this.startCouponNo = startCouponNo;
    }

    public Integer getStartCouponNo() {
        return startCouponNo;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "CouponExportLog{" +
                "siteId=" + siteId +
                ", id=" + id +
                ", ruleId=" + ruleId +
                ", startCouponNo=" + startCouponNo +
                ", endCouponNo=" + endCouponNo +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createTimeInfo='" + createTimeInfo + '\'' +
                ", updateTimeInfo='" + updateTimeInfo + '\'' +
                ", isExport=" + isExport +
                '}';
    }
}
