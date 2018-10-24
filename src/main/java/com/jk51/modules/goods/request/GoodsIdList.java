package com.jk51.modules.goods.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jk51.commons.string.StringUtil;

import javax.validation.constraints.NotNull;

// 忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
// 驼峰命名
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GoodsIdList {
    //-----添加字段-----start
    Integer appChecked;
    Integer appCheckedValue;
    Integer onLineShopChecked;
    Integer onLineShopCheckedValue;
    Integer status;//0:批量还原、单个删除、1:线上商城(上架/下架/还原)、2:app(上架/下架/还原)

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAppChecked() {
        return appChecked;
    }

    public void setAppChecked(Integer appChecked) {
        this.appChecked = appChecked;
    }

    public Integer getAppCheckedValue() {
        return appCheckedValue;
    }

    public void setAppCheckedValue(Integer appCheckedValue) {
        this.appCheckedValue = appCheckedValue;
    }

    public Integer getOnLineShopChecked() {
        return onLineShopChecked;
    }

    public void setOnLineShopChecked(Integer onLineShopChecked) {
        this.onLineShopChecked = onLineShopChecked;
    }

    public Integer getOnLineShopCheckedValue() {
        return onLineShopCheckedValue;
    }

    public void setOnLineShopCheckedValue(Integer onLineShopCheckedValue) {
        this.onLineShopCheckedValue = onLineShopCheckedValue;
    }

    //-----添加字段-----end
    @NotNull
    String goodsIds;
    @NotNull
    Integer siteId;

    public String getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }

    public int[] getIdList() {
        String[] t = goodsIds.split(",");
        int[] ids = new int[t.length];
        for (int i = 0; i < t.length; i++) {
            ids[i] = StringUtil.convertToInt(t[i]);
        }

        return ids;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
}
