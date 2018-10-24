package com.jk51.model.order;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-03-26
 * 修改记录:
 */
public class BestStoreQueryParams {

    private Double lng;

    private Double lat;

    private Integer siteId;

    private Integer cityId;

    private String goodsIds;

    private String userId;
    private String buyerId;
    private String mobile;
    private String proActivityId;

    public String getProActivityId() {
        return proActivityId;
    }

    public void setProActivityId(String proActivityId) {
        this.proActivityId = proActivityId;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }


    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BestStoreQueryParams{");
        sb.append("lng=").append(lng);
        sb.append(", lat=").append(lat);
        sb.append(", siteId=").append(siteId);
        sb.append(", cityId=").append(cityId);
        sb.append(", goodsIds=").append(goodsIds);
        sb.append('}');
        return sb.toString();
    }

}
