package com.jk51.modules.pandian.param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-10-31
 * 修改记录:
 */
public class ClerkParam {

    private Integer siteId;
    private Integer storeId;
    private Integer storeAdminId;

    public ClerkParam(int siteId,int storeId,int storeAdminId){
        this.siteId = siteId;
        this.storeId = storeId;
        this.setStoreAdminId(storeAdminId);
    }

    public ClerkParam(){

    }

    public static ClerkParam getClerkParam(Integer siteId,Integer storeId,Integer storeAdminId){
        ClerkParam result =  new ClerkParam();
        result.setSiteId(siteId);
        result.setStoreId(storeId);
        result.setStoreAdminId(storeAdminId);
        return result;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
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
}
