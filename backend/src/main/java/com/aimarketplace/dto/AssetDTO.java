package com.aimarketplace.dto;

import lombok.Data;
import java.util.List;

@Data
public class AssetDTO {
    private Long id;
    private String assetType;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String tags;
    private String status;
    private Long currentVersionId;
    private String createdBy;
    private String createdAt;
    private String updatedAt;

    // Version info
    private List<AssetVersionDTO> versions;
}
