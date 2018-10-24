package com.jk51.modules.im.util;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 聊天参数
 * 作者: gaojie
 * 创建日期: 2017-06-08
 * 修改记录:
 */
public class IMParameter {


    //初始化查询数据设置参数，TBD
    static{


    }

    //单位分钟
    public static final Integer clerkTimeoutTime = 2;
    public static final Integer clerkTimeoutTimeRemind = 1;
    public static final Integer callTimeoutTime = 2;
    public static final Integer memberTimeoutTimeEvaluateTiem = 1;
    public static final Integer memberTimeoutTimeOverConversationTime = 2;



    public static final String ALLCLERKBUSY = "当前店员较忙，您可以继续输入问题，系统将尽快为您匹配服务";
    public static final String MEMBERBUSY = "本次咨询服务已结束，您可以继续对该次服务做出评价";
    public static final String CLERK_REMIND_WILL_CLOSE = "您还有60s回复会员问题，超时将关闭重新匹配店员";
    public static final String CLERK_REMIND_DID_CLOSE = "很抱歉，由于您没在规定时间内回复会员消息，系统已经重新分单。请及时回复，保证会话的体验";
    public static final String evaluated_REMIND = "会员已完成评价";
    public static final String MEMBER_BUSY_TO_CLERK = "会员长时间未发问,本次咨询结束";


    public static final Integer CALLTYPE = 2;
    public static final Integer TEXTTYPE = 1;


    public static final Integer isRace = 0;
    public static final Integer isNotRace = 1;
    public static final Integer isSystemMessage = 0;
    public static final Integer isNotSystemMessage = 1;

    //评价满意的分数
    public static final Integer SATISFACTION = 10;

    //评价一般的分数
    public static final Integer ORDINARY = 6;

    //评价不满意的分数
    public static final Integer DISSATISFIED = 1;
}
