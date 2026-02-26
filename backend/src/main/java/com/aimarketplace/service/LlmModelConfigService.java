package com.aimarketplace.service;

import com.aimarketplace.dto.LlmModelDTO;
import com.aimarketplace.dto.LlmTestRequest;
import com.aimarketplace.dto.LlmTestResponse;
import com.aimarketplace.entity.LlmModelConfig;

import java.util.List;

public interface LlmModelConfigService {

    /**
     * 获取所有活跃的 LLM 模型配置
     */
    List<LlmModelDTO> getActiveModels();

    /**
     * 获取所有 LLM 模型配置（管理员）
     */
    List<LlmModelDTO> getAllModels();

    /**
     * 获取模型详情
     */
    LlmModelDTO getModelById(Long id);

    /**
     * 测试 LLM 模型
     */
    LlmTestResponse testModel(LlmTestRequest request);

    /**
     * 创建模型配置（管理员）
     */
    Long createModel(LlmModelConfig config);

    /**
     * 更新模型配置（管理员）
     */
    void updateModel(Long id, LlmModelConfig config);

    /**
     * 删除模型配置（管理员）
     */
    void deleteModel(Long id);
}
