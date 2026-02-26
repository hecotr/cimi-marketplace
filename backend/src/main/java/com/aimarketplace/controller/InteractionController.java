package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.InteractionRequest;
import com.aimarketplace.dto.LikeRequest;
import com.aimarketplace.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/interactions")
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    /**
     * 收藏资产
     */
    @PostMapping("/favorite")
    public Result<Void> addFavorite(
            HttpServletRequest request,
            @Valid @RequestBody InteractionRequest body) {
        Long userId = (Long) request.getAttribute("userId");
        interactionService.addFavorite(userId, body.getAssetId(), body.getAssetType());
        return Result.success();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/favorite")
    public Result<Void> removeFavorite(
            HttpServletRequest request,
            @RequestParam Long assetId,
            @RequestParam String assetType) {
        Long userId = (Long) request.getAttribute("userId");
        interactionService.removeFavorite(userId, assetId, assetType);
        return Result.success();
    }

    /**
     * 检查收藏状态
     */
    @GetMapping("/favorite/check")
    public Result<Map<String, Boolean>> checkFavorite(
            HttpServletRequest request,
            @RequestParam Long assetId,
            @RequestParam String assetType) {
        Long userId = (Long) request.getAttribute("userId");
        boolean isFavorited = interactionService.isFavorited(userId, assetId, assetType);

        Map<String, Boolean> result = new HashMap<>();
        result.put("isFavorited", isFavorited);
        return Result.success(result);
    }

    /**
     * 点赞
     */
    @PostMapping("/like")
    public Result<Void> addLike(
            HttpServletRequest request,
            @Valid @RequestBody LikeRequest body) {
        Long userId = (Long) request.getAttribute("userId");
        interactionService.addLike(userId, body.getAssetId(), body.getAssetType(), body.getVersionId());
        return Result.success();
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/like")
    public Result<Void> removeLike(
            HttpServletRequest request,
            @RequestParam Long assetId,
            @RequestParam String assetType,
            @RequestParam(required = false) Long versionId) {
        Long userId = (Long) request.getAttribute("userId");
        interactionService.removeLike(userId, assetId, assetType, versionId);
        return Result.success();
    }

    /**
     * 检查点赞状态
     */
    @GetMapping("/like/check")
    public Result<Map<String, Boolean>> checkLike(
            HttpServletRequest request,
            @RequestParam Long assetId,
            @RequestParam String assetType,
            @RequestParam(required = false) Long versionId) {
        Long userId = (Long) request.getAttribute("userId");
        boolean isLiked = interactionService.isLiked(userId, assetId, assetType, versionId);

        Map<String, Boolean> result = new HashMap<>();
        result.put("isLiked", isLiked);
        return Result.success(result);
    }
}
