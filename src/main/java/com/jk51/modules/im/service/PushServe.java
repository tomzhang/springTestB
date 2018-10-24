package com.jk51.modules.im.service;

import com.gexin.rp.sdk.http.IGtPush;

public class PushServe extends IGtPush {
    private String appId;
    private String appKey;
    private String masterSecret;
    private String AppSecret;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getMasterSecret() {
        return masterSecret;
    }

    public void setMasterSecret(String masterSecret) {
        this.masterSecret = masterSecret;
    }

    public String getAppSecret() {
        return AppSecret;
    }

    public void setAppSecret(String appSecret) {
        AppSecret = appSecret;
    }

    public PushServe(String appId, String appKey, String masterSecret) {
        super(appKey, masterSecret);
        this.appId=appId;
        this.appKey=appKey;
        this.masterSecret=masterSecret;
    }

    public PushServe(String appId, String appKey, String masterSecret, boolean useSSL) {
        super(appKey, masterSecret, useSSL);
        this.appId=appId;
        this.appKey=appKey;
        this.masterSecret=masterSecret;
    }
}
