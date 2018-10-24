package com.jk51.modules.pandian.param;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018-01-08
 * 修改记录:
 */
public class RepeatInventoryForConditionParam {

    @NotNull(message = "pandian_num不能为空")
    private String pandian_num;
    private Integer storeAdminId;
    private String drug_name;
    private String goods_code;
    private String bar_code;
    private Integer siteId;
    private Set<String> goodsCodes = new HashSet();

    public String getPandian_num() {
        return pandian_num;
    }

    public void setPandian_num(String pandian_num) {
        this.pandian_num = pandian_num;
    }

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getGoods_code() {
        return goods_code;
    }

    public void setGoods_code(String goods_code) {
        this.goods_code = goods_code;
    }

    public String getBar_code() {
        return bar_code;
    }

    public void setBar_code(String bar_code) {
        this.bar_code = bar_code;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Set<String> getGoodsCodes() {
        return goodsCodes;
    }

    public void setGoodsCodes(Set<String> goodsCodes) {
        this.goodsCodes = goodsCodes;
    }
}
