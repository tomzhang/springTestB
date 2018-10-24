package com.jk51.model.coupon.requestParams;

import com.jk51.model.order.BeforeCreateOrderReq;
import com.jk51.model.order.GoodsInfo;

import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.model.coupon.requestParams.
 * author   :zw
 * date     :2017/3/7
 * Update   :
 * 描述     ：查询使用优惠券所提供的参数
 */
public class OrderMessageParams {
    private boolean isFirstOrder;//是否首单
    private Integer orderType;//订单类型
    private Integer applyChannel;//适用渠道
    private Integer storeId;//门店id
    private Integer postFee;//邮费
    private Integer orderFee;//实付金额 商品金额-积分+邮费
    /**
     * map数据如：goodsId：1234，num:2,goodsPrice:9000
     */
    private List<Map<String, Integer>> goodsInfo;
    private Integer areaId;//地址id，省的id
    private Integer couponId;//优惠券id
    private Integer userId;//用户id
    private Integer siteId;//商家id
    private List<GoodsInfo> goodsInfoInfos;//商品列表

    private BeforeCreateOrderReq beforeCreateOrderReq;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public boolean isFirstOrder() {
        return isFirstOrder;
    }

    public void setFirstOrder(boolean firstOrder) {
        isFirstOrder = firstOrder;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getApplyChannel() {
        return applyChannel;
    }

    public void setApplyChannel(Integer applyChannel) {
        this.applyChannel = applyChannel;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getPostFee() {
        return postFee;
    }

    public void setPostFee(Integer postFee) {
        this.postFee = postFee;
    }

    public Integer getOrderFee() {
        return orderFee;
    }

    public void setOrderFee(Integer orderFee) {
        this.orderFee = orderFee;
    }

    public List<Map<String, Integer>> getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(List<Map<String, Integer>> goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public BeforeCreateOrderReq getBeforeCreateOrderReq() {
        return beforeCreateOrderReq;
    }

    public void setBeforeCreateOrderReq(BeforeCreateOrderReq beforeCreateOrderReq) {
        this.beforeCreateOrderReq = beforeCreateOrderReq;
    }

    public List<GoodsInfo> getGoodsInfoInfos () {
        return goodsInfoInfos;
    }

    public void setGoodsInfoInfos (List<GoodsInfo> goodsInfoInfos) {
        this.goodsInfoInfos = goodsInfoInfos;
    }

    @Override
    public String toString() {
        return "OrderMessageParams{" +
                "isFirstOrder=" + isFirstOrder +
                ", orderType=" + orderType +
                ", applyChannel=" + applyChannel +
                ", storeId=" + storeId +
                ", postFee=" + postFee +
                ", orderFee=" + orderFee +
                ", goodsInfo=" + goodsInfo +
                ", areaId=" + areaId +
                ", couponId=" + couponId +
                ", userId=" + userId +
                ", siteId=" + siteId +
                '}';
    }
}
