package com.jk51.modules.promotions.request;

/**
 * filename :com.jk51.modules.promotions.request.
 * author   :zw
 * date     :2017/8/16
 * Update   : 商品详情页最近秒杀券和优惠标签
 */
public class LabelParam {
    /**
     * 展示类型，1优惠标签，2秒杀倒计时
     */
    private Integer type;

    /**
     * 商品id
     */
    private Integer goodsId;
    /**
     * 会员标签逗号隔开
     */
    private String label;
    /**
     * 秒杀结束时间
     */
    private String saleTime;
    /**
     * 优惠券总数
     */
    private Integer couponCount;
    /**
     * 优惠活动总数
     */
    private Integer promotionsCount;
    /**
     * 活动详情
     */
    private String proruleDetail;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(String saleTime) {
        this.saleTime = saleTime;
    }

    public Integer getCouponCount() {
        return couponCount;
    }

    public void setCouponCount(Integer couponCount) {
        this.couponCount = couponCount;
    }

    public Integer getPromotionsCount() {
        return promotionsCount;
    }

    public void setPromotionsCount(Integer promotionsCount) {
        this.promotionsCount = promotionsCount;
    }

    public String getProruleDetail() {
        return proruleDetail;
    }

    public void setProruleDetail(String proruleDetail) {
        this.proruleDetail = proruleDetail;
    }
}
