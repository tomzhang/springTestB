package com.jk51.model.clerkvisit;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class BClerkVisit implements Serializable {
    private Integer id;

    /**
     * 商户ID
     */
    private Integer siteId;

    /**
     * 门店店员ID
     */
    private Integer storeAdminId;

    /**
     * 店员名称
     */
    private String adminName;

    /**
     * 门店ID
     */
    private Integer storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 会员ID
     */
    private Integer buyerId;

    /**
     * 会员姓名
     */
    private String buyerName;

    private Long buyerMobile;

    /**
     * 回访类型 10 复购
     */
    private Byte visitType;

    /**
     * 回访时间
     */
    private Date visitTime;

    /**
     * 实际回访时间
     */
    private Date realVisitTime;

    /**
     * 商品标题
     */
    private String goodsTitle;

    /**
     * 商品用完时间 生成回访的订单时间+商品消耗时间*数量
     */
    private Date doneTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态 10 待回访 20回访中 30 已回访 40 未回访
     */
    private Byte status;

    /**
     * 回访活动Id,用,隔开
     */
    private String activityIds;
    /**
     * 回访商品Id,用,隔开
     */
    private String goodsIds;

    private Date createTime;

    private Date updateTime;

    private String adminMobile;

    /**
     * 对应b_visit_statistics
     */
    private Integer bvsId;

    public String getAdminMobile() {
        return adminMobile;
    }

    public void setAdminMobile(String adminMobile) {
        this.adminMobile = adminMobile;
    }

    public String getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(String activityIds) {
        this.activityIds = activityIds;
    }

    public String getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
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

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public Long getBuyerMobile() {
        return buyerMobile;
    }

    public void setBuyerMobile(Long buyerMobile) {
        this.buyerMobile = buyerMobile;
    }

    public Byte getVisitType() {
        return visitType;
    }

    public void setVisitType(Byte visitType) {
        this.visitType = visitType;
    }

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }

    public Date getRealVisitTime() {
        return realVisitTime;
    }

    public void setRealVisitTime(Date realVisitTime) {
        this.realVisitTime = realVisitTime;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public Date getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(Date doneTime) {
        this.doneTime = doneTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getBvsId() {
        return bvsId;
    }

    public void setBvsId(Integer bvsId) {
        this.bvsId = bvsId;
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
        BClerkVisit other = (BClerkVisit) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSiteId() == null ? other.getSiteId() == null : this.getSiteId().equals(other.getSiteId()))
            && (this.getStoreAdminId() == null ? other.getStoreAdminId() == null : this.getStoreAdminId().equals(other.getStoreAdminId()))
            && (this.getAdminName() == null ? other.getAdminName() == null : this.getAdminName().equals(other.getAdminName()))
            && (this.getStoreId() == null ? other.getStoreId() == null : this.getStoreId().equals(other.getStoreId()))
            && (this.getStoreName() == null ? other.getStoreName() == null : this.getStoreName().equals(other.getStoreName()))
            && (this.getBuyerId() == null ? other.getBuyerId() == null : this.getBuyerId().equals(other.getBuyerId()))
            && (this.getBuyerName() == null ? other.getBuyerName() == null : this.getBuyerName().equals(other.getBuyerName()))
            && (this.getBuyerMobile() == null ? other.getBuyerMobile() == null : this.getBuyerMobile().equals(other.getBuyerMobile()))
            && (this.getVisitType() == null ? other.getVisitType() == null : this.getVisitType().equals(other.getVisitType()))
            && (this.getVisitTime() == null ? other.getVisitTime() == null : this.getVisitTime().equals(other.getVisitTime()))
            && (this.getRealVisitTime() == null ? other.getRealVisitTime() == null : this.getRealVisitTime().equals(other.getRealVisitTime()))
            && (this.getGoodsTitle() == null ? other.getGoodsTitle() == null : this.getGoodsTitle().equals(other.getGoodsTitle()))
            && (this.getDoneTime() == null ? other.getDoneTime() == null : this.getDoneTime().equals(other.getDoneTime()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSiteId() == null) ? 0 : getSiteId().hashCode());
        result = prime * result + ((getStoreAdminId() == null) ? 0 : getStoreAdminId().hashCode());
        result = prime * result + ((getAdminName() == null) ? 0 : getAdminName().hashCode());
        result = prime * result + ((getStoreId() == null) ? 0 : getStoreId().hashCode());
        result = prime * result + ((getStoreName() == null) ? 0 : getStoreName().hashCode());
        result = prime * result + ((getBuyerId() == null) ? 0 : getBuyerId().hashCode());
        result = prime * result + ((getBuyerName() == null) ? 0 : getBuyerName().hashCode());
        result = prime * result + ((getBuyerMobile() == null) ? 0 : getBuyerMobile().hashCode());
        result = prime * result + ((getVisitType() == null) ? 0 : getVisitType().hashCode());
        result = prime * result + ((getVisitTime() == null) ? 0 : getVisitTime().hashCode());
        result = prime * result + ((getRealVisitTime() == null) ? 0 : getRealVisitTime().hashCode());
        result = prime * result + ((getGoodsTitle() == null) ? 0 : getGoodsTitle().hashCode());
        result = prime * result + ((getDoneTime() == null) ? 0 : getDoneTime().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
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
        sb.append(", storeAdminId=").append(storeAdminId);
        sb.append(", adminName=").append(adminName);
        sb.append(", storeId=").append(storeId);
        sb.append(", storeName=").append(storeName);
        sb.append(", buyerId=").append(buyerId);
        sb.append(", buyerName=").append(buyerName);
        sb.append(", buyerMobile=").append(buyerMobile);
        sb.append(", visitType=").append(visitType);
        sb.append(", visitTime=").append(visitTime);
        sb.append(", realVisitTime=").append(realVisitTime);
        sb.append(", goodsTitle=").append(goodsTitle);
        sb.append(", doneTime=").append(doneTime);
        sb.append(", remark=").append(remark);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
