package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.LlmModelConfigDTO;
import com.aimarketplace.dto.LlmModelConfigRequest;
import com.aimarketplace.service.LlmModelConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Admin LLM Config Controller
 */
@RestController
@RequestMapping("/api/admin/llm-config")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLlmConfigController {

    @Autowired
    private LlmModelConfigService configService;

    @PostMapping
    public Result<LlmModelConfigDTO> upsertConfig(@Valid @RequestBody LlmModelConfigRequest request) {
        LlmModelConfigDTO config = configService.upsertConfig(request);
        return Result.success(config);
    }

    @PutMapping("/bulk/{modelId}")
    public Result<List<LlmModelConfigDTO>> bulkUpdateConfigs(
            @PathVariable Long modelId,
            @Valid @RequestBody List<LlmModelConfigRequest> requests) {
        List<LlmModelConfigDTO> configs = configService.bulkUpdateConfigs(modelId, requests);
        return Result.success(configs);
    }

    @GetMapping("/model/{modelId}")
    public Result<List<LlmModelConfigDTO>> getConfigsByModelId(@PathVariable Long modelId) {
        List<LlmModelConfigDTO> configs = configService.getConfigsByModelId(modelId);
        return Result.success(configs);
    }

    @GetMapping("/model/{modelId}/public")
    public Result<List<LlmModelConfigDTO>> getPublicConfigsByModelId(@PathVariable Long modelId) {
        List<LlmModelConfigDTO> configs = configService.getPublicConfigsByModelId(modelId);
        return Result.success(configs);
    }

    @GetMapping("/model/{modelId}/key/{key}")
    public Result<LlmModelConfigDTO> getConfigByKey(@PathVariable Long modelId, @PathVariable String key) {
        LlmModelConfigDTO config = configService.getConfigByKey(modelId, key);
        return Result.success(config);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        configService.deleteConfig(id);
        return Result.success();
    }
}
