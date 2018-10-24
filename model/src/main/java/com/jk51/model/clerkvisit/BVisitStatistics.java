package com.jk51.model.clerkvisit;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class BVisitStatistics implements Serializable {
    private Integer id;

    private Integer siteId;

    /**
     * 活动ID
     */
    private Integer activityId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 应访会员数
     */
    private Integer memberNum;

    /**
     * 实访会员数
     */
    private Integer realMemberNum;

    /**
     * 门店数
     */
    private Integer storeNum;

    /**
     * 店员数
     */
    private Integer clerkNum;

    /**
     * 回访中店员下单数
     */
    private Integer tradeNum;

    /**
     * 回访中店员发券数
     */
    private Integer sendNum;

    /**
     * 发券核销数
     */
    private Integer sendUsedNum;

    /**
     * 短信发送数
     */
    private Integer smsNum;

    /**
     * 页面打开数
     */
    private Integer pageOpenNum;

    /**
     * 活动期间回访顾客购买活动商品数
     */
    private Integer goodsNum;

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

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(Integer memberNum) {
        this.memberNum = memberNum;
    }

    public Integer getRealMemberNum() {
        return realMemberNum;
    }

    public void setRealMemberNum(Integer realMemberNum) {
        this.realMemberNum = realMemberNum;
    }

    public Integer getStoreNum() {
        return storeNum;
    }

    public void setStoreNum(Integer storeNum) {
        this.storeNum = storeNum;
    }

    public Integer getClerkNum() {
        return clerkNum;
    }

    public void setClerkNum(Integer clerkNum) {
        this.clerkNum = clerkNum;
    }

    public Integer getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(Integer tradeNum) {
        this.tradeNum = tradeNum;
    }

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    public Integer getSendUsedNum() {
        return sendUsedNum;
    }

    public void setSendUsedNum(Integer sendUsedNum) {
        this.sendUsedNum = sendUsedNum;
    }

    public Integer getSmsNum() {
        return smsNum;
    }

    public void setSmsNum(Integer smsNum) {
        this.smsNum = smsNum;
    }

    public Integer getPageOpenNum() {
        return pageOpenNum;
    }

    public void setPageOpenNum(Integer pageOpenNum) {
        this.pageOpenNum = pageOpenNum;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
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
