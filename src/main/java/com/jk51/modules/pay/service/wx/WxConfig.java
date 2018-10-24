package com.jk51.modules.pay.service.wx;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Component
@ConfigurationProperties(prefix = "pay.wxpay")
public class WxConfig {
    private String appid;
    private String APPSECRET;
    private String mch_id;
    private String notify_url;
    private String spbill_create_ip;
    private String key;
    public WxConfig() {
    }
    public WxConfig(String appid, String APPSECRET, String mch_id, String notify_url, String spbill_create_ip, String key, String cert_path, String mp_verify_path) {
        this.appid = appid;
        this.APPSECRET = APPSECRET;
        this.mch_id = mch_id;
        this.notify_url = notify_url;
        this.spbill_create_ip = spbill_create_ip;
        this.key = key;
        this.cert_path = cert_path;
        this.mp_verify_path = mp_verify_path;
    }
    private String cert_path;

    private String mp_verify_path;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAPPSECRET() {
        return APPSECRET;
    }

    public void setAPPSECRET(String APPSECRET) {
        this.APPSECRET = APPSECRET;
    }

    public String getMp_verify_path() {
        return mp_verify_path;
    }

    public void setMp_verify_path(String mp_verify_path) {
        this.mp_verify_path = mp_verify_path;
    }

    public String getCert_path() {
        return cert_path;
    }

    public void setCert_path(String cert_path) {
        this.cert_path = cert_path;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
