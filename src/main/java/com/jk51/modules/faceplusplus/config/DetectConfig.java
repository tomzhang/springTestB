package com.jk51.modules.faceplusplus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "detect")
public class DetectConfig {

    private String tencentAppKey;
    private String tencentAppId;

    public String getTencentAppKey() {
        return tencentAppKey;
    }

    public void setTencentAppKey(String tencentAppKey) {
        this.tencentAppKey = tencentAppKey;
    }

    public String getTencentAppId() {
        return tencentAppId;
    }

    public void setTencentAppId(String tencentAppId) {
        this.tencentAppId = tencentAppId;
    }
}
