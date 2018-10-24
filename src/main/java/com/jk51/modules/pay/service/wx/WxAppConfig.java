package com.jk51.modules.pay.service.wx;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/9/12.
 */
@Component
@ConfigurationProperties(prefix = "pay.wxapppay")
public class WxAppConfig {

    private String appid;
    private String APPSECRET;
    private String mch_id;
    private String notify_url;
    private String spbill_create_ip;
    private String key;
    private String pack;

    private String cert_path;

    private String mp_verify_path;

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getAPPSECRET() {
        return APPSECRET;
    }

    public void setAPPSECRET(String APPSECRET) {
        this.APPSECRET = APPSECRET;
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

    public String getCert_path() {
        return cert_path;
    }

    public void setCert_path(String cert_path) {
        this.cert_path = cert_path;
    }

    public String getMp_verify_path() {
        return mp_verify_path;
    }

    public void setMp_verify_path(String mp_verify_path) {
        this.mp_verify_path = mp_verify_path;
    }

    public String getAppid() {

        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }
}
