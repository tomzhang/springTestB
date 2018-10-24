package com.jk51.modules.sms.smsConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-17
 * 修改记录:云片配置文件读取
 */
@Component
@ConfigurationProperties(prefix = "sms.yp")
public class YpSmsConfig {
    //请求APIKEY
    private String yp_sms_appkey;
    //请求地址
    private String yp_sms_url;

    public String getYp_sms_appkey() {
        return yp_sms_appkey;
    }

    public void setYp_sms_appkey(String yp_sms_appkey) {
        this.yp_sms_appkey = yp_sms_appkey;
    }

    public String getYp_sms_url() {
        return yp_sms_url;
    }

    public void setYp_sms_url(String yp_sms_url) {
        this.yp_sms_url = yp_sms_url;
    }
}
