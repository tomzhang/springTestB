package com.jk51.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class BMarketingMember {
    private Integer id;

    private Integer siteId;

    private Integer memberId;

    private Integer marketingPlanId;

    private Integer addNum;

    private Integer drawNum;

    private Boolean isDel;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private List<Prizes> prizesList;

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

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getMarketingPlanId() {
        return marketingPlanId;
    }

    public void setMarketingPlanId(Integer marketingPlanId) {
        this.marketingPlanId = marketingPlanId;
    }

    public Integer getAddNum() {
        return addNum;
    }

    public void setAddNum(Integer addNum) {
        this.addNum = addNum;
    }

    public Integer getDrawNum() {
        return drawNum;
    }

    public void setDrawNum(Integer drawNum) {
        this.drawNum = drawNum;
    }

    public Boolean getDel() {
        return isDel;
    }

    public void setDel(Boolean del) {
        isDel = del;
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

    public List<Prizes> getPrizesList() {
        return prizesList;
    }

    public void setPrizesList(List<Prizes> prizesList) {
        this.prizesList = prizesList;
    }

    @Override
    public String toString() {
        return "BMarketingMember{" +
            "id=" + id +
            ", siteId=" + siteId +
            ", memberId=" + memberId +
            ", marketingPlanId=" + marketingPlanId +
            ", addNum=" + addNum +
            ", drawNum=" + drawNum +
            ", isDel=" + isDel +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", prizesList=" + prizesList +
            '}';
    }

    static class Prizes {
        private Integer id;

        private Integer type;

        private Integer typeId;

        private String typeInfo;

        private Integer tag;

        private String remark;

        private Integer status;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createTime;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getTypeId() {
            return typeId;
        }

        public void setTypeId(Integer typeId) {
            this.typeId = typeId;
        }

        public String getTypeInfo() {
            return typeInfo;
        }

        public void setTypeInfo(String typeInfo) {
            this.typeInfo = typeInfo;
        }

        public Integer getTag() {
            return tag;
        }

        public void setTag(Integer tag) {
            this.tag = tag;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        @Override
        public String toString() {
            return "Prizes{" +
                "id=" + id +
                ", type=" + type +
                ", typeId=" + typeId +
                ", typeInfo='" + typeInfo + '\'' +
                ", tag=" + tag +
                ", remark='" + remark + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
        }
    }
}
