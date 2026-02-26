package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.entity.ApiKey;
import com.aimarketplace.service.ApiKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * 管理员 API Key 控制器
 */
@RestController
@RequestMapping("/api/admin/api-keys")
public class AdminApiKeyController {

    @Autowired
    private ApiKeyService apiKeyService;

    /**
     * 获取待审批的 API Keys
     */
    @GetMapping("/pending")
    public Result<List<ApiKey>> getPendingKeys() {
        return Result.success(apiKeyService.getPendingKeys());
    }

    /**
     * 审批通过
     */
    @PostMapping("/{id}/approve")
    public Result<Void> approve(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long approverId = (Long) request.getAttribute("userId");
        apiKeyService.approve(id, approverId);
        return Result.success();
    }

    /**
     * 审批拒绝
     */
    @PostMapping("/{id}/reject")
    public Result<Void> reject(
            @PathVariable Long id,
            HttpServletRequest request,
            @RequestBody Map<String, String> body) {
        Long approverId = (Long) request.getAttribute("userId");
        String reason = body.get("reason");
        apiKeyService.reject(id, approverId, reason);
        return Result.success();
    }
}
