package com.jk51.modules.integral.model;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-06-08 18:00
 * 修改记录:
 */
public class IntegralGoodsDetail {

    public Integer siteId;
    public Integer goodsId;
    public Date startTime;
    public Date endTime;
    public Integer startTimeInterval;
    public Integer endTimeInterval;
    public String drugName;
    public String comName;
    public String goodsTitle;
    public String goodsDesc;

    public String timeMessage;
    public Integer openStatus;

//    public String goodsIndications;
//    public String goodsUseMethod;
//    public String goodsContd;
//    public String goodsNote;
//    public String mainIngredient;
//    public String goodsAction;
//    public String adverseReactioins;
//    public String goodsDeposit;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
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

    public Integer getStartTimeInterval() {
        return startTimeInterval;
    }

    public void setStartTimeInterval(Integer startTimeInterval) {
        this.startTimeInterval = startTimeInterval;
    }

    public Integer getEndTimeInterval() {
        return endTimeInterval;
    }

    public void setEndTimeInterval(Integer endTimeInterval) {
        this.endTimeInterval = endTimeInterval;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public String getTimeMessage() {
        return timeMessage;
    }

    public void setTimeMessage(String timeMessage) {
        this.timeMessage = timeMessage;
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }
}
