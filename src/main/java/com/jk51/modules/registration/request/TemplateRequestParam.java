package com.jk51.modules.registration.request;

import com.jk51.model.order.Page;

/**
 * Created by mqq on 2017/4/20.
 */
public class TemplateRequestParam extends Page {
    private Integer storeId;//门店主键

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }
}
