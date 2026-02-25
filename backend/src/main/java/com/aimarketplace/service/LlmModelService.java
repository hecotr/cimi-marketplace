package com.aimarketplace.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aimarketplace.dto.LlmModelDTO;
import com.aimarketplace.dto.LlmTestRequest;
import com.aimarketplace.dto.LlmTestResponse;
import com.aimarketplace.entity.LlmModel;

import java.util.List;

public interface LlmModelService extends IService<LlmModel> {
    List<LlmModelDTO> getActiveModels();
    LlmModelDTO getModelDetail(Long id);
    LlmTestResponse testModel(Long userId, LlmTestRequest request);
}
