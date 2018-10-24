package com.jk51.model.account.requestParams;

import com.jk51.model.order.Page;

/**
 * Created by xiapeng on 2017/8/31.
 */
public class AccountRun extends Page{
        private String merchantName;
        private String sellerId;
        private String payType;
        private String tradesId;
        private String financeNo;
        private String payStatus;
        private String createStartTime;
        private String createEndTime;
        private String payStartTime;
        private String payEndTime;
        private String dealStartTime;
        private String dealEndTime;
        private String moneyStartTime;
        private String moneyEndTime;

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }

    public String getFinanceNo() {
        return financeNo;
    }

    public void setFinanceNo(String financeNo) {
        this.financeNo = financeNo;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getCreateStartTime() {
        return createStartTime;
    }

    public void setCreateStartTime(String createStartTime) {
        this.createStartTime = createStartTime;
    }

    public String getCreateEndTime() {
        return createEndTime;
    }

    public void setCreateEndTime(String createEndTime) {
        this.createEndTime = createEndTime;
    }

    public String getPayStartTime() {
        return payStartTime;
    }

    public void setPayStartTime(String payStartTime) {
        this.payStartTime = payStartTime;
    }

    public String getPayEndTime() {
        return payEndTime;
    }

    public void setPayEndTime(String payEndTime) {
        this.payEndTime = payEndTime;
    }

    public String getDealStartTime() {
        return dealStartTime;
    }

    public void setDealStartTime(String dealStartTime) {
        this.dealStartTime = dealStartTime;
    }

    public String getDealEndTime() {
        return dealEndTime;
    }

    public void setDealEndTime(String dealEndTime) {
        this.dealEndTime = dealEndTime;
    }

    public String getMoneyStartTime() {
        return moneyStartTime;
    }

    public void setMoneyStartTime(String moneyStartTime) {
        this.moneyStartTime = moneyStartTime;
    }

    public String getMoneyEndTime() {
        return moneyEndTime;
    }

    public void setMoneyEndTime(String moneyEndTime) {
        this.moneyEndTime = moneyEndTime;
    }
}
