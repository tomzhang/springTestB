package com.jk51.model.order.response;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:创建订单响应信息
 * 作者: baixiongfei
 * 创建日期: 2017/2/24
 * 修改记录:
 */
public class OrderResponse {

    private String code;//响应码

    private String message;//响应码描述

    private String value;

    /**
     * 下面两个参数主要用作订单下完单之后，发起支付时使用
     */
    private String siteId;//商户ID

    private String tradesId;//交易ID

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }
    private int isServiceOrder;

    public void setIsServiceOrder(int isServiceOrder) {
        this.isServiceOrder = isServiceOrder;
    }

    public int getIsServiceOrder() {
        return isServiceOrder;
    }
    @Override
    public String toString() {
        return "OrderResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", value='" + value + '\'' +
                ", siteId='" + siteId + '\'' +
                ", tradesId='" + tradesId + '\'' +
                ", isServiceOrder='" + isServiceOrder + '\'' +
                '}';
    }
}
