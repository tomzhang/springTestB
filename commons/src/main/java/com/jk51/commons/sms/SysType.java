package com.jk51.commons.sms;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：短信类型
 * 作者: XC
 * 创建日期: 2018-08-30 18:32
 * 修改记录:
 **/
public class SysType {
    //登陆验证码
    public static final int LOGIN_VERIFY_CODE = 100;
    //修改密码验证码
    public static final int CHANGE_PASSWORD_VERIFY_CODE = 110;
    //店员app会员注册验证码
    public static final int CLERK_REGISTER_VERIFY_CODE = 120;
    //店员app找回密码验证码
    public static final int CLERK_RETRIEVE_PASSWORD_VERIFY_CODE = 130;
    //语音验证码
    public static final int VOICE_VERIFY_CODE_4 = 140;
    //网站登陆验证码
    public static final int WEBSITE_LOGIN_VERIFY_CODE = 150;
    //网站找回密码验证码
    public static final int WEBSITE_RETRIEVE_PASSWORD_VERIFY_CODE = 160;
    //语音验证码
    public static final int VOICE_VERIFY_CODE_7 = 170;
    //商户申请人登录注册验证码
    public static final int APPLICANT_VERIFY_CODE = 180;


    //商家发货后提醒顾客按照提货码去提货
    public static final int LADING_CODE = 200;
    //商家发货后提醒顾客提货地址
    public static final int LADING_ADRESS = 300;
    //邀请分销商加盟时的邀请码
    public static final int INVITATION_CODE = 400;
    //短信提醒顾客有新订单
    public static final int NEW_ORDER_FOR_CUSTOMER = 500;
    //短信通知顾客付款
    public static final int PAYMENT_ORDER = 600;
    //发送给顾客的营销短信
    public static final int MARKETING_MESSAGES = 700;
    //有新订单时提醒【商家总部】
    public static final int NEW_ORDER_FOR_MERCHANT = 800;
    //有新订单时提醒【门店店员】
    public static final int NEW_ORDER_FOR_CLERK = 810;
    //电话提醒，有新订单时提醒【门店店员】
    public static final int NEW_ORDER_FOR_CLERK_BY_PHONE = 820;
    //预警值提醒商家充值
    public static final int PREWARNING_VALUE = 900;
    //申请开站成功短信
    public static final int APPLY_SUCCESS = 1000;
    //申请开站失败短信
    public static final int APPLY_FAIL = 1010;
}
