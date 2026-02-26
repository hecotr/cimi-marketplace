package com.aimarketplace.service.impl;

import com.aimarketplace.common.BusinessException;
import com.aimarketplace.dto.AssetDTO;
import com.aimarketplace.dto.AssetPublishRequest;
import com.aimarketplace.dto.AssetVersionDTO;
import com.aimarketplace.entity.ApprovalRecord;
import com.aimarketplace.entity.Asset;
import com.aimarketplace.entity.AssetVersion;
import com.aimarketplace.entity.Category;
import com.aimarketplace.entity.User;
import com.aimarketplace.mapper.ApprovalRecordMapper;
import com.aimarketplace.mapper.AssetMapper;
import com.aimarketplace.mapper.AssetVersionMapper;
import com.aimarketplace.mapper.CategoryMapper;
import com.aimarketplace.mapper.UserMapper;
import com.aimarketplace.service.AssetService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AssetServiceImpl implements AssetService {

    @Autowired
    private AssetMapper assetMapper;

    @Autowired
    private AssetVersionMapper assetVersionMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApprovalRecordMapper approvalRecordMapper;

    @Override
    public IPage<AssetDTO> getAssets(String assetType, String status, Long categoryId,
                                      String keyword, int page, int size) {
        Page<Asset> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        if (assetType != null) {
            wrapper.eq(Asset::getAssetType, assetType);
        }
        if (status != null) {
            wrapper.eq(Asset::getStatus, status);
        } else {
            // 默认只显示已审核通过的
            wrapper.eq(Asset::getStatus, "approved");
        }
        if (categoryId != null) {
            wrapper.eq(Asset::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                .like(Asset::getName, keyword)
                .or()
                .like(Asset::getDescription, keyword)
            );
        }
        wrapper.orderByDesc(Asset::getCreatedAt);

        IPage<Asset> assetPage = assetMapper.selectPage(pageParam, wrapper);

        return assetPage.convert(this::toDTO);
    }

    @Override
    public List<AssetDTO> getMyAssets(Long userId, String assetType, String status) {
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Asset::getCreatedBy, userId);
        if (assetType != null) {
            wrapper.eq(Asset::getAssetType, assetType);
        }
        if (status != null) {
            wrapper.eq(Asset::getStatus, status);
        }
        wrapper.orderByDesc(Asset::getCreatedAt);

        List<Asset> assets = assetMapper.selectList(wrapper);
        return assets.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public AssetDTO getAssetById(Long id) {
        Asset asset = assetMapper.selectById(id);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        AssetDTO dto = toDTO(asset);

        // 加载版本信息
        List<AssetVersion> versions = assetVersionMapper.selectList(
            new LambdaQueryWrapper<AssetVersion>()
                .eq(AssetVersion::getAssetId, id)
                .orderByDesc(AssetVersion::getVersionNo)
        );
        dto.setVersions(versions.stream().map(this::toVersionDTO).collect(Collectors.toList()));

        return dto;
    }

    @Override
    @Transactional
    public Long publishAsset(Long userId, AssetPublishRequest request) {
        // 创建资产
        Asset asset = new Asset();
        asset.setAssetType(request.getAssetType());
        asset.setName(request.getName());
        asset.setDescription(request.getDescription());
        asset.setCategoryId(request.getCategoryId());
        asset.setTags(request.getTags());
        asset.setCreatedBy(userId);
        asset.setStatus("draft");
        asset.setCreatedAt(LocalDateTime.now());
        asset.setUpdatedAt(LocalDateTime.now());

        assetMapper.insert(asset);

        // 创建第一个版本
        AssetVersion version = new AssetVersion();
        version.setAssetId(asset.getId());
        version.setVersionNo(1);
        version.setContent(request.getContent());
        version.setFileType(request.getFileType());
        version.setViewCount(0);
        version.setDownloadCount(0);
        version.setLikeCount(0);
        version.setCreatedAt(LocalDateTime.now());

        assetVersionMapper.insert(version);

        // 更新资产的当前版本
        asset.setCurrentVersionId(version.getId());
        assetMapper.updateById(asset);

        return asset.getId();
    }

    @Override
    @Transactional
    public void updateAsset(Long id, Long userId, AssetPublishRequest request) {
        Asset asset = assetMapper.selectById(id);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        if (!asset.getCreatedBy().equals(userId)) {
            throw new BusinessException("无权修改此资产");
        }
        if ("approved".equals(asset.getStatus())) {
            throw new BusinessException("已审核通过的资产不能直接修改，请创建新版本");
        }

        asset.setName(request.getName());
        asset.setDescription(request.getDescription());
        asset.setCategoryId(request.getCategoryId());
        asset.setTags(request.getTags());
        asset.setUpdatedAt(LocalDateTime.now());

        assetMapper.updateById(asset);

        // 更新当前版本的内容
        if (asset.getCurrentVersionId() != null) {
            AssetVersion version = assetVersionMapper.selectById(asset.getCurrentVersionId());
            if (version != null) {
                version.setContent(request.getContent());
                version.setFileType(request.getFileType());
                assetVersionMapper.updateById(version);
            }
        }
    }

    @Override
    public void submitForReview(Long id, Long userId) {
        Asset asset = assetMapper.selectById(id);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        if (!asset.getCreatedBy().equals(userId)) {
            throw new BusinessException("无权操作此资产");
        }
        if (!"draft".equals(asset.getStatus()) && !"rejected".equals(asset.getStatus())) {
            throw new BusinessException("当前状态不能提交审核");
        }

        asset.setStatus("pending_review");
        asset.setUpdatedAt(LocalDateTime.now());
        assetMapper.updateById(asset);
    }

    @Override
    @Transactional
    public void approve(Long id, Long reviewerId, String comment) {
        Asset asset = assetMapper.selectById(id);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        if (!"pending_review".equals(asset.getStatus())) {
            throw new BusinessException("当前状态不需要审核");
        }

        asset.setStatus("approved");
        asset.setUpdatedAt(LocalDateTime.now());
        assetMapper.updateById(asset);

        // 创建审核记录
        ApprovalRecord record = new ApprovalRecord();
        record.setApprovalType("asset");
        record.setTargetId(id);
        record.setTargetType(asset.getAssetType());
        record.setReviewerId(reviewerId);
        record.setStatus("approved");
        record.setComment(comment);
        record.setCreatedAt(LocalDateTime.now());
        approvalRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void reject(Long id, Long reviewerId, String comment) {
        Asset asset = assetMapper.selectById(id);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        if (!"pending_review".equals(asset.getStatus())) {
            throw new BusinessException("当前状态不需要审核");
        }

        asset.setStatus("rejected");
        asset.setUpdatedAt(LocalDateTime.now());
        assetMapper.updateById(asset);

        // 创建审核记录
        ApprovalRecord record = new ApprovalRecord();
        record.setApprovalType("asset");
        record.setTargetId(id);
        record.setTargetType(asset.getAssetType());
        record.setReviewerId(reviewerId);
        record.setStatus("rejected");
        record.setComment(comment);
        record.setCreatedAt(LocalDateTime.now());
        approvalRecordMapper.insert(record);
    }

    @Override
    public void offline(Long id, Long userId) {
        Asset asset = assetMapper.selectById(id);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        if (!asset.getCreatedBy().equals(userId)) {
            throw new BusinessException("无权操作此资产");
        }

        asset.setStatus("offline");
        asset.setUpdatedAt(LocalDateTime.now());
        assetMapper.updateById(asset);
    }

    @Override
    public void deleteAsset(Long id, Long userId) {
        Asset asset = assetMapper.selectById(id);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        if (!asset.getCreatedBy().equals(userId)) {
            throw new BusinessException("无权删除此资产");
        }

        // 删除关联的版本
        assetVersionMapper.delete(
            new LambdaQueryWrapper<AssetVersion>()
                .eq(AssetVersion::getAssetId, id)
        );

        // 删除资产
        assetMapper.deleteById(id);
    }

    @Override
    public void incrementViewCount(Long versionId) {
        AssetVersion version = assetVersionMapper.selectById(versionId);
        if (version != null) {
            version.setViewCount(version.getViewCount() + 1);
            assetVersionMapper.updateById(version);
        }
    }

    @Override
    public void incrementDownloadCount(Long versionId) {
        AssetVersion version = assetVersionMapper.selectById(versionId);
        if (version != null) {
            version.setDownloadCount(version.getDownloadCount() + 1);
            assetVersionMapper.updateById(version);
        }
    }

    private AssetDTO toDTO(Asset asset) {
        AssetDTO dto = new AssetDTO();
        BeanUtils.copyProperties(asset, dto);

        // 获取分类名称
        if (asset.getCategoryId() != null) {
            Category category = categoryMapper.selectById(asset.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
        }

        // 获取创建者名称
        if (asset.getCreatedBy() != null) {
            User user = userMapper.selectById(asset.getCreatedBy());
            if (user != null) {
                dto.setCreatedBy(user.getUsername());
            }
        }

        if (asset.getCreatedAt() != null) {
            dto.setCreatedAt(asset.getCreatedAt().toString());
        }
        if (asset.getUpdatedAt() != null) {
            dto.setUpdatedAt(asset.getUpdatedAt().toString());
        }

        return dto;
    }

    private AssetVersionDTO toVersionDTO(AssetVersion version) {
        AssetVersionDTO dto = new AssetVersionDTO();
        BeanUtils.copyProperties(version, dto);
        if (version.getCreatedAt() != null) {
            dto.setCreatedAt(version.getCreatedAt().toString());
        }
        return dto;
    }
}
