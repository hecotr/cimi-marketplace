package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.ApiKeyApplyRequest;
import com.aimarketplace.entity.ApiKey;
import com.aimarketplace.service.ApiKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/api-keys")
public class ApiKeyController {

    @Autowired
    private ApiKeyService apiKeyService;

    /**
     * 申请 API Key
     */
    @PostMapping
    public Result<Long> applyKey(
            HttpServletRequest request,
            @Valid @RequestBody ApiKeyApplyRequest applyRequest) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(apiKeyService.applyKey(userId, applyRequest));
    }

    /**
     * 获取我的 API Keys
     */
    @GetMapping("/my")
    public Result<List<ApiKey>> getMyKeys(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(apiKeyService.getMyKeys(userId));
    }

    /**
     * 撤销 API Key
     */
    @PostMapping("/{id}/revoke")
    public Result<Void> revokeKey(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        apiKeyService.revoke(id, userId);
        return Result.success();
    }
}
