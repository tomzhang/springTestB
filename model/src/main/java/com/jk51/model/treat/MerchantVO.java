package com.jk51.model.treat;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:商家列表展示数据
 * 作者: chen
 * 创建日期: 2017-03-07
 * 修改记录:
 */
public class MerchantVO {
    private Integer id;
    private Integer merchantId;
    private String merchantName;
    private String companyName;
    private String logUrl;
    private String shopUrl;
    private Date createTime;
    private Integer siteStatus;
    private String setValue;
    private Integer payDayValue;
    private Integer setType;
    private Date thelastTime;

    @DateTimeFormat(pattern="yyyy-MM-dd ")
    @JsonFormat(pattern="yyyy-MM-dd ",timezone = "GMT+8")
    public Date getThelastTime() {
        return thelastTime;
    }

    public void setThelastTime(Date thelastTime) {
        this.thelastTime = thelastTime;
    }

    public MerchantVO(){}



    public String getLogUrl() {
        return logUrl;
    }

    public void setLogUrl(String logUrl) {
        this.logUrl = logUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getShopUrl() {

        if(shopUrl.contains("http://")) return shopUrl;
        return "http://"+shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        if(shopUrl.contains("http://")) this.shopUrl = shopUrl;
        else this.shopUrl = "http://"+shopUrl;
    }

    @DateTimeFormat(pattern="yyyy-MM-dd ")
    @JsonFormat(pattern="yyyy-MM-dd ",timezone = "GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date create_time) {
        this.createTime = create_time;
    }

    public Integer getSiteStatus() {
        return siteStatus;
    }

    public void setSiteStatus(Integer siteStatus) {
        this.siteStatus = siteStatus;
    }

    public String getSetValue() {
        return setValue;
    }

    public void setSetValue(String setValue) {
        this.setValue = setValue;
    }

    public Integer getPayDayValue() {
        return payDayValue;
    }

    public void setPayDayValue(Integer payDayValue) {
        this.payDayValue = payDayValue;
    }

    public Integer getSetType() {
        return setType;
    }

    public void setSetType(Integer setType) {
        this.setType = setType;
    }
}
