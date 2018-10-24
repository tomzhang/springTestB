package com.jk51.model.coupon;

import com.jk51.model.order.Trades;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户领取的优惠券的实体类 对应表名 b_coupon_detail
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司 <br/>
 * 作者: chenpeng                        <br/>
 * 创建日期: 2017-03-06                   <br/>
 * 修改记录:
 */
public class CouponDetail implements Serializable {

    private Integer siteId;
    private Integer id;

    /**
     * couponRule id
     */
    private String couponNo;

    /**
     * 其实是couponActivity的id
     */
    private String source;

    /**
     * {@link CouponRule#ruleId}
     */
    private int ruleId;

    private String managerId;
    private Timestamp createTime;
    private Timestamp updateTime;
    private int isCopy;
    private int isShare;
    private String shareUrl;
    private Integer userId;
    private Double money;
    private int shareNum;
    private int version;
    private Integer storeId;

    /**
     * {@link Trades#tradesId}，表示该券由哪个订单赠送的，类似于订餐送券的时候，表示券的来源
     */
    private String sendOrderId;

    /**
     * {@link Trades#tradesId}，表示该券由哪个订单使用了，结合{@link CouponDetail#status}使用
     */
    private String orderId;

    /**
     * 优惠券状态 0:已使用 1:待使用
     * 例如：
     * 订单取消，优惠券状态会由 0->1，此时的orderId无效
     */
    private int status;
    private Integer type;
    private Integer distanceReduce;
    private Integer distanceDiscount;

    /**
     * 优惠金额，该订单下，该优惠券优惠了多少金额，以分为单位
     * 有可能为null，表示该数据没有保存（以前的数据）
     */
    private Integer discountAmount;
    private LocalDateTime recoveryTime;
    private Integer recoveryStatus;
    private Integer returnStatus;

    // 用来存储CouponDetail对应的CouponRule和CouponActivity
    private CouponRule couponRule;
    private CouponActivity couponActivity;


    public static CouponDetail build(CouponRule couponRule, Integer userId, String source, String manageId, String code) {
        CouponDetail couponDetail = new CouponDetail();
        couponDetail.setSiteId(couponRule.getSiteId());
        couponDetail.setCouponNo(code);
        couponDetail.setStatus(1);
        couponDetail.setIsCopy(0);
        couponDetail.setIsShare(1);//如果可分享要生成分享链接
        couponDetail.setManagerId(Optional.ofNullable(manageId).orElse(""));
        couponDetail.setSource(Optional.ofNullable(source).orElse(""));
        couponDetail.setRuleId(couponRule.getRuleId());
        couponDetail.setUserId(userId);
        return couponDetail;
    }

    public static CouponDetail build(CouponRule couponRule, Integer userId, String source, String manageId, String code, String tradeId) {
        CouponDetail couponDetail = new CouponDetail();
        couponDetail.setSiteId(couponRule.getSiteId());
        couponDetail.setCouponNo(code);
        couponDetail.setStatus(1);
        couponDetail.setIsCopy(0);
        couponDetail.setIsShare(1);//如果可分享要生成分享链接
        couponDetail.setManagerId(Optional.ofNullable(manageId).orElse(""));
        couponDetail.setSource(Optional.ofNullable(source).orElse(""));
        couponDetail.setRuleId(couponRule.getRuleId());
        couponDetail.setUserId(userId);
        couponDetail.setSendOrderId(tradeId);
        return couponDetail;
    }


    public static CouponDetail build(CouponRuleActivity couponRule, Integer userId, String source, String manageId, String code, Integer storeId) {
        CouponDetail couponDetail = new CouponDetail();
        couponDetail.setSiteId(couponRule.getSiteId());
        couponDetail.setCouponNo(code);
        couponDetail.setStatus(1);
        couponDetail.setIsCopy(0);
        couponDetail.setIsShare(1);//如果可分享要生成分享链接
        couponDetail.setManagerId(Optional.ofNullable(manageId).orElse(""));
        couponDetail.setSource(Optional.ofNullable(source).orElse(""));
        couponDetail.setRuleId(couponRule.getRuleId());
        couponDetail.setUserId(userId);
        couponDetail.setStoreId(storeId);
        return couponDetail;
    }

    public static CouponDetail build(CouponRule couponRule, String source, Integer userId, String code, String orderId) {
        CouponDetail couponDetail = new CouponDetail();
        couponDetail.setSiteId(couponRule.getSiteId());
        couponDetail.setCouponNo(code);
        couponDetail.setStatus(1);
        couponDetail.setIsCopy(0);
        couponDetail.setIsShare(1);
        couponDetail.setSource(source);
        couponDetail.setRuleId(couponRule.getRuleId());
        couponDetail.setUserId(userId);
        couponDetail.setSendOrderId(orderId);
        return couponDetail;
    }

    public static CouponDetail build(Integer siteId, String source, Integer ruleId, String couponNo, Integer userId, String orderId) {
        CouponDetail couponDetail = new CouponDetail();
        couponDetail.setSiteId(siteId);
        couponDetail.setCouponNo(couponNo);
        couponDetail.setStatus(1);
        couponDetail.setIsCopy(0);
        couponDetail.setIsShare(1);
        couponDetail.setSource(source);
        couponDetail.setRuleId(ruleId);
        couponDetail.setUserId(userId);
        couponDetail.setSendOrderId(orderId);
        return couponDetail;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
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

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
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

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public int getShareNum() {
        return shareNum;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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

    public LocalDateTime getRecoveryTime() {
        return recoveryTime;
    }

    public void setRecoveryTime(LocalDateTime recoveryTime) {
        this.recoveryTime = recoveryTime;
    }

    public Integer getRecoveryStatus() {
        return recoveryStatus;
    }

    public void setRecoveryStatus(Integer recoveryStatus) {
        this.recoveryStatus = recoveryStatus;
    }

    public Integer getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(Integer returnStatus) {
        this.returnStatus = returnStatus;
    }

    public CouponRule getCouponRule() {
        return couponRule;
    }

    public void setCouponRule(CouponRule couponRule) {
        this.couponRule = couponRule;
    }

    public CouponActivity getCouponActivity() {
        return couponActivity;
    }

    public void setCouponActivity(CouponActivity couponActivity) {
        this.couponActivity = couponActivity;
    }
}
