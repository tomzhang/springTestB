package com.jk51.modules.im.service.iMRecode.response;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-12-13
 * 修改记录:
 */
public class GoodsInfo {

    private Integer goodsId;
    private String drugName;
    private String specifCation;
    private Integer shopPrice;
    private String imageId;


    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getSpecifCation() {
        return specifCation;
    }

    public void setSpecifCation(String specifCation) {
        this.specifCation = specifCation;
    }

    public Integer getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(Integer shopPrice) {
        this.shopPrice = shopPrice;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
