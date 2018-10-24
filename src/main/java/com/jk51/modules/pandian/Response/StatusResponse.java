package com.jk51.modules.pandian.Response;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-03
 * 修改记录:
 */
public class StatusResponse {

    private String pandian_num;
    private String storeName;
    private String errorMessage;
    private Integer storeId;
    private Integer siteId;


    public String getPandian_num() {
        return pandian_num;
    }

    public void setPandian_num(String pandian_num) {
        this.pandian_num = pandian_num;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatusResponse response = (StatusResponse) o;

        if (!pandian_num.equals(response.pandian_num)) return false;
        if (!storeId.equals(response.storeId)) return false;
        return siteId.equals(response.siteId);
    }

    @Override
    public int hashCode() {
        int result = pandian_num.hashCode();
        result = 31 * result + storeId.hashCode();
        result = 31 * result + siteId.hashCode();
        return result;
    }
}
