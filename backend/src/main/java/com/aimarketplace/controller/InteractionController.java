package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import jakarta.validation.constraints.*;

/**
 * Interaction Controller
 */
@RestController
@RequestMapping("/api/interaction")
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    @PostMapping("/favorite")
    public Result<Void> toggleFavorite(@RequestBody FavoriteRequest request) {
        interactionService.addFavorite(request.getUserId(), request.getAssetId(), request.getAssetType());
        return Result.success();
    }

    @DeleteMapping("/favorite")
    public Result<Void> removeFavorite(@RequestParam Long assetId, @RequestParam String assetType) {
        interactionService.removeFavorite(getCurrentUserId(), assetId, assetType);
        return Result.success();
    }

    @GetMapping("/favorites")
    public Result<List<?>> getFavorites(
            @RequestParam(required = false) String assetType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<?> favorites = interactionService.getUserFavorites(getCurrentUserId(), assetType, page, size);
        return Result.success(favorites);
    }

    @PostMapping("/like")
    public Result<Boolean> toggleLike(@RequestBody LikeRequest request) {
        boolean liked = interactionService.toggleLike(request.getUserId(), request.getAssetId(), request.getAssetType());
        return Result.success(liked);
    }

    @GetMapping("/liked")
    public Result<Boolean> isLiked(@RequestParam Long assetId, @RequestParam String assetType) {
        boolean liked = interactionService.isLiked(getCurrentUserId(), assetId, assetType);
        return Result.success(liked);
    }

    @GetMapping("/favorited")
    public Result<Boolean> isFavorited(@RequestParam Long assetId, @RequestParam String assetType) {
        boolean favorited = interactionService.isFavorited(getCurrentUserId(), assetId, assetType);
        return Result.success(favorited);
    }

    private Long getCurrentUserId() {
        // Get user ID from authentication context
        // This is a simplified version - in production, extract from JWT token
        return 1L;
    }

    static class FavoriteRequest {
        private Long userId;
        private Long assetId;
        private String assetType;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getAssetId() { return assetId; }
        public void setAssetId(Long assetId) { this.assetId = assetId; }
        public String getAssetType() { return assetType; }
        public void setAssetType(String assetType) { this.assetType = assetType; }
    }

    static class LikeRequest {
        private Long userId;
        private Long assetId;
        private String assetType;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getAssetId() { return assetId; }
        public void setAssetId(Long assetId) { this.assetId = assetId; }
        public String getAssetType() { return assetType; }
        public void setAssetType(String assetType) { this.assetType = assetType; }
    }
}
