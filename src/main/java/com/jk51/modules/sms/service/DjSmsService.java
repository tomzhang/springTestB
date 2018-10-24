package com.jk51.modules.sms.service;

import com.jk51.commons.ccprest.result.Sms;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.sms.smsConfig.DianJiConfig;
import com.jk51.modules.sms.smsConfig.ZTSmsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.UriEncoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-07-23
 * 修改记录:
 */
@Service
public class DjSmsService {

    /*
   日志记录器
    */
    private static final Logger logger = LoggerFactory.getLogger(DjSmsService.class);

    @Autowired
    private DianJiConfig dianJiConfig;
    @Autowired
    private SmsService smsService;//短信发送平台:type：

    public String SendMessage(Integer siteId, String product_number, String oldMobile, Integer type, Integer smsType) {
        String index = "-1";
        Integer Fee = 1;
        Long currentDate = System.currentTimeMillis();
        try {
            String url = String.format(dianJiConfig.getDj_sms_url(), dianJiConfig.getDj_sms_username(), MD5Pwd(dianJiConfig.getDj_sms_password(), oldMobile, currentDate), oldMobile, SendTpl(product_number), currentDate);
            Map<String, Object> result = JacksonUtils.json2map(OkHttpUtil.get(url));
            logger.info("短信发送返回值：" + result);
            //对请求的返回值做出判断
            List<Map> resultList = JacksonUtils.json2list(JacksonUtils.obj2json(result.get("Rets")), Map.class);
            for (Map ret : resultList) {
                index = ret.get("Rspcode").toString();
                Fee = Integer.parseInt(ret.get("Fee").toString());
            }
            Map<String, Object> log = new HashMap<>();
            log.put("phone", oldMobile);
            log.put("msg", product_number);
            log.put("channel", "DJ");
            log.put("Fee", Fee);
            if (!StringUtil.isEmpty(type) && type == -1) {//服务商：给51后台发信息，不收商家费用
                smsService.insertLogToMerchant(siteId, null, null, type, JacksonUtils.mapToJson(log), String.valueOf(result));
            } else {
                smsService.insertLog(siteId, null, null, smsType, JacksonUtils.mapToJson(log), String.valueOf(result));
            }
        } catch (Exception e) {
            logger.info("点集通讯发送短信失败，原因" + e.getMessage());
        }
        return index;
    }

    /*
        选择要发送的短信语句
     */
    private String SendTpl(String product_number) {
        String stl_value = "";
        try {  //中间可能需要插入参数，不需要匹配模版，可直接定义语句
            stl_value = URLEncoder.encode(product_number, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("发送语句格式不正确", e);
        }
        return stl_value;
    }

    //对密码进行指定格式的加密
    private String MD5Pwd(String password, String mobile, Long currentDate) {
        String result = "";
        try {
            result = EncryptUtils.encryptToMD5(password + mobile + currentDate).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5加密有误", e);
        }
        return result;
    }
}
