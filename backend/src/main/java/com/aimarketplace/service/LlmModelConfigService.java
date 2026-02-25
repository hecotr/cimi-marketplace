package com.aimarketplace.service;

import com.aimarketplace.dto.LlmModelConfigDTO;
import com.aimarketplace.dto.LlmModelConfigRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * LLM Model Config Service Interface
 */
public interface LlmModelConfigService extends IService<com.aimarketplace.entity.LlmModelConfig> {

    /**
     * Create or update config
     */
    LlmModelConfigDTO upsertConfig(LlmModelConfigRequest request);

    /**
     * Get all configs for a model
     */
    List<LlmModelConfigDTO> getConfigsByModelId(Long modelId);

    /**
     * Get public configs (non-encrypted)
     */
    List<LlmModelConfigDTO> getPublicConfigsByModelId(Long modelId);

    /**
     * Get config by key
     */
    LlmModelConfigDTO getConfigByKey(Long modelId, String key);

    /**
     * Delete config
     */
    void deleteConfig(Long id);

    /**
     * Bulk update configs
     */
    List<LlmModelConfigDTO> bulkUpdateConfigs(Long modelId, List<LlmModelConfigRequest> requests);
}
