package com.jk51.modules.sms.smsConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-21
 * 修改记录:上海正通短信平台配置文件读取
 */
@Component
@ConfigurationProperties(prefix = "sms.zt")
public class ZTSmsConfig {
    //请求短信请求地址
    private String zt_sms_url;
    //语音请求地址
    private String zt_yysms_url;
    //用户名
    private String zt_sms_username;
    //发送密码
    private String zt_sms_password;

    public String getZt_sms_url() {
        return zt_sms_url;
    }

    public void setZt_sms_url(String zt_sms_url) {
        this.zt_sms_url = zt_sms_url;
    }

    public String getZt_yysms_url() {
        return zt_yysms_url;
    }

    public void setZt_yysms_url(String zt_yysms_url) {
        this.zt_yysms_url = zt_yysms_url;
    }

    public String getZt_sms_username() {
        return zt_sms_username;
    }

    public void setZt_sms_username(String zt_sms_username) {
        this.zt_sms_username = zt_sms_username;
    }

    public String getZt_sms_password() {
        return zt_sms_password;
    }

    public void setZt_sms_password(String zt_sms_password) {
        this.zt_sms_password = zt_sms_password;
    }

}
