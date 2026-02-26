package com.aimarketplace.dto;

import lombok.Data;

@Data
public class AssetVersionDTO {
    private Long id;
    private Integer versionNo;
    private String content;
    private String storagePath;
    private String fileType;
    private Integer viewCount;
    private Integer downloadCount;
    private Integer likeCount;
    private String createdAt;
}
