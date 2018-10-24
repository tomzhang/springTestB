package com.jk51;

import com.alibaba.druid.util.StringUtils;
import com.jk51.modules.storage.StorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 文件名:com.jk51.Bootstrap
 * 描述: 应用启动入口类，请勿随意改动
 * 作者: wangzhengfei
 * 创建日期: 2017-01-13
 * 修改记录:
 */

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.jk51")
@EnableAsync
@EnableConfigurationProperties(StorageProperties.class)
public class Bootstrap {

    public static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    private static final String ENV_KEY_PROFILE = "spring.profiles.active";

    private static final String DEFAULT_PROFILE = "dev";

    public static void main(String[] args) throws Exception {
        SpringApplication application = new SpringApplication(Bootstrap.class);
        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
        if (!source.containsProperty(ENV_KEY_PROFILE) && !System.getenv().containsKey(ENV_KEY_PROFILE)
                && StringUtils.isEmpty(System.getProperty(ENV_KEY_PROFILE))) {
            logger.warn("未指定当前运行环境({}),使用默认环境[{}]", ENV_KEY_PROFILE, DEFAULT_PROFILE);
            application.setAdditionalProfiles(DEFAULT_PROFILE);
        }
        application.run(args);
    }
}
