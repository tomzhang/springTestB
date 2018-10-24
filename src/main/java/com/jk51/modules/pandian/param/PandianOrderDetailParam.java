package com.jk51.modules.pandian.param;

import java.util.Set;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-06
 * 修改记录:
 */
public class PandianOrderDetailParam {

    private String pandian_num;
    private Integer siteId;
    private Integer storeId;
    private Integer profitOrLossStatu;
    private Integer pageNum;
    private Integer pageSize;
    private String goods_code; //商品编码
    private String drug_name; //商品名称
    private String goods_company; //生产厂家
    private Integer inventory_confirm; //盘点确认flag
    private String checkerName;
    private Set<Integer> checkerStoreAdminId;
    private Integer isInventoryed;

    public String getGoods_company() {
        return goods_company;
    }

    public void setGoods_company(String goods_company) {
        this.goods_company = goods_company;
    }

    public String getPandian_num() {
        return pandian_num;
    }

    public void setPandian_num(String pandian_num) {
        this.pandian_num = pandian_num;
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

    public Integer getProfitOrLossStatu() {
        return profitOrLossStatu;
    }

    public void setProfitOrLossStatu(Integer profitOrLossStatu) {
        this.profitOrLossStatu = profitOrLossStatu;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getGoods_code() {
        return goods_code;
    }

    public void setGoods_code(String goods_code) {
        this.goods_code = goods_code;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public Integer getInventory_confirm() {
        return inventory_confirm;
    }

    public void setInventory_confirm(Integer inventory_confirm) {
        this.inventory_confirm = inventory_confirm;
    }

    public String getCheckerName() {
        return checkerName;
    }

    public void setCheckerName(String checkerName) {
        this.checkerName = checkerName;
    }

    public Set<Integer> getCheckerStoreAdminId() {
        return checkerStoreAdminId;
    }

    public void setCheckerStoreAdminId(Set<Integer> checkerStoreAdminId) {
        this.checkerStoreAdminId = checkerStoreAdminId;
    }

    public Integer getIsInventoryed() {
        return isInventoryed;
    }

    public void setIsInventoryed(Integer isInventoryed) {
        this.isInventoryed = isInventoryed;
    }
}
