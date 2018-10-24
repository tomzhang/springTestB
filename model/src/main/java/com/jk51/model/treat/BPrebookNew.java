package com.jk51.model.treat;

import java.util.Date;

/**
 * 预约列表JavaBean
 *
 * @auhter zy
 * @create 2018-03-05 17:27
 */
public class BPrebookNew {

    private Integer siteId;

    private String prebookNumber;

    private String prebookTrades;

    private String prebookPhone;

    private Integer prebookGoodsId;

    private String prebookGoodsName;

    private Integer prebookGoodsNum;

    private Integer prebookStyle;

    private String prebookAddress;

    private Integer prebookStatus;

    private Date prebookTime;

    private Date createTime;

    private Date updateTime;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getPrebookNumber() {
        return prebookNumber;
    }

    public void setPrebookNumber(String prebookNumber) {
        this.prebookNumber = prebookNumber;
    }

    public String getPrebookTrades() {
        return prebookTrades;
    }

    public void setPrebookTrades(String prebookTrades) {
        this.prebookTrades = prebookTrades;
    }

    public String getPrebookPhone() {
        return prebookPhone;
    }

    public void setPrebookPhone(String prebookPhone) {
        this.prebookPhone = prebookPhone;
    }

    public Integer getPrebookGoodsId() {
        return prebookGoodsId;
    }

    public void setPrebookGoodsId(Integer prebookGoodsId) {
        this.prebookGoodsId = prebookGoodsId;
    }

    public String getPrebookGoodsName() {
        return prebookGoodsName;
    }

    public void setPrebookGoodsName(String prebookGoodsName) {
        this.prebookGoodsName = prebookGoodsName;
    }

    public Integer getPrebookGoodsNum() {
        return prebookGoodsNum;
    }

    public void setPrebookGoodsNum(Integer prebookGoodsNum) {
        this.prebookGoodsNum = prebookGoodsNum;
    }

    public Integer getPrebookStyle() {
        return prebookStyle;
    }

    public void setPrebookStyle(Integer prebookStyle) {
        this.prebookStyle = prebookStyle;
    }

    public String getPrebookAddress() {
        return prebookAddress;
    }

    public void setPrebookAddress(String prebookAddress) {
        this.prebookAddress = prebookAddress;
    }

    public Integer getPrebookStatus() {
        return prebookStatus;
    }

    public void setPrebookStatus(Integer prebookStatus) {
        this.prebookStatus = prebookStatus;
    }

    public Date getPrebookTime() {
        return prebookTime;
    }

    public void setPrebookTime(Date prebookTime) {
        this.prebookTime = prebookTime;
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
    public String toString() {
        return "BPrebookNew{" +
            "siteId=" + siteId +
            ", prebookNumber=" + prebookNumber +
            ", prebookPhone=" + prebookPhone +
            ", prebookGoodsId=" + prebookGoodsId +
            ", prebookGoodsName='" + prebookGoodsName + '\'' +
            ", prebookGoodsNum=" + prebookGoodsNum +
            ", prebookStyle=" + prebookStyle +
            ", prebookAddress='" + prebookAddress + '\'' +
            ", prebookStatus='" + prebookStatus + '\'' +
            ", prebookTime=" + prebookTime +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            '}';
    }

}
