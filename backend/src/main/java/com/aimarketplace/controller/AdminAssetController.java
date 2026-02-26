package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.AssetDTO;
import com.aimarketplace.service.AssetService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 管理员资产控制器
 */
@RestController
@RequestMapping("/api/admin/assets")
public class AdminAssetController {

    @Autowired
    private AssetService assetService;

    /**
     * 获取待审核资产列表
     */
    @GetMapping("/pending")
    public Result<IPage<AssetDTO>> getPendingAssets(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(assetService.getAssets(null, "pending_review", null, null, page, size));
    }

    /**
     * 审核通过
     */
    @PostMapping("/{id}/approve")
    public Result<Void> approve(
            @PathVariable Long id,
            HttpServletRequest request,
            @RequestBody(required = false) Map<String, String> body) {
        Long reviewerId = (Long) request.getAttribute("userId");
        String comment = body != null ? body.get("comment") : null;
        assetService.approve(id, reviewerId, comment);
        return Result.success();
    }

    /**
     * 审核拒绝
     */
    @PostMapping("/{id}/reject")
    public Result<Void> reject(
            @PathVariable Long id,
            HttpServletRequest request,
            @RequestBody Map<String, String> body) {
        Long reviewerId = (Long) request.getAttribute("userId");
        String comment = body.get("comment");
        assetService.reject(id, reviewerId, comment);
        return Result.success();
    }
}
