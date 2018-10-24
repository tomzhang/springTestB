package com.jk51.modules.sms.smsConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-02-22
 * 修改记录:
 */
@Component
@ConfigurationProperties(prefix = "sms._7moor")
public class QiMoorConfig {

    //请求头信息
    private String accountID;
    private String password;
    private String apisecret;
    private String url_temp;
    private String url_sms;

    //语音请求信息
    private String url_web_call;
    private String action;
    private String PBX;
    private String serviceNo;
    private String timeout;
    private String maxCallTime;

    public String getUrl_web_call() {
        return url_web_call;
    }

    public void setUrl_web_call(String url_web_call) {
        this.url_web_call = url_web_call;
    }

    public QiMoorConfig() {
    }

    public QiMoorConfig(String accountID, String password, String apisecret, String url_temp, String url_sms, String url_webCall, String action, String PBX, String serviceNo, String timeout, String maxCallTime) {
        this.accountID = accountID;
        this.password = password;
        this.apisecret = apisecret;
        this.url_temp = url_temp;
        this.url_sms = url_sms;
        this.url_web_call = url_webCall;
        this.action = action;
        this.PBX = PBX;
        this.serviceNo = serviceNo;
        this.timeout = timeout;
        this.maxCallTime = maxCallTime;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getMaxCallTime() {
        return maxCallTime;
    }

    public void setMaxCallTime(String maxCallTime) {
        this.maxCallTime = maxCallTime;
    }




    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPBX() {
        return PBX;
    }

    public void setPBX(String PBX) {
        this.PBX = PBX;
    }

    public String getServiceNo() {
        return serviceNo;
    }

    public void setServiceNo(String serviceNo) {
        this.serviceNo = serviceNo;
    }

    public String getApisecret() {
        return apisecret;
    }

    public void setApisecret(String apisecret) {
        this.apisecret = apisecret;
    }

    public String getUrl_temp() {
        return url_temp;
    }

    public void setUrl_temp(String url_temp) {
        this.url_temp = url_temp;
    }

    public String getUrl_sms() {
        return url_sms;
    }

    public void setUrl_sms(String url_sms) {
        this.url_sms = url_sms;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
