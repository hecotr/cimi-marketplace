package com.aimarketplace.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * Category Request DTO
 */
@Data
public class CategoryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private String icon;

    private Long parentId;

    private Integer sortOrder;

    private String status = "active";
}
