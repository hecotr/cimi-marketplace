package com.aimarketplace.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Asset Publish Request DTO
 */
@Data
public class AssetPublishRequest {

    @NotNull(message = "Asset type is required")
    private String type; // 'llm_model' or 'skill'

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private Long categoryId;

    private String content;

    private String storagePath;

    private String version = "1.0.0";

    private Long creatorId;

    private String changeNotes;
}
