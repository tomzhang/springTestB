package com.jk51.model.order;

import java.util.Date;

public class SBGoodsPrebook {
    private String prebookPhone;

    private Integer prebookGoodsId;

    private String prebookGoodsName;

    private Integer prebookGoodsNum;

    private Integer prebookClerkId;

    private String prebookClerk;

    private String prebookTrades;

    private Date prebookAcceptTime;

    private Date prebookTradesTime;

    private Integer prebookState;

    private Date createTime;

    private Date updateTime;
	
	private Integer prebookId;

    private Integer siteId;

    public Integer getPrebookId() {
        return prebookId;
    }

    public void setPrebookId(Integer prebookId) {
        this.prebookId = prebookId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getPrebookPhone() {
        return prebookPhone;
    }

    public void setPrebookPhone(String prebookPhone) {
        this.prebookPhone = prebookPhone == null ? null : prebookPhone.trim();
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
        this.prebookGoodsName = prebookGoodsName == null ? null : prebookGoodsName.trim();
    }

    public Integer getPrebookGoodsNum() {
        return prebookGoodsNum;
    }

    public void setPrebookGoodsNum(Integer prebookGoodsNum) {
        this.prebookGoodsNum = prebookGoodsNum;
    }

    public Integer getPrebookClerkId() {
        return prebookClerkId;
    }

    public void setPrebookClerkId(Integer prebookClerkId) {
        this.prebookClerkId = prebookClerkId;
    }

    public String getPrebookClerk() {
        return prebookClerk;
    }

    public void setPrebookClerk(String prebookClerk) {
        this.prebookClerk = prebookClerk == null ? null : prebookClerk.trim();
    }

    public String getPrebookTrades() {
        return prebookTrades;
    }

    public void setPrebookTrades(String prebookTrades) {
        this.prebookTrades = prebookTrades == null ? null : prebookTrades.trim();
    }

    public Date getPrebookAcceptTime() {
        return prebookAcceptTime;
    }

    public void setPrebookAcceptTime(Date prebookAcceptTime) {
        this.prebookAcceptTime = prebookAcceptTime;
    }

    public Date getPrebookTradesTime() {
        return prebookTradesTime;
    }

    public void setPrebookTradesTime(Date prebookTradesTime) {
        this.prebookTradesTime = prebookTradesTime;
    }

    public Integer getPrebookState() {
        return prebookState;
    }

    public void setPrebookState(Integer prebookState) {
        this.prebookState = prebookState;
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
}
