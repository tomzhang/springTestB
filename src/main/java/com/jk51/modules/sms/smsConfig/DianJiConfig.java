package com.jk51.modules.sms.smsConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-07-23
 * 修改记录:
 */
@Component
@ConfigurationProperties(prefix = "sms.dj")
public class DianJiConfig {
    //请求短信请求地址
    private String dj_sms_url;
    //用户名
    private String dj_sms_username;
    //发送密码
    private String dj_sms_password;

    public String getDj_sms_url() {
        return dj_sms_url;
    }

    public void setDj_sms_url(String dj_sms_url) {
        this.dj_sms_url = dj_sms_url;
    }

    public String getDj_sms_username() {
        return dj_sms_username;
    }

    public void setDj_sms_username(String dj_sms_username) {
        this.dj_sms_username = dj_sms_username;
    }

    public String getDj_sms_password() {
        return dj_sms_password;
    }

    public void setDj_sms_password(String dj_sms_password) {
        this.dj_sms_password = dj_sms_password;
    }
}
