package com.jk51.modules.marketing.request;

public enum PrizesType {
    NONE(0, "谢谢惠顾"),
    COUPON(1, "优惠券"),
    SCORE(2, "积分");

    private int val;

    private String desc;

    PrizesType(int val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "PrizesType{" +
            "val=" + val +
            ", desc='" + desc + '\'' +
            '}';
    }
}
