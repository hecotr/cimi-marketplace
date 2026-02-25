package com.aimarketplace.dto;

import lombok.Data;

@Data
public class SkillQueryRequest {
    private String keyword;
    private String category;
    private String sortBy; // created_at, view_count, download_count, like_count
    private Integer page = 1;
    private Integer size = 20;
}
