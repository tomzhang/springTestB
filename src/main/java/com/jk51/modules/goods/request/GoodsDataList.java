package com.jk51.modules.goods.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.validation.constraints.NotNull;

// 忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
// 驼峰命名
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GoodsDataList {
    @NotNull
    private Integer siteId;
    @NotNull
    private GoodsData[] items;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public GoodsData[] getItems() {
        return items;
    }

    public void setItems(GoodsData[] items) {
        this.items = items;
    }
}
