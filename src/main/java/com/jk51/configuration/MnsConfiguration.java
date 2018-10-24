package com.jk51.configuration;

import com.aliyun.mns.client.CloudAccount;
import com.jk51.mq.MnsProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云mns消息队列配置
 */
@Configuration
@ComponentScan("com.jk51.mq")
public class MnsConfiguration {
    @Autowired
    MnsProperty mnsProperty;

    @Bean
    public CloudAccount initCloudAccount() {
        return new CloudAccount(mnsProperty.getAccessId(), mnsProperty.getAccessKey(), mnsProperty.getMNSEndpoint());
    }
}


