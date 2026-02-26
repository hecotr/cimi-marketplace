package com.aimarketplace.service;

import com.aimarketplace.dto.AssetDTO;
import com.aimarketplace.dto.AssetPublishRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface AssetService {

    /**
     * 获取资产列表（分页）
     */
    IPage<AssetDTO> getAssets(String assetType, String status, Long categoryId,
                               String keyword, int page, int size);

    /**
     * 获取用户自己的资产
     */
    List<AssetDTO> getMyAssets(Long userId, String assetType, String status);

    /**
     * 获取资产详情
     */
    AssetDTO getAssetById(Long id);

    /**
     * 发布资产（草稿或提交审核）
     */
    Long publishAsset(Long userId, AssetPublishRequest request);

    /**
     * 更新资产
     */
    void updateAsset(Long id, Long userId, AssetPublishRequest request);

    /**
     * 提交审核
     */
    void submitForReview(Long id, Long userId);

    /**
     * 审核通过（管理员）
     */
    void approve(Long id, Long reviewerId, String comment);

    /**
     * 审核拒绝（管理员）
     */
    void reject(Long id, Long reviewerId, String comment);

    /**
     * 下架资产
     */
    void offline(Long id, Long userId);

    /**
     * 删除资产
     */
    void deleteAsset(Long id, Long userId);

    /**
     * 增加浏览量
     */
    void incrementViewCount(Long versionId);

    /**
     * 增加下载量
     */
    void incrementDownloadCount(Long versionId);
}
