package com.aimarketplace.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Category DTO
 */
@Data
public class CategoryDTO {

    private Long id;

    private String name;

    private String description;

    private String icon;

    private Long parentId;

    private String parentName;

    private Integer sortOrder;

    private String status; // 'active', 'inactive'

    private Integer assetCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<CategoryDTO> children;
}
