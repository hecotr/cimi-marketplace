package com.aimarketplace.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * LLM Model Config Request DTO
 */
@Data
public class LlmModelConfigRequest {

    @NotNull(message = "LLM Model ID is required")
    private Long llmModelId;

    @NotBlank(message = "Config key is required")
    private String configKey;

    private String configValue;

    private String configType = "string";

    private Boolean isEncrypted = false;
}
