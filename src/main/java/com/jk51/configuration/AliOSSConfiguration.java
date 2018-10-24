package com.jk51.configuration;

import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Configuration
@EnableConfigurationProperties(AliOSSConfiguration.AliOSSConfig.class)
public class AliOSSConfiguration {
    @Autowired
    private AliOSSConfig aliOSSConfig;

    @Bean
    public OSSClient ossClient() {
        String endpoint;
        if (Arrays.asList("dev", "pre").contains(System.getProperty("spring.profiles.active"))) {
            endpoint = aliOSSConfig.getEndpoint();
        } else {
            endpoint = aliOSSConfig.getImgUrl();
        }
        return new OSSClient(endpoint, aliOSSConfig.getAccessKeyId(), aliOSSConfig.getAccessKeySecret());
    }

    public static String randomBucketKey(String suffix) {
        return String.format("51jk_upload_%s_%d.%s",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            System.currentTimeMillis(),
            suffix);
    }




    @ConfigurationProperties(prefix = "oss")
    public static class AliOSSConfig {
        public final static String BUCKET_NAME = "51jk-storage";
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
        private String imgUrl;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        @Override
        public String toString() {
            return "AliOSSConfig{" +
                "endpoint='" + endpoint + '\'' +
                ", accessKeyId='" + accessKeyId + '\'' +
                ", accessKeySecret='" + accessKeySecret + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
        }
    }
}
