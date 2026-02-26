package com.aimarketplace.service.impl;

import com.aimarketplace.config.ObjectStorageConfig;
import com.aimarketplace.service.ObjectStorageService;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ObjectStorageServiceImpl implements ObjectStorageService {

    @Autowired
    private ObjectStorageConfig config;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        if ("minio".equals(config.getType())) {
            ObjectStorageConfig.MinioConfig minioConfig = config.getMinio();
            minioClient = MinioClient.builder()
                    .endpoint(minioConfig.getEndpoint())
                    .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey())
                    .build();

            // 确保 bucket 存在
            try {
                boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .build());
                if (!exists) {
                    minioClient.makeBucket(MakeBucketArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .build());
                    log.info("Created bucket: {}", minioConfig.getBucket());
                }
            } catch (Exception e) {
                log.error("Failed to check/create bucket", e);
            }
        }
    }

    @Override
    public void uploadFile(String key, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(config.getMinio().getBucket())
                    .object(key)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build());
            log.info("Uploaded file: {}", key);
        } catch (Exception e) {
            log.error("Failed to upload file: {}", key, e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public String getPresignedUrl(String key, int expirySeconds) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(config.getMinio().getBucket())
                    .object(key)
                    .expiry(expirySeconds, TimeUnit.SECONDS)
                    .build());
        } catch (Exception e) {
            log.error("Failed to get presigned URL for: {}", key, e);
            throw new RuntimeException("获取下载链接失败: " + e.getMessage());
        }
    }

    @Override
    public boolean fileExists(String key) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(config.getMinio().getBucket())
                    .object(key)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteFile(String key) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(config.getMinio().getBucket())
                    .object(key)
                    .build());
            log.info("Deleted file: {}", key);
        } catch (Exception e) {
            log.error("Failed to delete file: {}", key, e);
            throw new RuntimeException("文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream getFile(String key) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(config.getMinio().getBucket())
                    .object(key)
                    .build());
        } catch (Exception e) {
            log.error("Failed to get file: {}", key, e);
            throw new RuntimeException("获取文件失败: " + e.getMessage());
        }
    }
}
