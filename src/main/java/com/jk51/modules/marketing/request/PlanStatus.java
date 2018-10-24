package com.jk51.modules.marketing.request;

public enum PlanStatus {
    CREATE_PLAN(100, "创建一个互动活动"),
    SETTING_PRIZES(101, "设置奖品及概率"),
    SETTING_TYPE(102, "设置活动类型"),
    COMPLETE(200, "完成发布");

    private int val;

    private String desc;

    PlanStatus(int val, String desc) {
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
        return "PlanStatus{" +
            "val=" + val +
            ", desc='" + desc + '\'' +
            '}';
    }
}
