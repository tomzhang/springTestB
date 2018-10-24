package com.jk51.model.order.response;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: baixiongfei
 * 创建日期: 2017/3/15
 * 修改记录:
 */
public class BeforeOrderResponse {

    //订单的最初价格
    private int orderOriginalPrice;

    //订单的运费
    private int orderFreight;

    //积分抵扣价格
    private int integralDeductionPrice;

    //用户存量积分
    private int integral;

    //优惠券抵扣金额
    private int couponDeductionPrice;

    //最终的订单金额
    private int orderRealPrice;

    public int getOrderOriginalPrice() {
        return orderOriginalPrice;
    }

    public void setOrderOriginalPrice(int orderOriginalPrice) {
        this.orderOriginalPrice = orderOriginalPrice;
    }

    public int getOrderFreight() {
        return orderFreight;
    }

    public void setOrderFreight(int orderFreight) {
        this.orderFreight = orderFreight;
    }

    public int getIntegralDeductionPrice() {
        return integralDeductionPrice;
    }

    public void setIntegralDeductionPrice(int integralDeductionPrice) {
        this.integralDeductionPrice = integralDeductionPrice;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public int getCouponDeductionPrice() {
        return couponDeductionPrice;
    }

    public void setCouponDeductionPrice(int couponDeductionPrice) {
        this.couponDeductionPrice = couponDeductionPrice;
    }

    public int getOrderRealPrice() {
        return orderRealPrice;
    }

    public void setOrderRealPrice(int orderRealPrice) {
        this.orderRealPrice = orderRealPrice;
    }

    @Override
    public String toString() {
        return "BeforeOrderResponse{" +
                "orderOriginalPrice=" + orderOriginalPrice +
                ", orderFreight=" + orderFreight +
                ", integralDeductionPrice=" + integralDeductionPrice +
                ", integral=" + integral +
                ", couponDeductionPrice=" + couponDeductionPrice +
                ", orderRealPrice=" + orderRealPrice +
                '}';
    }
}
