package com.jk51.modules.pay.service.ali;

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
@ConfigurationProperties(prefix = "pay.alipay")
public class AliConfig {
    private String app_id;
    private String seller_id;
    private String private_key;
    private String public_key;
    private String notify_url;
    private String ali_public_key;
    private String log_path;
    private String return_url;
    public String charset;
    public String sign_type;
    public String format;
    public String url;
    private String app_auth_token;
    private String pid;

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setApp_auth_token(String app_auth_token) {
        this.app_auth_token = app_auth_token;
    }

    public String getApp_auth_token() {
        return app_auth_token;
    }

    public String getAli_public_key() {
        return ali_public_key;
    }

    public void setAli_public_key(String ali_public_key) {
        this.ali_public_key = ali_public_key;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getPrivate_key() {
        return private_key;
    }

    public void setPrivate_key(String private_key) {
        this.private_key = private_key;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getLog_path() {
        return log_path;
    }

    public void setLog_path(String log_path) {
        this.log_path = log_path;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public AliConfig() {
    }

    public AliConfig(String app_id, String seller_id, String private_key, String public_key, String notify_url, String ali_public_key, String log_path, String return_url) {
        this.app_id = app_id;
        this.seller_id = seller_id;
        this.private_key = private_key;
        this.public_key = public_key;
        this.notify_url = notify_url;
        this.ali_public_key = ali_public_key;
        this.log_path = log_path;
        this.return_url = return_url;
    }
}
