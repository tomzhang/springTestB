package com.jk51.model.promotions;

/**
 * Created by javen on 2017/11/22.
 */
public class EasyToSeeParam {
    private Integer siteId;
    private String goodsIds;
    private String mobile;
    private Integer userId;
    // 0 搜索商品  1 商品详情
    private Integer searchType;


    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }
}
