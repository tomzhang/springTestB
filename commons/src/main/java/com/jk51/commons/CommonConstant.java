package com.jk51.commons;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-01-19
 * 修改记录:2017-02-25
 */
public class CommonConstant {
    public static final String CHARSET = "UTF-8";
    public static final String FORMAT = "JPG";
    public static final String DES = "DES";
    public static final String AES = "AES";
    public static final String SHA1 = "SHA1";
    public static final String MD5 = "MD5";
    public static final String BARCODE_PNG = ".png";
    public static final String DOT = ".";
    public static final String FILE_SUFFIX_EXCEL_2003 = "xls";
    public static final String FILE_SUFFIX_EXCEL_2007 = "xlsx";
    public static final String REGEXP_DOT = "[.]";

    //  订单状态交易状态:110(等侍买家付款), 120(等待卖家发货),130(等侍买家确认收货),140(买家已签收，货到付款专用)，220(用户确认收货)，230(门店确认收货)
    // 150(交易成功)，160(用户未付款主动关闭)，170(超时未付款，系统关闭)，180(商家关闭订单)，200(直购 待取货),210（直购 已取货），900（已退款),
    public static final int WAIT_PAYMENT_BUYERS = 110; //等侍买家付款 未支付
    public static final int WAIT_SELLER_SHIPPED = 120; //等待卖家发货 已支付 待发货
    public static final int HAVE_SHIPPED = 130; //等侍买家确认收货 已发货
    //public static final int SIGN_RECEIVED = 140; //买家已签收，货到付款专用 （确认） 【交易成功】
    public static final int TRANSACTION_SUCCESS = 150; //交易成功 【交易成功】
    public static final int USER_NOT_PAY_CLOSE = 160; //用户未付款主动关闭 已取消
    public static final int UNPAID_OVERTIME = 170; //超时未付款，系统关闭 已取消
    public static final int BUSINESSES_CLOSE_ORDER = 180; //商家关闭订单 已取消
    public static final int BUYER_APPLY_FOR_REFUND = 190; //买家申请退款 退款中
    public static final int WAIT_QU_HUO = 200; //带自提
    public static final int HAVE_TAKE_GOODS = 210; //已取货||已自提  直购
    public static final int TRADES_REFUND_SUCCESS = 900; //退款成功 已退款
    public static final int USER_RECEIVED = 220; //用户确认收货（送货上门）
    public static final int STORE_RECEIVED = 230; //门店确认收货（送货上门）
    public static final int SYSTEM_RECEIVED = 800; //系统确认收货（送货上门）
    public static final int SYSTEM_STORE_CANCEL = 240; //240已取消【门店自提待自提后可取消订单】

    //是否已付款
    public static final int IS_PAYMENT_ZERO = 0; //未付款
    public static final int IS_PAYMENT_ONE = 1; //已付款

    //是否有申请退款 默认为0(无退款申请) 大于0表示有退款 100=等待受理（退款中）  110=受理失败 （拒绝退款）120=等待退款 130=退款成功 140=退款失败
    public static final int IS_REFUND_NO = 0; //无退款申请
    public static final int IS_REFUND_ONE = 100; //等待受理（退款中）
    public static final int IS_REFUND_TWO = 110; //受理失败 （拒绝退款）
    public static final int IS_REFUND_FOUR = 120; //退款成功


    public static final int BARCODE_LENGTH = 10;


    //备货状态
    public static final int STOCKUP_WAIT_READY = 110;  //未备货
    public static final int STOCKUP_REDAY = 120;  //已备货

    //发货状态
    public static final int SHIPPED_WAIT_DELIVERY = 110;  //未发货
    public static final int SHIPPED = 120; //已发货
    public static final int SHIPPED_RECEIVED = 9999;  //确认收货

    //资金可结算状态
    public static final int SETTLEMENT_STATUS_NO = 100;  //不结算
    public static final int SETTLEMENT_STATUS_WAIT = 150;  //待结算
    public static final int SETTLEMENT_STATUS_MAY = 200;   //可结算
    public static final int SETTLEMENT_STATUS_ALREADY = 250;   //已结算


    //业务来源 source_business
    public static final String SOURCE_BUSINESS_WAIT_READY = "付款成功，待备货";
    public static final String SOURCE_BUSINESS_WAIT_DELIVERY = "货物已备好，待发货";
    public static final String SOURCE_BUSINESS_WAIT_QU = "货物已备好，待自提";
    public static final String SOURCE_BUSINESS_YI_QU = "已自提";
    public static final String SOURCE_BUSINESS_SHIPPED = "已发货";
    public static final String SOURCE_BUSINESS_TAKE_GOOD = "直购订单，已取货";
    public static final String SOURCE_BUSINESS_HAVE_REFUND = "已退款";
    public static final String SOURCE_BUSINESS_USER_CLOSE = "用户未付款,用户主动关闭";
    public static final String SOURCE_BUSINESS_MERCHANTS_CLOSE = "用户未付款,商家关闭订单";
    public static final String SOURCE_BUSINESS_USER_RECEIVED = "用户确认收货（送货上门）";
    public static final String SOURCE_BUSINESS_STORE_RECEIVED = "门店确认收货（送货上门）";
    public static final String SOURCE_BUSINESS_APPLY_REFUND = "申请退款";
    public static final String SOURCE_BUSINESS_SYSTEM_CANEL = "超时未付款，系统取消订单";
    public static final String SOURCE_BUSINESS_SYSTEM_CONFIRM = "门店自提|直购 系统确认完成订单";
    public static final String SOURCE_BUSINESS_SYSTEM_DELIVERY = "送货上门，系统确认收货";
    public static final String SOURCE_BUSINESS_MERCHANT_REFUND = "商家发起退款";
    public static final String SOURCE_BUSINESS_STORE_REFUND = "门店发起退款";
    public static final String SOURCE_BUSINESS_FENGNIAO = "蜂鸟确认收货";
    public static final String SOURCE_BUSINESS_STORE_HAVE_REFUND = "门店处理-已退款";
    public static final String SOURCE_BUSINESS_MERCHANT_HAVE_REFUND = "商家处理-已退款";


    //退款状态 100=等待受理（退款中）  110=受理失败 （拒绝退款）120=等待退款 130=退款成功 140=退款失败
    public static final int REFUND_WAIT = 100;   //等待受理（退款中）
    public static final int REFUSED_REFUND = 110;   //受理失败 （拒绝退款）
    public static final int REFUND_SUCCESS = 120;   //退款成功

    // 订单类型
    public static final int POST_STYLE_DIRECT_PURCHASE = 170;  // 直购
    public static final int POST_STYLE_EXTRACT = 160;  //自提
    public static final int POST_STYLE_DOOR = 150;  //送货上门

    //订单类型、前端
    public static final String ORDER_TYPE_HOME_DELIVERY = "1";//送货上门订单
    public static final String ORDER_TYPE_STORE_TAKE = "2";//门店自提订单

    //订单来源
    public static final int TRADES_SOURCE_DIRECT = 140;

    //是否为处方药
    public static final int TRADES_PRESCRIPTION_TRUE = 1;//是处方药
    public static final int TRADES_PRESCRIPTION_FALSE = 0;//不是处方药

    //订单接口响应码
    public static final String TRADES_RESP_CODE_SUCCESS = "0000";//订单创建成功
    public static final String TRADES_RESP_CODE_MISSINF_PARAMS = "0001";//必选参数为空
    public static final String TRADES_RESP_CODE_GOODS_NOT_NORMAL = "0002";//商品状态不正常，不可以购买
    public static final String TRADES_RESP_CODE_INTEGRAL_TOO = "0003";//积分超过用户现有的积分值
    public static final String TRADES_RESP_CODE_FAILED = "0004";//订单创建失败
    public static final String TRADES_RESP_CODE_PHONE_FAILED = "0005";//用户手机号码格式错误
    public static final String TRADES_RESP_CODE_SUCCESS_NOPAY = "0006";//订单创建成功零元订单不跳支付页面
    public static final String TRADES_RESP_CODE_INTEGRAL_LIMIT = "0007";//积分商品兑换已达上限
    public static final String TRADES_RESP_CODE_INTEGRAL_LIMIT_EACH = "0008";//积分商品每人兑换已达上限
    public static final String TRADES_RESP_CODE_GIFT_BUY_NUM = "0009";//赠品商品购买超过库存
    public static final String TRADES_RESP_CODE_GROUP_PURCHASE = "0010";//拼团活动数据出现异常
    public static final String TRADES_RESP_CODE_CONCESSION_ERROR = "0011";//优惠预下单计算异常

    //支付类型
    public static final String TRADES_PAY_TYPE_ALI = "ali";//支付宝
    public static final String TRADES_PAY_TYPE_WX = "wx";//微信支付
    public static final String TRADES_PAY_TYPE_BIL = "bil";//快钱
    public static final String TRADES_PAY_TYPE_UNIONPAY = "unionPay";//银联支付
    public static final String TRADES_PAY_TYPE_HEALTH_INSURANCE = "health_insurance";//医保卡
    public static final String TRADES_PAY_TYPE_CASH = "cash";//现金支付
    public static final String TRADES_PAY_TYPE_INTEGRAL = "integral";//积分兑换
    public static final String TRADES_PAY_TYPE_COLLECT = "ali,wx,bil,unionPay,health_insurance,cash,integral".toLowerCase();

    //区分交易佣金的订单类型
    public static final String TRADESCOMMISSION_ORDER_TYPE_DIRECT_PURCHASE = "0";//直购订单(送货上门、门店自提、药房直购)
    public static final String TRADESCOMMISSION_ORDER_TYPE_DISTRIBUTOR = "1";//分销订单(三级分销)

    //用户是否首单
    public static final int USER_TRADES_IS_FIRST_ORDER_YES = 1;//是首单
    public static final int USER_TRADES_IS_FIRST_ORDER_NO = 0;//不是首单

    //商家O2O开启设置标志
    public static final String SITE_O2O_CARRIAGE_CONFIG = "site_o2o_carriage_config";

    //商家O2O(51jk后台默认)
    public static final String SITE_O2O_CARRIAGE_CONFIG_FEFAULT = "site_o2o_carriage_default_config";


    //积分
    public static final int USE_CASE_REGIST = 110;

    public static final int USE_CASE_BUY = 120;

    public static final int USE_CASE_DIFF = 130;

    public static final int USE_CASE_CHECKIN = 140;

    public static final int TYPE_REGIST = 10;

    public static final int TYPE_CHECKIN = 20;

    public static final int TYPE_PERFECT_INFO = 30;

    public static final int TYPE_BUY = 40;

    public static final int TYPE_CONSULT_ASSESS = 50;

    public static final int TYPE_ORDER_ASSESS = 60;

    public static final int TYPE_ORDER_CONVENT = 70;//积分兑换商品

    public static final int TYPE_ORDER_BACK = 80;//退赠送积分

    public static final int TYPE_ORDER_DIFF = 90;//抵扣现金积分

    public static final int TYPE_ORDER_CHANGE = 100;//使用线上积分兑换成线下积分共商户使用

    public static final int STATUS_SETTING = 0;

    public static final int TYPE_GAME_ASSESS = 200;

    public static final String LOG_DESC_OTHER_ASSESS = "抽奖送积分";

    public static final String LOG_DESC_REGIST = "注册送积分";

    public static final String LOG_DESC_CHECKIN = "签到送积分";

    public static final String LOG_DESC_BUY = "购物送积分";

    public static final String LOG_DESC_PERFECT_INFO = "完善信息送积分";

    public static final String LOG_DESC_CONSULT_ASSESS = "咨询评价送积分";

    public static final String MSG_STATUS_CONSULT_ASSESS = "暂不支持咨询评价送积分";

    public static final String LOG_DESC_ORDER_ASSESS = "订单评价送积分";

    public static final String MSG_STATUS_ORDER_ASSESS = "暂不支持订单评价送积分";

    public static final String LOG_DESC_CHECKIN_SERIES = "连续签到送积分";

    public static final String LOG_DESC_DIFF = "积分抵现金";

    public static final String MSG_STATUS_REGISTER = "暂不支持注册送积分！";

    public static final String MSG_EXIST = "该用户已通过注册获得积分";

    public static final String MSG_CHECKIN_HASACTION = "已通过签到获取积分";

    public static final String MSG_CHECKIN_TOMORROW = "你今天未签到";

    public static final String MSG_STATUS_BUY = "暂不支持购物送积分";
    public static final String MSG_STATUS_REPEAT = "重复兑换";
    public static final String MSG_STATUS_TRADES = "交易不存在或状态错误";

    public static final String MSG_STATUS_DIFF = "暂不支持积分抵现金";

    public static final String MSG_STATUS_CHECKIN_T = "暂不支持签到送积分";

    public static final String MSG_MEMBER_NOTEXISTS = "该会员不存在";

    public static final String META_KEY_CLOSE = "trades_auto_close_time";
    public static final String META_KEY_AFFIRM = "trades_auto_confirm_time";
    public static final String META_KEY_FINISH = "trades_allow_refund_time";
    public static final String META_KEY_INTEGRAL_SHIPPING = "integral_shipping";


}
