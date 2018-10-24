package com.jk51.modules.erpprice.domain.pojo;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author
 */
public class ErpSettingPO {
    private Integer storeId;
    private Integer areaCode;
    private Integer priority;
    private Byte storesStatus;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(Integer areaCode) {
        this.areaCode = areaCode;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Byte getStoresStatus() {
        return storesStatus;
    }

    public void setStoresStatus(Byte storesStatus) {
        this.storesStatus = storesStatus;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
