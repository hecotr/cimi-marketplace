package com.aimarketplace.dto;

import lombok.Data;

@Data
public class LlmTestResponse {
    private String response;
    private Integer responseTime;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
}
