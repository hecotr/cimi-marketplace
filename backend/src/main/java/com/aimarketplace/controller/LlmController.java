package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.LlmModelDTO;
import com.aimarketplace.dto.LlmTestRequest;
import com.aimarketplace.dto.LlmTestResponse;
import com.aimarketplace.service.LlmModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/llm")
@RequiredArgsConstructor
public class LlmController {

    private final LlmModelService llmModelService;

    @GetMapping("/models")
    public Result<List<LlmModelDTO>> getModels() {
        return Result.success(llmModelService.getActiveModels());
    }

    @GetMapping("/models/{id}")
    public Result<LlmModelDTO> getModelDetail(@PathVariable Long id) {
        return Result.success(llmModelService.getModelDetail(id));
    }

    @PostMapping("/test")
    public Result<LlmTestResponse> testModel(@RequestBody LlmTestRequest request) {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        return Result.success(llmModelService.testModel(userId, request));
    }
}
