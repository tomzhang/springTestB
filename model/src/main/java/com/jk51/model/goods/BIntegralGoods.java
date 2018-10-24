package com.jk51.model.goods;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 积分商品实体类
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-05-27 10:06
 * 修改记录:
 */
public class BIntegralGoods {

    //auto id
    private Long id;
    //the merchant id
    private Integer siteId;
    //the goods id
    private Integer goodsId;
    //The integral goods number of redemption
    private Integer num;
    //Whether to delete integral goods
    private Integer isDel;
    //Redeem the required points for the goods
    private Integer integralExchanges;
    //a group of the supply store ids
    private String storeIds;
    //status
    private Integer status;
    //the exchanging start time
    private Date startTime;
    //the exchanging end time
    private Date endTime;
    //the creating time
    private Date createTime;
    //the updating time
    private Date updateTime;
    //a few hours later started to exchange goods
    private Integer integralGoodsStartedByHours;
    //a few hours later ended to exchange goods
    private Integer integralGoodsEndedByHours;


    private Integer limitCount;

    private Integer limitEach;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getIntegralExchanges() {
        return integralExchanges;
    }

    public void setIntegralExchanges(Integer integralExchanges) {
        this.integralExchanges = integralExchanges;
    }

    public String getStoreIds() {
        return storeIds;
    }

    public void setStoreIds(String storeIds) {
        this.storeIds = storeIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getIntegralGoodsStartedByHours() {
        return integralGoodsStartedByHours;
    }

    public void setIntegralGoodsStartedByHours(Integer integralGoodsStartedByHours) {
        this.integralGoodsStartedByHours = integralGoodsStartedByHours;
    }

    public Integer getIntegralGoodsEndedByHours() {
        return integralGoodsEndedByHours;
    }

    public void setIntegralGoodsEndedByHours(Integer integralGoodsEndedByHours) {
        this.integralGoodsEndedByHours = integralGoodsEndedByHours;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Integer getLimitCount() {return limitCount;}

    public void setLimitCount(Integer limitCount) {this.limitCount = limitCount;}

    public Integer getLimitEach() {return limitEach;}

    public void setLimitEach(Integer limitEach) {this.limitEach = limitEach;}
}
