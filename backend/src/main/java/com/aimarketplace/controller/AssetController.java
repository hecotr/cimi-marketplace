package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.AssetDTO;
import com.aimarketplace.dto.AssetPublishRequest;
import com.aimarketplace.service.AssetService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    /**
     * 获取资产列表
     */
    @GetMapping
    public Result<IPage<AssetDTO>> getAssets(
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(assetService.getAssets(assetType, status, categoryId, keyword, page, size));
    }

    /**
     * 获取我的资产
     */
    @GetMapping("/my")
    public Result<List<AssetDTO>> getMyAssets(
            HttpServletRequest request,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) String status) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(assetService.getMyAssets(userId, assetType, status));
    }

    /**
     * 获取资产详情
     */
    @GetMapping("/{id}")
    public Result<AssetDTO> getAssetById(@PathVariable Long id) {
        AssetDTO asset = assetService.getAssetById(id);
        // 增加浏览量
        if (asset.getCurrentVersionId() != null) {
            assetService.incrementViewCount(asset.getCurrentVersionId());
        }
        return Result.success(asset);
    }

    /**
     * 发布资产
     */
    @PostMapping
    public Result<Long> publishAsset(
            HttpServletRequest request,
            @Valid @RequestBody AssetPublishRequest publishRequest) {
        Long userId = (Long) request.getAttribute("userId");
        Long assetId = assetService.publishAsset(userId, publishRequest);
        return Result.success(assetId);
    }

    /**
     * 更新资产
     */
    @PutMapping("/{id}")
    public Result<Void> updateAsset(
            @PathVariable Long id,
            HttpServletRequest request,
            @Valid @RequestBody AssetPublishRequest publishRequest) {
        Long userId = (Long) request.getAttribute("userId");
        assetService.updateAsset(id, userId, publishRequest);
        return Result.success();
    }

    /**
     * 提交审核
     */
    @PostMapping("/{id}/submit")
    public Result<Void> submitForReview(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        assetService.submitForReview(id, userId);
        return Result.success();
    }

    /**
     * 下架资产
     */
    @PostMapping("/{id}/offline")
    public Result<Void> offline(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        assetService.offline(id, userId);
        return Result.success();
    }

    /**
     * 删除资产
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAsset(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        assetService.deleteAsset(id, userId);
        return Result.success();
    }

    /**
     * 下载资产（增加下载量）
     */
    @PostMapping("/{id}/download")
    public Result<Void> download(@PathVariable Long id) {
        AssetDTO asset = assetService.getAssetById(id);
        if (asset.getCurrentVersionId() != null) {
            assetService.incrementDownloadCount(asset.getCurrentVersionId());
        }
        return Result.success();
    }
}
