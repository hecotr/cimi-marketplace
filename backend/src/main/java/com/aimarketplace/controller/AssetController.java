package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.AssetDTO;
import com.aimarketplace.dto.AssetPublishRequest;
import com.aimarketplace.entity.AssetVersion;
import com.aimarketplace.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

/**
 * Asset Controller
 */
@RestController
@RequestMapping("/api/asset")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @PostMapping
    public Result<AssetDTO> createAsset(@Valid @RequestBody AssetPublishRequest request) {
        AssetDTO asset = assetService.createAsset(request);
        return Result.success(asset);
    }

    @PutMapping("/{id}")
    public Result<AssetDTO> updateAsset(@PathVariable Long id, @Valid @RequestBody AssetPublishRequest request) {
        AssetDTO asset = assetService.updateAsset(id, request);
        return Result.success(asset);
    }

    @PostMapping("/{id}/publish")
    public Result<AssetDTO> publishAsset(@PathVariable Long id) {
        AssetDTO asset = assetService.publishAsset(id);
        return Result.success(asset);
    }

    @PostMapping("/{id}/version")
    public Result<AssetDTO> createVersion(@PathVariable Long id, @Valid @RequestBody AssetPublishRequest request) {
        AssetDTO asset = assetService.createVersion(id, request);
        return Result.success(asset);
    }

    @GetMapping("/{id}")
    public Result<AssetDTO> getAsset(@PathVariable Long id) {
        assetService.incrementViewCount(id);
        AssetDTO asset = assetService.getAssetById(id);
        return Result.success(asset);
    }

    @GetMapping("/{id}/version/{version}")
    public Result<AssetDTO> getAssetByVersion(@PathVariable Long id, @PathVariable String version) {
        AssetDTO asset = assetService.getAssetByVersion(id, version);
        return Result.success(asset);
    }

    @GetMapping("/{id}/versions")
    public Result<List<AssetVersion>> getAssetVersions(@PathVariable Long id) {
        List<AssetVersion> versions = assetService.getAssetVersions(id);
        return Result.success(versions);
    }

    @GetMapping("/list")
    public Result<List<AssetDTO>> getPublishedAssets(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<AssetDTO> assets = assetService.getPublishedAssets(type, page, size);
        return Result.success(assets);
    }

    @GetMapping("/search")
    public Result<List<AssetDTO>> searchAssets(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<AssetDTO> assets = assetService.searchAssets(keyword, type, categoryId, page, size);
        return Result.success(assets);
    }

    @GetMapping("/my/drafts")
    public Result<List<AssetDTO>> getMyDrafts(@RequestHeader("X-User-Id") Long userId) {
        List<AssetDTO> drafts = assetService.getMyDrafts();
        return Result.success(drafts);
    }

    @GetMapping("/my/published")
    public Result<List<AssetDTO>> getMyPublished(@RequestHeader("X-User-Id") Long userId) {
        List<AssetDTO> published = assetService.getMyPublished();
        return Result.success(published);
    }

    @PostMapping("/{id}/download")
    public Result<Void> downloadAsset(@PathVariable Long id) {
        assetService.incrementDownloadCount(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return Result.success();
    }
}
