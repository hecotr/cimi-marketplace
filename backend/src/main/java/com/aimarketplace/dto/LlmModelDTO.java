package com.aimarketplace.dto;

import lombok.Data;

@Data
public class LlmModelDTO {
    private Long id;
    private String name;
    private String provider;
    private String modelName;
    private String description;
    private String apiEndpoint;
    private Integer maxTokens;
    private String status;
}
