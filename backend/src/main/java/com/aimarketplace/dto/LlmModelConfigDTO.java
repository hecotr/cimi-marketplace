package com.aimarketplace.dto;

import lombok.Data;

/**
 * LLM Model Config DTO
 */
@Data
public class LlmModelConfigDTO {

    private Long id;

    private Long llmModelId;

    private String modelName;

    private String configKey;

    private String configValue;

    private String configType; // 'string', 'number', 'boolean', 'json'

    private Boolean isEncrypted;

    private String createdAt;

    private String updatedAt;
}
