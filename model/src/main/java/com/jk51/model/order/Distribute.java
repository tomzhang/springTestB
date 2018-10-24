package com.jk51.model.order;

import java.util.Arrays;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:查询分单需要的入参信息
 * 作者: baixiongfei
 * 创建日期: 2017/2/16
 * 修改记录:
 */
public class Distribute {

    private int siteId;//站点ID

    private Integer buyerId;//买家(用户)唯一ID

    private String orderType;//订单类型，1：送货上门订单，2：门店自提订单

    private String userDeliveryProvince;//用户送货上门地址所在省或者直辖市，
    private Integer userDeliveryProvinceCode;//用户送货上门地址所在省或者直辖市的代码
    private String userDeliveryCity;//用户送货上门地址所在城市或者直辖市

    private String userDeliveryDistrict;//用户送货上门地址所在城市的区，比如：浦东新区
    private Integer userDeliveryCityCode;//用户送货上门地址所在城市或者直辖市的区的代码

    private String userDeliveryAddr;//用户在选择送货上门时候填写的收货地址

    private int userIntegral;//用户积分

    private int deliveryMethod;//商家快递方式，120(平邮),130(快递),140(EMS)

    private List<OrderGoods> orderGoods;//商品信息

    private int userCouponId;//使用的优惠券ID

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getUserDeliveryProvince() {
        return userDeliveryProvince;
    }

    public void setUserDeliveryProvince(String userDeliveryProvince) {
        this.userDeliveryProvince = userDeliveryProvince;
    }

    public Integer getUserDeliveryProvinceCode() {
        return userDeliveryProvinceCode;
    }

    public void setUserDeliveryProvinceCode(Integer userDeliveryProvinceCode) {
        this.userDeliveryProvinceCode = userDeliveryProvinceCode;
    }

    public String getUserDeliveryCity() {
        return userDeliveryCity;
    }

    public void setUserDeliveryCity(String userDeliveryCity) {
        this.userDeliveryCity = userDeliveryCity;
    }

    public Integer getUserDeliveryCityCode() {
        return userDeliveryCityCode;
    }

    public void setUserDeliveryCityCode(Integer userDeliveryCityCode) {
        this.userDeliveryCityCode = userDeliveryCityCode;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getUserDeliveryAddr() {
        return userDeliveryAddr;
    }

    public void setUserDeliveryAddr(String userDeliveryAddr) {
        this.userDeliveryAddr = userDeliveryAddr;
    }

    public int getUserIntegral() {
        return userIntegral;
    }

    public void setUserIntegral(int userIntegral) {
        this.userIntegral = userIntegral;
    }

    public int getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(int deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public List<OrderGoods> getOrderGoods() {
        return orderGoods;
    }

    public void setOrderGoods(List<OrderGoods> orderGoods) {
        this.orderGoods = orderGoods;
    }

    public int getUserCouponId() {
        return userCouponId;
    }

    public void setUserCouponId(int userCouponId) {
        this.userCouponId = userCouponId;
    }

    public String getUserDeliveryDistrict() {
        return userDeliveryDistrict;
    }

    public void setUserDeliveryDistrict(String userDeliveryDistrict) {
        this.userDeliveryDistrict = userDeliveryDistrict;
    }
    //地球平均半径
    private static final double EARTH_RADIUS = 6378137;
    //把经纬度转为度（°）
    private static double rad(double d){
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    public static int getDistance(double lng1, double lat1, double lng2, double lat2){
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(
                Math.sqrt(
                        Math.pow(Math.sin(a/2),2)
                                + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)
                )
        );
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return (int)s+1000;
    }

    @Override
    public String toString() {
        return "Distribute{" +
                "siteId=" + siteId +
                ", buyerId=" + buyerId +
                ", orderType='" + orderType + '\'' +
                ", userDeliveryProvince='" + userDeliveryProvince + '\'' +
                ", userDeliveryProvinceCode='" + userDeliveryProvinceCode + '\'' +
                ", userDeliveryCity='" + userDeliveryCity + '\'' +
                ", userDeliveryDistrict='" + userDeliveryDistrict + '\'' +
                ", userDeliveryCityCode='" + userDeliveryCityCode + '\'' +
                ", userDeliveryAddr='" + userDeliveryAddr + '\'' +
                ", userIntegral=" + userIntegral +
                ", deliveryMethod=" + deliveryMethod +
                ", orderGoods=" + orderGoods +
                ", userCouponId=" + userCouponId +
                '}';
    }
}
