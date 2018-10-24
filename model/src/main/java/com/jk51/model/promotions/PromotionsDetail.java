package com.jk51.model.promotions;

import java.time.LocalDateTime;

/**
 * 活动使用详情实体类
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
public class PromotionsDetail {
    private Integer id;
    private Integer siteId;

    /**
     * 优惠活动编码
     */
    private String promotionsNo;

    /**
     * 订单id 对应 b_trades 表的 trades_id
     */
    private String orderId;

    /**
     * 状态0:已使用 1:以退款
     */
    private Integer status;

    /**
     * 优惠活动发放id   b_promotions_activity
     */
    private Integer activityId;

    /**
     * 优惠金额，该订单下，该活动优惠了多少金额，以分为单位
     * 有可能为null，表示该数据没有保存（以前的数据）
     */
    private Integer discountAmount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 规则id表 b_promotions_rule
     */
    private Integer ruleId;
    /**
     * 使用用户id
     */
    private Integer userId;

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getPromotionsNo() {
        return promotionsNo;
    }

    public void setPromotionsNo(String promotionsNo) {
        this.promotionsNo = promotionsNo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(LocalDateTime refundTime) {
        this.refundTime = refundTime;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "PromotionsDetail{" +
                "id=" + id +
                ", siteId=" + siteId +
                ", promotionsNo='" + promotionsNo + '\'' +
                ", orderId='" + orderId + '\'' +
                ", status=" + status +
                ", activityId=" + activityId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", refundTime=" + refundTime +
                ", ruleId=" + ruleId +
                ", userId=" + userId +
                '}';
    }
}
