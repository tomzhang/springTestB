package com.jk51.model.task;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class YbJkexcrecord implements Serializable {
    private Integer id;

    private Integer siteId;

    private Integer storeId;

    private Integer storeAdminId;

    /**
     * 订单号
     */
    private Long excId;

    /**
     * 钱
     */
    private String money;

    /**
     * 健康豆
     */
    private String jkd;

    /**
     * 是否兑换
     */
    private Boolean isUse;

    /**
     * 是否有效 10 有效 20 无效
     */
    private Byte status;

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

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public Long getExcId() {
        return excId;
    }

    public void setExcId(Long excId) {
        this.excId = excId;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getJkd() {
        return jkd;
    }

    public void setJkd(String jkd) {
        this.jkd = jkd;
    }

    public Boolean getIsUse() {
        return isUse;
    }

    public void setIsUse(Boolean isUse) {
        this.isUse = isUse;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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
        YbJkexcrecord other = (YbJkexcrecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSiteId() == null ? other.getSiteId() == null : this.getSiteId().equals(other.getSiteId()))
            && (this.getStoreId() == null ? other.getStoreId() == null : this.getStoreId().equals(other.getStoreId()))
            && (this.getStoreAdminId() == null ? other.getStoreAdminId() == null : this.getStoreAdminId().equals(other.getStoreAdminId()))
            && (this.getExcId() == null ? other.getExcId() == null : this.getExcId().equals(other.getExcId()))
            && (this.getMoney() == null ? other.getMoney() == null : this.getMoney().equals(other.getMoney()))
            && (this.getJkd() == null ? other.getJkd() == null : this.getJkd().equals(other.getJkd()))
            && (this.getIsUse() == null ? other.getIsUse() == null : this.getIsUse().equals(other.getIsUse()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSiteId() == null) ? 0 : getSiteId().hashCode());
        result = prime * result + ((getStoreId() == null) ? 0 : getStoreId().hashCode());
        result = prime * result + ((getStoreAdminId() == null) ? 0 : getStoreAdminId().hashCode());
        result = prime * result + ((getExcId() == null) ? 0 : getExcId().hashCode());
        result = prime * result + ((getMoney() == null) ? 0 : getMoney().hashCode());
        result = prime * result + ((getJkd() == null) ? 0 : getJkd().hashCode());
        result = prime * result + ((getIsUse() == null) ? 0 : getIsUse().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
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
        sb.append(", storeId=").append(storeId);
        sb.append(", storeAdminId=").append(storeAdminId);
        sb.append(", excId=").append(excId);
        sb.append(", money=").append(money);
        sb.append(", jkd=").append(jkd);
        sb.append(", isUse=").append(isUse);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}