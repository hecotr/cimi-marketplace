package com.aimarketplace.service.impl;

import com.aimarketplace.entity.Favorite;
import com.aimarketplace.entity.LikeRecord;
import com.aimarketplace.mapper.FavoriteMapper;
import com.aimarketplace.mapper.LikeRecordMapper;
import com.aimarketplace.service.InteractionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InteractionServiceImpl implements InteractionService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Override
    public void addFavorite(Long userId, Long assetId, String assetType) {
        // 检查是否已收藏
        if (isFavorited(userId, assetId, assetType)) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setAssetId(assetId);
        favorite.setAssetType(assetType);
        favorite.setCreatedAt(LocalDateTime.now());

        favoriteMapper.insert(favorite);
    }

    @Override
    public void removeFavorite(Long userId, Long assetId, String assetType) {
        favoriteMapper.delete(
            new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getAssetId, assetId)
                .eq(Favorite::getAssetType, assetType)
        );
    }

    @Override
    public boolean isFavorited(Long userId, Long assetId, String assetType) {
        return favoriteMapper.selectCount(
            new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getAssetId, assetId)
                .eq(Favorite::getAssetType, assetType)
        ) > 0;
    }

    @Override
    public List<Long> getFavoriteAssetIds(Long userId, String assetType) {
        List<Favorite> favorites = favoriteMapper.selectList(
            new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(assetType != null, Favorite::getAssetType, assetType)
                .orderByDesc(Favorite::getCreatedAt)
        );

        return favorites.stream()
            .map(Favorite::getAssetId)
            .collect(Collectors.toList());
    }

    @Override
    public void addLike(Long userId, Long assetId, String assetType, Long versionId) {
        // 检查是否已点赞
        if (isLiked(userId, assetId, assetType, versionId)) {
            return;
        }

        LikeRecord like = new LikeRecord();
        like.setUserId(userId);
        like.setAssetId(assetId);
        like.setAssetType(assetType);
        like.setAssetVersionId(versionId);
        like.setCreatedAt(LocalDateTime.now());

        likeRecordMapper.insert(like);
    }

    @Override
    public void removeLike(Long userId, Long assetId, String assetType, Long versionId) {
        likeRecordMapper.delete(
            new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getAssetId, assetId)
                .eq(LikeRecord::getAssetType, assetType)
                .eq(versionId != null, LikeRecord::getAssetVersionId, versionId)
        );
    }

    @Override
    public boolean isLiked(Long userId, Long assetId, String assetType, Long versionId) {
        return likeRecordMapper.selectCount(
            new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getAssetId, assetId)
                .eq(LikeRecord::getAssetType, assetType)
                .eq(versionId != null, LikeRecord::getAssetVersionId, versionId)
        ) > 0;
    }
}
