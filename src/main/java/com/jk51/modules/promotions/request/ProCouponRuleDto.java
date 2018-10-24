package com.jk51.modules.promotions.request;

import com.jk51.model.order.Page;

/**
 * Created by Administrator on 2017/8/9.
 */
public class ProCouponRuleDto extends Page {

    private  Integer  id;//查询优惠活动或优惠券详情时的主键信息
    private Integer ruleType;//1优惠券 2 优惠活动
    private String proRuleName;//优惠活动或是优惠券的名称
    private String proRuleType;//优惠活动或是优惠券的优惠类型
    private String status;//状态
    private String startTime;//创建的起始时间
    private String endTime;//创建的终止时间
    private String rule_name;
    private  String coupon_type;
    private String total;
    private String use_amount;
    private String amount;
    private String create_time;
    private String rule_id;
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRule_id() {
        return rule_id;
    }

    public void setRule_id(String rule_id) {
        this.rule_id = rule_id;
    }

    public String getRule_name() {
        return rule_name;
    }

    public void setRule_name(String rule_name) {
        this.rule_name = rule_name;
    }

    public String getCoupon_type() {
        return coupon_type;
    }

    public void setCoupon_type(String coupon_type) {
        this.coupon_type = coupon_type;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getUse_amount() {
        return use_amount;
    }

    public void setUse_amount(String use_amount) {
        this.use_amount = use_amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }



    private Integer siteId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getProRuleName() {
        return proRuleName;
    }

    public void setProRuleName(String proRuleName) {
        this.proRuleName = proRuleName;
    }

    public String getProRuleType() {
        return proRuleType;
    }

    public void setProRuleType(String proRuleType) {
        this.proRuleType = proRuleType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
