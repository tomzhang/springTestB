package com.jk51.model.coupon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.jk51.commons.json.LocalDateTimeDeserializerForLongFormatter;
import com.jk51.commons.json.LocalDateTimeSerializerForLongFormatter;

import java.time.LocalDateTime;

/**
 * Created by ztq on 2018/3/15
 * Description:
 */
public class CouponDetailConcessionLog {

    private Integer siteId;
    private Integer id;

    /**
     * b_coupon_detail表的外键关联
     */
    private Integer couponDetailId;

    /**
     * 优惠券编码
     */
    private String couponNo;

    /**
     * tradesId，表示该券由哪个订单使用了
     */
    private String orderId;

    /**
     * 优惠券来源
     */
    private String source;

    /**
     * 店员id
     */
    private String managerId;

    /**
     * 创建时间及领取时间或是补发时间
     */
    @JsonSerialize(using = LocalDateTimeSerializerForLongFormatter.class)
    @JsonDeserialize(using = LocalDateTimeDeserializerForLongFormatter.class)
    private LocalDateTime createTime;

    /**
     * 是否可复制0:不可复制1:可复制
     */
    private int isCopy;

    /**
     * 是否可分享0:不可分享1:可分享
     */
    private int isShare;

    /**
     * 如果可分享记录可分享数量
     */
    private int shareNum;

    /**
     * b_coupon_rule主键关联
     */
    private int ruleId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 门店id
     */
    private Integer storeId;

    /**
     * tradesId，表示该券由哪个订单赠送的，类似于订餐送券的时候，表示券的来源
     */
    private String sendOrderId;

    /**
     * 0 非补发获取 1通过补发获取
     */
    private Integer type;

    /**
     * 现金券满距减元的最终所减的价格
     */
    private Integer distanceReduce;

    /**
     * 打折券满距打折最终所打的折扣
     */
    private Integer distanceDiscount;

    /**
     * 优惠券优惠了多少金额（单位分）
     */
    private Integer discountAmount;


    /* -- constructor -- */

    public CouponDetailConcessionLog() {
    }

    public CouponDetailConcessionLog(CouponDetail couponDetail, LocalDateTime createTime) {
        setSiteId(couponDetail.getSiteId());
        setCouponDetailId(couponDetail.getId());
        setCouponNo(couponDetail.getCouponNo());

        setOrderId(Preconditions.checkNotNull(couponDetail.getOrderId()));
        setSource(couponDetail.getSource());
        setManagerId(couponDetail.getManagerId());
        setCreateTime(createTime);

        setIsCopy(couponDetail.getIsCopy());
        setIsShare(couponDetail.getIsShare());
        setShareNum(couponDetail.getShareNum());
        setRuleId(couponDetail.getRuleId());

        setUserId(couponDetail.getUserId());
        setStoreId(couponDetail.getStoreId());
        setSendOrderId(couponDetail.getSendOrderId());
        setType(couponDetail.getType());

        setDistanceReduce(couponDetail.getDistanceReduce());
        setDistanceDiscount(couponDetail.getDistanceDiscount());
        setDiscountAmount(couponDetail.getDiscountAmount());
    }


    /* -- setter & getter -- */

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCouponDetailId() {
        return couponDetailId;
    }

    public void setCouponDetailId(Integer couponDetailId) {
        this.couponDetailId = couponDetailId;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public int getIsCopy() {
        return isCopy;
    }

    public void setIsCopy(int isCopy) {
        this.isCopy = isCopy;
    }

    public int getIsShare() {
        return isShare;
    }

    public void setIsShare(int isShare) {
        this.isShare = isShare;
    }

    public int getShareNum() {
        return shareNum;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getSendOrderId() {
        return sendOrderId;
    }

    public void setSendOrderId(String sendOrderId) {
        this.sendOrderId = sendOrderId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

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

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }
}
