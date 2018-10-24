package com.jk51.modules.coupon.request;

import com.jk51.model.order.Page;

/**
 * Created by Administrator on 2017/6/12.
 */
public class CouponGoods extends Page {

    private String siteId;

    private String ruleId;

    private String goodsName;

    private String[] list;

    private Integer type; //优惠活动跳商品和优惠券跳商品两个合为一个接口 -1时为优惠活动的跳转商品列表  null或是""时为优惠商城跳转的商品列表


    public String[] getList() {
        return list;
    }

    public void setList(String[] list) {
        this.list = list;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}
