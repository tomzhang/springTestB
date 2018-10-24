package com.jk51.model;

import com.jk51.model.order.Page;

/**
 * Created by Administrator on 2017/8/14.
 */
public class GoodsParam extends Page {

    private Integer siteId;

    private String goodsTitle;

    private String spa;

    private String imgHash;

    private String userCateId;

    private String comName;

    private String goodsTemplate;

    private String goodsCode;

    private String startPrice;

    private String endPrice;

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(String startPrice) {
        this.startPrice = startPrice;
    }

    public String getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(String endPrice) {
        this.endPrice = endPrice;
    }

    public String getGoodsTemplate() {
        return goodsTemplate;
    }

    public void setGoodsTemplate(String goodsTemplate) {
        this.goodsTemplate = goodsTemplate;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getUserCateId() {
        return userCateId;
    }

    public void setUserCateId(String userCateId) {
        this.userCateId = userCateId;
    }

    public String getImgHash() {

        return imgHash;
    }

    public void setImgHash(String imgHash) {
        this.imgHash = imgHash;
    }

    public String getSpa() {

        return spa;
    }

    public void setSpa(String spa) {
        this.spa = spa;
    }

    public String getGoodsTitle() {

        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public Integer getSiteId() {

        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
}
