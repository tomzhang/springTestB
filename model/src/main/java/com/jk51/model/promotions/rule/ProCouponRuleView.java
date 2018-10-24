package com.jk51.model.promotions.rule;

/**
 * Created by Administrator on 2017/8/16.
 */
public class ProCouponRuleView {
    private int isAllType;//是否全品类 0 是 1不是
    private double maxMoney;//最高减多少 分
    private double maxDiscount;//最高折扣多少
    private int maxSendNum;//最高送几件
    private String proruleDetail;//规则详情
    private Object obj;//对应活动的实体类

    public int getIsAllType() {
        return isAllType;
    }

    public void setIsAllType(int isAllType) {
        this.isAllType = isAllType;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(double maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public double getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(double maxMoney) {
        this.maxMoney = maxMoney;
    }


    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getProruleDetail() {
        return proruleDetail;
    }

    public void setProruleDetail(String proruleDetail) {
        this.proruleDetail = proruleDetail;
    }

    public int getMaxSendNum() {
        return maxSendNum;
    }

    public void setMaxSendNum(int maxSendNum) {
        this.maxSendNum = maxSendNum;
    }
}

