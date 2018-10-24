package com.jk51.modules.erpprice.domain.pojo;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

public class GoodsErpPriceDTO {
    @NotNull(message = "siteId不能为空")
    private Integer siteId;
    @NotEmpty(message = "goodsIds不能为空")
    private List<Integer> goodsIds;
    @NotNull(message = "erpStoreId不能为空")
    private Integer erpStoreId;
    @NotNull(message = "erpAreaCode不能为空")
    private Integer erpAreaCode;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public List<Integer> getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(List<Integer> goodsIds) {
        this.goodsIds = goodsIds;
    }

    public Integer getErpStoreId() {
        return erpStoreId;
    }

    public void setErpStoreId(Integer erpStoreId) {
        this.erpStoreId = erpStoreId;
    }

    public Integer getErpAreaCode() {
        return erpAreaCode;
    }

    public void setErpAreaCode(Integer erpAreaCode) {
        this.erpAreaCode = erpAreaCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
