package com.jk51.model.coupon.requestParams;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chenpeng
 * 创建日期: 2017/3/21
 * 修改记录:
 */
public class CouponView {

    private int isAllType;//是否全品类 0 是 1不是
    private double maxMoney;//最高减多少 分
    private double maxDiscount;//最高折扣多少
    private Integer maxSendNum;//最高送几件
    private String ruleDetail;//规则详情

    public int getIsAllType() {
        return isAllType;
    }

    public void setIsAllType(int isAllType) {
        this.isAllType = isAllType;
    }

    public double getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(double maxMoney) {
        this.maxMoney = maxMoney;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(double maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public Integer getMaxSendNum() {
        return maxSendNum;
    }

    public void setMaxSendNum(Integer maxSendNum) {
        this.maxSendNum = maxSendNum;
    }

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
    }
}
