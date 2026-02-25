package com.aimarketplace.dto;

import lombok.Data;

@Data
public class SkillDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String type;
    private Integer viewCount;
    private Integer downloadCount;
    private Integer likeCount;
    private Boolean isLiked;
    private Boolean isFavorited;
}
