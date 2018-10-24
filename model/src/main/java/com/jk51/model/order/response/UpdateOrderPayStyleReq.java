package com.jk51.model.order.response;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: baixiongfei
 * 创建日期: 2017/3/30
 * 修改记录:
 */
public class UpdateOrderPayStyleReq {

    private Integer siteId;//商家ID

    private String tradesId;//订单号

    /**
     * 支付方式
     * ali(支付宝)，wx(微信)，bil(快钱)，unionPay(银联)，health_insurance(医保)，cash(现金)
     */
    private String payStyle;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }

    public String getPayStyle() {
        return payStyle;
    }

    public void setPayStyle(String payStyle) {
        this.payStyle = payStyle;
    }

    @Override
    public String toString() {
        return "UpdateOrderPayStyleReq{" +
                "siteId=" + siteId +
                ", tradesId='" + tradesId + '\'' +
                ", payStyle='" + payStyle + '\'' +
                '}';
    }
}
