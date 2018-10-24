package com.jk51.modules.distribution.util;

import com.jk51.commons.string.StringUtil;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-03-23 11:52
 * 修改记录:
 */
public class ParamFormat {

    public static Integer formatToInteger(Object obj){

        if (obj != null){
            if(obj instanceof  String && StringUtil.isNotBlank((String)obj)){
                return  Integer.parseInt((String)obj);
            }else if(obj instanceof Integer){
                return (Integer)obj;
            }
        }
        return null;
    }

    public static Long formatToLong(Object obj){

        if (obj != null){
            if(obj instanceof  String && StringUtil.isNotBlank((String)obj)){
                return  Long.parseLong((String)obj);
            }else if(obj instanceof Integer){
                return Long.valueOf((Integer)obj);
            }else if(obj instanceof Long){
                return (Long)obj;
            }
        }
        return null;
    }

    public static String formatToString(Object obj){

        if (obj != null){
            if(obj instanceof  String && StringUtil.isNotBlank((String)obj)){
                return  (String)obj;
            }else if(obj instanceof Integer){
                return (Integer)obj + "";
            }else if(obj instanceof Long){
                return (Long)obj + "";
            }
        }
        return null;
    }

}
