package com.jk51.modules.merchant.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.validation.constraints.NotNull;

// 忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
// 驼峰命名
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class QRcodeTips {
    @NotNull
    protected Integer siteId;
    @NotNull
    protected String qrcodeTips;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getQrcodeTips() {
        return qrcodeTips;
    }

    public void setQrcodeTips(String qrcodeTips) {
        this.qrcodeTips = qrcodeTips;
    }
}
