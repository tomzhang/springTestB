package com.jk51.modules.pandian.Response;

import com.jk51.modules.pandian.param.StoreOrderStatus;

import java.util.List;

public class PandianUploadStatus {
    private Integer id;
    private Integer planId;
    private Integer uploadType;
    private List<StoreOrderStatus> stores;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getUploadType() {
        return uploadType;
    }

    public void setUploadType(Integer uploadType) {
        this.uploadType = uploadType;
    }

    public List<StoreOrderStatus> getStores() {
        return stores;
    }

    public void setStores(List<StoreOrderStatus> stores) {
        this.stores = stores;
    }

    @Override
    public String toString() {
        return "PandianUploadStatus{" +
            "id=" + id +
            ", planId=" + planId +
            ", uploadType=" + uploadType +
            ", stores=" + stores +
            '}';
    }
}
