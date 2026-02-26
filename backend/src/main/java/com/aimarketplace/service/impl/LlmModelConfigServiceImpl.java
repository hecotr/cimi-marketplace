package com.aimarketplace.service.impl;

import com.aimarketplace.common.BusinessException;
import com.aimarketplace.dto.LlmModelDTO;
import com.aimarketplace.dto.LlmTestRequest;
import com.aimarketplace.dto.LlmTestResponse;
import com.aimarketplace.entity.Category;
import com.aimarketplace.entity.LlmModelConfig;
import com.aimarketplace.mapper.CategoryMapper;
import com.aimarketplace.mapper.LlmModelConfigMapper;
import com.aimarketplace.service.LlmModelConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LlmModelConfigServiceImpl implements LlmModelConfigService {

    @Autowired
    private LlmModelConfigMapper modelConfigMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<LlmModelDTO> getActiveModels() {
        List<LlmModelConfig> models = modelConfigMapper.selectList(
            new LambdaQueryWrapper<LlmModelConfig>()
                .eq(LlmModelConfig::getStatus, "active")
                .orderByAsc(LlmModelConfig::getId)
        );

        return models.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<LlmModelDTO> getAllModels() {
        List<LlmModelConfig> models = modelConfigMapper.selectList(
            new LambdaQueryWrapper<LlmModelConfig>()
                .orderByAsc(LlmModelConfig::getId)
        );

        return models.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public LlmModelDTO getModelById(Long id) {
        LlmModelConfig config = modelConfigMapper.selectById(id);
        if (config == null) {
            throw new BusinessException("模型配置不存在");
        }
        return toDTO(config);
    }

    @Override
    public LlmTestResponse testModel(LlmTestRequest request) {
        LlmModelConfig config = modelConfigMapper.selectById(request.getModelConfigId());
        if (config == null) {
            throw new BusinessException("模型配置不存在");
        }

        long startTime = System.currentTimeMillis();

        try {
            // 构建 OpenAI 兼容的请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getModelName());
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", request.getPrompt())
            ));

            // 合并默认参数
            if (config.getDefaultParams() != null) {
                requestBody.putAll(config.getDefaultParams());
            }
            if (request.getParameters() != null) {
                requestBody.putAll(request.getParameters());
            }

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = config.getApiEndpoint();
            if (!url.endsWith("/")) {
                url += "/";
            }
            url += "chat/completions";

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
            );

            long responseTime = System.currentTimeMillis() - startTime;

            // 解析响应
            LlmTestResponse testResponse = new LlmTestResponse();
            testResponse.setResponseTime((int) responseTime);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                // 提取内容
                if (body.containsKey("choices")) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        if (message != null) {
                            testResponse.setResponse((String) message.get("content"));
                        }
                    }
                }

                // 提取 token 使用量
                if (body.containsKey("usage")) {
                    Map<String, Object> usage = (Map<String, Object>) body.get("usage");
                    testResponse.setPromptTokens((Integer) usage.getOrDefault("prompt_tokens", 0));
                    testResponse.setCompletionTokens((Integer) usage.getOrDefault("completion_tokens", 0));
                    testResponse.setTotalTokens((Integer) usage.getOrDefault("total_tokens", 0));
                }
            }

            return testResponse;

        } catch (Exception e) {
            log.error("LLM 测试失败: {}", e.getMessage(), e);
            throw new BusinessException("模型调用失败: " + e.getMessage());
        }
    }

    @Override
    public Long createModel(LlmModelConfig config) {
        config.setCreatedAt(LocalDateTime.now());
        config.setUpdatedAt(LocalDateTime.now());
        config.setStatus("active");
        modelConfigMapper.insert(config);
        return config.getId();
    }

    @Override
    public void updateModel(Long id, LlmModelConfig config) {
        LlmModelConfig existing = modelConfigMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("模型配置不存在");
        }

        config.setId(id);
        config.setUpdatedAt(LocalDateTime.now());
        modelConfigMapper.updateById(config);
    }

    @Override
    public void deleteModel(Long id) {
        LlmModelConfig existing = modelConfigMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("模型配置不存在");
        }

        // 软删除：设置状态为 inactive
        existing.setStatus("inactive");
        existing.setUpdatedAt(LocalDateTime.now());
        modelConfigMapper.updateById(existing);
    }

    private LlmModelDTO toDTO(LlmModelConfig config) {
        LlmModelDTO dto = new LlmModelDTO();
        BeanUtils.copyProperties(config, dto);

        // 获取分类名称
        if (config.getCategoryId() != null) {
            Category category = categoryMapper.selectById(config.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
        }

        if (config.getCreatedAt() != null) {
            dto.setCreatedAt(config.getCreatedAt().toString());
        }

        return dto;
    }
}
