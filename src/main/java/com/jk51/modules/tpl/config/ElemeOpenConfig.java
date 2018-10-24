package com.jk51.modules.tpl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 基础配置类
 */
@Configuration
public class ElemeOpenConfig {

    /**
     * 饿了么开放平台开放平台api
     */
//联调http请求URL
    //public static final String API_URL = "https://exam-anubis.ele.me/anubis-webapi";
//正式http请求URL
    //public static final String API_URL = "https://exam-anubis.ele.me/anubis-webapi";

    @Value("${ele.appId}")
    public String API_URL;

    @Value("${ele.appId}")
    public String appId;

    @Value("${ele.secretKey}")
    public String secretKey;
    @Value("${ele.notify_url}")
    public String notify_url;

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAPI_URL() {
        return API_URL;
    }

    public void setAPI_URL(String API_URL) {
        this.API_URL = API_URL;
    }
}
