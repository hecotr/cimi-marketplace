package com.aimarketplace.dto;

import lombok.Data;

@Data
public class LlmTestRequest {
    private Long modelId;
    private String prompt;
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
}
