package com.jk51.modules.pandian.util;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-03
 * 修改记录:
 */
public class StatusConstant {


    //'盘点状态: 0待上传，100待下发，200已下发待确认，300已确认待审核，400已审核，500复盘,600关闭',

    public static final int WAITUPLOAD = 0;
    public static final int WAITSTART = 100;
    public static final int WAITCONFIRM = 200;
    public static final int WAITAUDIT = 300;
    public static final int AUDITED = 400;
    public static final int REPEATION = 500;
    public static final int CLOSE = 600;
}
