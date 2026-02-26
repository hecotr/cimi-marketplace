package com.aimarketplace.dto;

import lombok.Data;
import java.util.Map;

@Data
public class LlmModelDTO {
    private Long id;
    private String name;
    private String provider;
    private String modelName;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String apiProtocol;
    private String apiEndpoint;
    private Integer maxTokens;
    private String version;
    private Map<String, Object> defaultParams;
    private Map<String, Object> billingRule;
    private String status;
    private String createdAt;
}
