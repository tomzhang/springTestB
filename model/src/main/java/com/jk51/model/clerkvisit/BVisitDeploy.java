package com.jk51.model.clerkvisit;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class BVisitDeploy implements Serializable {
    private Integer id;

    /**
     * 商户ID
     */
    private Integer siteId;

    /**
     * 回访任务ID
     */
    private Integer clerkVisitId;

    /**
     * 调配之前门店ID
     */
    private Integer preStoreId;

    /**
     * 调配之前店员ID
     */
    private Integer preAdminId;

    /**
     * 调配之前店员姓名
     */
    private String preAdminName;

    /**
     * 调配之后门店ID
     */
    private Integer storeId;

    /**
     * 调配之后店员ID
     */
    private Integer adminId;

    /**
     * 调配之后店员姓名
     */
    private String adminName;

    /**
     * 操作人ID
     */
    private Integer operatorId;

    /**
     * 操作人名字
     */
    private String operatorName;

    private Date createTime;

    private Date updateTime;

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

    public Integer getClerkVisitId() {
        return clerkVisitId;
    }

    public void setClerkVisitId(Integer clerkVisitId) {
        this.clerkVisitId = clerkVisitId;
    }

    public Integer getPreStoreId() {
        return preStoreId;
    }

    public void setPreStoreId(Integer preStoreId) {
        this.preStoreId = preStoreId;
    }

    public Integer getPreAdminId() {
        return preAdminId;
    }

    public void setPreAdminId(Integer preAdminId) {
        this.preAdminId = preAdminId;
    }

    public String getPreAdminName() {
        return preAdminName;
    }

    public void setPreAdminName(String preAdminName) {
        this.preAdminName = preAdminName;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
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
        BVisitDeploy other = (BVisitDeploy) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSiteId() == null ? other.getSiteId() == null : this.getSiteId().equals(other.getSiteId()))
            && (this.getClerkVisitId() == null ? other.getClerkVisitId() == null : this.getClerkVisitId().equals(other.getClerkVisitId()))
            && (this.getPreStoreId() == null ? other.getPreStoreId() == null : this.getPreStoreId().equals(other.getPreStoreId()))
            && (this.getPreAdminId() == null ? other.getPreAdminId() == null : this.getPreAdminId().equals(other.getPreAdminId()))
            && (this.getPreAdminName() == null ? other.getPreAdminName() == null : this.getPreAdminName().equals(other.getPreAdminName()))
            && (this.getStoreId() == null ? other.getStoreId() == null : this.getStoreId().equals(other.getStoreId()))
            && (this.getAdminId() == null ? other.getAdminId() == null : this.getAdminId().equals(other.getAdminId()))
            && (this.getAdminName() == null ? other.getAdminName() == null : this.getAdminName().equals(other.getAdminName()))
            && (this.getOperatorId() == null ? other.getOperatorId() == null : this.getOperatorId().equals(other.getOperatorId()))
            && (this.getOperatorName() == null ? other.getOperatorName() == null : this.getOperatorName().equals(other.getOperatorName()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSiteId() == null) ? 0 : getSiteId().hashCode());
        result = prime * result + ((getClerkVisitId() == null) ? 0 : getClerkVisitId().hashCode());
        result = prime * result + ((getPreStoreId() == null) ? 0 : getPreStoreId().hashCode());
        result = prime * result + ((getPreAdminId() == null) ? 0 : getPreAdminId().hashCode());
        result = prime * result + ((getPreAdminName() == null) ? 0 : getPreAdminName().hashCode());
        result = prime * result + ((getStoreId() == null) ? 0 : getStoreId().hashCode());
        result = prime * result + ((getAdminId() == null) ? 0 : getAdminId().hashCode());
        result = prime * result + ((getAdminName() == null) ? 0 : getAdminName().hashCode());
        result = prime * result + ((getOperatorId() == null) ? 0 : getOperatorId().hashCode());
        result = prime * result + ((getOperatorName() == null) ? 0 : getOperatorName().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
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
        sb.append(", clerkVisitId=").append(clerkVisitId);
        sb.append(", preStoreId=").append(preStoreId);
        sb.append(", preAdminId=").append(preAdminId);
        sb.append(", preAdminName=").append(preAdminName);
        sb.append(", storeId=").append(storeId);
        sb.append(", adminId=").append(adminId);
        sb.append(", adminName=").append(adminName);
        sb.append(", operatorId=").append(operatorId);
        sb.append(", operatorName=").append(operatorName);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}