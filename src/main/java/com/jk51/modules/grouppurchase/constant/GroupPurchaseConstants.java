package com.jk51.modules.grouppurchase.constant;

import com.jk51.modules.grouppurchase.response.GroupInfo;

/**
 * Created by ztq on 2018/3/7
 * Description:
 */
public class GroupPurchaseConstants {

    /**
     * 团状态值
     * {@link GroupInfo#groupStatus}
     */
    public static final int GROUP_ING = 10; // 拼团中
    public static final int GROUP_SUCCESS = 20; // 拼团成功
    public static final int GROUP_FAIL = 30; // 拼团失败

    /**
     * 主状态值（给为微信端使用）
     * {@link GroupInfo#mainStatus}
     */
    public static final int MAIN_STATUS_OPEN_UNPAY = 11; // 开团成功（付款）
    public static final int MAIN_STATUS_OPEN_PAY = 12; // 开团成功（付款）
    public static final int MAIN_STATUS_JOIN_UNPAY = 13; // 参团成功（未付款）
    public static final int MAIN_STATUS_JOIN_PAY = 14; // 参团成功（付款）
    public static final int MAIN_STATUS_SUCCESS = 21; // 拼团成功
    public static final int MAIN_STATUS_FAIL_SYSTEM = 31; // 拼团失败（系统）
    public static final int MAIN_STATUS_FAIL_MERCHANT = 32; // 拼团失败（商家）
    public static final int MAIN_STATUS_FAIL_CUSTOMER = 33; // 拼团失败（顾客）



}
