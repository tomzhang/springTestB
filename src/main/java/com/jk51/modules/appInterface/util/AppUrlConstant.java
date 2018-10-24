package com.jk51.modules.appInterface.util;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yeah
 * 创建日期: 2017-03-22
 * 修改记录:
 */
public class AppUrlConstant {
    public static String SEND_SMS_URL = "/sms/tpl_service?phone={mobile}&code={msg}&tpl_id=1";
    public static String SEND_INVITE_URL="/sms/tpl_distri";
    public static String SEND_CALL_URL = "/qimoor/webcall?exten={mobile}&vcode={msg}";
    public static String GET_USABLE_URL = "/coupon/usableList";
    public static String COUPON_CENTER_URL = "/coupon/couponCenter";
    public static String ACTIVE_COUPON_URL = "/coupon/getActiveWithCoupon";
    public static String APP_REGISTER_REDISKEY = "APP_REGISTER_VALIDATE_CODE";
    public static String WX_URL_CREATEORDER = "/pay/wx/createorder";
    public static String WX_URL_MICROPAY = "/pay/wx/micropay";
    public static String ZFB_URL_CREATEORDER = "/pay/ali/createorder";
    public static String ZFB_URL_MICROPAY = "/pay/ali/pay";
    public static String XJFK_URL = "/trades/notifyCashier";//通知收银员收款

    public static String WX_CODE_CREATEORDER = "3";
    public static String WX_CODE_MICROPAY = "4";
    public static String ZFB_CODE_CREATEORDER = "5";
    public static String ZFB_CODE_MICROPAY = "6";
    public static String XJFK_CODE = "30";
}
