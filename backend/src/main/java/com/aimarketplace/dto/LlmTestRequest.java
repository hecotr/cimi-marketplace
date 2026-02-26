package com.aimarketplace.dto;

import lombok.Data;
import java.util.Map;

@Data
public class LlmTestRequest {
    private Long modelConfigId;
    private String prompt;
    private Map<String, Object> parameters;
}
