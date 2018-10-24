package com.jk51.modules.sms.service;

import com.jk51.commons.ccprest.result.Sms;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.offline.mapper.SMSLogMapper;
import com.jk51.modules.sms.exception.IllegalParameterException;
import com.jk51.modules.sms.smsConfig.SmsParams;
import com.jk51.modules.sms.smsConfig.YpSmsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-17
 * 修改记录:
 */
@Service
public class YpSmsService {
    private static final Logger log = LoggerFactory.getLogger(YpSmsService.class);

    private static final String APP_KEY = "07c4972e053a25f543eae0d16b62015c";

    @Autowired
    private YpSmsConfig ypConfig;
    //模板发送接口的http地址
    private static String ENCODING = "UTF-8";

    @Autowired
    private SmsService smsService;

    /* 单条短信发送,智能匹配短信模板
     * @param apikey 短信验证API
     * @param text   需要使用已审核通过的模板或者默认模板
     * @param mobile 接收的手机号,仅支持单号码发送
     */
    /*public int SendMessage(Integer siteId, int tpl_id, String phone, String code,Integer smsType) {
        String word = "【51健康】51健康提示，亲爱的用户，验证码是 " + code + "。如非本人操作，请忽略本短信。";
        Map<String, String> params = new HashMap<String, String>();//请求参数集合
        params.put("apikey", ypConfig.getYp_sms_appkey());
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", SendTemplate(code));
        params.put("mobile", phone);
        int result = Sms.singleSend(ypConfig.getYp_sms_url(), params);
        Map<String, Object> log = new HashMap<>();
        log.put("phone", phone);
        log.put("msg", word);
        log.put("channel", "YP");
        smsService.insertLog(siteId, null, null, smsType, JacksonUtils.mapToJson(log), String.valueOf(result));
        return result;
    }*/

    /*
    根据不同的要求匹配不同的模版发送信息
    并填入相关信息
    返回需要的短信内容
    建议：模版按照数字编排需要替换的字段，并统一字段数量
     */
    //验证码
    /*public String SendTemplate(String code) {
        //不同的业务请求要写不同的输出语句，这里只需要将模版中的字段替换即可
        String tpl_value = "，验证码是" + code;
        try {
            tpl_value = URLEncoder.encode("#content#", ENCODING) + "="
                + URLEncoder.encode(tpl_value, ENCODING) + "&"
                + URLEncoder.encode("#header#", ENCODING) + "="
                + URLEncoder.encode("51健康", ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("要求输出的语句字段和输入的字段不匹配", e);
        }
        return tpl_value;
    }*/

    //医生
    /*public int sendDoctor(Integer siteId, int tpl_id, String phone, String code) {
        String word = "【51健康】恭喜您" + phone + "已成功预约" + code + " ，到店后请出示本通知短信进行签到，请注意医生的排班信息以免延误。";
        Map<String, String> params = new HashMap<String, String>();//请求参数集合
        System.out.println(ypConfig.getYp_sms_appkey());
        System.out.println(ypConfig.getYp_sms_url());
        params.put("apikey", ypConfig.getYp_sms_appkey());
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", sendDoctor_tpl(phone, code));
        params.put("mobile", phone);
        int result = Sms.singleSend(ypConfig.getYp_sms_url(), params);
        Map<String, Object> log = new HashMap<>();
        log.put("phone", phone);
        log.put("msg", word);
        log.put("channel", "YP");
        smsService.insertLog(siteId, null, null, null, JacksonUtils.mapToJson(log), String.valueOf(result));
        return result;
    }*/

    //医生魔板
   /* public String sendDoctor_tpl(String phone, String code) {
        //不同的业务请求要写不同的输出语句，这里只需要将模版中的字段替换即可
        String tpl_value = "";
        try {
            tpl_value = URLEncoder.encode("#phone#", ENCODING) + "="
                + URLEncoder.encode(phone, ENCODING) + "&"
                + URLEncoder.encode("#name#", ENCODING) + "="
                + URLEncoder.encode(code, ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("要求输出的语句字段和输入的字段不匹配", e);
        }
        return tpl_value;
    }*/

    /**
     * 发送订单短信
     */
    /*public Integer SendOrderTemplate(int tpl_id, String phone, String header, String orderType, String tradesId) {
        if (StringUtil.isEmpty(phone)) {
            return -1;
        }
        Map<String, String> params = new HashMap<String, String>();//请求参数集合
        System.out.println(ypConfig.getYp_sms_appkey());
        System.out.println(ypConfig.getYp_sms_url());
        params.put("apikey", ypConfig.getYp_sms_appkey());
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", SendOrdertpl(header, orderType, tradesId));
        params.put("mobile", phone);
        return Sms.singleSend(ypConfig.getYp_sms_url(), params);
    }*/

    /**
     * 订单模板
     */
   /* public String SendOrdertpl(String header, String orderType, String tradesId) {
        //不同的业务请求要写不同的输出语句，这里只需要将模版中的字段替换即可
        String tpl_value = "";
        try {
            tpl_value = URLEncoder.encode("#header#", ENCODING) + "="
                + URLEncoder.encode(header, ENCODING) + "&"
                + URLEncoder.encode("#ordertype#", ENCODING) + "="
                + URLEncoder.encode(orderType, ENCODING) + "&"
                + URLEncoder.encode("#order#", ENCODING) + "="
                + URLEncoder.encode(tradesId, ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("要求输出的语句字段和输入的字段不匹配", e);
        }
        return tpl_value;
    }*/

    /**
     * 发送订单短信模版2
     */
    /*public Integer  SendOrderTemplate2(Integer siteId, int tpl_id, String phone, String ordertype, String order, String storename,Integer type) {
        String word = "【51健康】你有新" + ordertype + "订单" + order + ",请" + storename + "尽快处理。";
        if (StringUtil.isEmpty(phone)) {
            return -1;
        }
        Map<String, String> params = new HashMap<String, String>();//请求参数集合
        System.out.println(ypConfig.getYp_sms_appkey());
        System.out.println(ypConfig.getYp_sms_url());
        params.put("apikey", ypConfig.getYp_sms_appkey());
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", SendOrdertpl2(ordertype, order, storename));
        params.put("mobile", phone);
        int result = Sms.singleSend(ypConfig.getYp_sms_url(), params);
        Map<String, Object> log = new HashMap<>();
        log.put("phone", phone);
        log.put("msg", word);
        log.put("channel", "YP");
        smsService.insertLog(siteId, null, null, type, JacksonUtils.mapToJson(log), String.valueOf(result));
        return result;
    }*/

    /**
     * 订单模板
     */
 /*   public String SendOrdertpl2(String ordertype, String order, String storename) {
        //不同的业务请求要写不同的输出语句，这里只需要将模版中的字段替换即可
        String tpl_value = "";
        try {
            tpl_value = URLEncoder.encode("#ordertype#", ENCODING) + "="
                + URLEncoder.encode(ordertype, ENCODING) + "&"
                + URLEncoder.encode("#order#", ENCODING) + "="
                + URLEncoder.encode(order, ENCODING) + "&"
                + URLEncoder.encode("#storename#", ENCODING) + "="
                + URLEncoder.encode(storename, ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("要求输出的语句字段和输入的字段不匹配", e);
        }
        return tpl_value;
    }*/

    /**
     * 发送提货码短信
     */
    public Integer SendGetGoods(int tpl_id, String phone, String header, String code, String etime, String storephone) {
        Map<String, String> params = new HashMap<String, String>();//请求参数集合
        System.out.println(ypConfig.getYp_sms_appkey());
        System.out.println(ypConfig.getYp_sms_url());
        params.put("apikey", ypConfig.getYp_sms_appkey());
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", SendGetGoodsTemplate(header, code, etime, storephone));
        params.put("mobile", phone);
        return Sms.singleSend(ypConfig.getYp_sms_url(), params);
    }

    /**
     * 提货码模板
     */
    public String SendGetGoodsTemplate(String header, String code, String etime, String phone) {
        //不同的业务请求要写不同的输出语句，这里只需要将模版中的字段替换即可
        String tpl_value = "";
        try {
            tpl_value = URLEncoder.encode("#header#", ENCODING) + "="
                + URLEncoder.encode(header, ENCODING) + "&"
                + URLEncoder.encode("#code#", ENCODING) + "="
                + URLEncoder.encode(code, ENCODING) + "&"
                + URLEncoder.encode("#etime#", ENCODING) + "="
                + URLEncoder.encode(etime, ENCODING) + "&"
                + URLEncoder.encode("#phone#", ENCODING) + "="
                + URLEncoder.encode(phone, ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("要求输出的语句字段和输入的字段不匹配", e);
        }
        return tpl_value;
    }

    /**
     * 发送提货码短信
     */
    public Integer SendGetGoods2(Integer siteId, int tpl_id, String mobile, String shortMessageSign, String storeAddress, String sellphone, String SelfTakenCode,Integer type) {
        String word = "【51健康】" + shortMessageSign + "提醒您到" + storeAddress + "提货，电话：" + sellphone + "，提货码：" + SelfTakenCode + "。";
        Map<String, String> params = new HashMap<String, String>();//请求参数集合
        System.out.println(ypConfig.getYp_sms_appkey());
        System.out.println(ypConfig.getYp_sms_url());
        params.put("apikey", ypConfig.getYp_sms_appkey());
        params.put("tpl_id", String.valueOf(tpl_id));
        String tpl_value = "";
        try {
            tpl_value = URLEncoder.encode("#messagesign#", ENCODING) + "="
                + URLEncoder.encode(shortMessageSign, ENCODING) + "&"
                + URLEncoder.encode("#address#", ENCODING) + "="
                + URLEncoder.encode(storeAddress, ENCODING) + "&"
                + URLEncoder.encode("#sellphone#", ENCODING) + "="
                + URLEncoder.encode(sellphone, ENCODING) + "&"
                + URLEncoder.encode("#takencode#", ENCODING) + "="
                + URLEncoder.encode(SelfTakenCode, ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("要求输出的语句字段和输入的字段不匹配", e);
        }
        params.put("tpl_value", tpl_value);
        params.put("mobile", mobile);
        int result = Sms.singleSend(ypConfig.getYp_sms_url(), params);
        Map<String, Object> log = new HashMap<>();
        log.put("phone", mobile);
        log.put("msg", word);
        log.put("channel", "YP");
        smsService.insertLog(siteId, null, null, type, JacksonUtils.mapToJson(log), String.valueOf(result));
        return result;
    }

    /**
     * 发送分销短信
     */
   /* public Integer sendValiSMS(Integer siteId, int tpl_id, String phone, String code, String name, String url,Integer type) {
        String word = "【51健康】我悄悄告诉你一个验证码" + code + "，你就可以加入" + name + "了，戳下载" + url + "";
        Map<String, String> params = new HashMap<String, String>();//请求参数集合
        params.put("apikey", ypConfig.getYp_sms_appkey());
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", SendValiTemplate(code, name, url));
        params.put("mobile", phone);
        int result = Sms.singleSend(ypConfig.getYp_sms_url(), params);
        Map<String, Object> log = new HashMap<>();
        log.put("phone", phone);
        log.put("msg", word);
        log.put("channel", "YP");
        smsService.insertLog(siteId, null, null, type, JacksonUtils.mapToJson(log), String.valueOf(result));
        return result;
    }*/

    /**
     * 分销短信模板
     */
   /* public String SendValiTemplate(String code, String name, String url) {
        //不同的业务请求要写不同的输出语句，这里只需要将模版中的字段替换即可
        String tpl_value = "";
        try {
            tpl_value = URLEncoder.encode("#code#", ENCODING) + "="
                + URLEncoder.encode(code, ENCODING) + "&"
                + URLEncoder.encode("#name#", ENCODING) + "="
                + URLEncoder.encode(name, ENCODING) + "&"
                + URLEncoder.encode("#URL#", ENCODING) + "="
                + URLEncoder.encode(url, ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("要求输出的语句字段和输入的字段不匹配", e);
        }
        return tpl_value;
    }*/

    /**
     * app发送收款页面地址给用户
     *
     * @param tpl_id
     * @param mobile
     * @param merchantName
     * @param storeName
     * @param storeAdminName
     * @param money
     * @param url
     * @return
     */
   /* public String sendAppAddress(Integer siteId, int tpl_id, String mobile, String merchantName, String storeName, String storeAdminName, String money, String url,Integer type) {
        String word = "【51健康】" + merchantName + "" + storeName + "" + storeAdminName + "店员发来了一个待付款订单，需付金额：" + money + "元。点击链接完成付款:" + url + "";
        Map<String, String> params = new HashMap<String, String>();//请求参数集合
        String tpl_value = "";
        try {
            tpl_value = URLEncoder.encode("#mName#", ENCODING) + "="
                + URLEncoder.encode(merchantName, ENCODING) + "&"
                + URLEncoder.encode("#sName#", ENCODING) + "="
                + URLEncoder.encode(storeName, ENCODING) + "&"
                + URLEncoder.encode("#sadminName#", ENCODING) + "="
                + URLEncoder.encode(storeAdminName, ENCODING) + "&"
                + URLEncoder.encode("#money#", ENCODING) + "="
                + URLEncoder.encode(money, ENCODING) + "&"
                + URLEncoder.encode("#url#", ENCODING) + "="
                + URLEncoder.encode(url, ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("要求输出的语句字段和输入的字段不匹配", e);
        }
        params.put("apikey", ypConfig.getYp_sms_appkey());
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", tpl_value);
        params.put("mobile", mobile);
        int result = Sms.singleSend(ypConfig.getYp_sms_url(), params);
        Map<String, Object> log = new HashMap<>();
        log.put("phone", mobile);
        log.put("msg", word);
        log.put("channel", "YP");
        smsService.insertLog(siteId, null, null, type, JacksonUtils.mapToJson(log), String.valueOf(result));
        return word;
    }*/

    /**
     * 创建优惠活动时发送短信
     *
     * @param tpl_id
     * @param mobile
     * @return
     */
    public String activitySMS(Integer siteId, int tpl_id, String mobile, String mName, String time, String title, String texturl,Integer type) {
        String word = "尊敬的用户" + mName + "微商城" + time + "" + title + "会员福利，点击查看详情:" + texturl + "。回T退订";
        Map<String, String> params = new HashMap<String, String>();//请求参数集合
        String tpl_value = "";
        try {
            tpl_value = URLEncoder.encode("#mName#", ENCODING) + "="
                + URLEncoder.encode(mName, ENCODING) + "&"
                + URLEncoder.encode("#time#", ENCODING) + "="
                + URLEncoder.encode(time, ENCODING) + "&"
                + URLEncoder.encode("#title#", ENCODING) + "="
                + URLEncoder.encode(title, ENCODING) + "&"
                + URLEncoder.encode("#url#", ENCODING) + "="
                + URLEncoder.encode(texturl, ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("要求输出的语句字段和输入的字段不匹配", e);
        }
        params.put("apikey", APP_KEY);
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", tpl_value);
        params.put("mobile", mobile);
        int result = Sms.singleSend(ypConfig.getYp_sms_url(), params);
        Map<String, Object> log = new HashMap<>();
        log.put("phone", mobile);
        log.put("msg", word);
        log.put("channel", "YP");
        smsService.insertLog(siteId, null, null, type, JacksonUtils.mapToJson(log), String.valueOf(result));
        return String.valueOf(result);
    }

    /**
     * 发送提货码短信
     */
    /*public Integer ladingCode2(Integer siteId, String sendphone, String head, String storeName, String address, String storephone, String incode, String url, Integer tpl_id,Integer type) {
        String word = "" + head + "提醒您到" + storeName + "提货；地址：" + address + "；电话：" + storephone + "；提货码：" + incode + "点击查看条形码" + url + "";
        Map<String, String> params = new HashMap<String, String>();//请求参数集合
        System.out.println(ypConfig.getYp_sms_appkey());
        System.out.println(ypConfig.getYp_sms_url());
        params.put("apikey", ypConfig.getYp_sms_appkey());
        params.put("tpl_id", String.valueOf(tpl_id));
        String tpl_value = "";
        try {
            tpl_value = URLEncoder.encode("#head#", ENCODING) + "="
                + URLEncoder.encode(head, ENCODING) + "&"
                + URLEncoder.encode("#storeName#", ENCODING) + "="
                + URLEncoder.encode(storeName, ENCODING) + "&"
                + URLEncoder.encode("#address#", ENCODING) + "="
                + URLEncoder.encode(address, ENCODING) + "&"
                + URLEncoder.encode("#storephone#", ENCODING) + "="
                + URLEncoder.encode(storephone, ENCODING) + "&"
                + URLEncoder.encode("#incode#", ENCODING) + "="
                + URLEncoder.encode(incode, ENCODING) + "&"
                + URLEncoder.encode("#url#", ENCODING) + "="
                + URLEncoder.encode(url, ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("要求输出的语句字段和输入的字段不匹配", e);
        }
        params.put("tpl_value", tpl_value);
        params.put("mobile", sendphone);
        int result = Sms.singleSend(ypConfig.getYp_sms_url(), params);
        Map<String, Object> log = new HashMap<>();
        log.put("phone", sendphone);
        log.put("msg", word);
        log.put("channel", "YP");
        smsService.insertLog(siteId, null, null, type, JacksonUtils.mapToJson(log), String.valueOf(result));
        return result;
    }*/
    public int sendNormalMessage(int tpl_id, SmsParams smsParams ,String word,String paramName) {
        String phone = smsParams.getPhone();
        if (StringUtil.isEmpty(phone)) {
            return -1;
        }
        Integer siteId = smsParams.getSiteId();
        Integer smsType = smsParams.getSmsType();
        String[] args = smsParams.getArgs();
        if(tpl_id==1402527){
            args[0] = "，验证码是" + args[0];
        }

        String[] paramNames = paramName.split(",");

        Map<String, String> params = new HashMap<String, String>();//请求参数集合
        if(tpl_id==2143198){
            params.put("apikey", APP_KEY);
        }else {
            params.put("apikey", ypConfig.getYp_sms_appkey());
        }
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", sendNormalTemplate(args,paramNames));
        params.put("mobile", phone);
        int result = Sms.singleSend(ypConfig.getYp_sms_url(), params);
        Map<String, Object> log = new HashMap<>();
        log.put("phone", phone);
        log.put("msg", word);
        log.put("channel", "YP");
        smsService.insertLog(siteId, null, null, smsType, JacksonUtils.mapToJson(log), String.valueOf(result));
        return result;
    }

    public String sendNormalTemplate(String[] args,String[] paramNames){
        String tpl_value = "";
        if(paramNames.length!=args.length){
            throw new IllegalParameterException("参数不合法");
        }
        try {
            for (int i=0; i<args.length; i++){
                if(tpl_value!=""){
                    tpl_value = tpl_value + "&" + URLEncoder.encode("#"+paramNames[i]+"#", ENCODING) + "="
                        + URLEncoder.encode(args[i], ENCODING);
                }else {
                    tpl_value = URLEncoder.encode("#"+paramNames[i]+"#", ENCODING) + "="
                        + URLEncoder.encode(args[i], ENCODING);
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("要求输出的语句字段和输入的字段不匹配", e);
        }
        return tpl_value;
    }
}
