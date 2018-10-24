package com.jk51.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class BMarketingPlan {
    private Integer id;

    private Integer siteId;

    private String name;

    private String description;

    private Integer type;

    private Integer style;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    private Integer defaultNum;

    private Integer addCondition;

    private String addConditionInfo;

    private Integer addNum;

    private Integer status;

    private Boolean stop;

    private Boolean isDel;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private List<PrizesSetting> prizesSettingList;

    private Date serverTime;

    private Integer operatorType;

    private Integer operatorId;

    private Integer pageNum;

    private Integer pageSize;

    private Integer ceilingSum;

    private Integer receiveSum;

    private Integer winnersSum;

    private Integer couponActivityId;

    private String flag;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStyle() {
        return style;
    }

    public void setStyle(Integer style) {
        this.style = style;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getDefaultNum() {
        return defaultNum;
    }

    public void setDefaultNum(Integer defaultNum) {
        this.defaultNum = defaultNum;
    }

    public Integer getAddCondition() {
        return addCondition;
    }

    public void setAddCondition(Integer addCondition) {
        this.addCondition = addCondition;
    }

    public String getAddConditionInfo() {
        return addConditionInfo;
    }

    public void setAddConditionInfo(String addConditionInfo) {
        this.addConditionInfo = addConditionInfo;
    }

    public Integer getAddNum() {
        return addNum;
    }

    public void setAddNum(Integer addNum) {
        this.addNum = addNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getStop() {
        return stop;
    }

    public void setStop(Boolean stop) {
        this.stop = stop;
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

    public List<PrizesSetting> getPrizesSettingList() {
        return prizesSettingList;
    }

    public void setPrizesSettingList(List<PrizesSetting> prizesSettingList) {
        this.prizesSettingList = prizesSettingList;
    }

    public Date getServerTime() {
        return serverTime;
    }

    public void setServerTime(Date serverTime) {
        this.serverTime = serverTime;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCeilingSum() {
        return ceilingSum;
    }

    public void setCeilingSum(Integer ceilingSum) {
        this.ceilingSum = ceilingSum;
    }

    public Integer getReceiveSum() {
        return receiveSum;
    }

    public void setReceiveSum(Integer receiveSum) {
        this.receiveSum = receiveSum;
    }

    public Integer getWinnersSum() {
        return winnersSum;
    }

    public void setWinnersSum(Integer winnersSum) {
        this.winnersSum = winnersSum;
    }

    public Integer getCouponActivityId() {
        return couponActivityId;
    }

    public void setCouponActivityId(Integer couponActivityId) {
        this.couponActivityId = couponActivityId;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "BMarketingPlan{" +
            "id=" + id +
            ", siteId=" + siteId +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", type=" + type +
            ", style=" + style +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", defaultNum=" + defaultNum +
            ", addCondition=" + addCondition +
            ", addConditionInfo='" + addConditionInfo + '\'' +
            ", addNum=" + addNum +
            ", status=" + status +
            ", stop=" + stop +
            ", isDel=" + isDel +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", prizesSettingList=" + prizesSettingList +
            ", serverTime=" + serverTime +
            ", operatorType=" + operatorType +
            ", operatorId=" + operatorId +
            ", pageNum=" + pageNum +
            ", pageSize=" + pageSize +
            ", ceilingSum=" + ceilingSum +
            ", receiveSum=" + receiveSum +
            ", winnersSum=" + winnersSum +
            ", couponActivityId=" + couponActivityId +
            ", flag='" + flag + '\'' +
            '}';
    }

    public static class PrizesSetting {
        private Integer id;

        private Integer type;

        private Integer typeId;

        private String typeInfo;

        private Float chances;

        private Integer ceiling;

        private Integer receive;

        private String info;

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

        public Float getChances() {
            return chances;
        }

        public void setChances(Float chances) {
            this.chances = chances;
        }

        public Integer getCeiling() {
            return ceiling;
        }

        public void setCeiling(Integer ceiling) {
            this.ceiling = ceiling;
        }

        public Integer getReceive() {
            return receive;
        }

        public void setReceive(Integer receive) {
            this.receive = receive;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        @Override
        public String toString() {
            return "PrizesSetting{" +
                "id=" + id +
                ", type=" + type +
                ", typeId=" + typeId +
                ", typeInfo='" + typeInfo + '\'' +
                ", chances=" + chances +
                ", ceiling=" + ceiling +
                ", receive=" + receive +
                ", info='" + info + '\'' +
                '}';
        }
    }
}
