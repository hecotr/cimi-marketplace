package com.aimarketplace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aimarketplace.dto.LlmModelConfigDTO;
import com.aimarketplace.dto.LlmModelConfigRequest;
import com.aimarketplace.entity.LlmModel;
import com.aimarketplace.entity.LlmModelConfig;
import com.aimarketplace.mapper.LlmModelConfigMapper;
import com.aimarketplace.mapper.LlmModelMapper;
import com.aimarketplace.service.LlmModelConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LLM Model Config Service Implementation
 */
@Service
public class LlmModelConfigServiceImpl extends ServiceImpl<LlmModelConfigMapper, LlmModelConfig> implements LlmModelConfigService {

    @Autowired
    private LlmModelMapper llmModelMapper;

    @Override
    @Transactional
    public LlmModelConfigDTO upsertConfig(LlmModelConfigRequest request) {
        // Check if model exists
        LlmModel model = llmModelMapper.selectById(request.getLlmModelId());
        if (model == null) {
            throw new RuntimeException("LLM Model not found");
        }

        // Check if config already exists
        LlmModelConfig existing = baseMapper.findByModelIdAndKey(request.getLlmModelId(), request.getConfigKey());

        if (existing != null) {
            // Update existing
            existing.setConfigValue(request.getConfigValue());
            existing.setConfigType(request.getConfigType());
            existing.setIsEncrypted(request.getIsEncrypted());
            existing.setUpdatedAt(LocalDateTime.now());
            updateById(existing);
            return entityToDTO(existing);
        } else {
            // Create new
            LlmModelConfig config = new LlmModelConfig();
            config.setLlmModelId(request.getLlmModelId());
            config.setConfigKey(request.getConfigKey());
            config.setConfigValue(request.getConfigValue());
            config.setConfigType(request.getConfigType());
            config.setIsEncrypted(request.getIsEncrypted());
            config.setCreatedAt(LocalDateTime.now());
            config.setUpdatedAt(LocalDateTime.now());
            save(config);
            return entityToDTO(config);
        }
    }

    @Override
    public List<LlmModelConfigDTO> getConfigsByModelId(Long modelId) {
        List<LlmModelConfig> configs = baseMapper.findByModelId(modelId);
        return configs.stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public List<LlmModelConfigDTO> getPublicConfigsByModelId(Long modelId) {
        List<LlmModelConfig> configs = baseMapper.findPublicByModelId(modelId);
        return configs.stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public LlmModelConfigDTO getConfigByKey(Long modelId, String key) {
        LlmModelConfig config = baseMapper.findByModelIdAndKey(modelId, key);
        return config != null ? entityToDTO(config) : null;
    }

    @Override
    @Transactional
    public void deleteConfig(Long id) {
        removeById(id);
    }

    @Override
    @Transactional
    public List<LlmModelConfigDTO> bulkUpdateConfigs(Long modelId, List<LlmModelConfigRequest> requests) {
        // Check if model exists
        LlmModel model = llmModelMapper.selectById(modelId);
        if (model == null) {
            throw new RuntimeException("LLM Model not found");
        }

        List<LlmModelConfigDTO> results = new ArrayList<>();

        for (LlmModelConfigRequest request : requests) {
            request.setLlmModelId(modelId);
            LlmModelConfigDTO dto = upsertConfig(request);
            results.add(dto);
        }

        return results;
    }

    private LlmModelConfigDTO entityToDTO(LlmModelConfig config) {
        LlmModelConfigDTO dto = new LlmModelConfigDTO();
        BeanUtils.copyProperties(config, dto);

        // Format timestamps
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (config.getCreatedAt() != null) {
            dto.setCreatedAt(config.getCreatedAt().format(formatter));
        }
        if (config.getUpdatedAt() != null) {
            dto.setUpdatedAt(config.getUpdatedAt().format(formatter));
        }

        // Load model name
        LlmModel model = llmModelMapper.selectById(config.getLlmModelId());
        if (model != null) {
            dto.setModelName(model.getName());
        }

        return dto;
    }
}
