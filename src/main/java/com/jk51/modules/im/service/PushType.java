package com.jk51.modules.im.service;

public enum PushType {
    NOTIFY_OTHER_CLIENT_QUIT("notifyotherclientquit", "登录提醒:通知该用户的其他客户端下线"),
    ORDER_NEWORDER("neworder", "新订单提醒"),
    TASK_NEWTASK("task_newTask", "新任务提醒"),
    TASK_PUNISHMENTTASK("task_PunishmentTask", "任务惩罚提醒"),
    TASK_FINISH("task_finishTask", "任务完成提醒"),
    TASK_NEWORDERTASK("task_newOrderTask", "新订单任务提醒"),
    TASK_NEWREGISTERTASK("task_newRegisterTask", "新注册任务提醒"),
    TASK_NEWEXAM("task_newExam", "新答题任务提醒"),
    NOTIFY_CHANGE_STORES("notifychangestores", "门店调配提醒"),
    ORDER_NOTIFYSHIPMENT("order_notifyshipments", "通知发货提醒"),
    NOTIFY_CHANGE_PASSWORD("notifychangepassword", "账号密码重置提醒"),
    CONSTRAINT_OUT("constraintout", "强制退出提醒"),
    NOTIFY_SEND_COUPON("system_coupon", "系统消息"),
    NOTIFY_PAY_SUCCESS("system_payok", "支付成功消息"),
    NOTIFY_PAY_FAIL("system_payfail", "支付失败消息"),
    ORDER_Webcall("radesWebcall", "门店订单支付电话提醒"),
    TASK_VISIT("task_visit", "回访任务提醒");



    public static final String settingId = "999999";
    private String value;
    private String description;

    private PushType(String val, String desc) {
        this.value = val;
        this.description = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
