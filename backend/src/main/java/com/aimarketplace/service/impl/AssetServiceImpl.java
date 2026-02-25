package com.aimarketplace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aimarketplace.dto.AssetDTO;
import com.aimarketplace.dto.AssetPublishRequest;
import com.aimarketplace.entity.Asset;
import com.aimarketplace.entity.AssetVersion;
import com.aimarketplace.entity.User;
import com.aimarketplace.mapper.*;
import com.aimarketplace.service.AssetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Asset Service Implementation
 */
@Service
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements AssetService {

    @Autowired
    private AssetVersionMapper assetVersionMapper;

    @Autowired
    private ApprovalRecordMapper approvalRecordMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public AssetDTO createAsset(AssetPublishRequest request) {
        Asset asset = new Asset();
        asset.setName(request.getName());
        asset.setType(request.getType());
        asset.setDescription(request.getDescription());
        asset.setCategoryId(request.getCategoryId());
        asset.setContent(request.getContent());
        asset.setStoragePath(request.getStoragePath());
        asset.setVersion(request.getVersion());
        asset.setStatus("draft");
        asset.setViewCount(0);
        asset.setDownloadCount(0);
        asset.setLikeCount(0);
        asset.setCreatedBy(request.getCreatorId());
        asset.setCreatedAt(LocalDateTime.now());
        asset.setUpdatedAt(LocalDateTime.now());

        save(asset);

        // Create initial version
        AssetVersion version = new AssetVersion();
        version.setAssetId(asset.getId());
        version.setVersion(request.getVersion());
        version.setContent(request.getContent());
        version.setStoragePath(request.getStoragePath());
        version.setChangeNotes("Initial version");
        version.setCreatedBy(request.getCreatorId());
        version.setCreatedAt(LocalDateTime.now());
        version.setIsCurrent(true);
        assetVersionMapper.insert(version);

        return entityToDTO(asset);
    }

    @Override
    @Transactional
    public AssetDTO publishAsset(Long assetId) {
        Asset asset = getById(assetId);
        if (asset == null) {
            throw new RuntimeException("Asset not found");
        }
        if (!asset.getStatus().equals("draft")) {
            throw new RuntimeException("Only draft assets can be published");
        }

        asset.setStatus("pending");
        asset.setUpdatedAt(LocalDateTime.now());
        updateById(asset);

        return entityToDTO(asset);
    }

    @Override
    @Transactional
    public AssetDTO updateAsset(Long assetId, AssetPublishRequest request) {
        Asset asset = getById(assetId);
        if (asset == null) {
            throw new RuntimeException("Asset not found");
        }
        if (!asset.getStatus().equals("draft")) {
            throw new RuntimeException("Only draft assets can be updated");
        }

        asset.setName(request.getName());
        asset.setDescription(request.getDescription());
        asset.setCategoryId(request.getCategoryId());
        asset.setContent(request.getContent());
        asset.setStoragePath(request.getStoragePath());
        asset.setUpdatedAt(LocalDateTime.now());
        updateById(asset);

        return entityToDTO(asset);
    }

    @Override
    @Transactional
    public AssetDTO createVersion(Long assetId, AssetPublishRequest request) {
        Asset asset = getById(assetId);
        if (asset == null) {
            throw new RuntimeException("Asset not found");
        }

        // Deactivate current version
        AssetVersion currentVersion = assetVersionMapper.findCurrentByAssetId(assetId);
        if (currentVersion != null) {
            currentVersion.setIsCurrent(false);
            assetVersionMapper.updateById(currentVersion);
        }

        // Create new version
        AssetVersion newVersion = new AssetVersion();
        newVersion.setAssetId(assetId);
        newVersion.setVersion(request.getVersion());
        newVersion.setContent(request.getContent());
        newVersion.setStoragePath(request.getStoragePath());
        newVersion.setChangeNotes(request.getChangeNotes());
        newVersion.setCreatedBy(request.getCreatorId());
        newVersion.setCreatedAt(LocalDateTime.now());
        newVersion.setIsCurrent(true);
        assetVersionMapper.insert(newVersion);

        // Update asset
        asset.setContent(request.getContent());
        asset.setStoragePath(request.getStoragePath());
        asset.setVersion(request.getVersion());
        asset.setUpdatedAt(LocalDateTime.now());
        updateById(asset);

        return entityToDTO(asset);
    }

    @Override
    public AssetDTO getAssetById(Long assetId) {
        Asset asset = getById(assetId);
        return asset != null ? entityToDTO(asset) : null;
    }

    @Override
    public AssetDTO getAssetByVersion(Long assetId, String version) {
        Asset asset = getById(assetId);
        if (asset == null) {
            return null;
        }

        AssetVersion assetVersion = assetVersionMapper.findByAssetIdAndVersion(assetId, version);
        if (assetVersion != null) {
            asset.setContent(assetVersion.getContent());
            asset.setStoragePath(assetVersion.getStoragePath());
        }

        return entityToDTO(asset);
    }

    @Override
    public List<AssetDTO> getPublishedAssets(String type, int page, int size) {
        Page<Asset> pageParam = new Page<>(page, size);
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        wrapper.eq("type", type);
        wrapper.eq("status", "published");
        wrapper.orderByDesc("created_at");
        Page<Asset> result = page(pageParam, wrapper);
        return result.getRecords().stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public List<AssetDTO> getUserAssets(Long userId, int page, int size) {
        Page<Asset> pageParam = new Page<>(page, size);
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        wrapper.eq("created_by", userId);
        wrapper.orderByDesc("created_at");
        Page<Asset> result = page(pageParam, wrapper);
        return result.getRecords().stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public List<AssetDTO> getDraftAssets(Long userId) {
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        wrapper.eq("created_by", userId);
        wrapper.eq("status", "draft");
        wrapper.orderByDesc("created_at");
        return list(wrapper).stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public List<AssetDTO> getPublishedAssetsByUser(Long userId) {
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        wrapper.eq("created_by", userId);
        wrapper.eq("status", "published");
        wrapper.orderByDesc("created_at");
        return list(wrapper).stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public List<AssetDTO> searchAssets(String keyword, String type, Long categoryId, int page, int size) {
        Page<Asset> pageParam = new Page<>(page, size);
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "published");

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("name", keyword).or().like("description", keyword));
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq("type", type);
        }
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }

        wrapper.orderByDesc("created_at");
        Page<Asset> result = page(pageParam, wrapper);
        return result.getRecords().stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void incrementViewCount(Long assetId) {
        Asset asset = getById(assetId);
        if (asset != null) {
            asset.setViewCount((asset.getViewCount() == null ? 0 : asset.getViewCount()) + 1);
            updateById(asset);
        }
    }

    @Override
    @Transactional
    public void incrementDownloadCount(Long assetId) {
        Asset asset = getById(assetId);
        if (asset != null) {
            asset.setDownloadCount((asset.getDownloadCount() == null ? 0 : asset.getDownloadCount()) + 1);
            updateById(asset);
        }
    }

    @Override
    @Transactional
    public void deleteAsset(Long assetId) {
        removeById(assetId);
    }

    @Override
    public List<AssetVersion> getAssetVersions(Long assetId) {
        return assetVersionMapper.findByAssetId(assetId);
    }

    private AssetDTO entityToDTO(Asset asset) {
        AssetDTO dto = new AssetDTO();
        BeanUtils.copyProperties(asset, dto);

        // Load category name
        if (asset.getCategoryId() != null) {
            com.aimarketplace.entity.Category category = categoryMapper.selectById(asset.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
        }

        // Load creator name
        if (asset.getCreatedBy() != null) {
            User user = userMapper.selectById(asset.getCreatedBy());
            if (user != null) {
                dto.setCreatorName(user.getUsername());
            }
        }

        // Load approver name
        if (asset.getApprovedBy() != null) {
            User user = userMapper.selectById(asset.getApprovedBy());
            if (user != null) {
                dto.setApproverName(user.getUsername());
            }
        }

        // Get rejection reason from approval record
        if ("rejected".equals(asset.getStatus())) {
            QueryWrapper<com.aimarketplace.entity.ApprovalRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("asset_id", asset.getId());
            wrapper.eq("action", "reject");
            wrapper.orderByDesc("created_at");
            wrapper.last("LIMIT 1");
            com.aimarketplace.entity.ApprovalRecord record = approvalRecordMapper.selectOne(wrapper);
            if (record != null) {
                dto.setRejectionReason(record.getComment());
            }
        }

        return dto;
    }
}
