package com.jk51.modules.appInterface.util;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-23
 * 修改记录:
 */
public class CallInterface {


    private static Logger logger = LoggerFactory.getLogger(CallInterface.class);

    /**
     *post 调用接口,没有返回结果
     *
     * @param url
     * @param param
     * @param errorMessage
     * *@return ReturnDto
     * */
    public static ReturnDto getPostNoValue(String url, Map<String, Object> param, String errorMessage){


        String str = "";
        try {
            str = HttpClient.doHttpPost(url,param);
        } catch (IOException e) {
            logger.error(errorMessage,e);
            return ReturnDto.buildStatusERRO(errorMessage);
        }

        Map<String,Object> resultMap = new HashMap<>();
        try {
            resultMap = JacksonUtils.json2map(str);
        } catch (Exception e) {
            logger.error(errorMessage,e);
            return ReturnDto.buildStatusERRO(errorMessage);
        }

        if(StringUtil.isEmpty(resultMap)|| StringUtil.isEmpty(resultMap.get("status"))||!resultMap.get("status").equals("OK")){

            if(!StringUtil.isEmpty(resultMap.get("errorMessage"))){
                return ReturnDto.buildStatusERRO((String) resultMap.get("errorMessage"));
            }else{
                return ReturnDto.buildStatusERRO(errorMessage);
            }

        }else{
            return ReturnDto.buildStatusOK();
        }
    }

   /* *//**
     *post 调用接口,有返回结果
     *
     * @param url
     * @param param
     * @param errorMessage
     * *@return ReturnDto
     * *//*
    public static ReturnDto getPostValue(String url, Map<String, Object> param, String errorMessage){


        String str = "";
        try {
            str = HttpClient.doHttpPost(url,param);
        } catch (IOException e) {
            logger.error(errorMessage,e);
            return ReturnDto.buildFailReturnDto(errorMessage);
        }

        Map<String,Object> resultMap = new HashMap<>();
        try {
            resultMap = JacksonUtils.json2map(str);
        } catch (Exception e) {
            logger.error(errorMessage,e);
            return ReturnDto.buildFailReturnDto(errorMessage);
        }

        if(StringUtil.isEmpty(resultMap)||StringUtil.isEmpty(resultMap.get("status"))||!resultMap.get("status").equals("OK")){
            return ReturnDto.buildFailReturnDto(errorMessage);
        }else{
            return ReturnDto.buildSuccessReturnDto();
        }
    }*/
}
