package com.jk51.model.account.requestParams;

import com.jk51.model.order.Page;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chenpeng
 * 创建日期: 2017/3/14
 * 修改记录:
 */
public class QueryOrderBill extends Page{

    private String sellerId;
    private String sellerName;
    private String payStyle;
    private Integer accountCheckingStatus;
    private Integer payCheckingStatus;
    private Integer refundCheckingStatus;
    private String tradesId;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getPayStyle() {
        return payStyle;
    }

    public void setPayStyle(String payStyle) {
        this.payStyle = payStyle;
    }

    public Integer getAccountCheckingStatus() {
        return accountCheckingStatus;
    }

    public void setAccountCheckingStatus(Integer accountCheckingStatus) {
        this.accountCheckingStatus = accountCheckingStatus;
    }

    public Integer getPayCheckingStatus() {
        return payCheckingStatus;
    }

    public void setPayCheckingStatus(Integer payCheckingStatus) {
        this.payCheckingStatus = payCheckingStatus;
    }

    public Integer getRefundCheckingStatus() {
        return refundCheckingStatus;
    }

    public void setRefundCheckingStatus(Integer refundCheckingStatus) {
        this.refundCheckingStatus = refundCheckingStatus;
    }

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }

}
