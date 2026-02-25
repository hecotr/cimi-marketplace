package com.aimarketplace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aimarketplace.dto.AssetDTO;
import com.aimarketplace.entity.Asset;
import com.aimarketplace.entity.Favorite;
import com.aimarketplace.entity.LikeRecord;
import com.aimarketplace.mapper.AssetMapper;
import com.aimarketplace.mapper.FavoriteMapper;
import com.aimarketplace.mapper.LikeRecordMapper;
import com.aimarketplace.service.AssetService;
import com.aimarketplace.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interaction Service Implementation
 */
@Service
public class InteractionServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements InteractionService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Autowired
    private AssetMapper assetMapper;

    @Autowired
    private AssetService assetService;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long assetId, String assetType) {
        Favorite existing = favoriteMapper.findByUserAndAsset(userId, assetId, assetType);
        if (existing != null) {
            return; // Already favorited
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setAssetId(assetId);
        favorite.setAssetType(assetType);
        favorite.setCreatedAt(LocalDateTime.now());
        save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long assetId, String assetType) {
        Favorite favorite = favoriteMapper.findByUserAndAsset(userId, assetId, assetType);
        if (favorite != null) {
            removeById(favorite.getId());
        }
    }

    @Override
    public boolean isFavorited(Long userId, Long assetId, String assetType) {
        return favoriteMapper.findByUserAndAsset(userId, assetId, assetType) != null;
    }

    @Override
    public List<AssetDTO> getUserFavorites(Long userId, String assetType, int page, int size) {
        List<Favorite> favorites;
        if (assetType != null && !assetType.isEmpty()) {
            favorites = favoriteMapper.findByUserIdAndType(userId, assetType);
        } else {
            favorites = favoriteMapper.findByUserId(userId);
        }

        List<Long> assetIds = favorites.stream()
                .map(Favorite::getAssetId)
                .distinct()
                .collect(Collectors.toList());

        if (assetIds.isEmpty()) {
            return List.of();
        }

        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        wrapper.in("id", assetIds);
        wrapper.eq("status", "published");
        wrapper.orderByDesc("created_at");

        Page<Asset> pageParam = new Page<>(page, size);
        Page<Asset> result = assetMapper.selectPage(pageParam, wrapper);

        return result.getRecords().stream()
                .map(asset -> {
                    AssetDTO dto = assetService.getAssetById(asset.getId());
                    if (dto != null) {
                        dto.setIsFavorite(true);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addLike(Long userId, Long assetId, String assetType) {
        LikeRecord existing = likeRecordMapper.findByUserAndAsset(userId, assetId, assetType);
        if (existing != null) {
            return; // Already liked
        }

        LikeRecord likeRecord = new LikeRecord();
        likeRecord.setUserId(userId);
        likeRecord.setAssetId(assetId);
        likeRecord.setAssetType(assetType);
        likeRecord.setCreatedAt(LocalDateTime.now());
        likeRecordMapper.insert(likeRecord);

        // Update asset like count
        Asset asset = assetMapper.selectById(assetId);
        if (asset != null) {
            asset.setLikeCount((asset.getLikeCount() == null ? 0 : asset.getLikeCount()) + 1);
            assetMapper.updateById(asset);
        }
    }

    @Override
    @Transactional
    public void removeLike(Long userId, Long assetId, String assetType) {
        LikeRecord likeRecord = likeRecordMapper.findByUserAndAsset(userId, assetId, assetType);
        if (likeRecord != null) {
            likeRecordMapper.deleteById(likeRecord.getId());

            // Update asset like count
            Asset asset = assetMapper.selectById(assetId);
            if (asset != null && asset.getLikeCount() != null && asset.getLikeCount() > 0) {
                asset.setLikeCount(asset.getLikeCount() - 1);
                assetMapper.updateById(asset);
            }
        }
    }

    @Override
    public boolean isLiked(Long userId, Long assetId, String assetType) {
        return likeRecordMapper.findByUserAndAsset(userId, assetId, assetType) != null;
    }

    @Override
    @Transactional
    public boolean toggleLike(Long userId, Long assetId, String assetType) {
        if (isLiked(userId, assetId, assetType)) {
            removeLike(userId, assetId, assetType);
            return false;
        } else {
            addLike(userId, assetId, assetType);
            return true;
        }
    }
}
