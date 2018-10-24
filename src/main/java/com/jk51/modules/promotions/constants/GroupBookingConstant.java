package com.jk51.modules.promotions.constants;

import org.springframework.stereotype.Component;

/**
 * 团购活动使用到的常量
 *
 * @author zhutianqiong
 * @version coupon_thirteen_1117
 * @since 2017/11/22
 */
@Component
public class GroupBookingConstant {
    /**
     * 拼单失败处理消息队列主题
     */
    public static final String MQ_TOPIC_NAME = "GroupBookingFailOperation1";

    /**
     * 拼单人数不足提醒时间点，按百分比计算
     * 单位：%
     */
    public static final int REMIND_DURATION_BEFORE_FAIL = 40;

    /**
     * 拼单人数不足提醒的定时任务执行时间间距
     * 单位：秒
     */
    public static final int REMIND_SCHEDULE_TIME_INTERVAL = 90;

    /**
     * 拼单人数不足提醒 first
     */
    public static final String GROUP_REMIND_FIRST = "您的拼团还有%s就要到期了，还差%d个人呦，快去叫上身边的小伙伴一起拼吧~";

    /**
     * 拼单人数不足提醒 remark
     */
    public static final String GROUP_REMIND_REMARK = "点击此处邀请小伙伴拼单~";

    /**
     * 超时未付款提醒 first
     */
    public static final String GROUP_NOPAY_REMIND_FIRST = "您好，您有一笔订单还未支付，超过支付时效将自动取消，赶快支付吧~";

    /**
     * 超时未付款提醒 remark
     */
    public static final String GROUP_NOPAY_REMIND_REMARK = "点击进入订单详情页完成支付";

    public static final String GROUP_NOPAY_REMIND_KEYWORD5 = "未支付，即将自动取消";

    /**
     * 拼单失败通知（退款） first部分
     */
    public static final String GROUP_FAIL_AND_REFUND_FIRST = "您的拼单因人数不足而失败。退款已提交，" +
        "最迟1-7个工作日内会退回到您的支付账户。若使用的是微信零钱支付，退款会退至微信零钱账户内（可到微信：我-钱包-零钱内查看）；" +
        "信用卡与存储卡支付的款项会在1-3个工作日内原路径退回。";

    /**
     * 拼单失败通知（退款） remark部分
     */
    public static final String GROUP_FAIL_AND_REFUND_REMARK = "";

    /**
     * 创建拼单成功提醒 first
     */
    public static final String GROUP_START_SUCCESS_FIRST = "恭喜您完成支付，创建拼单成功啦，邀请好友参加拼单更快哦";

    /**
     * 创建拼单成功提醒 remark
     */
    public static final String GROUP_START_SUCCESS_REMARK = "点击分享给好友，拼单成功率会更高哦~";

    /**
     * 参加拼单成功提醒 first
     */
    public static final String GROUP_JOIN_SUCCESS_FIRST = "恭喜您支付完成，成功参加了%s创建的拼单，赶快邀请小伙伴一起来拼单吧~";

    /**
     * 参加拼单成功提醒 remark
     */
    public static final String GROUP_JOIN_SUCCESS_REMARK = GROUP_START_SUCCESS_REMARK;

    /**
     * 拼单成功通知 first部分
     */
    public static final String GROUP_SUCCESS_FIRST = "恭喜您拼单成功";

    /**
     * 拼单成功通知 remark部分
     */
    public static final String GROUP_SUCCESS_REMARK = "";

    public static final String GROUP_SUCCESS_KEYWORD_3 = "我们会尽快发货（一般1~7天到货），自提订单请及时到门店取货。";

    /**
     * 拼单取消通知 first部分
     */
    public static final String GROUP_CANCEL_FIRST = "真遗憾，您的拼单已取消";

    /**
     * 拼单取消通知 remark部分
     */
    public static final String GROUP_CANCEL_REMARK = "";


}
