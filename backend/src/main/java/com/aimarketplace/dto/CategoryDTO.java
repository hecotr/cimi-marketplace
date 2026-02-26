package com.aimarketplace.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private Long parentId;
    private String assetType;
    private Integer sortOrder;
}
