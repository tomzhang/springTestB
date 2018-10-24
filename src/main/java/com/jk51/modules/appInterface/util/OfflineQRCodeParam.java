package com.jk51.modules.appInterface.util;

public class OfflineQRCodeParam {
    private String tradesId;
    private Integer storeId;
    private Integer storeAdminId;
    private String wxShopDomain;

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public String getWxShopDomain() {
        return wxShopDomain;
    }

    public void setWxShopDomain(String wxShopDomain) {
        this.wxShopDomain = wxShopDomain;
    }

    @Override
    public String toString() {
        return "OfflineQRCodeParam{" +
            "tradesId='" + tradesId + '\'' +
            ", storeId=" + storeId +
            ", storeAdminId=" + storeAdminId +
            ", wxShopDomain='" + wxShopDomain + '\'' +
            '}';
    }
}
