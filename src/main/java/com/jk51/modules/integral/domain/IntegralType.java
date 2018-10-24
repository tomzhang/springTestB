package com.jk51.modules.integral.domain;


public enum IntegralType {
    // 用户注册
    USER_REGISTER(10),
    // 用户签到
    USER_SIGN(20),
    // 补全信息
    USER_INFO(30),
    // 下单满额
    USER_BUYER(40),
    // 咨询
    USER_ASK(50),
    // 订单评价
    USER_TRADE_COMMENT(60);

    private int type;

    IntegralType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.valueOf(type);
    }
}
