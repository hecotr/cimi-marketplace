package com.aimarketplace.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Object Storage Service Interface
 */
public interface ObjectStorageService {

    /**
     * Upload file to object storage
     */
    String uploadFile(MultipartFile file, String path) throws Exception;

    /**
     * Delete file from object storage
     */
    void deleteFile(String path) throws Exception;

    /**
     * Get public URL for a file
     */
    String getPublicUrl(String path);

    /**
     * Check if file exists
     */
    boolean fileExists(String path);
}
