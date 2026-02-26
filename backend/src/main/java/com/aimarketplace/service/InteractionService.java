package com.aimarketplace.service;

import java.util.List;

public interface InteractionService {

    /**
     * 收藏资产
     */
    void addFavorite(Long userId, Long assetId, String assetType);

    /**
     * 取消收藏
     */
    void removeFavorite(Long userId, Long assetId, String assetType);

    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long userId, Long assetId, String assetType);

    /**
     * 获取用户收藏的资产ID列表
     */
    List<Long> getFavoriteAssetIds(Long userId, String assetType);

    /**
     * 点赞
     */
    void addLike(Long userId, Long assetId, String assetType, Long versionId);

    /**
     * 取消点赞
     */
    void removeLike(Long userId, Long assetId, String assetType, Long versionId);

    /**
     * 检查是否已点赞
     */
    boolean isLiked(Long userId, Long assetId, String assetType, Long versionId);
}
