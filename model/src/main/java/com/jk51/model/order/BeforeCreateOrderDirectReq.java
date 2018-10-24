package com.jk51.model.order;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 店员帮助用户下门店直购订单前查询计算商品价格优惠信息时的入参
 * 作者: baixiongfei
 * 创建日期: 2017/3/15
 * 修改记录:
 */
public class BeforeCreateOrderDirectReq {

    private int siteId;//商家ID

    private int storesId;//门店ID

    private String mobile;//会员手机

    private String integralUse;//积分使用

    private String couponId;

    private List<OrderGoods> orderGoods;//商品信息

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getStoresId() {
        return storesId;
    }

    public void setStoresId(int storesId) {
        this.storesId = storesId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<OrderGoods> getOrderGoods() {
        return orderGoods;
    }

    public void setOrderGoods(List<OrderGoods> orderGoods) {
        this.orderGoods = orderGoods;
    }

    public String getIntegralUse() {
        return integralUse;
    }

    public void setIntegralUse(String integralUse) {
        this.integralUse = integralUse;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }
    @Override
    public String toString() {
        return "BeforeCreateOrderDirectReq{" +
                "siteId=" + siteId +
                ", storesId=" + storesId +
                ", mobile='" + mobile + '\'' +
                ", integralUse='" + integralUse + '\'' +
                ", couponId='" + couponId + '\'' +
                ", orderGoods=" + orderGoods +
                '}';
    }
}
