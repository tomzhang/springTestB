package com.jk51.modules.privatesend.util;


public class TemplateIdConstant {

    public static String URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";

    public static String URL_TEMPLATE_ALL = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=";

    /**
     * 创建拼单成功提醒  1
     */
    public static String TOGETHER_ORDER_CREATE_SUCCESS = "创建拼单成功提醒";

    /**
     * 参加拼单成功提醒  1
     */
    public static String TOGETHER_ORDER_JOIN_SUCCESS = "参加拼单成功提醒";

    /**
     * 拼单成功通知  1
     */
    public static String TOGETHER_ORDER_CREATE_SUCCESS_NOTICE = "拼单成功通知";

    /**
     * 拼单失败通知  1
     */
    public static String TOGETHER_ORDER_CREATE_FAIL_NOTICE = "拼单失败通知";

    /**
     * 拼单失败通知(退款)  1
     */
    public static String TOGETHER_ORDER_CREATE_FAIL_NOTICE_REFUND = "拼单失败通知(退款)";

    /**
     * 拼单人数不足提醒  1
     */
    public static String TOGETHER_ORDER_PEOPLE_LACK = "拼单人数不足提醒";

    /**
     * 订单取消通知    1
     */
    public static String TOGETHER_ORDER_CANCEL = "订单取消通知";

    /**
     * 未支付超时通知  1
     */
    public static String TOGETHER_ORDER_UNPAY_OVERTIME = "未支付超时通知";

    /**
     * 商品发货通知   1
     */
    public static String TOGETHER_ORDER_SEND_NOTICE = "商品发货通知";

    /**
     * 订单待付款提醒   1
     */
    public static String ORDER_TO_PAY_NOTICE = "订单待付款提醒";

    /**
     * 订单提货通知（门店自提通知）  1
     */
    public static String ORDER_STORE_TO_TAKE = "门店自提通知";

    /**
     * 订单发货通知  1
     */
    public static String ORDER_SEND_NOTICE = "订单发货通知";

    /**
     * 订单签收    1
     */
    public static String ORDER_SIGN = "订单签收";

    /**
     * 退款成功通知  1
     */
    public static String ORDER_REFUND_SUCCESS = "退款成功通知";

    /**
     * 退款失败通知  1
     */
    public static String ORDER_REFUND_FAIL = "退款失败通知";

    /**
     * 交易成功通知  1
     */
    public static String ORDER_IS_SUCCESS = "交易成功通知";

    /**
     * 抽中红包通知 1
     */
    public static String RED_BACKET_SUCCESS = "抽中红包通知";

    /**
     * 心电检测结果通知
     */
    public static String ECG_RESULT_MESSAGE = "心电检测结果通知";

    /**
     * 设备连接成功提醒
     */
    public static String ECG_SUCCESS_MESSAGE = "设备连接成功提醒";

    /**
     * 开通成功通知
     */
    public static String COUPON_IS_SUCCESS = "开通成功通知";

    /**
     * 服务到期提醒
     */
    public static String COUPON_IS_EXPIRE = "服务到期提醒";

}
