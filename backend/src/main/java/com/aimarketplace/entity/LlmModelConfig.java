package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * LLM Model Config Entity
 */
@Data
@TableName("llm_model_config")
public class LlmModelConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long llmModelId;

    private String configKey;

    private String configValue;

    private String configType; // 'string', 'number', 'boolean', 'json'

    private Boolean isEncrypted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
