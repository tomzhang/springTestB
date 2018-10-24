package com.jk51.model.distribute;

import java.io.Serializable;

/**
 * 分销app配置
 */
public class DDisappconfig implements Serializable {
    private Integer id;

    private Integer siteId;

    private String appName;

    private String ios;

    private String android;

    private String host;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIos() {
        return ios;
    }

    public void setIos(String ios) {
        this.ios = ios;
    }

    public String getAndroid() {
        return android;
    }

    public void setAndroid(String android) {
        this.android = android;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}