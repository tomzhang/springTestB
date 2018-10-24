package com.jk51.modules.offline.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-08-02
 * 修改记录:
 */
public class ErpMerchantUtils {

    private Integer id;
    private Integer siteId;
    private String mobiles;
    private String emails;
    private String erpUrl;
    private Integer trades;
    private Integer storage;
    private Integer member;
    private Integer price;
    private Integer pandian;
    private Integer status;

    public ErpMerchantUtils() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getMobiles() {
        return mobiles;
    }

    public void setMobiles(String mobiles) {
        this.mobiles = mobiles;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public Integer getTrades() {
        return trades;
    }

    public void setTrades(Integer trades) {
        this.trades = trades;
    }

    public Integer getStorage() {
        return storage;
    }

    public void setStorage(Integer storage) {
        this.storage = storage;
    }

    public Integer getMember() {
        return member;
    }

    public void setMember(Integer member) {
        this.member = member;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPandian() {
        return pandian;
    }

    public void setPandian(Integer pandian) {
        this.pandian = pandian;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErpUrl() {
        return erpUrl;
    }

    public void setErpUrl(String erpUrl) {
        this.erpUrl = erpUrl;
    }

    @Override
    public String toString() {
        return "ErpMerchantUtils{" +
            "id=" + id +
            ", siteId=" + siteId +
            ", mobiles='" + mobiles + '\'' +
            ", emails='" + emails + '\'' +
            ", erpUrl='" + erpUrl + '\'' +
            ", trades=" + trades +
            ", storage=" + storage +
            ", member=" + member +
            ", price=" + price +
            ", pandian=" + pandian +
            ", status=" + status +
            '}';
    }
}
