package com.jk51.modules.pandian.param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-10-30
 * 修改记录:
 */
public class InventoryConfirmInfo {

    private String pandian_num;
    private String goods_code;
    private Integer storeAdminId;


    public String getPandian_num() {
        return pandian_num;
    }

    public void setPandian_num(String pandian_num) {
        this.pandian_num = pandian_num;
    }

    public String getGoods_code() {
        return goods_code;
    }

    public void setGoods_code(String goods_code) {
        this.goods_code = goods_code;
    }

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    @Override
    public String toString() {
        return "InventoryConfirmInfo{" +
            "pandian_num='" + pandian_num + '\'' +
            ", goods_code='" + goods_code + '\'' +
            ", storeAdminId=" + storeAdminId +
            '}';
    }
}
