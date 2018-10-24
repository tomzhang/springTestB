package com.jk51.configuration;

import com.jk51.modules.im.service.PushServe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PushServeConfiguration {
    private String appId = "Oicr6g41T19KXnTaMyp7D8";
    private String appKey = "25KOV0iXH3Aj0hbvKmTMV6";
    private String masterSecret = "HTtGSVoSdw9n2PvM2aw9O5";
    private String AppSecret = "Z5A7Q3snot6msDcnXwOnj2";
    @Bean("storeHelpPush")
    public PushServe storeHelpPush(){
        return new PushServe(appId, appKey, masterSecret);
    }




    private String appId_1 = "b7WDwNCynF8NiQoXlIDqY2";
    private String appKey_1 = "YIrXYqGcHTApsCkoOlpQFA";
    private String masterSecret_1 = "Eyn02MZbK995TOPswwmUn1";
    private String AppSecret_1 = "9VCf6PmPew69uIXE0HNCs3";
    @Bean("storeXiaoWuPush")
    public PushServe storeXiaoWuPush(){
        return new PushServe(appId_1, appKey_1, masterSecret_1);
    }
}
