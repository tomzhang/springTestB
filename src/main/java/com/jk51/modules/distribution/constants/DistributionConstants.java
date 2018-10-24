package com.jk51.modules.distribution.constants;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-05-09 10:56
 * 修改记录:
 */
public class DistributionConstants {

    // 订单状态
    // 0：交易失败（退款）
    public final static Integer ORDER_SUCCESS_STATUS = 0;
    // 1：交易成功
    public final static Integer ORDER_FAIL_STATUS = 1 ;
    // 2：交易结束
    public final static Integer ORDER_END_STATUS = 2;

    // 操作类型
    // 1：奖励
    public final static Integer REWARD_TYPE = 1;
    // 2：提现
    public final static Integer WITHDRAW_TYPE = 2;

    //奖励记录表状态
    // 1：待处理
    public final static Integer WAIT_STATUS = 1;
    // 2：成功
    public final static Integer SUCCESS_STATUS = 2;
    // 3：失败
    public final static Integer FAIL_STATUS = 3;

    //推荐等级
    // 1-1级
    public final static Integer DISTRIBUTOR_LEVEL_1 = 1;
    // 2-2级
    public final static Integer DISTRIBUTOR_LEVEL_2 = 2;
    // 3-3级
    public final static Integer DISTRIBUTOR_LEVEL_3 = 3;

    //奖励状态
    // 0-待确认
    public final static Integer REWARD_WATI_STATUS = 0;
    // 1-已确认
    public final static Integer REWARD_OK_STATUS = 1;

    public final static Integer ROOT_DISTRIBUTOR_CODE = 0;

    public final static Integer MERCHANT_CODE = 2147483647;

    public final static Integer UNKNOWN_CODE = 2147483646;

    public final static Integer PAY_STATUS_FAIL = 0;

    public final static Integer PAY_STATUS_SUCCESS = 1;

}
