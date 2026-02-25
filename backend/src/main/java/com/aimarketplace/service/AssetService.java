package com.aimarketplace.service;

import com.aimarketplace.dto.AssetDTO;
import com.aimarketplace.dto.AssetPublishRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Asset Service Interface
 */
public interface AssetService extends IService<com.aimarketplace.entity.Asset> {

    /**
     * Create a new asset (draft)
     */
    AssetDTO createAsset(AssetPublishRequest request);

    /**
     * Publish an asset for approval
     */
    AssetDTO publishAsset(Long assetId);

    /**
     * Update an existing asset
     */
    AssetDTO updateAsset(Long assetId, AssetPublishRequest request);

    /**
     * Create a new version of an asset
     */
    AssetDTO createVersion(Long assetId, AssetPublishRequest request);

    /**
     * Get asset by ID
     */
    AssetDTO getAssetById(Long assetId);

    /**
     * Get asset by ID with version
     */
    AssetDTO getAssetByVersion(Long assetId, String version);

    /**
     * Get published assets by type
     */
    List<AssetDTO> getPublishedAssets(String type, int page, int size);

    /**
     * Get user's assets
     */
    List<AssetDTO> getUserAssets(Long userId, int page, int size);

    /**
     * Get draft assets
     */
    List<AssetDTO> getDraftAssets(Long userId);

    /**
     * Get published assets by user
     */
    List<AssetDTO> getPublishedAssetsByUser(Long userId);

    /**
     * Search assets
     */
    List<AssetDTO> searchAssets(String keyword, String type, Long categoryId, int page, int size);

    /**
     * Increment view count
     */
    void incrementViewCount(Long assetId);

    /**
     * Increment download count
     */
    void incrementDownloadCount(Long assetId);

    /**
     * Delete asset
     */
    void deleteAsset(Long assetId);

    /**
     * Get all versions of an asset
     */
    List<com.aimarketplace.entity.AssetVersion> getAssetVersions(Long assetId);
}
