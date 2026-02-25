package com.aimarketplace.service;

import com.aimarketplace.dto.AssetDTO;

import java.util.List;

/**
 * Interaction Service Interface
 */
public interface InteractionService {

    /**
     * Add favorite
     */
    void addFavorite(Long userId, Long assetId, String assetType);

    /**
     * Remove favorite
     */
    void removeFavorite(Long userId, Long assetId, String assetType);

    /**
     * Check if asset is favorited by user
     */
    boolean isFavorited(Long userId, Long assetId, String assetType);

    /**
     * Get user's favorites
     */
    List<AssetDTO> getUserFavorites(Long userId, String assetType, int page, int size);

    /**
     * Add like
     */
    void addLike(Long userId, Long assetId, String assetType);

    /**
     * Remove like
     */
    void removeLike(Long userId, Long assetId, String assetType);

    /**
     * Check if asset is liked by user
     */
    boolean isLiked(Long userId, Long assetId, String assetType);

    /**
     * Toggle like
     */
    boolean toggleLike(Long userId, Long assetId, String assetType);
}
