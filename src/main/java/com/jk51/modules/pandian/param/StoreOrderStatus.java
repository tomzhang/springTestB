package com.jk51.modules.pandian.param;

public class StoreOrderStatus {
    private Integer storeId;

    private String storesNumber;

    private Integer status;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoresNumber() {
        return storesNumber;
    }

    public void setStoresNumber(String storesNumber) {
        this.storesNumber = storesNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StoreOrderStatus{" +
            "storeId=" + storeId +
            ", storesNumber='" + storesNumber + '\'' +
            ", status=" + status +
            '}';
    }
}
