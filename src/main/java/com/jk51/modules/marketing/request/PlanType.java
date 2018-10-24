package com.jk51.modules.marketing.request;

public enum PlanType {
    BIG_TURNTABLE(0, "大转盘"),
    SCRATCH_CARD(1, "刮刮乐");

    private int val;

    private String desc;

    PlanType(int val, String desc) {
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
        return "PlanType{" +
            "val=" + val +
            ", desc='" + desc + '\'' +
            '}';
    }
}
