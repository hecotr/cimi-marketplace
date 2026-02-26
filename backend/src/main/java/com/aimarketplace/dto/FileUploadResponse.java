package com.aimarketplace.dto;

import lombok.Data;

@Data
public class FileUploadResponse {
    private String key;
    private String url;
    private Long size;
    private String fileName;
}
