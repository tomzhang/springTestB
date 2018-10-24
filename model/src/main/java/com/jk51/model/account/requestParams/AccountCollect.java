package com.jk51.model.account.requestParams;

import com.jk51.model.order.Page;

/**
 * Created by xiapeng on 2017/9/5.
 */
public class AccountCollect extends Page{
    private String merchantName;
    private String merchantId;
    private String payStartTime;
    private String payEndTime;
    private String remitStartTime;
    private String remitEndTime;

    public String getRemitStartTime() {
        return remitStartTime;
    }

    public void setRemitStartTime(String remitStartTime) {
        this.remitStartTime = remitStartTime;
    }

    public String getRemitEndTime() {
        return remitEndTime;
    }

    public void setRemitEndTime(String remitEndTime) {
        this.remitEndTime = remitEndTime;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
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
