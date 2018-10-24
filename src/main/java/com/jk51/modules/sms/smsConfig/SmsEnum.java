package com.jk51.modules.sms.smsConfig;

public enum SmsEnum {
    /*
    *
    * */
    VERIFY_CODE(1,"验证码"),

    LOGISTICS_SMS(2,"物流短信"),

    ORDER_SMS(3,"订单消息"),

    DOCTOR_SMS(4,"医生预约"),

    THREE_DISTRIBUTION_SMS(5,"三级分销"),

    ORDER_SMS_APP(6,"发送手机订单短信(App)"),

    SEND_ORDER_ADDRESS(7,"App短信发送收款页面地址给顾客"),

    LADING_CODE_NEW(8,"提货码新版"),

    ACTIVITY_SMS(9,"创建优惠活动时发送短信"),

    SERVICE_CHARGE_SH(10,"充值成功短信(商户接收)"),

    SERVICE_CHARGE_HT(11,"充值成功短信(后台接收)"),

    SERVICE_CLOSE_SH(12,"信用用完提醒（停止商家服务  商户接收）"),

    SERVICE_CLOSE_HT(13,"信用用完提醒（停止商家服务  后台接收）"),

    SERVICE_BELOW_VALUE(14,"低于预警值"),

    SERVICE_LACK_BALANCE_SH(15,"余额不足提醒(商户接收)"),

    SERVICE_LACK_BALANCE_HT(16,"5余额不足提醒(后台接收)"),

    APPLY_SUCCESS(17,"开站申请通过提醒"),

    APPLY_FAIL(18,"开站申请未通过提醒");




    //模板id
    private int tpl_id;
    //描述
    private String desc;
    private SmsEnum(int tpl_id,String desc){
        this.tpl_id = tpl_id;
        this.desc = desc;
    }

    public int getTpl_id() {
        return tpl_id;
    }

    public void setTpl_id(int tpl_id) {
        this.tpl_id = tpl_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
