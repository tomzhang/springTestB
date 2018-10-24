package com.jk51.model.promotions.activity;

/**
 * Created by Administrator on 2017/11/20.
 */
public class GroupPeopleNum {
    private Integer siteId;

    private Long tradesId;

    private Integer sumPeople;  //参团总人数

    private Integer payingPeople;  //已付款人数

    private Integer status;  //拼团状态

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPayingPeople() {
        return payingPeople;
    }

    public void setPayingPeople(Integer payingPeople) {
        this.payingPeople = payingPeople;
    }

    public Integer getSumPeople() {

        return sumPeople;
    }

    public void setSumPeople(Integer sumPeople) {
        this.sumPeople = sumPeople;
    }

    public Long getTradesId() {
        return tradesId;
    }

    public void setTradesId(Long tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getSiteId() {

        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
}
