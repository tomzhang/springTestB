package com.jk51.modules.sms.service;

import com.jk51.commons.ccprest.result.Sms;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.modules.offline.mapper.SMSLogMapper;
import com.jk51.modules.sms.smsConfig.ZTSmsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-21
 * 修改记录:
 */
@Service
public class ZtSmsService {
    /*
    日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(ZtSmsService.class);

    @Autowired
    private ZTSmsConfig ztConfig;
    @Autowired
    private SmsService smsService;//短信发送平台:type：
    /*
    发送短信通知
         params:
         username	用户名（必填）
        password	密码（必填，可MD5加密,小写）
        content	发送内容（必填）
        product_number	产品ID
        dstime	定时时间，为空时表示立即发送（选填）格式：20130202120212
        xh	扩展的小号,必须为数字，没有请留空
        repeat	发送不可重复的短信, 1时可以重复，0不能重复
     */

    public String SendMessage(Integer siteId, String product_number, String oldMobile,Integer type,Integer smsType) {
        String index = "-1";
        String currentDate = DateUtils.GetCurrentTimeByType();
        String url = String.format(ztConfig.getZt_sms_url(), ztConfig.getZt_sms_username(), MD5Pwd(ztConfig.getZt_sms_password(), currentDate), currentDate, oldMobile, SendTpl(product_number), "727727");
//        String result = Sms.httpRequest(url, "POST");
        String result = OkHttpUtil.get(url);
        //对请求的返回值做出判断
        if (result.length() > 2 && result.substring(0, 1).equalsIgnoreCase("1")) {
            index = "0";
        }
        Map<String, Object> log = new HashMap<>();
        log.put("phone", oldMobile);
        log.put("msg", product_number);
        log.put("channel", "ZT");
        smsService.insertLog(siteId, null, null, smsType, JacksonUtils.mapToJson(log), String.valueOf(result));
        return index;
    }

    //给商户发信息，不调用短信费率
    public String SendMessage2(Integer siteId, String product_number, String oldMobile,Integer type) {
        String index = "-1";
        String currentDate = DateUtils.GetCurrentTimeByType();
        String url = String.format(ztConfig.getZt_sms_url(), ztConfig.getZt_sms_username(), MD5Pwd(ztConfig.getZt_sms_password(), currentDate), currentDate, oldMobile, SendTpl(product_number), "727727");
        String result = Sms.httpRequest(url, "POST");
        //对请求的返回值做出判断
        if (result.length() > 2 && result.substring(0, 1).equalsIgnoreCase("1")) {
            index = "0";
        }
        Map<String, Object> log = new HashMap<>();
        log.put("phone", oldMobile);
        log.put("msg", product_number);
        log.put("channel", "ZT");
        smsService.insertLogToMerchant(siteId, null, null, type, JacksonUtils.mapToJson(log), String.valueOf(result));
        return index;
    }

    /*
        选择要发送的短信语句
     */
    public String SendTpl(String product_number) {
        String stl_value = "";
        //中间可能需要插入参数，不需要匹配模版，可直接定义语句
        try {
            stl_value = URLEncoder.encode(product_number, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("发送语句格式不正确", e);
        }
        return stl_value;
    }

    //发送语音验证信息
    public String SendSoundMessage(String mobile) {
        String currentDate = DateUtils.GetCurrentTimeByType();
        String url = String.format(ztConfig.getZt_yysms_url(), ztConfig.getZt_sms_username(), MD5Pwd(ztConfig.getZt_sms_password(), currentDate), currentDate, mobile, SendSoundNum());
//        String result = Sms.httpRequest(url, "POST");
        String result = OkHttpUtil.get(url);
        return result;
    }

    //生成指定的语音验证码：必须为6位数字
    public String SendSoundNum() {
        //规则待定
        return "123456";
    }

    //对密码进行指定格式的加密
    public String MD5Pwd(String password, String currentDate) {
        String result = "";
        try {
            result = EncryptUtils.encryptToMD5(EncryptUtils.encryptToMD5(password).toLowerCase() + currentDate).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5加密有误", e);
        }
        return result;
    }
}
