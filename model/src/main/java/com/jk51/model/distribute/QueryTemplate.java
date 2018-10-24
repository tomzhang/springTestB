package com.jk51.model.distribute;

import com.jk51.model.order.Page;

import java.util.Date;

/**
 * Created by guosheng on 2017/4/19.
 */
public class QueryTemplate extends Page{
    private Integer id;

    private String name;

    private Byte type;

    private Byte accordingType;

    private Byte isUsed;

    private Byte useType;

    private Integer owner;

    private Date createTime;

    private Date updateTime;

    private String reward;

    private String discount;

    private Integer siteId;

    private Integer goodDistributeNum;

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward == null ? null : reward.trim();
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount == null ? null : discount.trim();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Byte getAccordingType() {
        return accordingType;
    }

    public void setAccordingType(Byte accordingType) {
        this.accordingType = accordingType;
    }

    public Byte getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Byte isUsed) {
        this.isUsed = isUsed;
    }

    public Byte getUseType() {
        return useType;
    }

    public void setUseType(Byte useType) {
        this.useType = useType;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
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

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getGoodDistributeNum() {
        return goodDistributeNum;
    }

    public void setGoodDistributeNum(Integer goodDistributeNum) {
        this.goodDistributeNum = goodDistributeNum;
    }
}
