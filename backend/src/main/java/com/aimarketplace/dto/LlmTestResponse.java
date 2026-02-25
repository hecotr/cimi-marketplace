package com.aimarketplace.dto;

import lombok.Data;

@Data
public class LlmTestResponse {
    private String response;
    private Integer responseTime;
    private TokenUsage tokenUsage;
    private String parameters;

    @Data
    public static class TokenUsage {
        private Integer inputTokens;
        private Integer outputTokens;
        private Integer totalTokens;
    }
}
