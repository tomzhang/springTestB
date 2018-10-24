package com.jk51.model.coupon;

import java.io.Serializable;
import java.util.Date;

/**
 * @author javen73
 */
public class BCouponDetailExtraLog implements Serializable {
    private Integer id;

    private Integer siteId;

    /**
     * 优惠券编码
     */
    private String couponNo;

    /**
     * 操作之前的状态
     */
    private Integer beforeStatus;

    /**
     * 操作之后的状态
     */
    private Integer afterStatus;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户手机号码
     */
    private String userMobile;

    /**
     * 操作人id
     */
    private Integer operationId;

    /**
     * 门店id
     */
    private Integer storeId;

    /**
     * 备注
     */
    private String remark;

    private Date createTime;


    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public Integer getBeforeStatus() {
        return beforeStatus;
    }

    public void setBeforeStatus(Integer beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public Integer getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(Integer afterStatus) {
        this.afterStatus = afterStatus;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public Integer getOperationId() {
        return operationId;
    }

    public void setOperationId(Integer operationId) {
        this.operationId = operationId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public BCouponDetailExtraLog() {
    }

    public BCouponDetailExtraLog(Integer siteId, String couponNo, Integer beforeStatus, Integer afterStatus, Integer userId, String userMobile, Integer operationId, Integer storeId, Date createTime) {
        this.siteId = siteId;
        this.couponNo = couponNo;
        this.beforeStatus = beforeStatus;
        this.afterStatus = afterStatus;
        this.userId = userId;
        this.userMobile = userMobile;
        this.operationId = operationId;
        this.storeId = storeId;
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        BCouponDetailExtraLog other = (BCouponDetailExtraLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSiteId() == null ? other.getSiteId() == null : this.getSiteId().equals(other.getSiteId()))
            && (this.getCouponNo() == null ? other.getCouponNo() == null : this.getCouponNo().equals(other.getCouponNo()))
            && (this.getBeforeStatus() == null ? other.getBeforeStatus() == null : this.getBeforeStatus().equals(other.getBeforeStatus()))
            && (this.getAfterStatus() == null ? other.getAfterStatus() == null : this.getAfterStatus().equals(other.getAfterStatus()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getUserMobile() == null ? other.getUserMobile() == null : this.getUserMobile().equals(other.getUserMobile()))
            && (this.getOperationId() == null ? other.getOperationId() == null : this.getOperationId().equals(other.getOperationId()))
            && (this.getStoreId() == null ? other.getStoreId() == null : this.getStoreId().equals(other.getStoreId()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSiteId() == null) ? 0 : getSiteId().hashCode());
        result = prime * result + ((getCouponNo() == null) ? 0 : getCouponNo().hashCode());
        result = prime * result + ((getBeforeStatus() == null) ? 0 : getBeforeStatus().hashCode());
        result = prime * result + ((getAfterStatus() == null) ? 0 : getAfterStatus().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getUserMobile() == null) ? 0 : getUserMobile().hashCode());
        result = prime * result + ((getOperationId() == null) ? 0 : getOperationId().hashCode());
        result = prime * result + ((getStoreId() == null) ? 0 : getStoreId().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", siteId=").append(siteId);
        sb.append(", couponNo=").append(couponNo);
        sb.append(", beforeStatus=").append(beforeStatus);
        sb.append(", afterStatus=").append(afterStatus);
        sb.append(", userId=").append(userId);
        sb.append(", userMobile=").append(userMobile);
        sb.append(", operationId=").append(operationId);
        sb.append(", storeId=").append(storeId);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
