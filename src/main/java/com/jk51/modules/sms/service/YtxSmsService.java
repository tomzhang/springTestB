package com.jk51.modules.sms.service;

import com.alibaba.rocketmq.shade.io.netty.util.internal.StringUtil;
import com.cloopen.rest.sdk.CCPRestSDK;
import com.jk51.commons.ccprest.result.BaseResult;
import com.jk51.modules.sms.smsConfig.YtxSmsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 备用 备用
 * 作者: chen
 * 创建日期: 2017-02-17
 * 修改记录:
 */
@Service
public class YtxSmsService {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(YtxSmsService.class);

    @Autowired
    private YtxSmsConfig ytx;

    /**
     * 发送通知
     * @param tel
     * @param verifyName
     * @return
     */
    public BaseResult sendMsgByYtx(String tel, String ...verifyName){
        HashMap<String, Object> result = null;
        //初始化SDK
        CCPRestSDK restAPI = getRestAPI(ytx.getYtx_sms_url(), ytx.getYtx_sms_port(),ytx.getYtx_sms_sid(),ytx.getYtx_sms_token(),ytx.getYtx_sms_appid());
        //获取结果
        result = restAPI.sendTemplateSMS(tel, ytx.getYtx_sms_tempid_msg(), verifyName);
        return status(result);

    }

    /**
     * 发送验证码
     * @param tel
     * @param regCode
     * @param verifyName
     * @return
     */
    public BaseResult sendRegCodeByYtx(String tel, String regCode ,String verifyName){

        HashMap<String, Object> result = null;
        //初始化SDK
        CCPRestSDK restAPI = getRestAPI(ytx.getYtx_sms_url(), ytx.getYtx_sms_port(),ytx.getYtx_sms_sid(),ytx.getYtx_sms_token(),ytx.getYtx_sms_appid());

        String smsValidMin=ytx.getYtx_sms_valid_min();	//验证码过期时间
        if(StringUtil.isNullOrEmpty(smsValidMin)){
            smsValidMin="5";
        }
        //发送请求接受结果
        result = restAPI.sendTemplateSMS(tel, ytx.getYtx_sms_tempid_regcode(),new String[]{regCode,verifyName, smsValidMin});
        return status(result);

    }

    /**
     * 初始化SDK
     * @param url
     * @param port
     * @param sid
     * @param token
     * @param appid
     * @return
     */
    private CCPRestSDK getRestAPI(String url,String port,String sid,String token,String appid){
        CCPRestSDK restAPI = new CCPRestSDK();

        restAPI.init(url, port);

        restAPI.setAccount(sid, token);//账号、密码

        restAPI.setAppId(appid);//应用ID
        return restAPI;
    }

    /**
     * 请求结果解析
     * @param result
     * @return
     */
    private BaseResult status( HashMap<String, Object> result){
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
//            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
//            Set<String> keySet = data.keySet();
//            for(String key:keySet){
//                Object object = data.get(key);
//                System.out.println(key +" = "+object);
//            }
            return BaseResult.success();
        }else{
            //异常返回输出错误码和错误信息
            String msg = "错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg");
            logger.error(msg);
            return BaseResult.failed();
        }
    }

}
