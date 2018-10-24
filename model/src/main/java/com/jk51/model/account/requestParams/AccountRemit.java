package com.jk51.model.account.requestParams;

import com.jk51.model.order.Page;

/**
 * Created by luf on 2017/7/27.
 */
public class AccountRemit extends Page{
    private String merchantName;
    private Integer merchantId;
    private String payStyle;
    private String remitAccountStatus;
    private String accountCheckingStatus;
    private long tradesId;
    private String payNumber;
    private String payStartTime;
    private String payEndTime;
    private String accountStatus;

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getPayStyle() {
        return payStyle;
    }

    public void setPayStyle(String payStyle) {
        this.payStyle = payStyle;
    }

    public String getRemitAccountStatus() {
        return remitAccountStatus;
    }

    public void setRemitAccountStatus(String remitAccountStatus) {
        this.remitAccountStatus = remitAccountStatus;
    }

    public String getAccountCheckingStatus() {
        return accountCheckingStatus;
    }

    public void setAccountCheckingStatus(String accountCheckingStatus) {
        this.accountCheckingStatus = accountCheckingStatus;
    }

    public long getTradesId() {
        return tradesId;
    }

    public void setTradesId(long tradesId) {
        this.tradesId = tradesId;
    }

    public String getPayNumber() {
        return payNumber;
    }

    public void setPayNumber(String payNumber) {
        this.payNumber = payNumber;
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

}
