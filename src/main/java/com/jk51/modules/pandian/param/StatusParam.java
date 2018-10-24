package com.jk51.modules.pandian.param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-03
 * 修改记录:
 */
public class StatusParam {

    private String pandian_num;
    private Integer storeId;
    private Integer siteId;
    private Integer storeAdminId;

    public String getPandian_num() {
        return pandian_num;
    }

    public void setPandian_num(String pandian_num) {
        this.pandian_num = pandian_num;
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

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatusParam that = (StatusParam) o;

        if (!pandian_num.equals(that.pandian_num)) return false;
        if (!storeId.equals(that.storeId)) return false;
        if (!siteId.equals(that.siteId)) return false;
        return storeAdminId.equals(that.storeAdminId);
    }

    @Override
    public int hashCode() {
        int result = pandian_num.hashCode();
        result = 31 * result + storeId.hashCode();
        result = 31 * result + siteId.hashCode();
        result = 31 * result + storeAdminId.hashCode();
        return result;
    }
}
