package com.jk51.modules.appInterface.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-05-24
 * 修改记录:
 */
public class ReturnMap extends HashMap{


    public static Map<String,Object> buildSuccessReturnMap(){
        Map<String,Object> result = new HashMap();
        result.put("status","OK");
        return result;
    }

    public static Map<String,Object> buildFailReturnMap(String errorMassage){
        Map<String,Object> result = new HashMap();
        result.put("status","ERROR");
        result.put("errorMessage",errorMassage);
        return result;
    }
}
