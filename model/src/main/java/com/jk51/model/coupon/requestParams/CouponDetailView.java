package com.jk51.model.coupon.requestParams;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chenpeng
 * 创建日期: 2017/3/23
 * 修改记录:
 */
public class CouponDetailView{

    private Integer couponType;//优惠券类型
    private Integer useScope;//使用范围
    private String storeIds;//具体门店ID  使用范围-1的时候,此字段为空  门店ID以 , 分隔
    private String couponNo;//优惠券编码
    private Integer couponId;//优惠券主键
    private Integer isLine;//是否线上1是线上2线下3线上线下均可使用
    private Integer ruleId;//优惠券对应的规则id
    private Integer isShare;//优惠券是否可分享0不可分享 1可分享
    private Integer activeId;//优惠券对应的活动id
    private String title;//活动标题
    private String image;//活动照片
    private String content;//活动内容
    private String marked_words;//活动内容
    private Integer distanceReduce;//优惠距离券  减价结果
    private Integer distanceDiscount;//优惠距离券 打折结果
    private Integer siteId;
    private String url;
    private String orderType;
    private Integer useStatus;
    private CouponView couponView;

    public Integer getDistanceReduce() {
        return distanceReduce;
    }

    public void setDistanceReduce(Integer distanceReduce) {
        this.distanceReduce = distanceReduce;
    }

    public Integer getDistanceDiscount() {
        return distanceDiscount;
    }

    public void setDistanceDiscount(Integer distanceDiscount) {
        this.distanceDiscount = distanceDiscount;
    }

    public String getStoreIds() {
        return storeIds;
    }

    public void setStoreIds(String storeIds) {
        this.storeIds = storeIds;
    }

    public Integer getCouponType() {
        return couponType;
    }

    public void setCouponType(Integer couponType) {
        this.couponType = couponType;
    }

    public Integer getUseScope() {
        return useScope;
    }

    public void setUseScope(Integer useScope) {
        this.useScope = useScope;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public Integer getIsLine() {
        return isLine;
    }

    public void setIsLine(Integer isLine) {
        this.isLine = isLine;
    }

    public CouponView getCouponView() {
        return couponView;
    }

    public void setCouponView(CouponView couponView) {
        this.couponView = couponView;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getIsShare() {
        return isShare;
    }

    public void setIsShare(Integer isShare) {
        this.isShare = isShare;
    }

    public Integer getActiveId() {
        return activeId;
    }

    public void setActiveId(Integer activeId) {
        this.activeId = activeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(Integer useStatus) {
        this.useStatus = useStatus;
    }

    public String getMarked_words() {
        return marked_words;
    }

    public void setMarked_words(String marked_words) {
        this.marked_words = marked_words;
    }
}
