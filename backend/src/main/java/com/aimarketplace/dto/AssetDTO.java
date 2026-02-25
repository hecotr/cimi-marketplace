package com.aimarketplace.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Asset Data Transfer Object
 */
@Data
public class AssetDTO {

    private Long id;

    private String name;

    private String type; // 'llm_model' or 'skill'

    private String description;

    private Long categoryId;

    private String categoryName;

    private String content;

    private String storagePath;

    private String status; // 'draft', 'pending', 'published', 'rejected'

    private String version;

    private Integer viewCount;

    private Integer downloadCount;

    private Integer likeCount;

    private Long createdBy;

    private String creatorName;

    private Long approvedBy;

    private String approverName;

    private LocalDateTime approvedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String rejectionReason;

    private Boolean isFavorite;

    private Boolean isLiked;
}
