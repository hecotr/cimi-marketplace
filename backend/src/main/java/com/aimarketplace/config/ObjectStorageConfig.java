package com.aimarketplace.config;

/**
 * Interface for object storage configuration
 * Supports multiple storage backends (MinIO, OSS, S3, etc.)
 */
public interface ObjectStorageConfig {

    /**
     * Get storage endpoint URL
     */
    String getEndpoint();

    /**
     * Get storage access key
     */
    String getAccessKey();

    /**
     * Get storage secret key
     */
    String getSecretKey();

    /**
     * Get default bucket name
     */
    String getBucketName();

    /**
     * Get storage type (minio, oss, s3, local)
     */
    String getStorageType();

    /**
     * Get public URL base for accessing files
     */
    String getPublicUrlBase();

    /**
     * Check if storage is enabled
     */
    boolean isEnabled();
}
