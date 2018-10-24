package com.jk51.modules.goods.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.validation.constraints.NotNull;

// 忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
// 驼峰命名
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
/**
 * 商品上下架数据
 */
public class GoodsIdRequireData {
    @NotNull(message = "商品编号不能为空")
    protected Integer goodsId;

    protected Integer siteId;
    protected Integer status;//0:单个商品删除

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
}
