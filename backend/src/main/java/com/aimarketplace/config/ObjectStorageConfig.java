package com.aimarketplace.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "storage")
public class ObjectStorageConfig {

    private String type = "minio"; // minio or s3

    private MinioConfig minio = new MinioConfig();
    private S3Config s3 = new S3Config();

    @Data
    public static class MinioConfig {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucket;
    }

    @Data
    public static class S3Config {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucket;
        private String region;
    }
}
