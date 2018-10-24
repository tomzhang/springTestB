package com.jk51.modules.pay.constants;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-24
 * 修改记录:
 */
public class PayConstant {
    //微信统一下单URL
    public static final String WX_UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //微信刷卡支付URL
    public static final String WX_MICRO_PAY_URL = "https://api.mch.weixin.qq.com/pay/micropay";
    //微信查询订单URL
    public static final String WX_ORDER_QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    //微信关闭订单URL
    public static final String WX_CLOSE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/closeorder";
    //微信退款URL
    public static final String WX_REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    //微信查询退款订单URL
    public static final String WX_REFUND_QUERY_URL = "https://api.mch.weixin.qq.com/pay/refundquery";
    //微信下载账单URL
    public static final String WX_DOWNLOAD_BILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";
    //微信上报URL
    public static final String WX_REPORT_URL = "https://api.mch.weixin.qq.com/pay/report";
    //微信短链接URL
    public static final String WX_SHORT_URL_URL = "https://api.mch.weixin.qq.com/pay/shorturl";
    //微信撤销URL
    public static final String WX_REVERSE_URL = "https://api.mch.weixin.qq.com/secapi/pay/reverse";
    //微信红包接口URL
    public static final String WX_SENDEEDPACK_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";

    //ali请求URL
    public static final String ALI_REQUEST_URL = "https://openapi.alipay.com/gateway.do";

    //支付方式：微信
    public static final String PAY_STYLE_WX = "wx";
    //支付方式：微信jsapi
    public static final String PAY_STYLE_WX_JSAPI = "wx_jsapi";
    //支付方式：微信native
    public static final String PAY_STYLE_WX_NATIVE = "wx_native";
    //支付方式：微信micropay
    public static final String PAY_STYLE_WX_MICROPAY = "wx_micropay";
    //支付方式：微信APP
    public static final String PAY_STYLE_WX_APP = "wx_app";
    //支付方式：支付宝
    public static final String PAY_STYLE_ALI = "ali";
    //支付方式：支付宝刷卡
    public static final String PAY_STYLE_ALI_PAY = "ali_pay";
    //支付方式：支付宝APP支付
    public static final String PAY_STYLE_ALI_APP = "ali_app";
    //支付方式：支付宝扫码
    public static final String PAY_STYLE_ALI_QRCODE = "ali_qrcode";
    //支付方式：银联
    public static final String PAY_STYLE_UNI = "uni";
    //支付方式：快钱
    public static final String PAY_STYLE_BIL = "bil";
    //支付方式：医保
    public static final String PAY_STYLE_HEALTH_INSURANCE = "health_insurance";
    //支付方式：现金
    public static final String PAY_STYLE_CASH = "cash";
    //支付接口调用成功
    public static final byte PAY_INTERFACE_EXE_SUCCESS = 1;
    //支付接口调用失败
    public static final byte PAY_INTERFACE_EXE_FAIL = 2;
    //支付接口调用错误
    public static final byte PAY_INTERFACE_EXE_ERROR = 3;
    //支付接口：微信预下单
    public static final String PAY_INTERFACE_WX_PO = "wx_po";
    //支付接口：微信支付
    public static final String PAY_INTERFACE_WX_PAY = "wx_pay";
    //支付接口：微信订单查询
    public static final String PAY_INTERFACE_WX_QR = "wx_qr";
    //支付接口：微信退款
    public static final String PAY_INTERFACE_WX_RF = "wx_rf";
    //支付接口：微信退款查询
    public static final String PAY_INTERFACE_WX_RFOQR = "wx_rfoqr";
    //支付接口：微信撤销订单
    public static final String PAY_INTERFACE_WX_RV = "wx_rv";
    //支付接口：微信关闭订单
    public static final String PAY_INTERFACE_WX_CLS = "wx_cls";
    //支付接口：微信通知商户
    public static final String PAY_INTERFACE_WX_CB = "wx_cb";
    //支付接口：微信获取openid
    public static final String PAY_INTERFACE_WX_GOP = "wx_gop";
    //支付接口：微信获取access_token
    public static final String PAY_INTERFACE_WX_GAT = "wx_gat";
    //支付接口：微信获取ticket
    public static final String PAY_INTERFACE_WX_GTK = "wx_gtk";
    //支付接口：支付宝预下单
    public static final String PAY_INTERFACE_ALI_PO = "ali_po";
    //支付接口：支付宝订单查询
    public static final String PAY_INTERFACE_ALI_QR = "ali_qr";
    //支付接口：支付宝支付
    public static final String PAY_INTERFACE_ALI_PAY = "ali_pay";
    //支付接口：支付宝退款
    public static final String PAY_INTERFACE_ALI_RF = "ali_rf";
    //支付接口：支付宝回调
    public static final String PAY_INTERFACE_ALI_CB = "ali_cb";

    public static final Integer PAY_STATUS_SUCCESS = 1;

    public static final Integer PAY_STATUS_FAIL = 0;

    public static final Integer PAY_STATUS_REFUND = 2;

    public static final Integer PAY_STATUS_REFUND_ERROR = 3;//付款失败

    public static final Integer PAY_STATUS_PAY_ERROR = 4;

    public static final String PAY_WX_FW_ERR_CODE_DES = "您的请求参数与订单信息不一致";
    public static final String PAY_WX_RECHARGE_ERR_CODE_DES = "基本账户余额不足，请充值后重新发起";
    public static final String PAY_WX_FW_ERR_CODE_DES_SHOW = "由于您现在已经切换到【商家收款】模式，该模式之前的订单已经无法退款，请线下手工处理。";
    public static final String PAY_WX_RECHARGE_ERR_CODE_DES_SHOW = "您的账户余额不足，无法完成退款，请登录支付宝/微信支付平台充值后在同意退款。";//20180803冯文军要求改提醒
            //"您的账户余额不足，无法完成退款，请先充值后再同意退款。";
}
