package com.jk51.modules.tpl.config;

/**
 * 请求常量
 */
public class RequestConstant {
    /**
     * 获取token
     */
    public static final String obtainToken = "/get_access_token";

    /**
     * 创建订单
     */
    public static final String orderCreate = "/v2/order";

    /**
     * 取消 订单
     */
    public static final String orderCancel = "/v2/order/cancel";

    /**
     * 订单查询
     */
    public static final String orderQuery = "/v2/order/query";


    /**
     * 添加门店
     */
    public static final String addStore = "/v2/chain_store";
}
