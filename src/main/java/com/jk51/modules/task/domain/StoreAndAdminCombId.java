package com.jk51.modules.task.domain;

public class StoreAndAdminCombId {
    private Integer adminId;
    private Integer storeId;

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "StoreAndAdminCombId{" +
                "adminId=" + adminId +
                ", storeId=" + storeId +
                '}';
    }
}
