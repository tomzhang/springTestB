package com.jk51.modules.pandian.util;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-10-19
 * 修改记录:
 */
public class Constant {

    public static final double NOT_INVENTORY_ACCOUNTING = 0;
    public static final String ONCE = "1";
    public static final int REPETITION = 500;

    public static final String REPETITIONSTR = "复盘";
    public static final String  ALLINVENTTORY = "全盘";
    public static final String  DYNAMICINVENTTORY = "动态盘点";
    public static final String  RANDOMINVENTTORY = "随机盘点";
    public static final String  NOTBATCHINVENTTORY = "无批次盘点";

    //盘点类型
    public static final Integer  ALL = 0;        //全盘
    public static final Integer  NOTBATCH = 2;    //无批次

    public static final int HAS_MODIFY = 1;

    //排序提醒类型
    public static final int REMIND_SAME = 1;
    public static final int REMIND_NOT_SAME = 2;
    public static final int NOT_REMIND = 3;
    public static final int NEXT_CHECKED = 4;
    public static final int NEXT_NOT_HAVE_INFO_IN_INVENTORY = 5;
    public static final int NEXT_NOT_HAVE_INFO_IN_REIDS = 6;
    public static final int CURRENT_NOT_HAVE_IN_REDIS = 7;
    public static final int HAS_REMIND = 8;

    public static final String CLERKS_EMPTY = "0";

    public static final String DONE = "已盘完";
    public static final String NOT_DONE = "未盘点确认";

    public static final String ALL_CLERK = "0";


    //盘点表按店员查询状态分类
    public static final Integer  GETALL = 0;
    public static final Integer  IS_CONFIRMED = 1;
    public static final Integer  NOT_CONFIRMED = 2;
}
