package com.jk51.modules.sms.smsConfig;

import com.alibaba.druid.util.Base64;
import com.jk51.commons.date.DateFormatConstant;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.md5.MD5Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-02-22
 * 修改记录:
 */
public class QiMoor {

    private String accept ="application/json";
    private String content_type = "application/json;charset=utf-8";
    private String content_length = "256";
    private String timeStamp ;
    private String sig ;
    private String authoriztion;


    public QiMoor(){
        timeStamp = DateUtils.formatDate(new Date(), DateFormatConstant.LONG_TIMESTAMP_FORMAT2);
    }

    /**
     * 获取sid sig //accountID+ apisecret + timpStamp MD5 大写
     * @param accountID
     * @return
     */
    public String getSid(String accountID,String apiSecret){
        StringBuilder sb = new StringBuilder();
        sb.append(accountID);
        sb.append(apiSecret);
        sb.append(timeStamp);
        return MD5Utils.encode(sb.toString());
    }

    /**
     * 获取authoriztion  base64编码
     * @param accountID
     * @return
     */
    public String getAuthoriztion(String accountID){
        StringBuilder sb = new StringBuilder();
        sb.append(accountID);
        sb.append("\\:");
        sb.append(timeStamp);
        return Base64.byteArrayToBase64(sb.toString().getBytes());
    }

    /**
     * 得到请求头信息
     * @return
     */
    public Map<String,Object> getHeaderMap2(String accountid){
        Map<String,Object> headerMap = new HashMap<>();
        headerMap.put("Accept",accept);
        headerMap.put("Content_Type",content_type);
        headerMap.put("Content_Length",content_length);
        headerMap.put("Authorization",getAuthoriztion(accountid));
        return headerMap;
    }

    public Map<String,String> getHeaderMap(String accountid){
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Accept",accept);
        headerMap.put("Content_Type",content_type);
        headerMap.put("Content_Length",content_length);
        headerMap.put("Authorization",getAuthoriztion(accountid));
        return headerMap;
    }


    /**
     * 短信模板请求的请求体
     * @return
     */
    public Map<String ,Object> getParam(String password){
        Map<String,Object> param = new HashMap<>();
        param.put("password",password);
        return param;
    }

    /**
     * 发送短信接口的请求体
     * @param password
     * @param num
     * @param templateNum
     * @param var
     * @return
     */
    public Map<String,Object> getParam(String password,String num,String templateNum,String ...var){
        Map<String,Object> param = new HashMap<>();
        param.put("password",password);
        param.put("num",num);
        param.put("templateNum",templateNum);
        for (int i = 1;i<=var.length;i++){
            param.put("var"+i,var[i]);
        }
        return param;
    }

    /**
     * 语音请求参数
     * @param action
     * @param pbx
     * @param account
     * @param serviceNo
     * @param exten
     * @param timeout
     * @param maxCallTime
     * @param variable
     * @return
     */
    public Map<String,Object> getParam(String action,String pbx,String account,String serviceNo,String exten,String timeout,String maxCallTime,String ... variable )
    {
        Map<String,Object>  param = new HashMap<>();
        param.put("Action",action);
        param.put("PBX",pbx);
        param.put("Account",account);
        param.put("ServiceNo",serviceNo);
        param.put("Exten",exten);
        param.put("Timeout",timeout);
        param.put("MaxCallTime",maxCallTime);
        //拼接参数
        String var ="";
        for (int i = 1;i<=variable.length;i++){
            var+=("var"+i+":"+variable[i]+",");
        }
        var = var.substring(0,var.length()-1);
        param.put("Variable",var);

        return param;

    }

}
