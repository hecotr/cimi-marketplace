package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.LlmModelDTO;
import com.aimarketplace.dto.LlmTestRequest;
import com.aimarketplace.dto.LlmTestResponse;
import com.aimarketplace.entity.LlmModelConfig;
import com.aimarketplace.service.LlmModelConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/llm")
public class LlmController {

    @Autowired
    private LlmModelConfigService llmModelConfigService;

    /**
     * 获取所有活跃的 LLM 模型列表
     */
    @GetMapping("/models")
    public Result<List<LlmModelDTO>> getModels() {
        return Result.success(llmModelConfigService.getActiveModels());
    }

    /**
     * 获取所有 LLM 模型配置（管理员）
     */
    @GetMapping("/admin/models")
    public Result<List<LlmModelDTO>> getAllModels() {
        return Result.success(llmModelConfigService.getAllModels());
    }

    /**
     * 获取模型详情
     */
    @GetMapping("/models/{id}")
    public Result<LlmModelDTO> getModelById(@PathVariable Long id) {
        return Result.success(llmModelConfigService.getModelById(id));
    }

    /**
     * 测试 LLM 模型
     */
    @PostMapping("/test")
    public Result<LlmTestResponse> testModel(@Valid @RequestBody LlmTestRequest request) {
        return Result.success(llmModelConfigService.testModel(request));
    }

    /**
     * 创建模型配置（管理员）
     */
    @PostMapping("/admin/models")
    public Result<Long> createModel(@RequestBody LlmModelConfig config) {
        return Result.success(llmModelConfigService.createModel(config));
    }

    /**
     * 更新模型配置（管理员）
     */
    @PutMapping("/admin/models/{id}")
    public Result<Void> updateModel(@PathVariable Long id, @RequestBody LlmModelConfig config) {
        llmModelConfigService.updateModel(id, config);
        return Result.success();
    }

    /**
     * 删除模型配置（管理员）
     */
    @DeleteMapping("/admin/models/{id}")
    public Result<Void> deleteModel(@PathVariable Long id) {
        llmModelConfigService.deleteModel(id);
        return Result.success();
    }
}
