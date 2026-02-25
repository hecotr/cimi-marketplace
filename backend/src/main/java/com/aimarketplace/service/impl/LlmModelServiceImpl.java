package com.aimarketplace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aimarketplace.dto.LlmModelDTO;
import com.aimarketplace.dto.LlmTestRequest;
import com.aimarketplace.dto.LlmTestResponse;
import com.aimarketplace.entity.LlmModel;
import com.aimarketplace.entity.LlmTestRecord;
import com.aimarketplace.mapper.LlmModelMapper;
import com.aimarketplace.mapper.LlmTestRecordMapper;
import com.aimarketplace.service.LlmModelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LlmModelServiceImpl extends ServiceImpl<LlmModelMapper, LlmModel> implements LlmModelService {

    private final LlmTestRecordMapper testRecordMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<LlmModelDTO> getActiveModels() {
        LambdaQueryWrapper<LlmModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LlmModel::getStatus, "active");
        wrapper.orderByDesc(LlmModel::getCreatedAt);
        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public LlmModelDTO getModelDetail(Long id) {
        LlmModel model = getById(id);
        return toDTO(model);
    }

    @Override
    public LlmTestResponse testModel(Long userId, LlmTestRequest request) {
        long startTime = System.currentTimeMillis();

        LlmModel model = getById(request.getModelId());
        if (model == null) {
            throw new RuntimeException("Model not found");
        }

        // TODO: Replace with actual LLM API call
        // For now, return a mock response
        String mockResponse = "This is a mock response from " + model.getName() +
                ". In production, this will be replaced with actual API calls to " +
                model.getApiEndpoint();

        long responseTime = System.currentTimeMillis() - startTime;

        // Save test record
        LlmTestRecord record = new LlmTestRecord();
        record.setUserId(userId);
        record.setModelId(request.getModelId());
        record.setPrompt(request.getPrompt());
        record.setResponse(mockResponse);
        try {
            record.setParameters(objectMapper.writeValueAsString(request));
        } catch (Exception e) {
            record.setParameters("{}");
        }
        record.setResponseTime((int) responseTime);
        record.setCreatedAt(LocalDateTime.now());
        testRecordMapper.insert(record);

        // Build response
        LlmTestResponse response = new LlmTestResponse();
        response.setResponse(mockResponse);
        response.setResponseTime((int) responseTime);
        response.setParameters(objectMapper.valueToTree(request).toString());

        // Mock token usage
        LlmTestResponse.TokenUsage tokenUsage = new LlmTestResponse.TokenUsage();
        tokenUsage.setInputTokens(request.getPrompt().length() / 4);
        tokenUsage.setOutputTokens(mockResponse.length() / 4);
        tokenUsage.setTotalTokens(tokenUsage.getInputTokens() + tokenUsage.getOutputTokens());
        response.setTokenUsage(tokenUsage);

        return response;
    }

    private LlmModelDTO toDTO(LlmModel model) {
        if (model == null) return null;
        LlmModelDTO dto = new LlmModelDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setProvider(model.getProvider());
        dto.setModelName(model.getModelName());
        dto.setDescription(model.getDescription());
        dto.setApiEndpoint(model.getApiEndpoint());
        dto.setMaxTokens(model.getMaxTokens());
        dto.setStatus(model.getStatus());
        return dto;
    }
}
