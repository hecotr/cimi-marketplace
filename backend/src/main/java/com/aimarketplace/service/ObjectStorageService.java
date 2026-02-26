package com.aimarketplace.service;

import java.io.InputStream;

public interface ObjectStorageService {

    /**
     * 上传文件
     */
    void uploadFile(String key, InputStream inputStream, long size, String contentType);

    /**
     * 获取预签名 URL（用于下载）
     */
    String getPresignedUrl(String key, int expirySeconds);

    /**
     * 检查文件是否存在
     */
    boolean fileExists(String key);

    /**
     * 删除文件
     */
    void deleteFile(String key);

    /**
     * 获取文件内容
     */
    InputStream getFile(String key);
}
