package com.jk51.modules.appInterface.util;

import com.jk51.commons.string.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-16
 * 修改记录:
 * 验证发送消息的请求参数是否正确
 */
public class ValidateIMParameter {

    public static Map<String,Object> validateIMParameter(Map<String,Object> param){
        Map<String,Object> result = new HashMap<String,Object>();

        /*//验证appid
        if(StringUtil.isEmpty(param.get("appId"))){
            result.put("status",false);
            result.put("msg","appId不能为空");
        }*/

        //验证pushType
        if(StringUtil.isEmpty(param.get("push_type"))){
            result.put("status","ERROR");
            result.put("errorMessage","push_type不能为空");
        }
        //验证sender
        if(StringUtil.isEmpty(param.get("sender"))){
            result.put("status","ERROR");
            result.put("errorMessage","sender不能为空");
        }
        //验证receiver
        if(StringUtil.isEmpty(param.get("receiver"))){
            result.put("status","ERROR");
            result.put("errorMessage","receiver不能为空");
        }
        //验证msgType
        if(StringUtil.isEmpty(param.get("msg_type"))){
            result.put("status","ERROR");
            result.put("errorMessage","msg_type不能为空");
        }
        //验证msgContent
        if(StringUtil.isEmpty(param.get("msg_content"))){
            result.put("status","ERROR");
            result.put("msg","msg_content不能为空");
        }
        //验证msgContent
        if(StringUtil.isEmpty(param.get("site_id"))){
            result.put("status","ERROR");
            result.put("msg","site_id不能为空");
        }

        return result;
    }
}
