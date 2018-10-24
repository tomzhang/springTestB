package com.jk51.model.coupon;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 优惠券活动和优惠券规则的中间表 对应表名 b_coupon_activity_coupon
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司 <br/>
 * 作者: think                           <br/>
 * 创建日期: 2017-03-06                   <br/>
 * 修改记录:
 */
public class CouponRuleActivity {

    private Integer id;
    private Integer siteId;
    private Integer ruleId;
    private Integer activeId;
    private Timestamp createTime;
    private Integer num;
    private Integer amount;
    private Integer sendNum;
    private Integer useNum;
    private Integer receiveNum;
    private Integer status; // 0未删除 1已删除
    private CouponRule couponRule;

    public CouponRuleActivity(Integer siteId, Integer ruleId, Integer activeId, Integer num) {
        this.siteId = siteId;
        this.ruleId = ruleId;
        this.activeId = activeId;
        this.num = num;
        this.createTime = Timestamp.valueOf(LocalDateTime.now());
        this.status = 0;
    }

    public CouponRuleActivity() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getActiveId() {
        return activeId;
    }

    public void setActiveId(Integer activeId) {
        this.activeId = activeId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    public Integer getUseNum() {
        return useNum;
    }

    public void setUseNum(Integer useNum) {
        this.useNum = useNum;
    }

    public Integer getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(Integer receiveNum) {
        this.receiveNum = receiveNum;
    }

    public CouponRule getCouponRule() {
        return couponRule;
    }

    public void setCouponRule(CouponRule couponRule) {
        this.couponRule = couponRule;
    }

    @Override
    public String toString() {
        return "CouponRuleActivity{" +
                "id=" + id +
                ", siteId=" + siteId +
                ", ruleId=" + ruleId +
                ", activeId=" + activeId +
                ", createTime=" + createTime +
                ", num=" + num +
                ", amount=" + amount +
                ", sendNum=" + sendNum +
                ", useNum=" + useNum +
                ", receiveNum=" + receiveNum +
                ", status=" + status +
                '}';
    }
}
