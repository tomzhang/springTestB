package com.jk51.modules.account.constants;

/**
 * Created by think on 2017/2/21.
 */
public class AccountConstants {

    public static final String[] PAYMENT_STATUS = {"在线支付", "交易", "买家已支付","蚂蚁优惠集分宝补贴","集分宝","红包"};//微信支付宝收款状态
    public static final String[] ALI_FASHIONABLE_STATUS = {"交易分账"};//支付宝分款状态
    public static final String[] ALI_COMMISSION_STATUS = {"收费", "服务费"};//支付宝佣金状态
    public static final String[] REFUND_STATUS = {"成功", "退款成功"};
    public static final String[] FILTER = {"在线支付", "交易", "买家已支付", "交易分账", "收费", "服务费"};//用于过滤掉这些状态之外的数据
    public static final String[] OTHER_PAY = {"health_insurance","cash"};
    public static final Integer ACCOUNT_CHECKING_STATUS_SUCCESS = 1; //支付对账成功
    public static final Integer ACCOUNT_CHECKING_STATUS_FAIL = 2; //支付对账Fail失败
    public static final String[] FILTER_PAY_TYPE = {"wx","ali","health_insurance","cash"};
    public static final Integer REFUND_CHECKING_SUCCESS = 1; //退款对账成功
    public static final Integer REFUND_CHECKING_FAIL = 0; //退款对账Fail失败
    //ali (支付宝) ，wx (微信)， bil(快钱)， unionPay(银联)， health_insurance（医保），cash（现金）
    public static final String PAY_TYPE_ALI = "ali";
    public static final String PAY_TYPE_WX = "wx";
    public static final String PAY_TYPE_BIL = "bil";
    public static final String PAY_TYPE_UNION_PAY = "unionPay";
    public static final String PAY_TYPE_HEALTH_INSURANCE = "health_insurance";
    public static final String PAY_TYPE_CASH = "cash";

    public static final String META_KEY_CLOSE = "trades_auto_close_time";
    public static final String META_KEY_AFFIRM = "trades_auto_confirm_time";
    public static final String META_KEY_FINISH = "trades_allow_refund_time";

    //pay_data_import
    public static final int PAY_DATA_IMPORT = 0;  //0=支付记录
    public static final int REFUND_DATA_IMPORT = 1; // 1=退款记录

    //SendWay 活动的发放类型 1直接发送到账户 2需领券 3发送红包 4门店 5店员',
    public static final int SEND_WAY_USER = 1;
    public static final int SEND_WAY_NEED_GET = 2; //2需领券
    public static final int SEND_WAY_RED = 3;
    public static final int SEND_WAY_STORE = 4;
    public static final int SEND_WAY_CLERK = 5;

    //error_code 结算异常订单错误码
    public static final Integer ERROR_TYPE =1;
    public static final Integer ERROR_STYLE=2;
    public static final Integer ERROR_PAYMENT=3;
    public static final Integer ERROR_PLATSPLIT=4;
    public static final Integer ERROR_TRADESSPLIT=5;
    public static final Integer ERROR_FREIGHT=6;
    public static final Integer ERROR_TRADESSTATUS=7;
    public static final Integer ERROR_DEALSTATUS=8;


    //订单状态
    public static final Integer IS_RETURNED = 900;

}
