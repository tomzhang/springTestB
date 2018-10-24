package com.jk51.commons.time;

import java.text.SimpleDateFormat;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-03-15
 * 修改记录:
 */
public class TimeUtil {
    public static String getTimes(Object object){
        try {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(object);
        }catch (Exception e){
            return null;
        }
    }
}
