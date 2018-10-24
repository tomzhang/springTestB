package com.jk51.modules.task.domain;

/**
 * 统计指标类型
 */
public enum TQuotaType {
    // 订单量
    TRADES_NUM((byte)10),
    // 商品销售数量
    GOODS_SALE_NUM((byte)20),
    // 订单金额(商品总价)
    TRADES_GOODS_PRICE_SUM((byte)30),
    // 商品销售金额(商品总价含运费)
    GOODS_PRICE_SUM((byte)40),
    // 订单金额(实付金额)
    TRADES_REAL_PRICE_SUM((byte)50),
    // 商品销售金额(实付金额)
    GOODS_REAL_SUM((byte)60),
    // 注册会员量
    REGISTER_MEMBER_NUM((byte)70);

    private Byte value;

    TQuotaType(Byte value) {
        this.value = value;
    }

    public Byte getValue() {
        return value;
    }
}
